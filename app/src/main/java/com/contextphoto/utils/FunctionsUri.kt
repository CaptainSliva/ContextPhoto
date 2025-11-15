package com.contextphoto.utils

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.File

object FunctionsUri {
    fun handleSelectedMedia(data: Intent?): List<Uri> {
        val uris = mutableListOf<Uri>()

        val singleUri = data?.data
        if (singleUri != null) {
            uris.add(singleUri)
            println(singleUri)
        } else {
            val clipData = data?.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    uris.add(uri)
                    println(uri)
                }
            }
        }

        if (uris.isNotEmpty()) {
            Log.d("MediaPicker", "Selected ${uris.size} files")
        }

        return uris
    }

    fun getRealPathFromUri(
        context: Context,
        uri: Uri,
    ): String? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileExtension = getFileExtension(context, uri)
            val file = File.createTempFile("tempFile", ".$fileExtension", context.cacheDir)

            inputStream.use { input ->
                file.outputStream().use { output ->
                    input?.copyTo(output)
                }
            }
            return file.absolutePath
        } catch (e: Exception) {
            Log.e("E: getRealPathFromUri()", e.toString())
            return null
        }
    }

    private fun getFileExtension(
        context: Context,
        uri: Uri,
    ): String {
        val mimeType = context.contentResolver.getType(uri)
        return when (mimeType) {
            "image/jpeg", "image/jpg" -> "jpg"
            "image/png" -> "png"
            "image/gif" -> "gif"
            "image/webp" -> "webp"
            "video/webm" -> "webm"
            "image/bmp" -> "bmp"
            "video/mp4" -> "mp4"
            "video/3gpp" -> "3gp"
            "video/avi" -> "avi"
            "video/quicktime" -> "mov"
            "audio/mpeg" -> "mp3"
            "audio/wav" -> "wav"
            "audio/ogg" -> "ogg"
            else -> "tmp"
        }
    }

    fun getImageDeleteUri(
        context: Context,
        path: String,
    ): Uri? { // TODO можно любые парметры даостать, если в projection их указать
        val cursor =
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID),
                MediaStore.Images.Media.DATA + " = ?",
                arrayOf(path),
                null,
            )
        val uri =
            if (cursor != null && cursor.moveToFirst()) {
                ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)),
                )
            } else {
                null
            }
        cursor?.close()
        return uri
    }

    fun convertUri(
        path: String,
        uri: Uri,
    ): Uri =
        when {
            (path.contains("VID")) ->
                ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    ContentUris.parseId(uri),
                )
            (path.contains("IMG")) ->
                ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    ContentUris.parseId(uri),
                )
            else -> uri
        }
}