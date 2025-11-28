package com.contextphoto.data

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.room.Room
import com.bumptech.glide.util.Util
import com.contextphoto.MainActivity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.java

// Перенесённые с view переменные
val PERMISSION_REQUEST_CODE = 101
val OPEN_DOCUMENT_REQUEST_CODE = 102
val PERMISSION_DELETE_REQUEST_CODE = 103
val baseFilePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/ContextPhoto/"
var allAlbums = mutableListOf<Album>()
var loadAlbumsFlag = false
var loadImagesFlag = false
var allPictures = mutableListOf<Picture>()
var listpicture = mutableListOf<Picture>()
var listpicturefind = mutableListOf<Picture>()
var albumBid = ""
var positionDeleteMedia = mutableListOf<Int>()

// Переменные для БД
const val versionDB = 1
const val commentDatabase = "comment_database"

// Новые переменные
lateinit var listPictures: Flow<Picture>
lateinit var imageUri: Uri
lateinit var openAlbum: Album
var bottomMenuVisible = mutableStateOf(false)
var selectProcess = mutableStateOf(false)
var dialogVisible = mutableStateOf(false)
var FABVisible = mutableStateOf(true)
