package com.contextphoto.data

import android.R.attr.name
import android.R.attr.password
import com.contextphoto.db.Comment
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import jakarta.inject.Inject

class FireBaseSource @Inject constructor(private val firebaseAuth: FirebaseAuth, private val firestore: FirebaseFirestore) {

    fun signInWithGoogle(gso: GoogleSignInAccount) = firebaseAuth.signInWithCredential(
        GoogleAuthProvider.getCredential(gso.idToken,null))

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun saveComments(uId: String, comments: List<Comment>) {
        comments.forEach {
            firestore.collection("comments").document(uId).set(it)
        }
    }

    fun deleteComments(uId: String) {
        firestore.collection("comments").document(uId).delete()
    }
}