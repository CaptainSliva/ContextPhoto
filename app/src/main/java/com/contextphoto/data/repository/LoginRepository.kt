package com.contextphoto.data.repository

import android.widget.Toast
import com.contextphoto.data.datasource.FireBaseSource
import com.contextphoto.utils.FunctionsApp.espRead
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class LoginRepository @Inject
    constructor(
        private val fireBaseSource: FireBaseSource
    ) {
    suspend fun signUp(email: String, password: String): Result<FirebaseUser?> {
        return fireBaseSource.signUp(email, password)
    }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser?> {
        return fireBaseSource.signIn(email, password)
    }

    fun signOut(): Result<Unit> {
        return fireBaseSource.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return fireBaseSource.getCurrentUser()
    }
}