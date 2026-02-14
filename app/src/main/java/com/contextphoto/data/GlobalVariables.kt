package com.contextphoto.data

import android.os.Environment

val PERMISSION_DELETE_REQUEST_CODE = 103
val baseFilePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/ContextPhoto/"
const val versionDB = 1
const val commentDatabase = "comment_database"
const val debugSpeedrun = "SpeedrunLogs"