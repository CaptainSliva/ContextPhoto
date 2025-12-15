package com.contextphoto.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.activity.compose.LocalActivity
import androidx.compose.ui.platform.LocalContext
import com.contextphoto.data.Picture
import com.contextphoto.utils.FunctionsMediaStore.copyMediaToAlbum
import com.contextphoto.utils.FunctionsMediaStore.deleteMediaFile
import com.contextphoto.utils.FunctionsUri.convertUri
import com.contextphoto.utils.FunctionsUri.getRealPathFromUri
import java.io.File

object FunctionsFiles {
    fun deleteAlbum(albumPath: File) {
        try {
            albumPath.listFiles().forEach {
                it.delete() // TODO fixme не удаляет альбомы если it не моего приложения
            }
            albumPath.delete()
        } catch (e: Exception) {
        }
    }

    fun renameAlbum(
        albumPath: File,
        newName: String,
    ) {
        try {
            val stringPath = albumPath.toString()
            println("Old dest! " + stringPath)
            val newDest = stringPath.slice(0..stringPath.lastIndexOf("/")) + newName
            println("New dest! " + newDest)
            albumPath.renameTo(File(newDest))
        } catch (e: Exception) {
        }
    }

    fun moveMediaToAlbum(
        context: Context,
        activity: Activity,
        sourceUri: Uri,
        albumName: String
    ): String {
        if (copyMediaToAlbum(context, sourceUri, albumName)) {
            try {
                deleteMediaFile(context, activity, sourceUri)
                return "Complete"
            } catch (e: Exception) {
                return "NoDelete"
            }
        } else {
            return "CopyError"
        }
    }
}