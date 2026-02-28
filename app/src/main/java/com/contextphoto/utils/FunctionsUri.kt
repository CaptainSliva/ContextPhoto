package com.contextphoto.utils

import android.R
import android.R.attr.path
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageSource.uri
import java.io.File
import kotlin.ranges.contains

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

    private inline fun getFileExtension(
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

    inline fun convertUri(
        path: String,
        uri: Uri,
    ): Uri {
        val extension = File(path).extension

        return when {
            extension.lowercase() in listOf("mp4", "avi", "mkv", "webm", "mov", "wmv", "flv", "m4v", "3gp", "ts", "mpeg", "mpg", "ogv") ->
                ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    ContentUris.parseId(uri),
                )
            extension.lowercase() in listOf("jpg", "jpeg", "png", "bmp", "webp", "heic", "heif", "gif") ->
                ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    ContentUris.parseId(uri),
                )
            else -> uri
        }
    }
}
