package com.contextphoto.data

import android.graphics.Bitmap
import android.net.Uri

data class Picture(
    val bID: String,
    val uri: Uri,
    val path: String,
    val thumbnail: Bitmap,
    val duration: String,
    val checked: Boolean
)
