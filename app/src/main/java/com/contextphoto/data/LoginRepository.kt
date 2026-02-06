package com.contextphoto.data

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import jakarta.inject.Inject

class LoginRepository
    @Inject
    constructor(
        private val fireBaseSource: FireBaseSource,
    ) {
        fun signInWithGoogle(gso: GoogleSignInAccount) = fireBaseSource.signInWithGoogle(gso)

        fun signOut() = fireBaseSource.signOut()
    }
