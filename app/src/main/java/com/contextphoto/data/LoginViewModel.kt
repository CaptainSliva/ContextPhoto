package com.contextphoto.data

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

}