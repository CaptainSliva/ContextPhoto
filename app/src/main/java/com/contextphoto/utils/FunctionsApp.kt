package com.contextphoto.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.contextphoto.data.baseFilePath
import com.contextphoto.db.CommentDatabase
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FunctionsApp {
    fun generatePictures(
        width: Int,
        height: Int,
        count: Int,
        delete: Boolean,
        addName: String,
    ) {
        val path = File(baseFilePath, "/Nagruzka album_$addName")

        if (delete) {
            path.listFiles()?.forEach { file ->
                Log.d("GeneratePictures", "Файл удалён: ${file.absolutePath}")
                file.delete()
            }
            path.delete()
        }

        path.mkdirs()

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (i in 0 until count) {
            val filename = "${System.currentTimeMillis()}_$i.jpg"
            val file = File(path, filename)
            try {
                FileOutputStream(file).use { fos ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                }
                Log.d("GeneratePictures", "Файл создан: ${file.absolutePath}")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val filesCount = path.listFiles()?.size ?: 0
        Log.d("GeneratePictures", "Всего файлов в папке: $filesCount")
    }

    inline fun durationTranslate(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return when {
            hours > 0 -> "$hours:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
            else -> "$minutes:${secs.toString().padStart(2, '0')}"
        }
    }

    fun firebaseFirestoreDatabaseTest(context: Context) {
        val TAG = "FireDataTest"
        val fdb = Firebase.firestore
        val db = CommentDatabase.getDatabse(context).commentDao()

        CoroutineScope(Dispatchers.IO).launch {
            db.getAllComments().collect {
                it.forEach { comment ->
                    fdb
                        .collection(espRead(context).first)
                        .add(comment)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        }.addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }
                }
            }
        }
    }

    fun espWrite(
        context: Context,
        email: String,
        jwtToken: String,
    ) {
        val masterKey =
            MasterKey
                .Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        val sharedPreferences =
            EncryptedSharedPreferences.create(
                context,
                "secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        sharedPreferences
            .edit {
                putString("email", email)
                    .putString("jwtToken", jwtToken)
            }
    }

    fun espRead(context: Context): Pair<String, String> {
        val masterKey =
            MasterKey
                .Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        val sharedPreferences =
            EncryptedSharedPreferences.create(
                context,
                "secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )

        return Pair(
            sharedPreferences.getString("email", "").toString(),
            sharedPreferences.getString("jwtToken", "").toString(),
        )
    }
}
