package com.contextphoto.item

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState

data class Picture(
    val bID: String,
    val uri: Uri,
    val path: String,
    val thumbnail: Bitmap,
    val date: List<String>,
    val duration: String,
    val haveComment: MutableState<Boolean>,
)
