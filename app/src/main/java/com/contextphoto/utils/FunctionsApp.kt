package com.contextphoto.utils

import android.R.attr.data
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.getString
import androidx.core.content.ContextCompat.startActivity
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.contextphoto.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore
import com.google.rpc.context.AttributeContext.Auth
import kotlin.jvm.java


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

    fun firebaseRealTimeDatabaseTest() {
        // Write a message to the database
        val database = Firebase.database

        val commentsDBRef = database.getReference().child("comments")
        val usersDBRef = database.getReference().child("user")
        commentsDBRef.child("comment1").setValue("Hello Firebase!!")
        commentsDBRef.child("comment2").setValue("Hello World!!")
        usersDBRef.child("uaser1").setValue("myUser")
    }

    fun firebaseFirestoreDatabaseTest() {
        val TAG = "FireDataTest"
        val fdb = Firebase.firestore

        // Create a new user with a first and last name
        var user = hashMapOf(
            "first" to "Ada",
            "last" to "Lovelace",
            "born" to 1816,
        )

// Add a new document with a generated ID
        fdb.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        val city = City(
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


}

data class City(
    val name: String? = null,
    val state: String? = null,
    val country: String? = null,
    @field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
    val isCapital: Boolean? = null,
    val population: Long? = null,
    val regions: List<String>? = null,
)