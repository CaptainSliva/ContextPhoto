package com.contextphoto.data

import android.content.Context
import android.util.Log
import com.contextphoto.utils.FunctionsMediaStore.getListAlbums
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class AlbumCache
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) {
        private val _listAlbums = MutableStateFlow<List<Album>>(emptyList())
        private val _loadAlbumState = MutableStateFlow(true)
        private val _albumBid = MutableStateFlow("")
        val listAlbums = _listAlbums.asStateFlow()
        val loadAlbums = _loadAlbumState.asStateFlow()

        fun loadAlbumList(): List<Album> {
            _listAlbums.value = getListAlbums(context)

            Log.d("Album Cache", _listAlbums.value.toString())
            return _listAlbums.value
        }

        fun updateAlbumList(newList: List<Album>) {
            _listAlbums.value = newList
        }

        fun updateAlbumID(bID: String) {
            _albumBid.value = bID
        }

        fun loadAlbumsState(state: Boolean? = null) {
            if (state != null) {
                _loadAlbumState.value = state
            } else {
                _loadAlbumState.value = !_loadAlbumState.value
            }
        }
    }
