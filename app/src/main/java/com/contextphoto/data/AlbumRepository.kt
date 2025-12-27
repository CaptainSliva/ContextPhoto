package com.contextphoto.data

import android.content.Context
import com.contextphoto.utils.FunctionsMediaStore.getListAlbums
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.update
import kotlin.collections.map
import kotlin.collections.remove

class AlbumRepository @Inject constructor(private val albumCache: AlbumCache) {

    fun getAlbumList() = albumCache.listAlbums.value
    fun loadAlbumList() = albumCache.loadAlbumList()

    fun addAlbum(album: Album) {
        albumCache.updateAlbumList(albumCache.listAlbums.value.toMutableList().apply { add(album) })
    }

    fun deleteAlbum(album: Album?) {
        albumCache.updateAlbumList(albumCache.listAlbums.value.toMutableList().apply { remove(album) })
    }

    fun updateAlbum(album: Album) {
        albumCache.updateAlbumList(
            albumCache.listAlbums.value.toMutableList().map
            {
                if (it.bID == album.bID) {
                    album
                } else {
                    it
                }
            }
        )
    }

    fun updateAlbumID(bID: String) {
        albumCache.updateAlbumID(bID)
        albumBid = bID
    }

}