package com.contextphoto.data

import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val repository: LoginRepository,
        private val firebaseAuth: FirebaseAuth,
    ) : ViewModel() {
        private val _signUpUserResult = MutableStateFlow("")
        private val _signInUserResult = MutableStateFlow("")
        private val _email = MutableStateFlow("")
        private val _name = MutableStateFlow("")
        val signUpUserResult = _signUpUserResult.asStateFlow()
        val signInUserResult = _signUpUserResult.asStateFlow()
        val email = _email.asStateFlow()
        val name = _name.asStateFlow()

        fun signIndocumentation() {
        }

        fun signInWithGoogle(gsa: GoogleSignInAccount) {
            repository.signInWithGoogle(gsa).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signInUserResult.value = "Успешно"
                } else {
                    _signInUserResult.value = "couldn't sign in user"
                }
            }
        }

        fun signOut() {
            repository.signOut()
        }
    }
