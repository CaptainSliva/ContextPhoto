package com.contextphoto.data

import android.R.attr.name
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import jakarta.inject.Inject

class FireBaseSource @Inject constructor(private val firebaseAuth: FirebaseAuth, private val firestore: FirebaseFirestore) {

    fun signUpUser(email:String, password:String) = firebaseAuth.createUserWithEmailAndPassword(email,password)

    fun signInUser(email: String, password: String) = firebaseAuth.signInWithEmailAndPassword(email,password)

    fun saveUser(email: String, password:String) = firestore.collection("users").document(email).set(User(email, password))

    fun signInWithGoogle(acct: GoogleSignInAccount) = firebaseAuth.signInWithCredential(
        GoogleAuthProvider.getCredential(acct.idToken,null))

    fun fetchUser()=firestore.collection("users").get()
}

data class User(
    val email: String,
    val password: String
)