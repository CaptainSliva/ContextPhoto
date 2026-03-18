package com.contextphoto.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import com.contextphoto.data.COMMENT_DATABASE
import com.contextphoto.data.baseCommentsPath
import com.contextphoto.utils.FunctionsMediaStore.copyMediaToAlbum
import com.contextphoto.utils.FunctionsMediaStore.deleteMediaFile
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
            Log.d("println", "Old dest! " + stringPath)
            val newDest = stringPath.slice(0..stringPath.lastIndexOf("/")) + newName
            Log.d("println", "New dest! " + newDest)
            albumPath.renameTo(File(newDest))
        } catch (e: Exception) {
        }
    }

    fun moveMediaToAlbum(
        context: Context,
        activity: Activity,
        sourceUri: Uri,
        albumName: String,
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

    fun createExportFile() {
        try {
            val file = File(baseCommentsPath, "$COMMENT_DATABASE.txt")
            if (!file.exists()) {
                file.createNewFile()
            }
        } catch (e: Exception) {
        }
    }
}
