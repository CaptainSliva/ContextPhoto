package com.contextphoto.data

import javax.inject.Inject

class MediaRepository
    @Inject
    constructor(
        private val mediaCache: MediaCache,
    ) {
        fun getDB() = mediaCache.db

        fun getLoadPicturesState() = mediaCache.loadPictures.value

        fun getPictureList() = mediaCache.listPicture.value

        fun loadPictureList(bID: String) = mediaCache.loadPictureList(bID)

        fun clearPictureList() = mediaCache.clearPictureList()

        fun getMediaPosition() = mediaCache.mediaPosition.value

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

        fun changePictureState(
            picID: String,
            state: Boolean,
        ) {
            mediaCache.changePictureState(picID, state)
        }

        fun clearSelectedMedia() {
            mediaCache.clearSelectedMedia()
        }

        fun updateMediaPosition(pos: Int) {
            mediaCache.updateMediaPosition(pos)
        }
    }
