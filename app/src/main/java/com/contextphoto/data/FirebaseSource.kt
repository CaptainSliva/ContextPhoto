package com.contextphoto.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import jakarta.inject.Inject

class FireBaseSource @Inject constructor(private val firebaseAuth: FirebaseAuth,private val firestore: FirebaseFirestore) {

}