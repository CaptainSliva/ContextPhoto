package com.contextphoto.data.datasource

import android.content.Context
import android.util.Log
import com.contextphoto.db.Comment
import com.contextphoto.db.CommentDatabase
import com.contextphoto.utils.FunctionsApp.espRead
import com.contextphoto.utils.FunctionsBitmap.md5
import com.contextphoto.utils.FunctionsMediaStore.getAllMedia
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsSource
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) {
        val db = CommentDatabase.getDatabase(context).commentDao()

        fun getAllComments() = db.getAllComments()

        suspend fun importCommentsToDatabase(listComments: List<Comment>) {
            db.dropAllComments()
//        listComments.forEach {
//            println(it)
//        }
            val allHashes = listComments.map { it.image_hash }
            Log.d("import_allHashes", "$allHashes")
            val listPaths = mutableSetOf<String>()
            Log.d("import_listPaths", "$listPaths")
            getAllMedia(context).forEach { media ->
                Log.d("currentMedia", media.toString())
                val hash = md5(media.thumbnail)
                Log.d("if", "(if $hash in $allHashes\n&& ${media.path} !in $listPaths)\nresult: ${(hash in allHashes && media.path !in listPaths)}")
                if (hash in allHashes && media.path !in listPaths) {
                    Log.d("addComment", "${listComments[allHashes.indexOf(hash)].copy(image_uri = media.uri.toString())}")
                    db.addComment(listComments[allHashes.indexOf(hash)].copy(image_uri = media.uri.toString()))
                    listPaths.add(media.path)
                }
            }
        }

        suspend fun exportCommentsToFirestore() {
            val fdb = Firebase.firestore
            deleteCommentsFromFirestore()
            db.getAllComments().first().forEach { comment ->
                fdb
                    .collection(espRead(context).first)
                    .add(comment)
                    .addOnSuccessListener { documentReference ->
                        Log.d(tag, "Document added ${documentReference.id}")
                    }.addOnFailureListener { e ->
                        Log.w(tag, "Error adding document", e)
                    }
            }
        }

        suspend fun importCommentsFromFirestore() {
            val fdb = Firebase.firestore
            val listComments = mutableListOf<Comment>()
            val collectionComment = fdb.collection(espRead(context).first).get().await()
            collectionComment.documents.forEach {
                println(it)
                val comment =
                    Comment(
                        id = 0,
                        image_uri = it.getString("image_uri") ?: "",
                        image_hash = it.getString("image_hash") ?: "",
                        image_comment = it.getString("image_comment") ?: "",
                    )
                if (comment.image_comment != "") {
                    listComments.add(comment)
                }
            }
            importCommentsToDatabase(listComments)
        }

        suspend fun deleteCommentsFromFirestore() {
            val fdb = Firebase.firestore
            val collectionRef = fdb.collection(espRead(context).first)

            try {
                val collectionComment = collectionRef.get().await()
                var deletedCount = 0

                for (document in collectionComment.documents) {
                    document.reference.delete().await()
                    deletedCount++
                }
            } catch (e: Exception) {
                Log.e(tag, "Ошибка при удалении из Firestore", e)
                throw e
            }
        }

        companion object {
            val tag = "FireData"
        }
    }
