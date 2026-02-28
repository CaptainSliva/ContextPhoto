package com.contextphoto.data.datasource

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.contextphoto.R
import com.contextphoto.data.mediaClasses.Picture
import com.contextphoto.db.CommentDatabase
import com.contextphoto.utils.FunctionsBitmap.md5
import com.contextphoto.utils.FunctionsMediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class MediaCache
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) {
    private val _listPicture = MutableStateFlow<List<Picture>>(emptyList())
    private val _loadPictureState = MutableStateFlow(true)
    private val _mediaPosition = MutableStateFlow(0)
    private val _imageComment = MutableStateFlow("")
    private val _listSelectedMedia = MutableStateFlow<List<Picture>>(emptyList())
    val listPicture = _listPicture.asStateFlow()
    val loadPictures = _loadPictureState.asStateFlow()
    val mediaPosition = _mediaPosition.asStateFlow()
    val imageComment = _imageComment.asStateFlow()
    val listSelectedMedia = _listSelectedMedia.asStateFlow()
    val db = CommentDatabase.Companion.getDatabse(context).commentDao()

    fun loadPictureList(bID: String, page: Int, rowSize: Int) {
        _listPicture.value += FunctionsMediaStore.getPieceOfMediaStore(context, bID, page, rowSize, _listPicture.value.size)
    }

    fun generatePicturesList(itemsCount: Int): List<Picture> = List(itemsCount) { Picture("", "".toUri(), "", BitmapFactory.decodeResource(context.resources, R.drawable.no_image_96), listOf(), "",
        mutableStateOf(false)
    ) }

    fun updatePictureList(newList: List<Picture>) {
        _listPicture.value = newList
    }

    fun clearPictureList() {
        _listPicture.value = emptyList()
    }

    suspend fun changeStatePictureComment(mediaIndex: Int, mediaThumbnail: Bitmap) {
        if (mediaIndex < _listPicture.value.size) {
            _listPicture.value[mediaIndex].haveComment.value =
                (db.findImageByHash(md5(mediaThumbnail))?.image_comment ?: "") != ""
        }
    }

    fun loadPicturesState(state: Boolean? = null) {
        if (state != null) {
            _loadPictureState.value = state
        } else {
            _loadPictureState.value = !_loadPictureState.value
        }
    }

    fun updateMediaPosition(pos: Int? = null) {
        if (pos != null) {
            _mediaPosition.value = pos
        }
        else {
            val currentSize = _listPicture.value.size-1
            val currentPos = _mediaPosition.value
            when {
                currentSize == currentPos -> _mediaPosition.value -= 1
                currentSize == 1 -> _mediaPosition.value = 0
                currentSize > currentPos -> _mediaPosition.value += 1
            }
        }
    }

    fun selectMedia(pic: Picture) {
        _listSelectedMedia.update { currentList ->
            currentList.toMutableList().apply {
                add(pic)
            }
        }
    }

    fun removeSelectMedia(pic: Picture) {
        _listSelectedMedia.update { currentList ->
            currentList.toMutableList().apply {
                remove(pic)
            }
        }
    }

    fun clearSelectedMedia() {
        _listSelectedMedia.value = emptyList()
    }

    suspend fun getImageComment(bitmap: Bitmap) {
        _imageComment.value = db.findImageByHash(md5(bitmap))?.image_comment ?: ""
    }

//    suspend fun getImageComment(bitmap: Bitmap) {
//        db.findImageByHashFlow(md5(bitmap))
//            .collect { _imageComment.value = it?.image_comment ?: "" }
//    }
}