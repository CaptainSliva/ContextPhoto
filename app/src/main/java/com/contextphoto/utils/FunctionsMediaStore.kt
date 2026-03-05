package com.contextphoto.utils

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.contextphoto.R
import com.contextphoto.data.PERMISSION_DELETE_REQUEST_CODE
import com.contextphoto.item.Album
import com.contextphoto.item.Picture
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
        val albumMap = HashMap<String, Album>()

        val contentUri = MediaStore.Files.getContentUri("external")
        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?"
        val selectionArgs =
            arrayOf(
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    .toString(),
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                    .toString(),
            )
        val projection =
            arrayOf(
                MediaStore.MediaColumns.BUCKET_ID,
                MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.MediaColumns.DATA,
            )
        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

        context.contentResolver
            .query(
                contentUri,
                projection,
                selection,
                selectionArgs,
                sortOrder,
            )?.use { cursor ->
                val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID)
                val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

                while (cursor.moveToNext()) {
                    val bucketId = cursor.getString(bucketIdColumn)
                    val bucketName = cursor.getString(bucketNameColumn)
                    val fileId = cursor.getLong(idColumn)
                    val filePath = cursor.getString(dataColumn)

                    val album = albumMap[bucketId]

                    if (album == null) {
                        val uri = ContentUris.withAppendedId(contentUri, fileId)
                        val path = filePath.substringBeforeLast("/") + "/"

                        val thumbnail =
                            getThumbnail(context, uri)
                                ?: BitmapFactory.decodeResource(context.resources, R.drawable.no_image)

                        albums.add(
                            Album(
                                bID = bucketId,
                                name = bucketName,
                                itemsCount = 1,
                                thumbnail = thumbnail,
                                path = File(path),
                            ),
                        )
                        albumMap[bucketId] = albums.last()
                    } else {
                        album.itemsCount++
                    }
                }
            }
        return albums
    }

    @Singleton
    @Provides
    fun getNewAlbum(
        context: Context,
        newAlbumName: String,
        viewModel: AlbumViewModel,
    ): MutableList<Album> {
        val albums = mutableListOf<Album>()
        val albumMap = HashMap<String, Album>()

        val contentUri = MediaStore.Files.getContentUri("external")
        val selection = "${MediaStore.MediaColumns.BUCKET_DISPLAY_NAME} = ? AND (${MediaStore.Files.FileColumns.MEDIA_TYPE} = ? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?)"
        val selectionArgs =
            arrayOf(
                newAlbumName,
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    .toString(),
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                    .toString(),
            )
        val projection =
            arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.BUCKET_ID,
                MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DATE_ADDED,
            )
        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

        viewModel.changeStateAlbum()
        viewModel.changeStateAlbum()

        context.contentResolver
            .query(
                contentUri,
                projection,
                selection,
                selectionArgs,
                sortOrder,
            )?.use { cursor ->
                val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID)
                val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

                while (cursor.moveToNext()) {
                    val bucketId = cursor.getString(bucketIdColumn)
                    val bucketName = cursor.getString(bucketNameColumn)
                    val fileId = cursor.getLong(idColumn)
                    val filePath = cursor.getString(dataColumn)

                    val existingAlbum = albumMap[bucketId]

                    if (existingAlbum == null) {
                        val uri = ContentUris.withAppendedId(contentUri, fileId)
                        val path = filePath.substringBeforeLast("/") + "/"
                        val thumbnail = getThumbnail(context, uri)

                        if (thumbnail != null) {
                            val album =
                                Album(
                                    bID = bucketId,
                                    name = bucketName,
                                    itemsCount = 1,
                                    thumbnail = thumbnail,
                                    path = File(path),
                                )

                            albums.add(album)
                            albumMap[bucketId] = album
                            viewModel.addAlbum(album)

                            Log.d("TAG URI", uri.toString())
                            println("URI $uri")
                            println("id - $fileId")
                            println("bucketId = $bucketId")
                            println("name = $bucketName")
                            println("thmb - $thumbnail")
                        }
                    } else {
                        existingAlbum.itemsCount++
                    }
                }
            }

        return albums
    }

    @Singleton
    @Provides
    fun getPieceMedia(
        context: Context,
        bucketIdArg: String = "",
        offset: Int,
        limit: Int,
        currentSize: Int,
    ): List<Picture> {
        var counter = 0
        if (currentSize > offset) return emptyList()
        if (currentSize < offset) Log.d("Cize", "$currentSize vs $offset")
        val listMedia = mutableListOf<Picture>()
        val contentUri = MediaStore.Files.getContentUri("external")
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
                MediaStore.Files.FileColumns.MEDIA_TYPE,
            )
        val selectionBuilder = StringBuilder()
        val selectionArgsList = mutableListOf<String>()
        if (bucketIdArg.isNotEmpty()) {
            selectionBuilder.append("${MediaStore.MediaColumns.BUCKET_ID} = ?")
            selectionArgsList.add(bucketIdArg)
        }
        if (selectionBuilder.isNotEmpty()) {
            selectionBuilder.append(" AND ")
        }
        selectionBuilder.append("(${MediaStore.Files.FileColumns.MEDIA_TYPE} = ? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?)")
        selectionArgsList.add(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                .toString(),
        )
        selectionArgsList.add(
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                .toString(),
        )
        val selection = selectionBuilder.toString()
        val selectionArgs = selectionArgsList.toTypedArray()
        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

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
                val mediaTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)

                if (cursor.count > offset) {
                    cursor.moveToPosition(offset - 1)
                } else {
                    return emptyList()
                }

                while (cursor.moveToNext() && counter < limit) {
                    val bucketId = cursor.getString(bucketIdColumn)
                    val id = cursor.getLong(idColumn)
                    val path = cursor.getString(pathColumn)
                    val mediaType = cursor.getInt(mediaTypeColumn)
                    val duration = cursor.getInt(durationColumn).toLong()
                    val dateAdded = cursor.getLong(dateAddedColumn)

                    counter++

                    val thumbnail =
                        when (mediaType) {
                            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> {
                                getThumbnail(
                                    context,
                                    ContentUris.withAppendedId(
                                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                        id,
                                    ),
                                )
                            }

                            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> {
                                getThumbnail(
                                    context,
                                    ContentUris.withAppendedId(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        id,
                                    ),
                                )
                            }

                            else -> {
                                null
                            }
                        }

                    if (thumbnail != null) {
                        val uri = ContentUris.withAppendedId(contentUri, id)
                        val formattedDate = dateFormat.format(Date(dateAdded * 1000)).toString().split("\n")

                        listMedia.add(
                            Picture(
                                bID = bucketId,
                                uri = uri,
                                path = path,
                                thumbnail = thumbnail,
                                date = formattedDate,
                                duration = if (duration > 0) durationTranslate(duration) else "",
                                haveComment = mutableStateOf(false),
                            ),
                        )
                    }
                }
            }

        return listMedia
    }

    @Singleton
    @Provides
    fun getPieceOfMediaStore(
        context: Context,
        bucketIdArg: String = "",
        page: Int,
        pageSize: Int,
        currentSize: Int,
    ): List<Picture> {
        val offset = page * pageSize
        return getPieceMedia(context, bucketIdArg, offset, pageSize, currentSize)
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

                    put(MediaStore.MediaColumns.IS_PENDING, 1)
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

            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            contentResolver.update(destinationUri, contentValues, null, null)

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
        if (mediaFiles.isNotEmpty()) {
            return mediaFiles[0]
        } else {
            return Picture("", "".toUri(), "", createBitmap(1, 1), listOf(), "", mutableStateOf(true))
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
}
