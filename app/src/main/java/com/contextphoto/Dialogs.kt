package com.contextphoto

import android.app.Activity
import android.net.Uri
import android.text.Layout
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.contextphoto.data.Album
import com.contextphoto.data.AlbumListViewModel
import com.contextphoto.data.PERMISSION_DELETE_REQUEST_CODE
import com.contextphoto.data.Picture
import com.contextphoto.data.allAlbums
import com.contextphoto.data.listpicture
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import java.io.File
import kotlin.collections.get
import kotlin.text.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlbumDialog(onDismissRequest: () -> Unit, mutableState: MutableState<Boolean>) {
    val context = LocalContext.current
    val modifier = Modifier.fillMaxWidth()
    val showCopyMoveDialog = remember { mutableStateOf(false) }
    var albumName by remember { mutableStateOf("") }
    var listUri by remember { mutableStateOf<List<Uri>?>(null) }
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
        modifier = Modifier.background(Color.DarkGray).fillMaxWidth()
    )
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier) {
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
// TODO add добавить альбом в список
                    mutableState.value = false
                    //onDismissRequest()
                },
                    modifier = Modifier.background(Color.Cyan))
                {
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
                   onDismissRequest: () -> Unit, mutableState: MutableState<Boolean>,
                   viewModel: AlbumListViewModel = AlbumListViewModel()
) {
    val context = LocalContext.current
    val modifier = Modifier.fillMaxWidth()
    LaunchedEffect({}) {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            getListAlbums(context, viewModel)
        }
    }
    val albumList by viewModel.albumList.collectAsState()

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
                            albumList.forEach { album ->
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
                mutableState.value = false
                onDismissRequest()
            },
                modifier = modifier) {
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
                            albumList.forEach { album ->
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
                mutableState.value = false
                onDismissRequest()
            },
                modifier = modifier)
            {
                Text(text = LocalContext.current.getString(R.string.move),
                    color = Color.White)
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
                 viewModel: AlbumListViewModel = AlbumListViewModel()) {

    val context = LocalContext.current
    val activity = LocalActivity.current
    val modifier = Modifier.fillMaxWidth()
    LaunchedEffect({}) {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            getListAlbums(context, viewModel)
        }
    }
    val albumList by viewModel.albumList.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(Color.DarkGray).fillMaxWidth()
    )
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier) {
            Text(text = context.getString(R.string.delete),
                color = Color.White)
            Text(text = "${context.getString(R.string.delete_album_text)} ${album.name}?",
                color = Color.White)
            Button(
                onClick = {
                    if (needDelete) {
                        showDeleteAlbumMessage(context, album)
                        allAlbums = albumList as MutableList<Album>
                    }
                    else {
                        deleteMediaFile(
                            context,
                            activity!!,
                            { intentSender ->
                                startIntentSenderForResult(
                                    activity,
                                    intentSender,
                                    PERMISSION_DELETE_REQUEST_CODE,
                                    null,
                                    0,
                                    0,
                                    0,
                                    null,
                                )
                            },
                        )
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
                Text(text = context.getString(R.string.cancel),
                    color = Color.White)
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameAlbumDialog(onDismissRequest: () -> Unit, album: Album, mutableState: MutableState<Boolean>,
                      viewModel: AlbumListViewModel = AlbumListViewModel()
) {

    val context = LocalContext.current
    var albumName by remember { mutableStateOf(album.name) }
    val modifier = Modifier.fillMaxWidth()
    LaunchedEffect({}) {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            getListAlbums(context, viewModel)
        }
    }
    val albumList by viewModel.albumList.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(Color.DarkGray).fillMaxWidth()
    )
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier) {
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
                            val albumsNames = allAlbums.map { it.name }
                            var newAlbum = allAlbums[0]
                            albumList.forEachIndexed { i, album ->
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
                        Log.i("NEWNAME", "$albumName")
// TODO add обновить альбом в списке
                    mutableState.value = false
                    onDismissRequest()
                },
                    modifier = Modifier.background(Color.Cyan))
                {
                    Text(text = LocalContext.current.getString(R.string.ok),
                        color = Color.White)
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentateDialog(onDismissRequest: () -> Unit, mutableState: MutableState<Boolean>) {

    val context = LocalContext.current
    var commentText by remember { mutableStateOf("") }
    val modifier = Modifier.fillMaxWidth()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(Color.DarkGray).fillMaxWidth()
    )
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier) {
            Text(text = context.getString(R.string.create_album_text),
                color = Color.White)
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
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
                },
                    modifier = Modifier.background(Color.Cyan))
                {
                    Text(text = LocalContext.current.getString(R.string.ok),
                        color = Color.White)
                }
            }
        }
    }

}