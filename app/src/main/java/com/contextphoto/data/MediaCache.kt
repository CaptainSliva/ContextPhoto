package com.contextphoto.data

import android.content.Context
import com.contextphoto.db.CommentDatabase
import com.contextphoto.utils.FunctionsMediaStore.getAllMedia
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class MediaCache
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) {
        private val _listPicture = MutableStateFlow<List<Picture>>(emptyList())
        private val _loadPictureState = MutableStateFlow(true)
        private val _mediaPosition = MutableStateFlow(0)
        val listPicture = _listPicture.asStateFlow()
        val loadPictures = _loadPictureState.asStateFlow()
        val mediaPosition = _mediaPosition.asStateFlow()
        val db = CommentDatabase.getDatabse(context).commentDao()

        fun loadPictureList(bID: String): List<Picture> {
            _listPicture.value = getAllMedia(context, bID)
            return _listPicture.value
        }

        fun updatePictureList(newList: List<Picture>) {
            _listPicture.value = newList
        }

        fun clearPictureList() {
            _listPicture.value = emptyList()
        }

        fun changePictureState(
            picID: String,
            state: Boolean,
        ) {
            _listPicture.value.forEach {
                if (it.bID == picID) it.checked = state
            }
        }

        fun clearSelectedMedia() {
            _listPicture.value.forEach {
                it.checked = false
            }
        }

        fun loadPicturesState(state: Boolean? = null) {
            if (state != null) {
                _loadPictureState.value = state
            } else {
                _loadPictureState.value = !_loadPictureState.value
            }
        }

        fun updateMediaPosition(pos: Int) {
            when {
                _listPicture.value.size == pos -> _mediaPosition.value = pos - 1
                else -> _mediaPosition.value = pos
            }
        }
    }
