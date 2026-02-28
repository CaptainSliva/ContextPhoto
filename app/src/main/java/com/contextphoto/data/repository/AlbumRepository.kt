package com.contextphoto.data.repository

import com.contextphoto.data.mediaClasses.Album
import com.contextphoto.data.datasource.AlbumCache
import jakarta.inject.Inject

class AlbumRepository
    @Inject
    constructor(
        private val albumCache: AlbumCache,
    ) {
        fun getLoadAlbumsState() = albumCache.loadAlbums.value

        fun getAlbumList() = albumCache.listAlbums.value

        fun getAlbumBid() = albumCache.albumBid.value

        fun loadAlbumList() = albumCache.loadAlbumList()

        fun addAlbum(album: Album) {
            albumCache.updateAlbumList(
                albumCache.listAlbums.value
                    .toMutableList()
                    .apply { add(album) },
            )
        }

        fun loadAlbumsStateChange(state: Boolean? = null) {
            if (state != null) {
                albumCache.loadAlbumsState(state)
            } else {
                albumCache.loadAlbumsState()
            }
        }

        fun deleteAlbum(album: Album?) {
            albumCache.updateAlbumList(
                albumCache.listAlbums.value
                    .toMutableList()
                    .apply { remove(album) },
            )
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
                    },
            )
        }

        fun updateAlbumID(bID: String) {
            albumCache.updateAlbumID(bID)
        }

        fun deleteMediaFromAlbum(
            bID: String,
            count: Int,
        ) {
            albumCache.updateAlbumList(
                albumCache.listAlbums.value.toMutableList().map {
                    if (it.bID == bID) {
                        it.itemsCount -= count
                    }
                    it
                },
            )
        }

        fun moveMediaToAlbum(
            bIDTo: String,
            bIDFrom: String,
            count: Int,
        ) { // To - в какой альбом перемещаю From - Из какого
            albumCache.updateAlbumList(
                albumCache.listAlbums.value.toMutableList().map {
                    when (it.bID) {
                        bIDTo -> {
                            it.itemsCount += count
                            it
                        }

                        bIDFrom -> {
                            it.itemsCount -= count
                            it
                        }

                        else -> {
                            it
                        }
                    }
                },
            )
        }

        fun copyMediaToAlbum(
            bID: String,
            count: Int,
        ) {
            albumCache.updateAlbumList(
                albumCache.listAlbums.value.toMutableList().map {
                    if (it.bID == bID) {
                        it.itemsCount += count
                    }
                    it
                },
            )
        }
    }
