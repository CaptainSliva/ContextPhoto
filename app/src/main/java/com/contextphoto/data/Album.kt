package com.contextphoto.data

import android.graphics.Bitmap
import java.io.File

data class Album(
    var bID: String,
    val name: String,
    var itemsCount: Int,
    val miniature: Bitmap,
    val path: File,
)
