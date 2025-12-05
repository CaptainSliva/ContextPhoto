package com.contextphoto.data

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.emptyList

class AlbumListViewModel: ViewModel() {
    val _albumList = MutableStateFlow<List<Album>>(emptyList())
    val albumList = _albumList.asStateFlow()


    fun addAlbum(album: Album) {
        _albumList.update { currentList ->
            currentList.toMutableList().apply {
                add(album)
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

}