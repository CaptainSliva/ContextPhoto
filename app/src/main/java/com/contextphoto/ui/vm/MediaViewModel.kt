package com.contextphoto.ui.vm

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contextphoto.data.navigation.Destination
import com.contextphoto.data.repository.AlbumRepository
import com.contextphoto.data.repository.MediaRepository
import com.contextphoto.item.Picture
import com.contextphoto.utils.FunctionsDialogs.showDeleteAlbumMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
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
        private val _numberFind = MutableStateFlow(0)
        private val _page = MutableStateFlow(0)
        private val _mutex = Mutex()
        val db = repository.getDB()
        val listMedia = _listMedia.asStateFlow()
        val listSelectedMedia = _listSelectedMedia.asStateFlow()
        val mediaPosition = _mediaPosition.asStateFlow()
        val bottomMenuVisible = _bottomMenuVisible.asStateFlow()
        val albumName = _albumName.asStateFlow()
        val numberFind = _numberFind.asStateFlow()

        fun loadPictureList(
            bID: String,
            rowSize: Int = 3,
        ) {
            viewModelScope.launch {
                _mutex.withLock {
                    withContext(Dispatchers.IO) {
                        if (repository.getLoadPicturesState()) {
                            if (repository.getPictureList().isEmpty()) _page.value = 0
                            repository.loadPictureList(bID, _page.value, rowSize + rowSize * 9)
                            _listMedia.value = repository.getPictureList()

                            Log.d("VM", _listMedia.value.toString())
                            Log.d("VM", "$bID, ${_page.value}, ${rowSize + rowSize * 9}")

                            val splitPath = if (bID.isNotEmpty() && _listMedia.value.size != 0) File(_listMedia.value[0].path).toString().split("/") else List(3) { "" }
                            _albumName.value = if (bID != "") splitPath[splitPath.size - 2] else Destination.Pictures().label
                            loadPicturesStateChange(false)
                            _page.value += 1
                        } else {
                            _listMedia.value = repository.getPictureList()
                        }
                    }
                }
            }
        }

        fun generatePicturesList(itemsCount: Int) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    _listMedia.value = repository.generatePicturesList(itemsCount)
                }
            }
        }

        fun loadPicturesStateChange(state: Boolean) {
            repository.loadPicturesStateChange(state)
        }

        fun clearMediaViewModelData() {
            viewModelScope.launch {
                _mutex.withLock {
                    withContext(Dispatchers.IO) {
                        repository.clearPictureList()
                        repository.clearSelectedMedia()
                        _listMedia.value = repository.getPictureList()
                        _listSelectedMedia.value = repository.getSelectedMediaList()
                        _numberFind.value = 0
//                        _page.value = 0
                        Log.d("VM", "clearPictureList: _page.value = ${_page.value}")
                    }
                }
            }
        }

        fun addFoundedPicture(pic: Picture) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    if (pic.path !in _listMedia.value.map { it.path }) {
                        repository.addPicture(pic)
                        _listMedia.value = repository.getPictureList()
                        _numberFind.value += 1
                        Log.d("Lvalue", _listMedia.value.toString())
                    }
                }
            }
        }

        fun deletePicture(pic: Picture) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.deletePicture(pic)
                    _listMedia.value = repository.getPictureList()
                }
            }
        }

        fun deletePictureByUri(uri: Uri) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.deletePictureByUri(uri)
                    _listMedia.value = repository.getPictureList()
                }
            }
        }

        fun deletePictureByMove() {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.deletePictureByMove()
                    _listSelectedMedia.value = repository.getSelectedMediaList()
                }
            }
        }

        fun updateMediaPosition(pos: Int) {
            repository.updateMediaPosition(pos)
            _mediaPosition.value = repository.getMediaPosition()
        }

        fun selectMedia(pic: Picture) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.selectMedia(pic)
                    _listSelectedMedia.value = repository.getSelectedMediaList()
                }
            }
        }

        fun removeSelectMedia(pic: Picture) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.removeSelectMedia(pic)
                    _listSelectedMedia.value = repository.getSelectedMediaList()
                }
            }
        }

        fun clearSelectedMedia() {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.clearSelectedMedia()
                    _listSelectedMedia.value = repository.getSelectedMediaList()
                }
            }
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

        fun changeStatePictureComment(
            mediaIndex: Int,
            mediaThumbnail: Bitmap,
        ) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.changeStatePictureComment(mediaIndex, mediaThumbnail)
                }
            }
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

        fun deleteAlbum(context: Context) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val albums = albumRepository.getAlbumList()
                    val index =
                        albumRepository
                            .getAlbumList()
                            .map { it.bID }
                            .indexOf(albumRepository.getAlbumBid())
                    albumRepository.deleteAlbum(albums[index])
                    showDeleteAlbumMessage(context, albums[index].name, albums[index].path)
                }
            }
        }
    }
