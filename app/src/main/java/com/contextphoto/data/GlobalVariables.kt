package com.contextphoto.data

import android.os.Environment

const val VERSION_DB = 1
const val COMMENT_DATABASE = "Context_photo_comment_database"
const val PERMISSION_DELETE_REQUEST_CODE = 103
val baseFilePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/ContextPhoto"
val baseCommentsPath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}"
