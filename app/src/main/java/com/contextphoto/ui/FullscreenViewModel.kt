package com.contextphoto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contextphoto.data.AlbumRepository
import com.contextphoto.data.MediaRepository
import com.contextphoto.data.Picture
import com.contextphoto.utils.FunctionsMediaStore.getImageDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
        val db = repository.getDB()
        val listMedia = _listMedia.asStateFlow()
        val mediaPosition = _mediaPosition.asStateFlow()
        val bottomMenuFullScreenVisible = _bottomMenuFullScreenVisible.asStateFlow()
        val deleteAction = _deleteAction.asStateFlow()

        fun loadPictureList() {
            _listMedia.value = repository.getPictureList()
            _mediaPosition.value = repository.getMediaPosition()
        }

        fun deletePicture(pic: Picture) {
            viewModelScope.launch {
                repository.deletePicture(pic)
                _listMedia.value = repository.getPictureList()
                albumRepository.loadAlbumsStateChange(true)
            }
        }

        fun updateMediaPosition(pos: Int) {
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

        fun deleteActionChange() {
            _deleteAction.value = !_deleteAction.value
        }
    }
