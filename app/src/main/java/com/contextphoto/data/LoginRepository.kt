package com.contextphoto.data

import android.R.attr.name
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import jakarta.inject.Inject

class LoginRepository @Inject constructor(private val fireBaseSource: FireBaseSource) {

    fun signInWithGoogle(gso: GoogleSignInAccount) = fireBaseSource.signInWithGoogle(gso)

    fun signOut() = fireBaseSource.signOut()
}