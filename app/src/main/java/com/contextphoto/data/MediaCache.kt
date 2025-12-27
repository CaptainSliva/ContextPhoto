package com.contextphoto.data

import android.content.Context
import com.contextphoto.utils.FunctionsMediaStore.getAllMedia
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaCache @Inject constructor(@param:ApplicationContext private val context: Context) {
    private val _listPicture = MutableStateFlow<List<Picture>>(emptyList())
    val listPicture = _listPicture.asStateFlow()

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

}
