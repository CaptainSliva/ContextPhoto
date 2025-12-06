package com.contextphoto

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contextphoto.data.Album
import com.contextphoto.data.AlbumViewModel
import com.contextphoto.utils.FunctionsDialogs.mediaPicker
import com.contextphoto.utils.FunctionsDialogs.showCreateAlbumMessage
import com.contextphoto.utils.FunctionsDialogs.showDeleteAlbumMessage
import com.contextphoto.utils.FunctionsDialogs.showRenameAlbumMessage
import com.contextphoto.utils.FunctionsFiles.moveMediaToAlbum
import com.contextphoto.utils.FunctionsMediaStore.copyMediaToAlbum
import com.contextphoto.utils.FunctionsMediaStore.deleteMediaFile
import com.contextphoto.utils.FunctionsMediaStore.getListAlbums
import com.contextphoto.utils.FunctionsMediaStore.getNewAlbum
import com.contextphoto.utils.FunctionsUri.handleSelectedMedia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlbumDialog(onDismissRequest: () -> Unit, mutableState: MutableState<Boolean>, viewModel: AlbumViewModel) {
    val context = LocalContext.current
    val modifier = Modifier.fillMaxWidth()
    val showCopyMoveDialog = rememberSaveable { mutableStateOf(false) }
    var albumName by rememberSaveable { mutableStateOf("") }
    var listUri by rememberSaveable { mutableStateOf<List<Uri>?>(null) }
    val pickVisualMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            mutableState.value = false
            showCopyMoveDialog.value = true
            listUri = uris.toList()
            //onDismissRequest()
        }
    }
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = handleSelectedMedia(result.data)
            mutableState.value = false
            showCopyMoveDialog.value = true
            listUri = data
            //onDismissRequest()
        }
    }
    AnimatedVisibility(visible = showCopyMoveDialog.value, enter = slideInVertically(),
        exit = slideOutVertically()) {
        CopyMoveDialog(listUri!!, albumName, {}, showCopyMoveDialog)
    }


    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth()
    )
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier) {
            Text(text = context.getString(R.string.create_album_text))
            OutlinedTextField(
                value = albumName,
                onValueChange = { albumName = it },
                label = { "Enter text"},
                placeholder = { "Hello World"},
                supportingText = {
                    Text("Минимум 6 символов")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    mutableState.value = false
                    onDismissRequest()
                })
                {
                    Text(text = context.getString(R.string.cancel),
                        color = Color.Red)
                }
                Button( onClick = {
                    // запуск диалога
                    if (albumName.isNotEmpty()) {
                        showCreateAlbumMessage(context, albumName)
                        mediaPicker(pickMediaLauncher, pickVisualMedia, ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                    } else {
                        Toast.makeText(context, context.getString(R.string.enter_name), Toast.LENGTH_SHORT).show()
                    }
                    Log.i("NEWNAME", "$albumName")
                    CoroutineScope(Dispatchers.IO).launch {
                        getNewAlbum(context, albumName, viewModel) // TODO Вот сюда viewModel
                    }

// TODO add добавить альбом в список
                    mutableState.value = false
                    //onDismissRequest()
                })
                {
                    Text(text = LocalContext.current.getString(R.string.ok))
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
                   onDismissRequest: () -> Unit, mutableState: MutableState<Boolean>,
                   viewModel: AlbumViewModel = AlbumViewModel()
) {
    val context = LocalContext.current
    val modifier = Modifier.fillMaxWidth()
    LaunchedEffect({}) {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            getListAlbums(context, viewModel)
        }
    }
    val albumList by viewModel.albumList.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest, // onDismissRequest вызывается при нажатии на пустое место, надо лямбду в него передавать то же, что при окончании диалога
        modifier = Modifier.fillMaxWidth()
        )
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()) {
            Text(text = LocalContext.current.getString(R.string.to_album))
            Button(onClick = {
                listUri.forEach {
                    if (copyMediaToAlbum(context, it, albumName)) {
                        if (it == listUri[listUri.size - 1]) {
                            val albumsNames = albumList.map { it.name }
                            lateinit var newAlbum: Album
                            albumList.forEach { album ->
                                if (album.name !in albumsNames) newAlbum = album
                            }
                            try {
                                viewModel.addAlbum(newAlbum)
//                                albumList = albumList.sortedBy { it.name } as MutableList<Album> // TODO как это реализовать
                            } catch (e: Exception) {
                                Toast.makeText(context, "Альбом \"$albumName\" уже создан", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "IO ex ${it.path}", Toast.LENGTH_SHORT).show()
                    }
                }
                mutableState.value = false
                onDismissRequest()
            },
                modifier = modifier) {
                Text(text = LocalContext.current.getString(R.string.copy))
            }
            Button(onClick = {
                listUri.forEach {
                    val result = moveMediaToAlbum(context, it, albumName)
                    if (result == "Complete") {
                        if (it == listUri[listUri.size - 1]) {
                            val albumsNames = albumList.map { it.name }
                            lateinit var newAlbum: Album
                            albumList.forEach { album ->
                                if (album.name !in albumsNames) newAlbum = album
                            }
                            viewModel.addAlbum(newAlbum)
                            //albumList = albumList.sortedBy { it.name } as MutableList<Album> TODO
                        }
                    } else if (result == "NoDelete") {
                        Toast.makeText(context, context.getString(R.string.cant_move), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "IO ex ${it.path}", Toast.LENGTH_SHORT).show()
                    }
                }
// TODO add обновить список альбомов и фото
                mutableState.value = false
                onDismissRequest()
            },
                modifier = modifier)
            {
                Text(text = LocalContext.current.getString(R.string.move))
            }
            Button(onClick = {
                mutableState.value = false
                onDismissRequest()
            },
                modifier = modifier
            ) {
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
fun DeleteDialog(onDismissRequest: () -> Unit, album: Album, needDelete: Boolean = true, mutableState: MutableState<Boolean>,
                 viewModel: AlbumViewModel = AlbumViewModel()) {
    // needDelete true - удалить альбом, false - удалить картинку
    val context = LocalContext.current
    val activity = LocalActivity.current
    val modifier = Modifier.fillMaxWidth()
    LaunchedEffect({}) {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            getListAlbums(context, viewModel)
        }
    }
    val albumList by viewModel.albumList.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth()
    )
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier) {
            Text(text = context.getString(R.string.delete))
            if (needDelete) Text(text = "${context.getString(R.string.delete_album_text)} ${album.name}?")
            Button(
                onClick = {
                    if (needDelete) {
                        showDeleteAlbumMessage(context, album)
                        albumList
                    }
                    else {
                        deleteMediaFile(context, activity!!)
                    }
// TODO add удалить фото или альбом из списка и обновить список альбомов
                    mutableState.value = false
                    onDismissRequest()
                },
                modifier = modifier
            ) {
                Text(text = context.getString(R.string.delete),
                    color = Color.Red)
            }
            Button(
                onClick = {
                    mutableState.value = false
                    onDismissRequest()
                },
                modifier = modifier
            ) {
                Text(text = context.getString(R.string.cancel))
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameAlbumDialog(onDismissRequest: () -> Unit, album: Album, mutableState: MutableState<Boolean>,
                      viewModel: AlbumViewModel = AlbumViewModel()
) {

    val context = LocalContext.current
    var albumName by rememberSaveable { mutableStateOf(album.name) }
    val modifier = Modifier.fillMaxWidth()
    LaunchedEffect({}) {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            getListAlbums(context, viewModel)
        }
    }
    val albumList by viewModel.albumList.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth()
    )
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier) {
            Text(text = context.getString(R.string.rename))
            OutlinedTextField(
                value = albumName,
                onValueChange = { albumName = it },
                label = {"Enter text"},
                placeholder = {"Hello World"},
                supportingText = {
                    Text("Минимум 6 символов")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    mutableState.value = false
                    onDismissRequest()
                })
                {
                    Text(text = context.getString(R.string.cancel),
                        color = Color.Red)
                }
                Button( onClick = {
                        if (albumName.isNotEmpty()) {
                            showRenameAlbumMessage(context, album, albumName)
                            val albumsNames = albumList.map { it.name }
                            var newAlbum = albumList[0]
                            albumList.forEachIndexed { i, album ->
                                if (album.name != albumsNames[i]) {
                                    newAlbum = album
                                    albumList[i].bID = album.bID
                                }
                            }
                            if (newAlbum != albumList[0]) {
                                // albumList = albumList.sortedBy { it.name } as MutableList<Album> TODO
                            } else {
                                Toast.makeText(context, context.getString(R.string.cant_rename_album), Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, context.getString(R.string.enter_name), Toast.LENGTH_SHORT).show()
                        }
                        Log.i("NEWNAME", "$albumName")
// TODO add обновить альбом в списке (нужно разобраться, есть баги)
                    mutableState.value = false
                    onDismissRequest()
                })
                {
                    Text(text = LocalContext.current.getString(R.string.ok),
                        color = Color.Blue)
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentateDialog(onDismissRequest: () -> Unit, mutableState: MutableState<Boolean>) {

    val context = LocalContext.current
    var commentText by rememberSaveable { mutableStateOf("") }
    val modifier = Modifier.fillMaxWidth()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth()
    )
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier) {
            Text(text = context.getString(R.string.commentate))
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = {"Enter text"},
                placeholder = {"Hello World"},
                supportingText = {
                    Text("Минимум 6 символов")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    mutableState.value = false
                    onDismissRequest()
                })
                {
                    Text(text = context.getString(R.string.cancel),
                        color = Color.Red)
                }
                Button( onClick = {

// TODO add обновить или добавить комментарий
                    mutableState.value = false
                    onDismissRequest()
                })
                {
                    Text(text = LocalContext.current.getString(R.string.ok),
                        color = Color.Blue)
                }
            }
        }
    }

}