package com.contextphoto.utils

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.contextphoto.data.Album
import com.contextphoto.data.AlbumListViewModel
import com.contextphoto.data.MediaViewModel
import com.contextphoto.data.PERMISSION_DELETE_REQUEST_CODE
import com.contextphoto.data.Picture
import com.contextphoto.data.listpicture
import com.contextphoto.utils.FunctionsApp.durationTranslate
import com.contextphoto.utils.FunctionsBitmap.getThumbnailSafe
import com.contextphoto.utils.FunctionsUri.convertUri
import com.contextphoto.utils.FunctionsUri.getRealPathFromUri
import java.io.File

object FunctionsMediaStore {
    fun getListAlbums(context: Context, viewModel: AlbumListViewModel) {
        val albums = mutableListOf<Album>()
        var itemsCount = hashMapOf<String, Int>()
        val contentUri = MediaStore.Files.getContentUri("external")

        val projection =
            arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.BUCKET_ID,
                MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
                MediaStore.MediaColumns.DATA,
            )
        val sortOrder = "${MediaStore.MediaColumns.BUCKET_DISPLAY_NAME} ASC"
        val uniqueAlbums = mutableListOf<String>()

        context.contentResolver
            .query(
                contentUri,
                projection,
                null,
                null,
                sortOrder,
            )?.use { cursor ->
                val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID)
                val bucketNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val bucketId = cursor.getString(bucketIdColumn)

                    var count = 1
                    if (itemsCount[bucketId] != null) {
                        count = itemsCount[bucketId]!! + 1
                        albums.forEach {
                            if (it.bID == bucketId) it.itemsCount = count
                        }
                    }
                    itemsCount[bucketId] = count

                    if (!uniqueAlbums.contains(bucketId)) {
                        uniqueAlbums.add(bucketId)
                        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                        val trashPath =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                        val path = trashPath.slice(0..trashPath.lastIndexOf("/"))
                        val id = cursor.getLong(idColumn)
                        val uri =
                            ContentUris.withAppendedId(
                                contentUri,
                                id,
                            )
                        val name = cursor.getString(bucketNameColumn)

                        val thumbnail = getThumbnailSafe(context, uri)

                        println("URI $uri")
                        println("id - $id\n")
                        println("bucketId = $bucketId")

                        println("name = $name")
                        println("thmb - $thumbnail")
                        viewModel.addAlbum(Album( // TODO fixme реализовать обновление количества элементов в альбоме
                            bucketId,
                            name,
                            1,
                            thumbnail,
                            File(path),
                            )
                        )
                    }
                }
            }
    }

    fun getAllMedia(
        context: Context,
        bucketIdArg: String = "", viewModel: MediaViewModel
    ) {
        val contentUri = MediaStore.Files.getContentUri("external")
        var n = 0
        val projection =
            arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.BUCKET_ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DURATION,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.MediaColumns.DATA,
            )
        val selection = "${MediaStore.MediaColumns.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(bucketIdArg)
        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC" // DATE_MODIFIED // DATE_TAKEN

        context.applicationContext.contentResolver
            .query(
                contentUri,
                projection,
                selection,
                selectionArgs,
                sortOrder,
            )?.use { cursor ->
                val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID)
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)
                val dateAdded = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)

                while (cursor.moveToNext()) {
//                        if (n == stop) break
                    val bucketId = cursor.getString(bucketIdColumn)
//                if (bucketIdArg != "") {
//                    if (bucketId == bucketIdArg) {
                    val id = cursor.getLong(idColumn)

//                        when {
//                            startId == 0L -> {
//                                startId = id
//                                n++
//                            }
//                            id == startId -> {
//                                println("startid - $startId end - $n/$stop")
//                                n++
//                                continue
//                            }
//                        }

//                        if (n in 0..stop) {
                    val path = cursor.getString(pathColumn)
                    val uri =
                        ContentUris.withAppendedId(
                            contentUri,
                            id,
                        )
                    val duration = cursor.getInt(durationColumn)
                    n++
                    if (duration > 0) {
                        val thumbnail =
                            getThumbnailSafe(
                                context,
                                ContentUris.withAppendedId(
                                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                    id
                                ),
                            )
                        println("video $n $id $uri, $path $bucketIdArg $dateAdded")
                        viewModel.addPicture(
                            Picture(
                                bucketId,
                                uri,
                                path,
                                thumbnail,
                                durationTranslate(duration),
                                false
                            )
                        )
//                            emit(Picture(bucketId, uri, path, thumbnail, durationTranslate(duration), false))
                    } else {
                        val thumbnail =
                            getThumbnailSafe(
                                context,
                                ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    id
                                ),
                            )
                        println("image $n $id $uri, $path $bucketId $dateAdded")
                        viewModel.addPicture(Picture(bucketId, uri, path, thumbnail, "", false))
//                            emit(Picture(bucketId, uri, path, thumbnail, "", false))
                    }
//                        }
//                        if (n == stop) {
//                            startId = id
//                        }
                }


//        return mediaFiles
            }
    }


        fun copyMediaToAlbum(
            context: Context,
            sourceUri: Uri,
            albumName: String,
        ): Boolean {
            Log.d("Soure uri", sourceUri.toString())
            val contentResolver = context.contentResolver
            var filePath = File("1")
            try {
                filePath = File(getRealPathFromUri(context, sourceUri))
            } catch (e: Exception) {
                Log.d("E: copyMediaToAlbum", e.toString())
                return false
            }

            try {
                var mimeType = contentResolver.getType(sourceUri)!!
                when {
                    mimeType.startsWith("image/") -> mimeType = "image/*"
                    mimeType.startsWith("video/") -> mimeType = "video/*"
                    else -> "file/*"
                }

                val contentValues =
                    ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filePath.name)
                        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            Environment.DIRECTORY_PICTURES + "/ContextPhoto/$albumName"
                        )

                        Log.i(
                            "Path",
                            "${MediaStore.MediaColumns.RELATIVE_PATH}, ${Environment.DIRECTORY_PICTURES}/ContextPhoto/$albumName"
                        )

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.MediaColumns.IS_PENDING, 1)
                        }
                    }

                val collection =
                    when {
                        mimeType.startsWith("image/") -> MediaStore.Images.Media.getContentUri("external")
                        mimeType.startsWith("video/") -> MediaStore.Video.Media.getContentUri("external")
                        else -> MediaStore.Files.getContentUri("external")
                    }

                val destinationUri =
                    contentResolver.insert(collection, contentValues) ?: return false

                contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                    contentResolver.openOutputStream(destinationUri)?.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    contentResolver.update(destinationUri, contentValues, null, null)
                }

                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        fun deleteMediaFile(
            context: Context,
            activity: Activity,
            onNeedPermission: ((IntentSender) -> Unit)? = null,
        ) {
            try {
                listpicture.forEach {
                    context.contentResolver.delete(convertUri(it.path, it.uri), null, null)
                }
            } catch (e: RecoverableSecurityException) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    val intentSender = e.userAction.actionIntent.intentSender
//                onNeedPermission?.invoke(intentSender) ?: run {
//                    activity.startIntentSenderForResult(
//                        intentSender,
//                        PERMISSION_DELETE_REQUEST_CODE,
//                        null,
//                        0,
//                        0,
//                        0,
//                        null,
//                    )
//                }
                    activity.startIntentSenderForResult(
                        intentSender,
                        PERMISSION_DELETE_REQUEST_CODE,
                        null,
                        0,
                        0,
                        0,
                        null,
                    )
                } else {
                    val pendingIntent =
                        MediaStore.createDeleteRequest(
                            context.contentResolver,
                            listpicture.map { convertUri(it.path, it.uri) },
                        )
//                onNeedPermission?.invoke(pendingIntent.intentSender) ?: run {
//                    activity.startIntentSenderForResult(
//                        pendingIntent.intentSender,
//                        PERMISSION_DELETE_REQUEST_CODE,
//                        null,
//                        0,
//                        0,
//                        0,
//                        null,
//                    )
//                }
                    activity.startIntentSenderForResult(
                        pendingIntent.intentSender,
                        PERMISSION_DELETE_REQUEST_CODE,
                        null,
                        0,
                        0,
                        0,
                        null,
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }