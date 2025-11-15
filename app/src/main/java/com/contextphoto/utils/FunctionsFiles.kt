package com.contextphoto.utils

import android.content.Context
import android.net.Uri
import com.contextphoto.data.listpicture
import com.contextphoto.utils.FunctionsMediaStore.copyMediaToAlbum
import com.contextphoto.utils.FunctionsUri.convertUri
import com.contextphoto.utils.FunctionsUri.getRealPathFromUri
import java.io.File

object FunctionsFiles {
    fun deleteAlbum(albumPath: File) {
        try {
            albumPath.listFiles().forEach {
                it.delete()
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
        sourceUri: Uri,
        albumName: String,
    ): String {
        if (copyMediaToAlbum(context, sourceUri, albumName)) {
            try {
                listpicture.forEach {
                    context.contentResolver.delete(convertUri(getRealPathFromUri(context, sourceUri)!!, sourceUri), null, null)
                }
                return "Complete"
            } catch (e: Exception) {
                return "NoDelete"
            }
        } else {
            return "CopyError"
        }
    }
}