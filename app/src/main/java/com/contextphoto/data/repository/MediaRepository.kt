package com.contextphoto.data.repository

import android.graphics.Bitmap
import com.contextphoto.item.Picture
import com.contextphoto.data.datasource.MediaCache
import javax.inject.Inject

class MediaRepository
    @Inject
    constructor(
        private val mediaCache: MediaCache,
    ) {
        fun getDB() = mediaCache.db

        fun getLoadPicturesState() = mediaCache.loadPictures.value

        fun getPictureList() = mediaCache.listPicture.value

        fun getSelectedMediaList() = mediaCache.listSelectedMedia.value

        fun loadPictureList(bID: String, page: Int, rowSize: Int) = mediaCache.loadPictureList(bID, page, rowSize)

        fun clearPictureList() = mediaCache.clearPictureList()

        fun getMediaPosition() = mediaCache.mediaPosition.value

        fun generatePicturesList(itemsCount: Int) = mediaCache.generatePicturesList(itemsCount)

        fun addPicture(picture: Picture) {
            mediaCache.updatePictureList(
                mediaCache.listPicture.value
                    .toMutableList()
                    .apply { add(picture) },
            )
        }

        fun loadPicturesStateChange(state: Boolean? = null) {
            if (state != null) {
                mediaCache.loadPicturesState(state)
            } else {
                mediaCache.loadPicturesState()
            }
        }

        fun deletePicture(picture: Picture?) {
            mediaCache.updatePictureList(
                mediaCache.listPicture.value
                    .toMutableList()
                    .apply { remove(picture) },
            )
        }

        suspend fun changeStatePictureComment(mediaIndex: Int, mediaThumbnail: Bitmap) {
            mediaCache.changeStatePictureComment(mediaIndex, mediaThumbnail)
        }

        fun updateMediaPosition(pos: Int? = null) {
            mediaCache.updateMediaPosition(pos)
        }

        fun deletePictureByMove() {
            mediaCache.listSelectedMedia.value.forEach {
                deletePicture(it)
            }
        }

        fun selectMedia(pic: Picture) {
            mediaCache.selectMedia(pic)
        }

        fun removeSelectMedia(pic: Picture) {
            mediaCache.removeSelectMedia(pic)
        }

        fun clearSelectedMedia() {
            mediaCache.clearSelectedMedia()
        }

        suspend fun getImageComment(bitmap: Bitmap): String {
            mediaCache.getImageComment(bitmap)
            return mediaCache.imageComment.value
        }
    }
