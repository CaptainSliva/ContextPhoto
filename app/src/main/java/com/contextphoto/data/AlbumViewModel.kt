package com.contextphoto.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlin.collections.emptyList

class AlbumViewModel : ViewModel() {
    private val _albumList = MutableStateFlow<List<Album>>(emptyList())
    private val _loadAlbumState = MutableStateFlow(true)
    private val _selectedAlbum = MutableStateFlow<Album?>(null)
    val albumList = _albumList.asStateFlow()
    val loadAlbums = _loadAlbumState.asStateFlow()
    val selectedAlbum = _selectedAlbum.asStateFlow()

    fun addAlbum(album: Album) {
        if (loadAlbums.value) {
            _albumList.update { currentList ->
                currentList.toMutableList().apply {
                    add(album)
                }
            }
        }
    }

    fun deleteAlbum(album: Album?) {
        _albumList.update { currentList ->
            currentList.filterNot { it.bID == album?.bID }
        }
        _selectedAlbum.value = null
    }

    fun updateAlbum(album: Album) {
        _albumList.update { currentList ->
            currentList.map {
                if (it.bID == album.bID) {
                    album
                } else {
                    it
                }
            }
        }
        _selectedAlbum.value = null
    }

    fun selectAlbum(album: Album) {
        _selectedAlbum.value = album
    }

    fun changeState(state: Boolean = true) {
        _loadAlbumState.value = state
    }
}
