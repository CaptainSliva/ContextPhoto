package com.contextphoto.ui

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contextphoto.data.AlbumRepository
import com.contextphoto.data.MediaRepository
import com.contextphoto.data.Picture
import com.contextphoto.utils.FunctionsMediaStore.getImageDate
import com.google.api.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullscreenViewModel @Inject constructor(
    private val repository: MediaRepository,
    private val albumRepository: AlbumRepository
): ViewModel() {
    private val _listMedia = MutableStateFlow<List<Picture>>(emptyList())
    private val _loadPictureState = MutableStateFlow<Boolean>(true)
    private val _mediaPosition = MutableStateFlow(0)
    private val _bottomMenuFullScreenVisible = MutableStateFlow(false)
    private val _dateInfoList = MutableStateFlow<List<String>>(listOf("", ""))
    private val _isVideo = MutableStateFlow(false)
    private val _deleteAction = MutableStateFlow(false)
    val listMedia = _listMedia.asStateFlow()
    val loadPictureState = _loadPictureState.asStateFlow()
    val mediaPosition = _mediaPosition.asStateFlow()
    val bottomMenuFullScreenVisible = _bottomMenuFullScreenVisible.asStateFlow()
    val dateInfoList = _dateInfoList.asStateFlow()
    val isVideo = _isVideo.asStateFlow()
    val deleteAction = _deleteAction.asStateFlow()

    fun loadPictureList() {
        _listMedia.value = repository.getPictureList()
    }

    fun deletePicture(pic: Picture) {
        viewModelScope.launch {
            repository.deletePicture(pic)
            _listMedia.value = repository.getPictureList()
            albumRepository.loadAlbumsStateChange(true)
        }
    }

    fun updateMediaPosition(pos: Int) {
        when {
            _listMedia.value.size == pos -> _mediaPosition.value = pos-1
            else -> _mediaPosition.value = pos
        }
    }

    fun updateDateInfo(context: android.content.Context, pos: Int) {
        _dateInfoList.value = getImageDate(context, _listMedia.value[pos].path)
    }

    fun changeMediaType(isVideo: Boolean) {
        _isVideo.value = isVideo
    }

    fun changeStateBottomMenuFullScreen(state: Boolean? = null) {
        if (state != null) {
            _bottomMenuFullScreenVisible.value = state
        }
        else {
            _bottomMenuFullScreenVisible.value = !bottomMenuFullScreenVisible.value
        }
    }

    fun deleteActionChange() { _deleteAction.value = !_deleteAction.value }

}