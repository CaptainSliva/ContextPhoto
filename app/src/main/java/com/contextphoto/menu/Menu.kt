package com.contextphoto.menu

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.contextphoto.R
import com.contextphoto.data.navigation.Destination
import com.contextphoto.item.Picture
import com.contextphoto.dialog.ChooseAlbumDialog
import com.contextphoto.dialog.CommentateDialog
import com.contextphoto.dialog.DeleteAlbumDialog
import com.contextphoto.dialog.DeleteMediaDialog
import com.contextphoto.dialog.RenameAlbumDialog
import com.contextphoto.ui.AlbumViewModel
import com.contextphoto.ui.FullscreenViewModel
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.utils.FunctionsBitmap.md5
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Composable
fun MainDropdownMenu(navController: NavController, onClickEvent: () -> Unit = {}) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        Box {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(context.getString(R.string.menu_search_photo)) },
                    onClick = {
                        onClickEvent()
                        navController.navigate(Destination.SearchPhoto().route)
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text(context.getString(R.string.menu_settings)) },
                    onClick = {
                        onClickEvent()
                        navController.navigate(Destination.Settings().route)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable // ListAlbums переименовать, удалить
fun PopupMenuAlbumScreen(
    onDismissRequest: () -> Unit,
    mutableState: MutableState<Boolean>,
    albumViewModel: AlbumViewModel,
) {
    val renameAlbumDialogVisible = rememberSaveable { mutableStateOf(false) }
    val deleteDialogVisible = rememberSaveable { mutableStateOf(false) }
    Box {
        DropdownMenu(
            expanded = mutableState.value,
            onDismissRequest = {
                mutableState.value = false
                onDismissRequest()
            },
        ) {
            DropdownMenuItem(
                text = { Text(LocalContext.current.getString(R.string.rename)) },
                onClick = {
                    renameAlbumDialogVisible.value = true
                },
            )
            DropdownMenuItem(
                text = { Text(LocalContext.current.getString(R.string.delete)) },
                onClick = {
                    deleteDialogVisible.value = true
                },
            )
        }
    }

    AnimatedVisibility(
        visible = renameAlbumDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        RenameAlbumDialog(
            {},
            renameAlbumDialogVisible,
            albumViewModel,
        )
    }
    AnimatedVisibility(
        visible = deleteDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        DeleteAlbumDialog(
            {},
            deleteDialogVisible,
            albumViewModel,
        )
    }
}

@Composable // ListMedia поделиться, в альбом, комментировать, удалить
fun BottomMenuPictureScreen(mediaViewModel: MediaViewModel) {
    val toAlbumDialogVisible = rememberSaveable { mutableStateOf(false) }
    val commentateDialogVisible = rememberSaveable { mutableStateOf(false) }
    val deleteDialogVisible = rememberSaveable { mutableStateOf(false) }
    val commentsStateDialogVisible = rememberSaveable { mutableStateListOf<MutableState<Boolean>>() }
    val listSelectedMedia by mediaViewModel.listSelectedMedia.collectAsStateWithLifecycle()

    AnimatedVisibility(
        visible = toAlbumDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        ChooseAlbumDialog({}, toAlbumDialogVisible, listSelectedMedia)
    }

    if (commentateDialogVisible.value) {
        commentsStateDialogVisible.clear()
        for (i in 0..<listSelectedMedia.size) {
            commentsStateDialogVisible.add(remember { mutableStateOf(true) })
        }
        if (commentsStateDialogVisible.all { !it.value }) commentateDialogVisible.value = false
    } else {
        commentsStateDialogVisible.clear()
    }
    for (i in 0..<commentsStateDialogVisible.size) {
        AnimatedVisibility(
            visible = commentsStateDialogVisible[i].value,
            enter = slideInVertically(),
            exit = slideOutVertically(),
        ) {
            CommentateDialog({}, commentsStateDialogVisible[i], listSelectedMedia[i])
        }
    }

    AnimatedVisibility(
        visible = deleteDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        if (listSelectedMedia.isNotEmpty())
        DeleteMediaDialog(
            {},
            deleteDialogVisible,
            Destination.Pictures().route,
            listSelectedMedia[0].bID,
            mediaViewModel = mediaViewModel,
        )
    }

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        Row(
            modifier =
                Modifier
                    .background(Color.Black)
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center,
        ) {
            ButtonShare(listSelectedMedia)
            ButtonToAlbum(toAlbumDialogVisible)
            ButtonCommentate(commentateDialogVisible)
            ButtonDelete(deleteDialogVisible)
        }
    }
}

@Composable // ListMedia поделиться, комментировать, удалить
fun BottomMenuSearchPictureScreen(mediaViewModel: MediaViewModel) {
    val commentateDialogVisible = rememberSaveable { mutableStateOf(false) }
    val deleteDialogVisible = rememberSaveable { mutableStateOf(false) }
    val commentsStateDialogVisible = rememberSaveable { mutableStateListOf<MutableState<Boolean>>() }
    val listSelectedMedia by mediaViewModel.listSelectedMedia.collectAsStateWithLifecycle()


    if (commentateDialogVisible.value) {
        commentsStateDialogVisible.clear()
        for (i in 0..<listSelectedMedia.size) {
            commentsStateDialogVisible.add(remember { mutableStateOf(true) })
        }
        if (commentsStateDialogVisible.all { !it.value }) commentateDialogVisible.value = false
    } else {
        commentsStateDialogVisible.clear()
    }
    for (i in 0..<commentsStateDialogVisible.size) {
        AnimatedVisibility(
            visible = commentsStateDialogVisible[i].value,
            enter = slideInVertically(),
            exit = slideOutVertically(),
        ) {
            CommentateDialog({}, commentsStateDialogVisible[i], listSelectedMedia[i])
        }
    }

    AnimatedVisibility(
        visible = deleteDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        DeleteMediaDialog(
            {},
            deleteDialogVisible,
            Destination.Pictures().route,
            listSelectedMedia[0].bID,
            mediaViewModel = mediaViewModel,
        )
    }

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        Row(
            modifier =
                Modifier
                    .background(Color.Black)
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center,
        ) {
            ButtonShare(listSelectedMedia)
            ButtonCommentate(commentateDialogVisible)
            ButtonDelete(deleteDialogVisible)
        }
    }
}

@Composable // FullScreen поделиться, повернуть, комментировать, удалить
fun BottomMenuFullScreen(fullscreenViewModel: FullscreenViewModel) {
    val shareDialogVisible = rememberSaveable { mutableStateOf(false) }
    val commentateDialogVisible = rememberSaveable { mutableStateOf(false) }
    val deleteDialogVisible = rememberSaveable { mutableStateOf(false) }
    val listMedia by fullscreenViewModel.listMedia.collectAsStateWithLifecycle()
    val pos = fullscreenViewModel.mediaPosition.collectAsStateWithLifecycle()
    val commentText = remember { mutableStateOf("") }

    if (shareDialogVisible.value) {
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                commentText.value =
                    fullscreenViewModel.db
                        .findImageByHash(md5(listMedia[pos.value].thumbnail))
                        ?.image_comment
                        ?.trim()
                        ?: commentText.value.trim()
            }
        }
    }

    AnimatedVisibility(
        visible = commentateDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        CommentateDialog({}, commentateDialogVisible, listMedia[pos.value])
    }

    AnimatedVisibility(
        visible = deleteDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        DeleteMediaDialog(
            {},
            deleteDialogVisible,
            Destination.FullScreenImg().route,
            listMedia[0].bID,
            fullscreenViewModel = fullscreenViewModel,
        )
    }

    Column(
        modifier = Modifier.fillMaxHeight(), // if (!isVideo.value) Modifier.fillMaxHeight() else Modifier,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Row(
            modifier =
                Modifier
                    .background(Color.Black)
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center,
        ) {
            ButtonShare(listOf(listMedia[pos.value]), commentText.value)
            ButtonRotate()
            ButtonCommentate(commentateDialogVisible)
            ButtonDelete(deleteDialogVisible, fullscreenViewModel)
        }
    }
}

@Composable // FullScreen поделиться, повернуть, комментировать, удалить
fun BottomMenuFullScreenVideo(fullscreenViewModel: FullscreenViewModel) {
    val shareDialogVisible = rememberSaveable { mutableStateOf(false) }
    val commentateDialogVisible = rememberSaveable { mutableStateOf(false) }
    val deleteDialogVisible = rememberSaveable { mutableStateOf(false) }
    val listMedia by fullscreenViewModel.listMedia.collectAsStateWithLifecycle()
    val pos = fullscreenViewModel.mediaPosition.collectAsStateWithLifecycle()
    val commentText = remember { mutableStateOf("") }

    if (shareDialogVisible.value) {
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                commentText.value =
                    fullscreenViewModel.db
                        .findImageByHash(md5(listMedia[pos.value].thumbnail))
                        ?.image_comment
                        ?.trim()
                        ?: commentText.value.trim()
            }
        }
    }

    AnimatedVisibility(
        visible = commentateDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        CommentateDialog({}, commentateDialogVisible, listMedia[pos.value])
    }
    AnimatedVisibility(
        visible = deleteDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        DeleteMediaDialog(
            {},
            deleteDialogVisible,
            Destination.FullScreenImg().route,
            listMedia[0].bID,
            fullscreenViewModel = fullscreenViewModel,
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(), // if (!isVideo.value) Modifier.fillMaxHeight() else Modifier,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Row(
            modifier =
                Modifier
                    .background(Color.Black)
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center,
        ) {
            ButtonShare(listOf(listMedia[pos.value]), commentText.value)
            ButtonRotate()
            ButtonCommentate(commentateDialogVisible)
            ButtonDelete(deleteDialogVisible, fullscreenViewModel)
        }
    }
}

// Элементы меню

@Composable
fun ButtonShare(
    listSelectedMedia: List<Picture>,
    commentText: String = "",
) {
    val context = LocalContext.current
    Column(
        modifier =
            Modifier
                .padding(8.dp, 16.dp)
                .clickable(
                    onClick = {
                        if (listSelectedMedia.isNotEmpty()) {
                            when (commentText != "") {
                                true -> {
                                    val sendIntent = Intent()
                                    sendIntent.setAction(Intent.ACTION_SEND)
                                    sendIntent.setType("*/*")
                                    sendIntent.putExtra(
                                        Intent.EXTRA_STREAM,
                                        listSelectedMedia[0].uri,
                                    )
                                    sendIntent.putExtra(
                                        Intent.EXTRA_TEXT,
                                        commentText,
                                    )
                                    context.startActivity(Intent.createChooser(sendIntent, null))
                                }

                                else -> {
                                    val sendIntent = Intent()
                                    sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE)
                                    sendIntent.setType("*/*")
                                    sendIntent.putParcelableArrayListExtra(
                                        Intent.EXTRA_STREAM,
                                        ArrayList(listSelectedMedia.map { it.uri }),
                                    )
                                    context.startActivity(Intent.createChooser(sendIntent, null))
                                }
                            }
                        }
                    },
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            Icons.Outlined.Share,
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White),
        )
        Text(
            text = context.getString(R.string.share),
            style = MaterialTheme.typography.labelSmall,
            color = colorResource(R.color.white),
        )
    }
}

@Composable
fun ButtonCommentate(commentateDialogVisible: MutableState<Boolean>) {
    val context = LocalContext.current
    Column(
        modifier =
            Modifier
                .padding(8.dp, 16.dp)
                .clickable(
                    onClick = {
                        commentateDialogVisible.value = true
                    },
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            Icons.Outlined.Create,
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White),
        )
        Text(
            text = context.getString(R.string.commentate),
            style = MaterialTheme.typography.labelSmall,
            color = colorResource(R.color.white),
        )
    }
}

@Composable
fun ButtonDelete(deleteDialogVisible: MutableState<Boolean>) {
    val context = LocalContext.current
    Column(
        modifier =
            Modifier
                .padding(8.dp, 16.dp)
                .clickable(
                    onClick = {
                        deleteDialogVisible.value = true
                    },
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            Icons.Outlined.Delete,
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White),
        )
        Text(
            text = context.getString(R.string.delete),
            style = MaterialTheme.typography.labelSmall,
            color = colorResource(R.color.white),
        )
    }
}

@Composable
fun ButtonDelete(
    deleteDialogVisible: MutableState<Boolean>,
    fullscreenViewModel: FullscreenViewModel,
) {
    val context = LocalContext.current
    Column(
        modifier =
            Modifier
                .padding(8.dp, 16.dp)
                .clickable(
                    onClick = {
                        deleteDialogVisible.value = true
                    },
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            Icons.Outlined.Delete,
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White),
        )
        Text(
            text = context.getString(R.string.delete),
            style = MaterialTheme.typography.labelSmall,
            color = colorResource(R.color.white),
        )
    }
}

@Composable
fun ButtonRotate() {
    val context = LocalContext.current
    val activity = context as Activity
    val configuration = LocalConfiguration.current

    Column(
        modifier =
            Modifier
                .padding(8.dp, 16.dp)
                .clickable(
                    onClick = {
                        when (configuration.orientation) {
                            Configuration.ORIENTATION_LANDSCAPE -> {
                                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            }

                            Configuration.ORIENTATION_PORTRAIT -> {
                                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            }
                        }
                    },
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            Icons.Outlined.Refresh,
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White),
        )
        Text(
            text = context.getString(R.string.rotate),
            style = MaterialTheme.typography.labelSmall,
            color = colorResource(R.color.white),
        )
    }
}

@Composable
fun ButtonToAlbum(toAlbumDialogVisible: MutableState<Boolean>) {
    val context = LocalContext.current
    Column(
        modifier =
            Modifier
                .padding(8.dp, 16.dp)
                .clickable(
                    onClick = {
                        toAlbumDialogVisible.value = true
                    },
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            Icons.Outlined.Add,
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White),
        )
        Text(
            text = context.getString(R.string.to_album),
            style = MaterialTheme.typography.labelSmall,
            color = colorResource(R.color.white),
        )
    }
}
