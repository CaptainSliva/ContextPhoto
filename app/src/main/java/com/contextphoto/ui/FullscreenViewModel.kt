package com.contextphoto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contextphoto.data.FullscreenRepository
import com.contextphoto.data.Picture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullscreenViewModel @Inject constructor(private val repository: FullscreenRepository): ViewModel() {
    private val _listMedia = MutableStateFlow<List<Picture>>(emptyList())
    private val _loadPictureState = MutableStateFlow<Boolean>(true)
    private val _mediaPosition = MutableStateFlow(0)
    private val _bottomMenuFullScreenVisible = MutableStateFlow(false)
    val listMedia = _listMedia.asStateFlow()
    val loadPictureState = _loadPictureState.asStateFlow()
    val mediaPosition = _mediaPosition.asStateFlow()
    val bottomMenuFullScreenVisible = _bottomMenuFullScreenVisible.asStateFlow()

    fun loadPictureList(bID: String) {
        if (_listMedia.value.size  == 0) _loadPictureState.value = true
        if (loadPictureState.value) {
            _listMedia.value = emptyList()
            repository.clearPictureList()
            viewModelScope.launch {
                _listMedia.value = repository.loadPictureList(bID)
            }
        }
        _loadPictureState.value = false
    }

    fun deletePicture(pic: Picture) {
        viewModelScope.launch {
            repository.deletePicture(pic)
            _listMedia.value = repository.getPictureList()
        }
    }

    fun updatePictureList(mediaList: List<Picture>) {
        viewModelScope.launch {
            repository.updatePictureList(mediaList)
            _listMedia.value = repository.getPictureList()
        }
    }

    fun updateMediaPosition(pos: Int) {
        _mediaPosition.value = pos
    }

    fun resetPicturePosition() {
        _mediaPosition.value = 0
    }

    fun changeStateBottomMenuFullScreen(state: Boolean? = null) {
        if (state != null) {
            _bottomMenuFullScreenVisible.value = state
        }
        else {
            _bottomMenuFullScreenVisible.value = !bottomMenuFullScreenVisible.value
        }
    }

}