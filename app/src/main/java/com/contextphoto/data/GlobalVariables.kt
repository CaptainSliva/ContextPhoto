package com.contextphoto.data

import android.content.Context
import android.os.Environment
import androidx.room.Room
import com.contextphoto.MainActivity
import kotlin.jvm.java

val PERMISSION_REQUEST_CODE = 101
val OPEN_DOCUMENT_REQUEST_CODE = 102
val PERMISSION_DELETE_REQUEST_CODE = 103
lateinit var mainContext: MainActivity
val baseFilePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/ContextPhoto/"
var allAlbums = mutableListOf<Album>()
var loadAlbumsFlag = false
var loadImagesFlag = false
var allPictures = mutableListOf<Picture>()
var listpicture = mutableListOf<Picture>()
var listpicturefind = mutableListOf<Picture>()
var startId = 0L
var positionDeleteMedia = mutableListOf<Int>()
const val versionDB = 1
