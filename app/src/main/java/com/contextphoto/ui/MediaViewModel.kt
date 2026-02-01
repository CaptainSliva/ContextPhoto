package com.contextphoto.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.contextphoto.data.AlbumRepository
import com.contextphoto.data.Destination
import com.contextphoto.data.MediaRepository
import com.contextphoto.data.Picture
import com.google.firebase.firestore.AggregateField.count
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

@HiltViewModel
class MediaViewModel @Inject constructor (
    private val repository: MediaRepository,
    private val albumRepository: AlbumRepository
) : ViewModel() {
    private val _listMedia = MutableStateFlow<List<Picture>>(emptyList())
    private val _listSelectedMedia = MutableStateFlow<List<Picture>>(emptyList())
    private val _bottomMenuVisible = MutableStateFlow(false)
    private val _albumName = MutableStateFlow("")
//    private val _selectProcess = MutableStateFlow(false)
    val listMedia = _listMedia.asStateFlow()
    val listSelectedMedia = _listSelectedMedia.asStateFlow()
    val bottomMenuVisible = _bottomMenuVisible.asStateFlow()
    val albumName = _albumName.asStateFlow()
//    val selectProcess = _selectProcess.asStateFlow()

    fun loadPictureList(bID: String) {
        var splitPath = listOf("")
        if (repository.getLoadPicturesState()) {
            viewModelScope.launch {
                _listMedia.value = repository.loadPictureList(bID)
                splitPath = File(_listMedia.value[0].path).toString().split("/")
                _albumName.value = if (bID != "") splitPath[splitPath.size-2] else Destination.PICTURES().label
            }
        }
        else {
            viewModelScope.launch {
                _listMedia.value = repository.getPictureList()
            }
        }

    }

    fun loadPicturesStateChange(state: Boolean){
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
        //deleteMediaFromAlbum(pic.bID, 1)
    }

    fun deletePictureByMove() {
        viewModelScope.launch {
            _listSelectedMedia.value.forEach {
                repository.deletePicture(it)
                _listMedia.value = repository.getPictureList()

            }

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

    fun changeStateBottomMenu(state: Boolean? = null) {
        if (state != null) {
            _bottomMenuVisible.value = state
        }
        else {
            _bottomMenuVisible.value = !bottomMenuVisible.value
        }
    }

    fun deleteMediaFromAlbum(bID: String, count: Int) {
        albumRepository.deleteMediaFromAlbum(bID, count)
        albumRepository.loadAlbumsStateChange(true)
    }

    fun moveMediaToAlbum(bIDTo: String, bIDFrom: String, count: Int) {
        albumRepository.moveMediaToAlbum(bIDTo, bIDFrom, count)
        deletePictureByMove()
        albumRepository.loadAlbumsStateChange(true)
    }

    fun copyMediaToAlbum(bID: String, count: Int) {
        albumRepository.copyMediaToAlbum(bID, count)
        albumRepository.loadAlbumsStateChange(true)
    }

}