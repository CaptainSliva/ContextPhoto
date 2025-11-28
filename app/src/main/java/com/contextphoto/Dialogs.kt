package com.contextphoto

import android.app.Activity
import android.net.Uri
import android.text.Layout
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.core.content.ContentProviderCompat.requireContext
import com.contextphoto.data.Album
import com.contextphoto.data.PERMISSION_DELETE_REQUEST_CODE
import com.contextphoto.data.allAlbums
import com.contextphoto.ui.theme.ContextPhotoTheme
import com.contextphoto.utils.FunctionsDialogs.mediaPicker
import com.contextphoto.utils.FunctionsDialogs.showCreateAlbumMessage
import com.contextphoto.utils.FunctionsDialogs.showDeleteAlbumMessage
import com.contextphoto.utils.FunctionsDialogs.showRenameAlbumMessage
import com.contextphoto.utils.FunctionsFiles.moveMediaToAlbum
import com.contextphoto.utils.FunctionsMediaStore.copyMediaToAlbum
import com.contextphoto.utils.FunctionsMediaStore.deleteMediaFile
import com.contextphoto.utils.FunctionsMediaStore.getListAlbums
import com.contextphoto.utils.FunctionsUri.handleSelectedMedia
import kotlin.collections.get
import kotlin.text.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlbumDialog(onDismissRequest: () -> Unit) {
    val context = LocalContext.current
    var albumName by remember { mutableStateOf("") }
    var listUri by remember { mutableStateOf<List<Uri>?>(null) }
    val pickVisualMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            onDismissRequest()
            listUri = uris.toList()
        }
    }
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = handleSelectedMedia(result.data)
            onDismissRequest()
            listUri = data
        }
    }

    SideEffect {
        if (listUri?.isNotEmpty() ?: false) {
            println("ну должно работать")
            // TODO fixme беда с composable штукой
            //CopyMoveDialog(listUri!!, albumName, {})
        }
    }


    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(Color.DarkGray)
    )
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = context.getString(R.string.create_album_text),
                color = Color.White)
            OutlinedTextField(
                value = albumName,
                onValueChange = { albumName = it },
                label = { "Enter text"},
                placeholder = { "Hello World"},
                supportingText = {
                    Text("Минимум 6 символов",
                        color = Color.White)
                },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    onDismissRequest()
                }) {
                    Text(text = context.getString(R.string.cancel),
                        color = Color.Red)
                }
                Button(modifier = Modifier.background(Color.Cyan),
                    onClick = {
                    // запуск диалога
                    if (albumName.isNotEmpty()) {
                        showCreateAlbumMessage(context, albumName)
                        mediaPicker(pickMediaLauncher, pickVisualMedia, ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                    } else {
                        Toast.makeText(context, context.getString(R.string.enter_name), Toast.LENGTH_SHORT).show()
                    }
                    Log.i("NEWNAME", "$albumName")
// TODO add добавить альбом в список
                    onDismissRequest()
                }) {
                    Text(text = LocalContext.current.getString(R.string.ok),
                        color = Color.White)
                }
            }
        }
        // Sheet content
//        Button(onClick = {
//            onDismissRequest()
//        }) {
//            Text("Hide bottom sheet")
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CopyMoveDialog(listUri: List<Uri>,
                   albumName: String,
                   onDismissRequest: () -> Unit) {
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(Color.DarkGray)
    )
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()) {
            Text(text = LocalContext.current.getString(R.string.create_album_text),
                color = Color.White)
            Button(onClick = {
                listUri.forEach {
                    if (copyMediaToAlbum(context, it, albumName)) {
                        if (it == listUri[listUri.size - 1]) {
                            val albumsNames = allAlbums.map { it.name }
                            lateinit var newAlbum: Album
                            getListAlbums(context).forEach { album ->
                                if (album.name !in albumsNames) newAlbum = album
                            }
                            try {
                                allAlbums.add(newAlbum)
                                allAlbums = allAlbums.sortedBy { it.name } as MutableList<Album>
                            } catch (e: Exception) {
                                Toast.makeText(context, "Альбом \"$albumName\" уже создан", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "IO ex ${it.path}", Toast.LENGTH_SHORT).show()
                    }
                }
                onDismissRequest()
            }) {
                Text(text = LocalContext.current.getString(R.string.copy),
                    color = Color.White)
            }
            Button(onClick = {
                listUri.forEach {
                    val result = moveMediaToAlbum(context, it, albumName)
                    if (result == "Complete") {
                        if (it == listUri[listUri.size - 1]) {
                            val albumsNames = allAlbums.map { it.name }
                            lateinit var newAlbum: Album
                            getListAlbums(context).forEach { album ->
                                if (album.name !in albumsNames) newAlbum = album
                            }
                            allAlbums.add(newAlbum)
                            allAlbums = allAlbums.sortedBy { it.name } as MutableList<Album>
                        }
                    } else if (result == "NoDelete") {
                        Toast.makeText(context, context.getString(R.string.cant_move), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "IO ex ${it.path}", Toast.LENGTH_SHORT).show()
                    }
                }
// TODO add обновить список альбомов и фото
                onDismissRequest()
            }) {
                Text(text = LocalContext.current.getString(R.string.move),
                    color = Color.White)
            }
            Button(onClick = {
                onDismissRequest()
            }) {
                Text(text = LocalContext.current.getString(R.string.cancel),
                    color = Color.Red)
            }

        }
        // Sheet content
//        Button(onClick = {
//            onDismissRequest()
//        }) {
//            Text("Hide bottom sheet")
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun deleteDialog(onDismissRequest: () -> Unit, album: Album, needDelete: Boolean = true) {

    val context = LocalContext.current
    val activity = LocalActivity.current

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(Color.DarkGray)
    )
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = context.getString(R.string.delete),
                color = Color.White)
            Text(text = "${context.getString(R.string.delete_album_text)} ${album.name}?",
                color = Color.White)
            Button(
                onClick = {
                    if (needDelete) {
                        showDeleteAlbumMessage(context, album)
                        allAlbums = getListAlbums(context) as MutableList<Album>
                    }
//                    else {
//                        deleteMediaFile(
//                            context,
//                            activity,
//                            { intentSender -> // TODO fixme intent
//                                startIntentSenderForResult(
//                                    intentSender,
//                                    PERMISSION_DELETE_REQUEST_CODE,
//                                    null,
//                                    0,
//                                    0,
//                                    0,
//                                    null,
//                                )
//                            },
//                        )
//                    }
// TODO add удалить фото или альбом из списка и обновить список альбомов
                    onDismissRequest()
                }
            ) {
                Text(text = context.getString(R.string.delete),
                    color = Color.Red)
            }
            Button(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(text = context.getString(R.string.cancel),
                    color = Color.White)
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun renameAlbumDialog(onDismissRequest: () -> Unit, album: Album) {

    val context = LocalContext.current
    var albumName by remember { mutableStateOf(album.name) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(Color.DarkGray)
    )
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = context.getString(R.string.create_album_text),
                color = Color.White)
            OutlinedTextField(
                value = albumName,
                onValueChange = { albumName = it },
                label = {"Enter text"},
                placeholder = {"Hello World"},
                supportingText = {
                    Text("Минимум 6 символов",
                        color = Color.White)
                },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    onDismissRequest()
                }) {
                    Text(text = context.getString(R.string.cancel),
                        color = Color.Red)
                }
                Button(modifier = Modifier.background(Color.Cyan),
                    onClick = {
                        val newName = albumName
                        if (newName.isNotEmpty()) {
                            showRenameAlbumMessage(context, album, newName)
                            val albumsNames = allAlbums.map { it.name }
                            var newAlbum = allAlbums[0]
                            getListAlbums(context).forEachIndexed { i, album ->
                                if (album.name != albumsNames[i]) {
                                    newAlbum = album
                                    allAlbums[i].bID = album.bID
                                }
                            }
                            if (newAlbum != allAlbums[0]) {
                                allAlbums = allAlbums.sortedBy { it.name } as MutableList<Album>
                            } else {
                                Toast.makeText(context, context.getString(R.string.cant_rename_album), Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, context.getString(R.string.enter_name), Toast.LENGTH_SHORT).show()
                        }
                        Log.i("NEWNAME", "$newName")
// TODO add обновить альбом в списке
                        onDismissRequest()
                }) {
                    Text(text = LocalContext.current.getString(R.string.ok),
                        color = Color.White)
                }
            }
        }
    }

}




@Preview(showBackground = true)
@Composable
fun GreetngPrevieww() {
    ContextPhotoTheme {
        CreateAlbumDialog(
            {}
        )

    }
}