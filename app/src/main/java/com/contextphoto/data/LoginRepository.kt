package com.contextphoto.data

import android.R.attr.name
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import jakarta.inject.Inject

class LoginRepository @Inject constructor(private val fireBaseSource: FireBaseSource) {
    fun signUpUser(email: String, password: String) = fireBaseSource.signUpUser(email, password)

    fun signInUser(email: String, password: String) = fireBaseSource.signInUser(email, password)

    fun saveUser(email: String, password: String) = fireBaseSource.saveUser(email, password)

    fun signInWithGoogle(acct: GoogleSignInAccount) = fireBaseSource.signInWithGoogle(acct)

    fun fetchUser() = fireBaseSource.fetchUser()
}