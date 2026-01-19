package com.contextphoto.data

import android.text.TextUtils
import android.text.TextUtils.isEmpty
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import coil.util.CoilUtils.result
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _signUpUserResult = MutableStateFlow("")
    private val _signInUserResult = MutableStateFlow("")
    val signUpUserResult = _signUpUserResult.asStateFlow()
    val signInUserResult = _signUpUserResult.asStateFlow()

    fun signUpUser(email: String, password: String): Boolean {
        var result = true
        when {
            isEmpty(email) && isEmpty(password) -> {
                _signUpUserResult.value = "field must not be empty"
                result = false
            }
            password.length < 6 -> {
                _signUpUserResult.value = "password must not be less than 6"
                result = false
            }
            else -> {
                firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                    if (it.result?.signInMethods?.size == 0) {
                        repository.signUpUser(email, password).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseAuth.currentUser?.sendEmailVerification()
                                _signUpUserResult.value = "Успешно"
                            } else {
                                _signUpUserResult.value = it.exception?.message.toString()
                                result = false
                            } }
                    } else {
                        _signUpUserResult.value =  "email already exist"
                        result = false
                    }
                }
            }
        }
        return result
    }

    fun saveUser(email: String, name: String) {
        repository.saveUser(email, name).addOnCompleteListener {
            if (it.isSuccessful) {
                _signUpUserResult.value = "Успешно"
            }else{
                _signUpUserResult.value = it.exception?.message.toString()
            }
        }
    }

    fun signInUser(email: String, password: String): Boolean {
        var result = true

        when {
            TextUtils.isEmpty(email) && TextUtils.isEmpty(password) -> {
                _signInUserResult.value = "Enter email and password"
                result = false
            }
            else -> {
                firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                    if (it.result?.signInMethods?.size == 0) {
                        _signInUserResult.value = "Email does not exist"
                        result = false
                    } else {
                        repository.signInUser(email, password).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseAuth.currentUser?.isEmailVerified?.let { verified ->
                                    if (verified) {
                                        repository.fetchUser().addOnCompleteListener { userTask ->
                                            if (userTask.isSuccessful) {
                                                userTask.result?.documents?.forEach {
                                                    if (it.data!!["email"] == email) {
                                                        _signInUserResult.value = "Успешно"
                                                    }
                                                }
                                            } else {
                                                _signInUserResult.value = userTask.exception?.message.toString()
                                                result = false
                                            }
                                        }
                                    } else {
                                        _signInUserResult.value = "Email is not verified, check your email"
                                        result = false
                                    } }
                            } else {
                                _signInUserResult.value = task.exception?.message.toString()
                                result = false
                            }
                        }
                    }
                }
            }
        }
        return result
    }

    fun signInWithGoogle(acct: GoogleSignInAccount): Boolean {
        var result = true
        repository.signInWithGoogle(acct).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _signInUserResult.value = "Успешно"
            } else {
                _signInUserResult.value = "couldn't sign in user"
                result = false
            }

        }
        return result
    }
}