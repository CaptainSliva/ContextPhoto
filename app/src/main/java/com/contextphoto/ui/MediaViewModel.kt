package com.contextphoto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.contextphoto.data.MediaRepository
import com.contextphoto.data.Picture
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MediaViewModel @Inject constructor (
    private val repository: MediaRepository
) : ViewModel() {
    private val _listMedia = MutableStateFlow<List<Picture>>(emptyList())
    private val _listSelectedMedia = MutableStateFlow<List<Picture>>(emptyList())
    private val _loadPictureState = MutableStateFlow(true)
    private val _bottomMenuVisible = MutableStateFlow(false)
//    private val _selectProcess = MutableStateFlow(false)
    val listMedia = _listMedia.asStateFlow()
    val listSelectedMedia = _listSelectedMedia.asStateFlow()
    val loadPictureState = _loadPictureState.asStateFlow()
    val bottomMenuVisible = _bottomMenuVisible.asStateFlow()
//    val selectProcess = _selectProcess.asStateFlow()

    fun loadPictureList(bID: String) {
        if (_listMedia.value.size  == 0) changeState(true)
        if (loadPictureState.value) {
            viewModelScope.launch {
                _listMedia.value = repository.loadPictureList(bID)
            }
        }
        _loadPictureState.value = false
    }

    fun clearPictureList() {
        viewModelScope.launch {
            repository.clearPictureList()
            _listMedia.value = repository.getPictureList()
        }
    }
    
    fun addPicture(pic: Picture) {
        viewModelScope.launch { 
            repository.addPicture(pic)
            _listMedia.value = repository.getPictureList()
        }
    }

    fun deletePicture(pic: Picture) {
        viewModelScope.launch {
            repository.deletePicture(pic)
            _listMedia.value = repository.getPictureList() // TODO fixme не происходит удаление картинок пока они // не уйдут с экрана
        }
    }

    fun updatePicture(pic: Picture) {
        viewModelScope.launch {
            repository.updatePicture(pic)
            _listMedia.value = repository.getPictureList()
        }
    }

    fun selectMedia(pic: Picture) {
        _listSelectedMedia.update { currentList ->
            currentList.toMutableList().apply {
                add(pic)
            }
        }
        viewModelScope.launch {
            repository.changePictureState(pic.bID, true)
            _listMedia.value = repository.getPictureList()
        }
    }

    fun removeSelectMedia(pic: Picture) {
        _listSelectedMedia.update { currentList ->
            currentList.toMutableList().apply {
                remove(pic)
            }
        }
        viewModelScope.launch {
            repository.changePictureState(pic.bID, false)
            _listMedia.value = repository.getPictureList()
        }
    }

    fun clearSelectedMedia() {
        _listSelectedMedia.value = emptyList()
        repository.clearSelectedMedia()
    }


    fun changeState(state: Boolean = true) {
        if (state) {
            _loadPictureState.value = true
        } else {
            _loadPictureState.value = false
            repository.clearPictureList()
            _listMedia.value = repository.getPictureList()
        }
    }

    fun changeStateBottomMenu(state: Boolean? = null) {
        if (state != null) {
            _bottomMenuVisible.value = state
        }
        else {
            _bottomMenuVisible.value = !bottomMenuVisible.value
        }
    }

}