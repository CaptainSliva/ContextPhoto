package com.contextphoto.dialog

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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contextphoto.R
import com.contextphoto.data.Album
import com.contextphoto.data.Destination
import com.contextphoto.data.Picture
import com.contextphoto.db.Comment
import com.contextphoto.db.CommentDatabase
import com.contextphoto.ui.AlbumViewModel
import com.contextphoto.ui.FullscreenViewModel
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.utils.FunctionsBitmap.getThumbnail
import com.contextphoto.utils.FunctionsBitmap.md5
import com.contextphoto.utils.FunctionsDialogs.mediaPicker
import com.contextphoto.utils.FunctionsDialogs.showCreateAlbumMessage
import com.contextphoto.utils.FunctionsDialogs.showDeleteAlbumMessage
import com.contextphoto.utils.FunctionsDialogs.showRenameAlbumMessage
import com.contextphoto.utils.FunctionsFiles.moveMediaToAlbum
import com.contextphoto.utils.FunctionsMediaStore.copyMediaToAlbum
import com.contextphoto.utils.FunctionsMediaStore.deleteMediaFile
import com.contextphoto.utils.FunctionsMediaStore.getNewAlbum
import com.contextphoto.utils.FunctionsUri.handleSelectedMedia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlbumDialog(
    onDismissRequest: () -> Unit,
    mutableState: MutableState<Boolean>,
    albumViewModel: AlbumViewModel,
) {
    val context = LocalContext.current
    val modifier = Modifier.fillMaxWidth()
    val showCopyMoveDialog = rememberSaveable { mutableStateOf(false) }
    var albumName by rememberSaveable { mutableStateOf("") }
    var listUri by rememberSaveable { mutableStateOf<List<Uri>?>(null) }
    val pickVisualMedia =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(),
        ) { uris ->
            if (uris.isNotEmpty()) {
                // mutableState.value = false
                showCopyMoveDialog.value = true
                listUri = uris.toList()
            }
        }
    val pickMediaLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = handleSelectedMedia(result.data)
                // mutableState.value = false
                showCopyMoveDialog.value = true
                listUri = data
            }
        }
    AnimatedVisibility(
        visible = showCopyMoveDialog.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        CopyMoveDialog(onDismissRequest, mutableState, listUri!!, albumName, {}, showCopyMoveDialog, createAlbum = true)
    }

    ModalBottomSheet(
        onDismissRequest =
            {
                mutableState.value = false
                onDismissRequest()
            },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier,
        ) {
            Text(text = context.getString(R.string.create_album_text))
            OutlinedTextField(
                value = albumName,
                onValueChange = { if (it.length <= 52) albumName = it },
                supportingText = {
                    Text(context.getString(R.string.max_name_album_chars))
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(onClick = {
                    mutableState.value = false
                    onDismissRequest()
                }) {
                    Text(
                        text = context.getString(R.string.cancel),
                        color = Color.Red,
                    )
                }
                Button(onClick = {
                    // запуск диалога
                    if (albumName.isNotEmpty()) {
                        showCreateAlbumMessage(context, albumName)
                        mediaPicker(pickMediaLauncher, pickVisualMedia, ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                    } else {
                        Toast.makeText(context, context.getString(R.string.enter_name), Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(text = LocalContext.current.getString(R.string.ok))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CopyMoveDialog(
    createAlbumDismiss: () -> Unit,
    createAlbumState: MutableState<Boolean>,
    listUri: List<Uri>,
    albumName: String,
    onDismissRequest: () -> Unit,
    mutableState: MutableState<Boolean>,
    albumViewModel: AlbumViewModel = hiltViewModel(),
    createAlbum: Boolean = false,
    fromAlbumBid: String = "",
    toAlbumBId: String = "",
    mediaViewModel: MediaViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!
    val modifier = Modifier.fillMaxWidth()
    var findNewAlbumFlag = false
    val coroutineScope = rememberCoroutineScope()

//    LaunchedEffect({}) {
//        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
//            getListAlbums(context, albumViewModel)
//        }
//    }
    val albumList by albumViewModel.albumList.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest =
            {
                mutableState.value = false
                onDismissRequest()
                createAlbumState.value = false
                createAlbumDismiss()
            },
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(text = LocalContext.current.getString(R.string.to_album))
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (createAlbum) {
                            listUri.forEach {
                                if (copyMediaToAlbum(context, it, albumName)) {
                                    if (it == listUri[listUri.size - 1]) {
                                        if (albumName in albumList.map { it.name } && createAlbum) {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Альбом \"$albumName\" уже создан",
                                                    Toast.LENGTH_SHORT,
                                                ).show()
                                        }
                                    } else {
                                        Log.i("NEWNAME", "$albumName")
                                        findNewAlbumFlag = true
                                    }
                                } else {
                                    Toast
                                        .makeText(context, "IO ex ${it.path}", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                            if (findNewAlbumFlag) getNewAlbum(context, albumName, albumViewModel)
                        } else {
                            listUri.forEach { copyMediaToAlbum(context, it, albumName) }
                            mediaViewModel.copyMediaToAlbum(toAlbumBId, listUri.size - 1)
                        }

                        mutableState.value = false
                        onDismissRequest()
                        createAlbumState.value = false
                        createAlbumDismiss()
                    }
                },
                modifier = modifier,
            ) {
                Text(text = LocalContext.current.getString(R.string.copy))
            }
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (createAlbum) {
                            listUri.forEach {
                                val result = moveMediaToAlbum(context, activity, it, albumName)
                                if (result == "Complete") {
                                    if (it == listUri[listUri.size - 1]) {
                                        if (albumName in albumList.map { it.name }) {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Альбом \"$albumName\" уже создан",
                                                    Toast.LENGTH_SHORT,
                                                ).show()
                                        } else {
                                            Log.i("NEWNAME", "$albumName")
                                            findNewAlbumFlag = true
                                        }
                                    }
                                } else if (result == "NoDelete") {
                                    Toast
                                        .makeText(
                                            context,
                                            context.getString(R.string.cant_move),
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                } else {
                                    Toast
                                        .makeText(context, "IO ex ${it.path}", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                            if (findNewAlbumFlag) getNewAlbum(context, albumName, albumViewModel)
                        } else {
                            listUri.forEach { moveMediaToAlbum(context, activity, it, albumName) }
                            mediaViewModel.moveMediaToAlbum(
                                toAlbumBId,
                                fromAlbumBid,
                                listUri.size - 1,
                            )
                        }

                        mutableState.value = false
                        onDismissRequest()
                        createAlbumState.value = false
                        createAlbumDismiss()
                    }
                },
                modifier = modifier,
            ) {
                Text(text = LocalContext.current.getString(R.string.move))
            }
            Button(
                onClick = {
                    mutableState.value = false
                    onDismissRequest()
                    createAlbumState.value = false
                    createAlbumDismiss()
                },
                modifier = modifier,
            ) {
                Text(
                    text = LocalContext.current.getString(R.string.cancel),
                    color = Color.Red,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseAlbumDialog(
    onDismissRequest: () -> Unit,
    dialogVisibility: MutableState<Boolean>,
    listSelectedMedia: List<Picture>,
    albumViewModel: AlbumViewModel = hiltViewModel(),
) {
    albumViewModel.getAlbumList()
    val albumList by albumViewModel.albumList.collectAsStateWithLifecycle()
    var selectAlbum by remember { mutableStateOf<Album?>(null) }
    val showCopyMoveDialog = remember { mutableStateOf(false) }

    Log.d("TAG_LIST", albumList.toString())
//    Dialog(
//        onDismissRequest = {
//            onDismissRequest()
//            dialogVisibility.value = false
//        },
//    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            LazyColumn(modifier = Modifier.padding(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp)) {
                items(items = albumList) { album ->
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 3.dp)
                                .clickable(
                                    onClick = {
                                        showCopyMoveDialog.value = true
                                        selectAlbum = album
                                        // TODO fixme не копируются фотки в альбомы созданные не мной
                                    },
                                ),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        )
                        {
                            Image(
                                bitmap = album.thumbnail.asImageBitmap(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(65.dp),
                            )
                            Column(
                                modifier = Modifier.padding(start = 6.dp),
                            ) {
                                Text(
                                    text = album.name,
                                    maxLines = 2,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
//    }
    AnimatedVisibility(visible = showCopyMoveDialog.value) {
        CopyMoveDialog(
            onDismissRequest,
            dialogVisibility,
            listSelectedMedia.map { it.uri },
            selectAlbum!!.name,
            {},
            showCopyMoveDialog,
            fromAlbumBid = listSelectedMedia[0].bID,
            toAlbumBId = selectAlbum!!.bID,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAlbumDialog(
    onDismissRequest: () -> Unit,
    mutableState: MutableState<Boolean>,
    viewModel: AlbumViewModel,
) {
    val context = LocalContext.current
    val modifier = Modifier.fillMaxWidth()

    val selectAlbum by viewModel.selectedAlbum.collectAsStateWithLifecycle()
    val album = remember { selectAlbum?.copy() }
    val albumName = remember { selectAlbum!!.name }
    val albumPath = remember { selectAlbum!!.path }

    ModalBottomSheet(
        onDismissRequest =
            {
                mutableState.value = false
                onDismissRequest()
            },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier,
        ) {
            Text(text = context.getString(R.string.delete))
            Text(text = "${context.getString(R.string.delete_album_text)} $albumName}?")
            Button(
                onClick = {
                    showDeleteAlbumMessage(context, albumName, albumPath)
                    viewModel.deleteAlbum(album)
                    mutableState.value = false
                    onDismissRequest()
                },
                modifier = modifier,
            ) {
                Text(
                    text = context.getString(R.string.delete),
                    color = Color.Red,
                )
            }
            Button(
                onClick = {
                    mutableState.value = false
                    onDismissRequest()
                },
                modifier = modifier,
            ) {
                Text(text = context.getString(R.string.cancel))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteMediaDialog(
    onDismissRequest: () -> Unit,
    mutableState: MutableState<Boolean>,
    currentDestination: String,
    bID: String,
    mediaViewModel: MediaViewModel = hiltViewModel(),
    fullscreenViewModel: FullscreenViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!
    val modifier = Modifier.fillMaxWidth()
    val listSelectedMedia by mediaViewModel.listSelectedMedia.collectAsStateWithLifecycle()
    val listMedia by fullscreenViewModel.listMedia.collectAsStateWithLifecycle()
    val pos by fullscreenViewModel.mediaPosition.collectAsStateWithLifecycle()

    if (listSelectedMedia.isEmpty() && listMedia.isEmpty()) {
        mutableState.value = false
        onDismissRequest()
    }
    ModalBottomSheet(
        onDismissRequest =
            {
                mutableState.value = false
                onDismissRequest()
            },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier,
        ) {
            Text(text = context.getString(R.string.delete))
            Button(
                onClick = {
                    mediaViewModel.deleteMediaFromAlbum(bID, listSelectedMedia.size)
                    when (currentDestination) {
                        Destination.Pictures().route -> {
                            listSelectedMedia.forEach {
                                if (deleteMediaFile(context, activity, it.uri)) {
                                    mediaViewModel.deletePicture(it)
                                }
                            }
                        }

                        Destination.FullScreenImg().route -> {
                            if (deleteMediaFile(context, activity, listMedia[pos].uri)) {

//                                fullscreenViewModel.deleteActionChange(true)
//                                fullscreenViewModel.updateMediaPosition()
                                fullscreenViewModel.deletePicture(listMedia[pos])
//                                when {
//                                    (listMedia.size - 1 == pos) -> {
//                                        fullscreenViewModel.updateMediaPosition(pos - 1)
//                                    }
//                                    (pos == 0) -> {
//                                        fullscreenViewModel.updateMediaPosition(pos+1)
//                                    }
////                                    else -> {
////                                        fullscreenViewModel.updateMediaPosition(pos)
////                                        println("FNEEEE")
////                                        println("${fullscreenViewModel.mediaPosition.value}")
////                                    }
//                                }
                            }
                        }
                    }

                    mutableState.value = false
                    onDismissRequest()
                },
                modifier = modifier,
            ) {
                Text(
                    text = context.getString(R.string.delete),
                    color = Color.Red,
                )
            }
            Button(
                onClick = {
                    mutableState.value = false
                    onDismissRequest()
                },
                modifier = modifier,
            ) {
                Text(text = context.getString(R.string.cancel))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameAlbumDialog( // TODO fixme (не работает с альбомами которые не мои)
    onDismissRequest: () -> Unit,
    mutableState: MutableState<Boolean>,
    viewModel: AlbumViewModel,
) {
    val context = LocalContext.current
    val album by viewModel.selectedAlbum.collectAsStateWithLifecycle()
    var albumName by rememberSaveable { mutableStateOf(album!!.name) }
    val modifier = Modifier.fillMaxWidth()

    ModalBottomSheet(
        onDismissRequest =
            {
                mutableState.value = false
                onDismissRequest()
            },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier,
        ) {
            Text(text = context.getString(R.string.rename))
            OutlinedTextField(
                value = albumName,
                onValueChange = { albumName = it },
                label = { "Enter text" },
                placeholder = { "Hello World" },
                supportingText = {
                    Text("")
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(onClick = {
                    mutableState.value = false
                    onDismissRequest()
                }) {
                    Text(
                        text = context.getString(R.string.cancel),
                        color = Color.Red,
                    )
                }
                Button(onClick = {
                    if (albumName.isNotEmpty()) {
                        showRenameAlbumMessage(context, album!!, albumName)
                        viewModel.updateAlbum(Album(album!!.bID, albumName, album!!.itemsCount, album!!.thumbnail, album!!.path))
                        mutableState.value = false
                        onDismissRequest()
                    } else {
                        Toast.makeText(context, context.getString(R.string.enter_name), Toast.LENGTH_SHORT).show()
                    }
                    Log.i("NEWNAME", "$albumName")
                }) {
                    Text(
                        text = LocalContext.current.getString(R.string.ok),
                        color = Color.Blue,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentateDialog(
    onDismissRequest: () -> Unit,
    mutableState: MutableState<Boolean>,
    media: Picture,
) {
    val oldComment = remember { mutableStateOf("") }
    val context = LocalContext.current
    var commentText by rememberSaveable { mutableStateOf("") }
    val modifier = Modifier.fillMaxWidth()

    val db = CommentDatabase.getDatabse(context).commentDao()
    println(commentText)

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            commentText = db.findImageByHash(md5(getThumbnail(context, media.uri)))?.image_comment?.trim() ?: commentText.trim()
            oldComment.value = commentText
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = colorResource(R.color.medium_transparant_black)
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 70.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = context.getString(R.string.commentate))
                    Image(
                        contentDescription = null,
                        bitmap = media.thumbnail.asImageBitmap(),
                        contentScale = ContentScale.Crop
                    )
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        modifier = modifier
                            .heightIn(max = 160.dp)
                            .padding(horizontal = 16.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = modifier
                            .padding(horizontal = 16.dp),
                    ) {
                        Button(onClick = {
                            mutableState.value = false
                            onDismissRequest()
                        }) {
                            Text(
                                text = context.getString(R.string.cancel),
                                color = Color.Red,
                            )
                        }

                        Button(onClick = {
                            CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                                val commentText = commentText.trim()
                                val imagHash = md5(getThumbnail(context, media.uri))
                                when {
                                    commentText.length == 0 && oldComment.value.length != 0 -> {
                                        db.deleteCommentByHash(imagHash)
                                    }

                                    commentText.length != 0 && oldComment.value.length == 0 -> {
                                        db.addComment(
                                            Comment(
                                                0,
                                                media.uri.toString(),
                                                md5(getThumbnail(context, media.uri)),
                                                commentText,
                                            ),
                                        )
                                    }

                                    commentText.length != 0 && oldComment.value.length != 0 -> {
                                        db.replaceCommentByHash(
                                            md5(getThumbnail(context, media.uri)),
                                            commentText,
                                        )
                                    }
                                }
                            }

                            mutableState.value = false
                            onDismissRequest()
                        }) {
                            Text(
                                text = LocalContext.current.getString(R.string.ok),
                                color = Color.White,
                            )
                        }
                    }
                }
            }
        }
    }
}
