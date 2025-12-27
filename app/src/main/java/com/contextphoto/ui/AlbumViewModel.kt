package com.contextphoto.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contextphoto.data.Album
import com.contextphoto.data.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val repository: AlbumRepository
) : ViewModel() {
    private val _albumList = MutableStateFlow<List<Album>>(emptyList())
    private val _loadAlbumState = MutableStateFlow(true)
    private val _selectedAlbum = MutableStateFlow<Album?>(null)
    val albumList = _albumList.asStateFlow()
    val loadAlbums = _loadAlbumState.asStateFlow()
    val selectedAlbum = _selectedAlbum.asStateFlow()

    fun loadAlbumList() {
        if (loadAlbums.value) {
            viewModelScope.launch {
                _albumList.value = repository.loadAlbumList()
            }
        }
        _loadAlbumState.value = false
    }

    fun addAlbum(album: Album) {
        viewModelScope.launch {
            repository.addAlbum(album)
            _albumList.value = repository.getAlbumList()}
    }

    fun deleteAlbum(album: Album?) {
        viewModelScope.launch {
            repository.deleteAlbum(album)
            _albumList.value = repository.getAlbumList()
        }
        _selectedAlbum.value = null
    }

    fun updateAlbum(album: Album) {
        viewModelScope.launch {
            repository.updateAlbum(album)
            _albumList.value = repository.getAlbumList()
        }
        _selectedAlbum.value = null
    }

    fun updateAlbumID(bID: String) {
        viewModelScope.launch {
            repository.updateAlbumID(bID)
        }
    }

    fun selectAlbum(album: Album) {
        _selectedAlbum.value = album
    }

    fun changeStateAlbum(state: Boolean = true) {
        _loadAlbumState.value = state
    }
}