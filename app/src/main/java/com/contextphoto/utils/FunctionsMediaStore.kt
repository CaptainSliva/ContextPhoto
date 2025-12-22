package com.contextphoto.utils

import android.R.attr.path
import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contextphoto.data.Album
import com.contextphoto.data.AlbumViewModel
import com.contextphoto.data.MediaViewModel
import com.contextphoto.data.PERMISSION_DELETE_REQUEST_CODE
import com.contextphoto.data.Picture
import com.contextphoto.utils.FunctionsApp.durationTranslate
import com.contextphoto.utils.FunctionsBitmap.getThumbnailSafe
import com.contextphoto.utils.FunctionsUri.convertUri
import com.contextphoto.utils.FunctionsUri.getRealPathFromUri
import com.davemorrissey.labs.subscaleview.ImageSource.uri
import java.io.File

object FunctionsMediaStore {
    fun getListAlbums( // TODO fixme альбомы в памяти и на SD-карте считает за разные альбомы
        context: Context,
        viewModel: AlbumViewModel,
    ) {
        val albums = mutableListOf<Album>()
        val itemsCount = hashMapOf<String, Int>()
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
                            if (it.bID == bucketId) { // TODO fixme работает не пойми как /- грузить все альбомы в список и выдавать их ViewModel
                                it.itemsCount = count
                                // Если не брать каждый раз превью, тогда считает количество медиа в альбоме нормально но если брать, тогда и превью скорее всего не то, и количество медиа на цифре.
                                it.thumbnail = getThumbnailSafe(context, ContentUris.withAppendedId(contentUri, cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))))
                            }
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
                        val album =
                            Album(
                                bucketId,
                                name,
                                1,
                                thumbnail,
                                File(path),
                            )
                        albums.add(album)
                        viewModel.addAlbum(album)
                    }
                }
                albums.forEach {
                    viewModel.updateAlbum(it)
                }
                viewModel.changeState(false)
            }
    }

    fun getNewAlbum(
        context: Context,
        newAlbumName: String,
        viewModel: AlbumViewModel,
    ) {
        val albums = mutableListOf<Album>()
        val itemsCount = hashMapOf<String, Int>()
        val contentUri = MediaStore.Files.getContentUri("external")
        val selection = "${MediaStore.MediaColumns.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(newAlbumName)

        val projection =
            arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.BUCKET_ID,
                MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
                MediaStore.MediaColumns.DATA,
            )
//        val sortOrder = "${MediaStore.MediaColumns.BUCKET_DISPLAY_NAME} == $newAlbumName"
        val sortOrder = "${MediaStore.MediaColumns.BUCKET_DISPLAY_NAME} ASC"
        val uniqueAlbums = mutableListOf<String>()
        viewModel.changeState()

        context.contentResolver
            .query(
                contentUri,
                projection,
                selection,
                selectionArgs,
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
                            if (it.bID == bucketId) {
                                it.itemsCount = count
                            }
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
                        val album =
                            Album(
                                bucketId,
                                name,
                                1,
                                thumbnail,
                                File(path),
                            )
                        albums.add(album)
                        viewModel.addAlbum(album)
                    }
                }
                albums.forEach {
                    viewModel.updateAlbum(it)
                }
                viewModel.changeState(false)
            }
    }

    fun getAllMedia(
        context: Context,
        bucketIdArg: String = "",
        viewModel: MediaViewModel,
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
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        if (bucketIdArg != "") {
            selection = "${MediaStore.MediaColumns.BUCKET_ID} = ?"
            selectionArgs = arrayOf(bucketIdArg)
        }
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
                    val bucketId = cursor.getString(bucketIdColumn)
                    val id = cursor.getLong(idColumn)
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
                                    id,
                                ),
                            )
                        println("video $n $id $uri, $path $bucketId $dateAdded")
                        viewModel.addMedia(
                            Picture(
                                bucketId,
                                uri,
                                path,
                                thumbnail,
                                durationTranslate(duration),
                                //false,
                            ),
                        )
                    } else {
                        val thumbnail =
                            getThumbnailSafe(
                                context,
                                ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    id,
                                ),
                            )
                        println("image $n $id $uri, $path $bucketId $dateAdded")
                        viewModel.addMedia(Picture(bucketId, uri, path, thumbnail, "", ))
                    }
                }
            }
        viewModel.changeState(false)
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
                        Environment.DIRECTORY_PICTURES + "/ContextPhoto/$albumName",
                    )

                    Log.i(
                        "Path",
                        "${MediaStore.MediaColumns.RELATIVE_PATH}, ${Environment.DIRECTORY_PICTURES}/ContextPhoto/$albumName",
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
        sourceUri: Uri
    ) {

        try {
            val path = getRealPathFromUri(context, sourceUri)!!
            println("URI - $sourceUri")
            println("PATH - $path")
            println("URL - ${convertUri(path, sourceUri)}")
            context.contentResolver.delete(convertUri(path, sourceUri), null, null)
        }
        catch (recoverableSecurityException: RecoverableSecurityException ) {
            val intentSender =
                recoverableSecurityException.userAction.actionIntent.intentSender
            intentSender.let {
                startIntentSenderForResult(activity, it, PERMISSION_DELETE_REQUEST_CODE,
                    null, 0, 0, 0, null)
            }
        }
//
//        listSelectedMedia.forEach {
//            try {
//                context.contentResolver.delete(convertUri(it.path, it.uri), null, null)
//            }
//            catch (e: RecoverableSecurityException ) {
//                recoverableSecurityException.add(e)
//            }
//        }
//
//        recoverableSecurityException.forEach {
//            val intentSender =
//                it.userAction.actionIntent.intentSender
//            intentSender.let {
//                startIntentSenderForResult(activity, it, PERMISSION_DELETE_REQUEST_CODE,
//                    null, 0, 0, 0, null)
//            }
//        }


//             catch (e: RecoverableSecurityException) {
//                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
//                    val intentSender = e.userAction.actionIntent.intentSender
//                    activity.startIntentSenderForResult(
//                        intentSender,
//                        PERMISSION_DELETE_REQUEST_CODE,
//                        null,
//                        0,
//                        0,
//                        0,
//                        null,
//                    )
//                } else {
//                    val pendingIntent =
//                        MediaStore.createDeleteRequest(
//                            context.contentResolver,
//                            listpicture.map { convertUri(it.path, it.uri) },
//                        )
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
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
    }
}
