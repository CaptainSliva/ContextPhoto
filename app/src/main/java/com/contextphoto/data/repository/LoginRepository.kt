package com.contextphoto.data.repository

import com.contextphoto.data.datasource.FireBaseSource
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class LoginRepository
    @Inject
    constructor(
        private val fireBaseSource: FireBaseSource,
    ) {
        suspend fun signUp(
            email: String,
            password: String,
        ): Result<FirebaseUser?> = fireBaseSource.signUp(email, password)

        suspend fun signIn(
            email: String,
            password: String,
        ): Result<FirebaseUser?> = fireBaseSource.signIn(email, password)

        fun signOut(): Result<Unit> = fireBaseSource.signOut()

        fun getCurrentUser(): FirebaseUser? = fireBaseSource.getCurrentUser()
    }
