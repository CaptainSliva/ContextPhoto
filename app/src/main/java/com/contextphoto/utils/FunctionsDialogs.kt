package com.contextphoto.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.contextphoto.R
import com.contextphoto.data.Album
import com.contextphoto.data.baseFilePath
import com.contextphoto.utils.FunctionsFiles.deleteAlbum
import com.contextphoto.utils.FunctionsFiles.renameAlbum
import java.io.File

object FunctionsDialogs {
    fun showCreateAlbumMessage(
        context: Context,
        albumName: String,
    ) {
        if (albumName.isNotEmpty()) {
            Log.i("Path base", baseFilePath)
            if (!File(baseFilePath).exists()) {
                File(baseFilePath).mkdir()
            }
        } else {
            Toast.makeText(context, context.getString(R.string.enter_name), Toast.LENGTH_SHORT).show()
        }
    }

    fun showDeleteAlbumMessage(
        context: Context,
        album: Album,
    ) {
        if (album.path.exists()) {
            deleteAlbum(album.path)
        } else {
            Toast.makeText(context, "Альбом ${album.name} не найден", Toast.LENGTH_SHORT).show()
        }
    }

    fun showRenameAlbumMessage(
        context: Context,
        album: Album,
        newName: String,
    ) {
        if (album.path.exists()) {
            renameAlbum(album.path, newName)
        } else {
            Toast.makeText(context, "Альбом ${album.name} не найден", Toast.LENGTH_SHORT).show()
        }
    }

    fun mediaPicker(
        pickMediaLauncher: ActivityResultLauncher<Intent>,
        pickMedia: ActivityResultLauncher<PickVisualMediaRequest>,
        pick: ActivityResultContracts.PickVisualMedia.ImageAndVideo,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            visualMediaPick(pickMedia, pick)
        } else {
            systemMediaPick(pickMediaLauncher)
        }
    }

    // Современный пикер
    private fun visualMediaPick(
        pickMedia: ActivityResultLauncher<PickVisualMediaRequest>,
        pick: ActivityResultContracts.PickVisualMedia.ImageAndVideo,
    ) {
        pickMedia.launch(PickVisualMediaRequest(pick))
    }

    // Старый пикер
    private fun systemMediaPick(pickMediaLauncher: ActivityResultLauncher<Intent>) {
        val intent =
            Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                addCategory(Intent.CATEGORY_OPENABLE)
            }

        // Создаем выборщик для красивого заголовка диалога
        val chooserIntent = Intent.createChooser(intent, "")

        pickMediaLauncher.launch(chooserIntent)
    }
}