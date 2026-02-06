package com.contextphoto.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.contextphoto.R
import com.google.android.gms.auth.api.signin.GoogleSignIn.getClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore

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

    fun firebaseFirestoreDatabaseTest() {
        val TAG = "FireDataTest"
        val fdb = Firebase.firestore

        // Create a new user with a first and last name
        var user =
            hashMapOf(
                "first" to "Ada",
                "last" to "Lovelace",
                "born" to 1816,
            )

// Add a new document with a generated ID
        fdb
            .collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        val city =
            City(
                "Los Angeles",
                "CA",
                "USA",
                false,
                5000000L,
                listOf("west_coast", "socal"),
            )
        fdb.collection("cities").document("DC").set(city)

        val washingtonRef = fdb.collection("cities").document("DC")

// Set the "isCapital" field of the city 'DC'
        washingtonRef
            .update("isCapital", true)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }

    fun googleLogin(context: Context): Intent {
        FirebaseApp.initializeApp(context)
        val gso =
            GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        val mGoogleSignInClient = getClient(context, gso)
        return mGoogleSignInClient.signInIntent
    }

    fun googleLogout(
        context: Context,
        f: () -> Unit,
    ) {
        val gso =
            GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        val mGoogleSignInClient = getClient(context, gso)
        mGoogleSignInClient.signOut().addOnCompleteListener {
            f()
        }
    }

    fun espWrire(
        context: Context,
        name: String,
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
            .edit()
            .putString("name", name)
            .apply()
    }

    fun espRead(context: Context): String {
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

        return sharedPreferences.getString("name", "").toString()
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
