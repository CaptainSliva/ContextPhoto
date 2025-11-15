package com.contextphoto.utils

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Size
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.MessageDigest

object FunctionsBitmap {
    fun md5(bitmap: Bitmap): String { // На вход идёт Bitmap изображения
        val byteBuffer = ByteBuffer.allocate(bitmap.byteCount)
        bitmap.copyPixelsToBuffer(byteBuffer)
        val pixelData = byteBuffer.array()
        val md =
            MessageDigest.getInstance("MD5").apply {
                update(pixelData)
            }
        return BigInteger(1, md.digest()).toString(16).padStart(32, '0')
    }

    inline fun getThumbnailSafe(
        context: Context,
        uri: Uri,
    ): Bitmap =
        try {
            context.contentResolver.loadThumbnail(uri, Size(640, 480), null)
        } catch (e: Exception) {
            MediaStore.Images.Thumbnails.getThumbnail(
                context.contentResolver,
                ContentUris.parseId(uri),
                MediaStore.Images.Thumbnails.MINI_KIND,
                null,
            )
        }
}