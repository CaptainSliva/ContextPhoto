package com.contextphoto.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contextphoto.data.repository.AlbumRepository
import com.contextphoto.data.repository.MediaRepository
import com.contextphoto.data.mediaClasses.Picture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FullscreenViewModel
    @Inject
    constructor(
        private val repository: MediaRepository,
        private val albumRepository: AlbumRepository,
    ) : ViewModel() {
        private val _listMedia = MutableStateFlow<List<Picture>>(emptyList())
        private val _mediaPosition = MutableStateFlow(0)
        private val _bottomMenuFullScreenVisible = MutableStateFlow(false)
        private val _deleteAction = MutableStateFlow(false)
        private val _imageComment = MutableStateFlow("")
        val db = repository.getDB()
        val listMedia = _listMedia.asStateFlow()
        val mediaPosition = _mediaPosition.asStateFlow()
        val bottomMenuFullScreenVisible = _bottomMenuFullScreenVisible.asStateFlow()
        val deleteAction = _deleteAction.asStateFlow()
        val imageComment = _imageComment.asStateFlow()


        fun loadPictureList() {
            _listMedia.value = repository.getPictureList()
            _mediaPosition.value = repository.getMediaPosition()
        }

        fun deletePicture(pic: Picture) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.deletePicture(pic)
                    _listMedia.value = repository.getPictureList()
                    albumRepository.loadAlbumsStateChange(true)

                    if (_listMedia.value.size == _mediaPosition.value) {
                        updateMediaPosition(_mediaPosition.value - 1)
                        _mediaPosition.value = repository.getMediaPosition()
                    }
                }
            }
        }

        fun updateMediaPosition(pos: Int? = null) { // pos - Текущая позиция
            repository.updateMediaPosition(pos)
            _mediaPosition.value = repository.getMediaPosition()
        }

        fun changeStateBottomMenuFullScreen(state: Boolean? = null) {
            if (state != null) {
                _bottomMenuFullScreenVisible.value = state
            } else {
                _bottomMenuFullScreenVisible.value = !bottomMenuFullScreenVisible.value
            }
        }

        fun deleteActionChange(state: Boolean? = null) {
            if (state != null) {
                _deleteAction.value = state
            } else {
                _deleteAction.value = !_deleteAction.value
            }
        }

        fun getImageComment(bitmap: Bitmap) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    _imageComment.value = repository.getImageComment(bitmap)
                }
            }
        }

    }
