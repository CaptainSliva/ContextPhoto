package com.contextphoto.data

import android.os.Environment

const val PERMISSION_DELETE_REQUEST_CODE = 103
val baseFilePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/ContextPhoto/"
const val versionDB = 1
const val commentDatabase = "Context_photo_comment_database"
val baseCommentsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
