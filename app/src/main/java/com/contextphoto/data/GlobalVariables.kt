package com.contextphoto.data

import android.os.Environment

// Перенесённые с view переменные
val PERMISSION_REQUEST_CODE = 101
val OPEN_DOCUMENT_REQUEST_CODE = 102
val PERMISSION_DELETE_REQUEST_CODE = 103
val baseFilePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/ContextPhoto/"

// Переменные для БД
const val versionDB = 1
const val commentDatabase = "comment_database"

// Новые переменные
