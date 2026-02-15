package com.contextphoto.data.repository

import android.util.Log
import com.contextphoto.data.datasource.FireBaseSource
import com.contextphoto.data.datasource.SettingsSource
import com.contextphoto.db.Comment
import com.contextphoto.utils.FunctionsMediaStore.deleteCommentsFile
import com.contextphoto.utils.FunctionsMediaStore.exportCommentsToFile
import com.contextphoto.utils.FunctionsMediaStore.importCommentsFromFile
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.jvm.java

class SettingsRepository
    @Inject
    constructor(
        private val settingsSource: SettingsSource
    )
{
    suspend fun exportCommentsToStorage(): Boolean {
        try {
            deleteCommentsFile()
            val listComments = settingsSource.getAllComments()
            listComments.first().forEach { comment ->
                val serializeComment = Gson().toJson(comment)
                Log.d("listComments", comment.toString())
                Log.d("GsonString", serializeComment)
                exportCommentsToFile(serializeComment)
            }
            return true
        }
        catch (e: Exception) {return false}
    }

    suspend fun importCommentsFromStorage(): Boolean {
        try {
            val listString = importCommentsFromFile()
            val listComments = mutableListOf<Comment>()
            listString.forEach {
                val comment = Gson().fromJson(it, Comment::class.java)
                Log.d("resultString", comment.toString())
                listComments.add(comment)
            }
            settingsSource.importCommentsFromStorage(listComments)
            return true
        }
        catch (e: Exception) {return false}
    }

    suspend fun exportCommentsToFirestore() {
        settingsSource.exportCommentsToFirestore()
    }

    suspend fun importCommentsFromFirestore() {
        settingsSource.importCommentsFromFirestore()
    }

}