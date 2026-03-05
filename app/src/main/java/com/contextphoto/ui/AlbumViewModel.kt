package com.contextphoto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contextphoto.data.repository.AlbumRepository
import com.contextphoto.item.Album
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class AlbumViewModel
    @Inject
    constructor(
        private val repository: AlbumRepository,
    ) : ViewModel() {
        private val _albumList = MutableStateFlow<List<Album>>(emptyList())
        private val _selectedAlbum = MutableStateFlow<Album?>(null)
        private val _loadAlbums = MutableStateFlow(true)
        val albumList = _albumList.asStateFlow()
        val loadAlbums = _loadAlbums.asStateFlow()
        val selectedAlbum = _selectedAlbum.asStateFlow()

        fun getAlbumList() {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    _albumList.value = repository.getAlbumList()
                }
            }
        }

        fun loadAlbumList() {
            if (repository.getLoadAlbumsState()) {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        _albumList.value = repository.loadAlbumList()
                    }
                }
            }
            repository.loadAlbumsStateChange(false)
        }

        fun loadAlbumsStateChange(state: Boolean) {
            repository.loadAlbumsStateChange(state)
            _loadAlbums.value = repository.getLoadAlbumsState()
        }

        fun addAlbum(album: Album) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.addAlbum(album)
                    _albumList.value = repository.getAlbumList()
                }
            }
        }

        fun deleteAlbum(album: Album?) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.deleteAlbum(album)
                    _albumList.value = repository.getAlbumList()
                }
            }
            _selectedAlbum.value = null
        }

        fun updateAlbum(album: Album) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.updateAlbum(album)
                    _albumList.value = repository.getAlbumList()
                }
            }
            _selectedAlbum.value = null
        }

        fun updateAlbumID(bID: String) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.updateAlbumID(bID)
                }
            }
        }

        fun selectAlbum(album: Album) {
            _selectedAlbum.value = album
        }

        fun changeStateAlbum() {
            repository.loadAlbumsStateChange()
            _loadAlbums.value = repository.getLoadAlbumsState()
        }
    }
