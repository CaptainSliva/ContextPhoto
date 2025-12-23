package com.contextphoto.data

import android.content.Context
import com.contextphoto.utils.FunctionsMediaStore.getListAlbums
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.update

class AlbumRepository @Inject constructor() {

    fun loadAlbumList(context: Context) = getListAlbums(context)

    fun addAlbum(albumList: List<Album>, album: Album) = albumList.toMutableList().apply { add(album) }

    fun deleteAlbum(albumList: List<Album>, album: Album?) = albumList.filterNot { it.bID == album?.bID }

    fun updateAlbum(albumList: List<Album>, album: Album) =
        albumList.map {
            if (it.bID == album.bID) {
                album
            } else {
                it
            }
        }

}