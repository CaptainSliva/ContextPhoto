package com.contextphoto.data

import android.graphics.Bitmap
import android.net.Uri
import kotlinx.serialization.Serializable

data class Picture(
    val bID: String,
    val uri: Uri,
    val path: String,
    val thumbnail: Bitmap,
    val duration: String,
    var checked: Boolean
)
