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
    private val _mediaPosition = MutableStateFlow(0)
    private val _loadPictureState = MutableStateFlow(true)
    private val _bottomMenuVisible = MutableStateFlow(false)
    private val _bottomMenuFullScreenVisible = MutableStateFlow(false)
//    private val _selectProcess = MutableStateFlow(false)
    private val _checkboxVisible = MutableStateFlow(false)
    private val _albumBid = MutableStateFlow("")
    val listMedia = _listMedia.asStateFlow()
    val listSelectedMedia = _listSelectedMedia.asStateFlow()
    val mediaPosition = _mediaPosition.asStateFlow()
    val loadPictureState = _loadPictureState.asStateFlow()
    val bottomMenuVisible = _bottomMenuVisible.asStateFlow()
    val bottomMenuFullScreenVisible = _bottomMenuFullScreenVisible.asStateFlow()
//    val selectProcess = _selectProcess.asStateFlow()
    val checkboxVisible = _checkboxVisible.asStateFlow()
    val albumBid = _albumBid.asStateFlow()

    fun loadPictureList(bID: String) {
        if (_listMedia.value.size  == 0) changeState(true)
        if (loadPictureState.value) {
            viewModelScope.launch {
                _listMedia.value = repository.loadPictureList(bID)
            }
        }
        _loadPictureState.value = false
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
            _listMedia.value = repository.getPictureList()
        }
    }

    fun updatePicture(pic: Picture) {
        viewModelScope.launch {
            repository.updatePicture(pic)
            _listMedia.value = repository.getPictureList()
        }
    }

    fun updateMediaPosition(pos: Int) {
        _mediaPosition.value = pos
    }

    fun resetPicturePosition() {
        _mediaPosition.value = 0
    }

    fun selectMedia(pic: Picture) {
        _listSelectedMedia.update { currentList ->
            currentList.toMutableList().apply {
                add(pic)
            }
        }
    }

    fun removeSelectMedia(pic: Picture) {
        _listSelectedMedia.update { currentList ->
            currentList.toMutableList().apply {
                remove(pic)
            }
        }
    }

    fun clearSelectedMedia() {
        _listSelectedMedia.value = emptyList()
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

    fun changeStateBottomMenuFullScreen(state: Boolean? = null) {
        if (state != null) {
            _bottomMenuFullScreenVisible.value = state
        }
        else {
            _bottomMenuFullScreenVisible.value = !bottomMenuFullScreenVisible.value
        }
    }

    fun changeStateCheckBox(state: Boolean? = null) {
        if (state != null) {
            _checkboxVisible.value = state
        }
        else {
            _checkboxVisible.value = !checkboxVisible.value
        }
    }
}