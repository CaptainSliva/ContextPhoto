package com.contextphoto.utils

import android.R.attr.data
import android.R.attr.password
import android.content.Context
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.room.Room
import com.contextphoto.data.Album
import com.contextphoto.data.Picture
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore
import org.w3c.dom.Comment
import kotlin.jvm.java

object FunctionsApp {

    fun durationTranslate(milliseconds: Int): String {
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

    fun firebasePasswordAuth() {
        val auth = Firebase.auth

        auth.createUserWithEmailAndPassword("email", "password")
//        Thread.sleep(2000)
//        auth.signInWithEmailAndPassword("email", "password")
//        Thread.sleep(2000)
//        Firebase.auth.signOut()
//        Thread.sleep(2000)
//        auth.signInWithEmailAndPassword("email", "password")
//        Thread.sleep(2000)
//        Firebase.auth.signOut()

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