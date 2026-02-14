package com.contextphoto.utils

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.core.content.edit
import com.contextphoto.db.CommentDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch

object FunctionsApp {
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

    fun firebaseFirestoreDatabaseTest(context: Context,) {
        val TAG = "FireDataTest"
        val fdb = Firebase.firestore
        val db = CommentDatabase.getDatabse(context).commentDao()

        CoroutineScope(Dispatchers.IO).launch {
            db.getAllComments().collect {
                it.forEach { comment ->
                    fdb.collection(espRead(context).first).add(comment)
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
            sharedPreferences.getString("jwtToken", "").toString()
        )
    }

    fun espClear(context: Context) {
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
            .edit{
                putString("email", "")
                    .putString("jwtToken", "")
            }
    }
}

data class City(
    val name: String? = null,
    val state: String? = null,
    val country: String? = null,
    val isCapital: Boolean? = null,
    val population: Long? = null,
    val regions: List<String>? = null,
)
