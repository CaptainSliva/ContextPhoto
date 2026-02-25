package com.contextphoto.utils

import android.R.attr.data
import android.R.attr.text
import android.R.id.message
import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.contextphoto.data.Album
import com.contextphoto.data.PERMISSION_DELETE_REQUEST_CODE
import com.contextphoto.data.Picture
import com.contextphoto.data.baseCommentsPath
import com.contextphoto.data.commentDatabase
import com.contextphoto.ui.AlbumViewModel
import com.contextphoto.utils.FunctionsApp.durationTranslate
import com.contextphoto.utils.FunctionsBitmap.getThumbnail
import com.contextphoto.utils.FunctionsUri.convertUri
import com.contextphoto.utils.FunctionsUri.getRealPathFromUri
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Module
@InstallIn(SingletonComponent::class)
object FunctionsMediaStore {
    @Singleton
    @Provides
    fun getListAlbums(context: Context): MutableList<Album> {
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
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID)
                val bucketNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val bucketId = cursor.getString(bucketIdColumn)
                    val uri = cursor.getLong(idColumn)

                    var count = 1
                    if (itemsCount[bucketId] != null) {
                        count = itemsCount[bucketId]!! + 1
                        albums.forEach {
                            if (it.bID == bucketId) {
                                it.itemsCount = count
                                Log.d("TAG URI", uri.toString())

                                it.thumbnail = getThumbnail(context, ContentUris.withAppendedId(contentUri, uri))
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

                        val thumbnail = getThumbnail(context, uri)

                        println("URI $uri")
                        println("id - $id\n")
                        println("bucketId = $bucketId")

                        println("name = $name")
                        println("thmb - $thumbnail")
                        if (thumbnail != null) {
                            val album =
                                Album(
                                    bucketId,
                                    name,
                                    1,
                                    thumbnail,
                                    File(path),
                                )
                            albums.add(album)
                        }

                    }
                }
            }
        return albums
    }

    fun getNewAlbum(
        context: Context,
        newAlbumName: String,
        viewModel: AlbumViewModel,
    ): MutableList<Album> {
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
        viewModel.changeStateAlbum()

        context.contentResolver
            .query(
                contentUri,
                projection,
                selection,
                selectionArgs,
                sortOrder,
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID)
                val bucketNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val bucketId = cursor.getString(bucketIdColumn)
                    val uri = cursor.getLong(idColumn)

                    var count = 1
                    if (itemsCount[bucketId] != null) {
                        count = itemsCount[bucketId]!! + 1
                        albums.forEach {
                            if (it.bID == bucketId) {
                                it.itemsCount = count
                                Log.d("TAG URI", uri.toString())

                                it.thumbnail = getThumbnail(context, ContentUris.withAppendedId(contentUri, uri))
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

                        val thumbnail = getThumbnail(context, uri)

                        println("URI $uri")
                        println("id - $id\n")
                        println("bucketId = $bucketId")

                        println("name = $name")
                        println("thmb - $thumbnail")
                        if (thumbnail != null) {
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
                }
            }
        return albums
    }

    @Singleton
    @Provides
    fun getAllMedia(
        context: Context,
        bucketIdArg: String = "",
    ): List<Picture> {
        val listMedia = mutableListOf<Picture>()
        val contentUri = MediaStore.Files.getContentUri("external")
        val dateFormat = SimpleDateFormat("d MMMM yyyy\nHH:mm:ss", Locale("ru"))
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
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)

                while (cursor.moveToNext()) {
                    val bucketId = cursor.getString(bucketIdColumn)
                    val id = cursor.getLong(idColumn)
                    val path = cursor.getString(pathColumn)
                    val uri =
                        ContentUris.withAppendedId(
                            contentUri,
                            id,
                        )
                    val duration = cursor.getInt(durationColumn).toLong()
                    val dateAdded = cursor.getLong(dateAddedColumn)
                    n++
                    if (duration > 0) {
                        val thumbnail =
                            getThumbnail(
                                context,
                                ContentUris.withAppendedId(
                                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                    id,
                                ),
                            )
                        println("video $n $id $uri, $path $bucketId $dateAdded")
                        listMedia.add(
                            Picture(
                                bucketId,
                                uri,
                                path,
                                thumbnail,
                                dateFormat.format(Date(dateAdded * 1000)).toString().split("\n"),
                                durationTranslate(duration),
                                mutableStateOf(false),
                            ),
                        )
                    } else {
                        val thumbnail =
                            getThumbnail(
                                context,
                                ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    id,
                                ),
                            )
                        println("image $n $id $uri, $path $bucketId $dateAdded")
                        if (thumbnail != null) {
                            listMedia.add(
                                Picture(
                                    bucketId,
                                    uri,
                                    path,
                                    thumbnail,
                                    dateFormat.format(Date(dateAdded * 1000)).toString().split("\n"),
                                    "",
                                    mutableStateOf(false),
                                ),
                            )
                        }

                    }
                }
            }
        return listMedia
    }

    inline fun copyMediaToAlbum(
        context: Context,
        sourceUri: Uri,
        albumName: String,
    ): Boolean {
        Log.d("copyMediaToAlbum", sourceUri.toString())
        val contentResolver = context.contentResolver
        var filePath = File("1")
        try {
            filePath = File(getRealPathFromUri(context, sourceUri))
        } catch (e: Exception) {
            Log.d("copyMediaToAlbum", e.toString())
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
                        "copyMediaToAlbum",
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

    inline fun deleteMediaFile(
        context: Context,
        activity: Activity,
        sourceUri: Uri,
    ): Boolean {
        val path = getRealPathFromUri(context, sourceUri)!!
        try {
            println("URI - $sourceUri")
            println("PATH - $path")
            println("URL - ${convertUri(path, sourceUri)}")
            context.contentResolver.delete(convertUri(path, sourceUri), null, null)
            return true
        } catch (recoverableSecurityException: RecoverableSecurityException) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                val intentSender = recoverableSecurityException.userAction.actionIntent.intentSender
                activity.startIntentSenderForResult(
                    intentSender,
                    PERMISSION_DELETE_REQUEST_CODE,
                    null,
                    0,
                    0,
                    0,
                    null,
                )
                return true
            } else {
                val pendingIntent =
                    MediaStore.createDeleteRequest(
                        context.contentResolver,
                        listOf(convertUri(path, sourceUri)),
                    )
                activity.startIntentSenderForResult(
                    pendingIntent.intentSender,
                    PERMISSION_DELETE_REQUEST_CODE,
                    null,
                    0,
                    0,
                    0,
                    null,
                )
                return true
            }
//            val intentSender =
//                recoverableSecurityException.userAction.actionIntent.intentSender
//            intentSender.let {
//                startIntentSenderForResult(activity, it, PERMISSION_DELETE_REQUEST_CODE,
//                    null, 0, 0, 0, null)
//                return true // Тут просто на модификацию создавал запрос, поэтому фото с первого раза не удалялось
//            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    inline fun getPictureFromUri(
        context: Context,
        uri: Uri,
    ): Picture {
        val mediaFiles = mutableListOf<Picture>()
        val dateFormat = SimpleDateFormat("d MMMM yyyy\nHH:mm:ss", Locale("ru"))
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
        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

        context.contentResolver
            .query(
                uri,
                projection,
                null,
                null,
                sortOrder,
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                    val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID)
                    val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                    val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)
                    val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)

                    val id = cursor.getLong(idColumn)
                    val bucketId = cursor.getString(bucketIdColumn)
                    val path = cursor.getString(pathColumn)
                    val duration = cursor.getLong(durationColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn)

                    val thumbnail =
                        if (duration > 0) {
                            getThumbnail(context, ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id))
                        } else {
                            getThumbnail(context, ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id))
                        }
                    if (thumbnail != null) {
                        mediaFiles.add(
                            Picture(
                                bucketId,
                                uri,
                                path,
                                thumbnail!!,
                                dateFormat.format(Date(dateAdded * 1000)).toString().split("\n"),
                                if (duration > 0) durationTranslate(duration) else "",
                                mutableStateOf(false),
                            ),
                        )
                    }
                }
            }
        if (mediaFiles.isNotEmpty()) return mediaFiles[0]
        else {
            return Picture("", "".toUri(), "", createBitmap(1,1), listOf(), "", mutableStateOf(true))
        }
    }

    inline fun getImageDate(
        context: Context,
        path: String,
    ): List<String> { // TODO можно любые парметры даостать, если в projection их указать
        val contentUri = MediaStore.Files.getContentUri("external")
        val projection =
            arrayOf(
                MediaStore.MediaColumns.DATE_ADDED,
            )
        val selection = "${MediaStore.MediaColumns.DATA} = ?"
        val selectionArgs = arrayOf(path)

        val cursor =
            context.contentResolver.query(
                contentUri,
                projection,
                selection,
                selectionArgs,
                null,
            )
        var datePhoto = ""
        val dateFormat = SimpleDateFormat("d MMMM yyyy\nHH:mm:ss", Locale("ru"))
        cursor?.use { cursor ->
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            if (cursor.moveToFirst()) {
                val dateAdded = cursor.getLong(dateAddedColumn)
                datePhoto = dateFormat.format(Date(dateAdded * 1000)).toString()
            }
        }

        return datePhoto.split("\n")
    }

    @Singleton
    fun deleteCommentsFile() {
        val folder: File =
            baseCommentsPath
        val file = File(folder, "$commentDatabase.txt")
        file.delete()
    }

    @Singleton
    @Provides
    fun importCommentsFromFile(): List<String> {
        val folder: File =
            baseCommentsPath
        val file = File(folder, "$commentDatabase.txt")
        return if (file.exists()) {
            file.readLines()
        } else {
            emptyList()
        }
    }

    @Singleton
    inline fun exportCommentsToFile(text: String) {
        val folder: File =
            baseCommentsPath
        val file = File(folder, "$commentDatabase.txt")
        if (file.exists()) {
            file.appendText(text+"\n")
        } else {
            file.writeText(text+"\n")
        }
    }
}

