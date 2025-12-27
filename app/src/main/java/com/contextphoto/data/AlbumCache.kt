package com.contextphoto.data

import android.content.Context
import com.contextphoto.utils.FunctionsMediaStore.getListAlbums
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class AlbumCache @Inject constructor(@param:ApplicationContext private val context: Context) {
    private val _listAlbums = MutableStateFlow<List<Album>>(emptyList())
    private val _albumBid = MutableStateFlow("")
    val listAlbums = _listAlbums.asStateFlow()
    var albumBid = _albumBid.asStateFlow()

    fun loadAlbumList(): List<Album> {
        _listAlbums.value = getListAlbums(context)
        return _listAlbums.value
    }

    fun updateAlbumList(newList: List<Album>) {
        _listAlbums.value = newList
    }

    fun updateAlbumID(bID: String) {
        _albumBid.value = bID
    }

}
