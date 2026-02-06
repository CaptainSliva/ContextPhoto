package com.contextphoto.data

import android.graphics.Bitmap
import android.net.Uri

data class Picture(
    val bID: String,
    val uri: Uri,
    val path: String,
    val thumbnail: Bitmap,
    val date: List<String>,
    val duration: String,
    var checked: Boolean,
    var haveComment: Boolean,
)
