package com.contextphoto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contextphoto.data.repository.LoginRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow("")
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val isLoading = _isLoading.asStateFlow()
    val errorMessage = _errorMessage.asStateFlow()
    val currentUser = _currentUser.asStateFlow()

    init {
        getCurrentUser()
    }


    fun registration(email: String, password: String): Boolean {
        var loginSuccess = false
        _isLoading.value = true
        _errorMessage.value = ""

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val result = repository.signUp(email, password)
                    if (result.isSuccess) {
                        loginSuccess = true
                        getCurrentUser()
                        _errorMessage.value = "Совершен вход в аккаунт ${_currentUser.value?.email}"
                    } else {
                        _errorMessage.value =
                            "Ошибка регистрации: ${result.exceptionOrNull()?.message}"
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Ошибка: ${e.message}"
                }
                _isLoading.value = false
            }
        }
        return loginSuccess
    }

    fun login(email: String, password: String): Boolean {
        var loginSuccess = false
        _isLoading.value = true

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val result = repository.signIn(email, password)
                    if (result.isSuccess) {
                        loginSuccess = true
                        getCurrentUser()
                        _errorMessage.value = "Совершен вход в аккаунт ${_currentUser.value?.email}"
                    } else {
                        _errorMessage.value = "Ошибка входа: ${result.exceptionOrNull()?.message}"
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Ошибка: ${e.message}"
                }
                _isLoading.value = false
            }
        }
        return loginSuccess
    }

    fun logout(): Boolean {
        getCurrentUser()
        if (_currentUser.value?.email != null) {
            _errorMessage.value = "Совершен выход из аккаунта ${_currentUser.value?.email}"
        }
        val result = repository.signOut()
        return result.isSuccess
    }

    fun getCurrentUser() {
        _currentUser.value = repository.getCurrentUser()
    }

    fun clearError() {
        _errorMessage.value = ""
    }
}
