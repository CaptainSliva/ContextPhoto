package com.contextphoto.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contextphoto.data.repository.AlbumRepository
import com.contextphoto.data.Destination
import com.contextphoto.data.repository.MediaRepository
import com.contextphoto.data.Picture
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

@HiltViewModel
class MediaViewModel
    @Inject
    constructor(
        private val repository: MediaRepository,
        private val albumRepository: AlbumRepository,
    ) : ViewModel() {
        private val _listMedia = MutableStateFlow<List<Picture>>(emptyList())
        private val _listSelectedMedia = MutableStateFlow<List<Picture>>(emptyList())
        private val _mediaPosition = MutableStateFlow(0)
        private val _bottomMenuVisible = MutableStateFlow(false)
        private val _albumName = MutableStateFlow("")
        val db = repository.getDB()
        val listMedia = _listMedia.asStateFlow()
        val listSelectedMedia = _listSelectedMedia.asStateFlow()
        val mediaPosition = _mediaPosition.asStateFlow()
        val bottomMenuVisible = _bottomMenuVisible.asStateFlow()
        val albumName = _albumName.asStateFlow()

        fun loadPictureList(bID: String) {
            if (repository.getLoadPicturesState()) {
                viewModelScope.launch {
                    _listMedia.value = repository.loadPictureList(bID)
                    val splitPath = File(_listMedia.value[0].path).toString().split("/")
                    _albumName.value = if (bID != "") splitPath[splitPath.size - 2] else Destination.Pictures().label
                }
                loadPicturesStateChange(false)
            } else {
                viewModelScope.launch {
                    _listMedia.value = repository.getPictureList()
                }
            }
        }

        fun loadPicturesStateChange(state: Boolean) {
            repository.loadPicturesStateChange(state)
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
                _listMedia.value = repository.getPictureList()
            }
        }

        fun deletePictureByMove() {
            viewModelScope.launch {
                _listSelectedMedia.value.forEach {
                    repository.deletePicture(it)
                    _listMedia.value = repository.getPictureList()
                }
            }
        }

        fun updateMediaPosition(pos: Int) {
            repository.updateMediaPosition(pos)
            _mediaPosition.value = repository.getMediaPosition()
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

        fun changeStateBottomMenu(state: Boolean? = null) {
            if (state != null) {
                _bottomMenuVisible.value = state
            } else {
                _bottomMenuVisible.value = !bottomMenuVisible.value
            }
        }

        fun loadAlbumsStateChange(state: Boolean) {
            albumRepository.loadAlbumsStateChange(state)
        }

        fun changeStatePictureComment(mediaIndex: Int, mediaThumbnail: Bitmap) {
            viewModelScope.launch {
                repository.changeStatePictureComment(mediaIndex, mediaThumbnail)
            }
//            _listMedia.value = repository.getPictureList()
        }

        fun deleteMediaFromAlbum(
            bID: String,
            count: Int,
        ) {
            albumRepository.deleteMediaFromAlbum(bID, count)
            albumRepository.loadAlbumsStateChange(true)
        }

        fun moveMediaToAlbum(
            bIDTo: String,
            bIDFrom: String,
            count: Int,
        ) {
            albumRepository.moveMediaToAlbum(bIDTo, bIDFrom, count)
            deletePictureByMove()
            albumRepository.loadAlbumsStateChange(true)
        }

        fun copyMediaToAlbum(
            bID: String,
            count: Int,
        ) {
            albumRepository.copyMediaToAlbum(bID, count)
            albumRepository.loadAlbumsStateChange(true)
        }
    }
