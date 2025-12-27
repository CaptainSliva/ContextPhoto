package com.contextphoto.data

import javax.inject.Inject

class MediaRepository @Inject constructor(private val mediaCache: MediaCache) {

    fun getPictureList() = mediaCache.listPicture.value
    fun loadPictureList(bID: String) = mediaCache.loadPictureList(bID)
    fun clearPictureList() = mediaCache.clearPictureList()

    fun addPicture(picture: Picture) {
        mediaCache.updatePictureList(mediaCache.listPicture.value.toMutableList().apply { add(picture) })
    }

    fun deletePicture(picture: Picture?) {
        mediaCache.updatePictureList(mediaCache.listPicture.value.toMutableList().apply { remove(picture) })
    }

    fun updatePicture(picture: Picture) {
        mediaCache.updatePictureList(
            mediaCache.listPicture.value.toMutableList().map
            {
                if (it.bID == picture.bID) {
                    picture
                } else {
                    it
                }
            }
        )
    }
    
}