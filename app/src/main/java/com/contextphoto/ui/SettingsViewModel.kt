package com.contextphoto.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contextphoto.data.repository.LoginRepository
import com.contextphoto.data.repository.SettingsRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val repository: SettingsRepository,
        private val loginRepository: LoginRepository
    ) : ViewModel()
{
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    private val _operationCompleted = MutableStateFlow(false)
    private val _token = MutableStateFlow("")
    private val _stateInfo = MutableStateFlow("")
    val currentUser = _currentUser.asStateFlow()
    val operationCompleted = _operationCompleted.asStateFlow()
    val stateInfo = _stateInfo.asStateFlow()

    init {
        getCurrentUser()
    }

    suspend fun exportCommentsToStorage() {
        viewModelScope.launch {
            repository.exportCommentsToStorage()
            changeOperationStatus(true)
            _stateInfo.value = "Экспорт в файл завершен"
        }
    }

    suspend fun importCommentsFromStorage() {
        viewModelScope.launch {
            repository.importCommentsFromStorage()
            changeOperationStatus(true)
            _stateInfo.value = "Импорт из файла завершен"
        }
    }

    fun getCurrentUser() {
        _currentUser.value = loginRepository.getCurrentUser()
    }

    suspend fun exportCommentsToFirestore() {
        viewModelScope.launch {
            repository.exportCommentsToFirestore()
            Log.d("TAGGG", "Corutina export stop")
            changeOperationStatus(true)
            _stateInfo.value = "Экспорт в Firebase завершен"
        }

    }

    suspend fun importCommentsFromFirestore() {
        viewModelScope.launch {
            repository.importCommentsFromFirestore()
            changeOperationStatus(true)
            _stateInfo.value = "Импорт из Firebase завершен"
        }
    }

    fun changeOperationStatus(status: Boolean) {
        _operationCompleted.value = status
    }

    fun setToken(token: String) {_token.value = token}
    fun getToken() = _token.value


}