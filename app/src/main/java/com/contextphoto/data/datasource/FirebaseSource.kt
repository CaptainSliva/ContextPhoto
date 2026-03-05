package com.contextphoto.data.datasource

import com.contextphoto.db.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class FireBaseSource
    @Inject
    constructor(
        private val firebaseAuth: FirebaseAuth,
        private val firestore: FirebaseFirestore,
    ) {
        // Регистрация
        suspend fun signUp(
            email: String,
            password: String,
        ): Result<FirebaseUser?> =
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                Result.success(authResult.user)
            } catch (e: Exception) {
                Result.failure(e)
            }

        // Вход
        suspend fun signIn(
            email: String,
            password: String,
        ): Result<FirebaseUser?> =
            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                Result.success(authResult.user)
            } catch (e: Exception) {
                Result.failure(e)
            }

        // Выход
        fun signOut(): Result<Unit> =
            try {
                firebaseAuth.signOut()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }

        // Получение текущего пользователя
        fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

        fun saveComments(
            uId: String,
            comments: List<Comment>,
        ) {
            comments.forEach {
                firestore.collection("comments").document(uId).set(it)
            }
        }

        fun deleteComments(uId: String) {
            firestore.collection("comments").document(uId).delete()
        }
    }
