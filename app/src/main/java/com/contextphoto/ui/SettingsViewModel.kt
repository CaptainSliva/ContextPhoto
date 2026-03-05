package com.contextphoto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contextphoto.data.repository.LoginRepository
import com.contextphoto.data.repository.SettingsRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val repository: SettingsRepository,
        private val loginRepository: LoginRepository,
    ) : ViewModel() {
        private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
        private val _operationCompleted = MutableStateFlow(false)
        private val _token = MutableStateFlow("")
        private val _stateInfo = MutableStateFlow("")
        private val _fileText = MutableStateFlow<List<String>>(emptyList())
        private val _wheelVisibility = MutableStateFlow(false)
        val currentUser = _currentUser.asStateFlow()
        val operationCompleted = _operationCompleted.asStateFlow()
        val stateInfo = _stateInfo.asStateFlow()
        val fileText = _fileText.asStateFlow()
        val wheelVisibility = _wheelVisibility.asStateFlow()

        init {
            getCurrentUser()
        }

        fun exportCommentsToStorage() {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    setWheelState(true)
                    _fileText.value = repository.exportCommentsToStorage()
                    setWheelState(false)
                    changeOperationStatus(true)
                }
            }
        }

        fun importCommentsFromStorage() {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    setWheelState(true)
                    repository.importCommentsFromStorage(_fileText.value)
                    setWheelState(false)
                    changeOperationStatus(true)
                }
            }
        }

        fun getCurrentUser() {
            _currentUser.value = loginRepository.getCurrentUser()
        }

        fun exportCommentsToFirestore() {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    setWheelState(true)
                    repository.exportCommentsToFirestore()
                    setWheelState(false)
                    changeOperationStatus(true)
                    _stateInfo.value = "Экспорт в Firebase завершен"
                }
            }
        }

        fun importCommentsFromFirestore() {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    setWheelState(true)
                    repository.importCommentsFromFirestore()
                    setWheelState(false)
                    changeOperationStatus(true)
                    _stateInfo.value = "Импорт из Firebase завершен"
                }
            }
        }

        fun changeOperationStatus(status: Boolean) {
            _operationCompleted.value = status
        }

        fun changeStateInfo(info: String) {
            _stateInfo.value = info
        }

        fun setFileText(fileText: List<String>) {
            _fileText.value = fileText
        }

        fun setWheelState(state: Boolean) {
            _wheelVisibility.value = state
        }

        fun setToken(token: String) {
            _token.value = token
        }

        fun getToken() = _token.value
    }
