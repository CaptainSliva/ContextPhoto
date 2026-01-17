package com.contextphoto.data

import android.content.Context
import javax.inject.Inject
import kotlin.collections.remove

class FullscreenRepository @Inject constructor(private val fullscreenCache: FullScreenCache) {
    fun getPictureList() = fullscreenCache.listPicture.value
    fun clearPictureList() = fullscreenCache.clearPictureList()
    fun updatePictureList(mediaList: List<Picture>) = fullscreenCache.updatePictureList(mediaList)

    fun loadPictureList(bID: String) = fullscreenCache.loadPictureList(bID)

    fun deletePicture(picture: Picture?) {
        fullscreenCache.updatePictureList(fullscreenCache.listPicture.value.toMutableList().apply { remove(picture) })
    }
}