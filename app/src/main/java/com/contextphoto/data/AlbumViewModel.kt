package com.contextphoto.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.emptyList

class AlbumViewModel: ViewModel() {
    private val _albumList = MutableStateFlow<List<Album>>(emptyList())
    private val _loadAlbumsState = MutableStateFlow<Boolean>(true)
    val albumList = _albumList.asStateFlow()
    val loadAlbums = _loadAlbumsState.asStateFlow()


    fun addAlbum(album: Album) {
        if (loadAlbums.value) {
            _albumList.update { currentList ->
                currentList.toMutableList().apply {
                    add(album)
                }
            }
        }
    }

    fun deleteAlbum(album: Album) {
        _albumList.update { currentList ->
            currentList.toMutableList().apply {
                remove(album)
            }
        }
    }

    fun updateAlbum(album: Album) {
        _albumList.update { currentList ->
            currentList.map {
                if (it.name == album.name) {
                    album
                }
                else {
                    it
                }
            }
        }
    }

    fun changeState(state: Boolean = true) {
        if (state) _loadAlbumsState.value = true
        else _loadAlbumsState.value = false
    }

}