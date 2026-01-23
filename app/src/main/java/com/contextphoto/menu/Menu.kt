package com.contextphoto.menu

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contextphoto.R
import com.contextphoto.ui.AlbumViewModel
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.data.Picture
import com.contextphoto.dialog.CommentateDialog
import com.contextphoto.dialog.DeleteAlbumDialog
import com.contextphoto.dialog.RenameAlbumDialog
import kotlin.collections.isNotEmpty
import kotlin.collections.map
import androidx.navigation.NavController
import com.contextphoto.data.Destination
import com.contextphoto.dialog.ChooseAlbumDialog
import com.contextphoto.dialog.DeleteMediaDialog
import com.contextphoto.ui.FullscreenViewModel


@Composable
fun MainDropdownMenu(navController: NavController) {
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
                    text = { Text(context.getString(R.string.menu_settings)) },
                    onClick = {
                        navController.navigate(Destination.SETTINGS.route)
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text(context.getString(R.string.menu_search_photo)) },
                    onClick = {
                        navController.navigate(Destination.SEARCH_PHOTO.route)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable// ListAlbums переименовать, удалить
fun PopupMenuAlbumScreen(onDismissRequest: () -> Unit, mutableState: MutableState<Boolean>, albumViewModel: AlbumViewModel) {
    val renameAlbumDialogVisible = rememberSaveable { mutableStateOf(false) }
    val deleteDialogVisible = rememberSaveable { mutableStateOf(false) }
    Box {
        DropdownMenu(
            expanded = mutableState.value,
            onDismissRequest = {
                mutableState.value = false
                onDismissRequest()
            }
        ) {
            DropdownMenuItem(
                text = { Text(LocalContext.current.getString(R.string.rename)) },
                onClick = {
                    renameAlbumDialogVisible.value = true
                }
            )
            DropdownMenuItem(
                text = { Text(LocalContext.current.getString(R.string.delete)) },
                onClick = {
                    deleteDialogVisible.value = true
                }
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
            albumViewModel
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
            albumViewModel
        )
    }

}

@Composable// ListMedia поделиться, в альбом, комментировать, удалить
fun BottomMenuPictureScreen(mediaViewModel: MediaViewModel) {
    val toAlbumDialogVisible = rememberSaveable { mutableStateOf(false) }
    val commentateDialogVisible = rememberSaveable { mutableStateOf(false) }
    val deleteDialogVisible = rememberSaveable { mutableStateOf(false) }
    val listSelectedMedia by mediaViewModel.listSelectedMedia.collectAsStateWithLifecycle()

    AnimatedVisibility(
        visible = toAlbumDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        ChooseAlbumDialog({}, toAlbumDialogVisible, listSelectedMedia)
    }
    AnimatedVisibility(
        visible = commentateDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        CommentateDialog({}, commentateDialogVisible, listSelectedMedia)
    }
    AnimatedVisibility(
        visible = deleteDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        DeleteMediaDialog(
            {},
            deleteDialogVisible,
            Destination.PICTURES.route,
            mediaViewModel = mediaViewModel
        )
    }

    Column(modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom) {
        Row(
            modifier =
                Modifier
                    .background(Color.Black)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            ButtonShare(listSelectedMedia)
            ButtonToAlbum(listSelectedMedia, toAlbumDialogVisible)
            ButtonCommentate(listSelectedMedia, commentateDialogVisible)
            ButtonDelete(deleteDialogVisible, mediaViewModel)
        }
    }

}

@Composable// FullScreen поделиться, повернуть, комментировать, удалить
fun BottomMenuFullScreen(fullscreenViewModel: FullscreenViewModel) {
    val shareDialogVisible = rememberSaveable { mutableStateOf(false) }
    val commentateDialogVisible = rememberSaveable { mutableStateOf(false) }
    val deleteDialogVisible = rememberSaveable { mutableStateOf(false) }
    val listMedia by fullscreenViewModel.listMedia.collectAsStateWithLifecycle()
    val pos = fullscreenViewModel.mediaPosition.collectAsStateWithLifecycle()
    AnimatedVisibility(
        visible = shareDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {

    }
    AnimatedVisibility(
        visible = commentateDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        CommentateDialog({}, commentateDialogVisible, listOf(listMedia[pos.value]))
    }
    AnimatedVisibility(
        visible = deleteDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        DeleteMediaDialog(
            {},
            deleteDialogVisible,
            Destination.FULLSCREENIMG.route,
            fullscreenViewModel = fullscreenViewModel
        )
    }

    Column(modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom) {
        Row(
            modifier =
                Modifier
                    .background(Color.Black)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            ButtonShare(listOf(listMedia[pos.value]))
            ButtonRotate()
            ButtonCommentate(listOf(listMedia[pos.value]), commentateDialogVisible)
            ButtonDelete(deleteDialogVisible, fullscreenViewModel)

        }
    }

}


// Элементы меню

@Composable
fun ButtonShare(listSelectedMedia: List<Picture>) {
    val context = LocalContext.current
    Column(
        modifier =
            Modifier
                .padding(8.dp, 16.dp)
                .clickable(
                    onClick = {
                        if (listSelectedMedia.isNotEmpty()) {
                            // val sendCommentText = db.findImageByHash(md5(it.thumbnail))
                            val sendIntent = Intent()
                            sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE)
                            sendIntent.setType("*/*")
                            sendIntent.putParcelableArrayListExtra(
                                Intent.EXTRA_STREAM,
                                ArrayList(listSelectedMedia.map { it.uri })
                            )
//                        sendIntent.putExtra(Intent.EXTRA_TEXT, sendCommentText)
                            // context.startActivity(sendIntent)
                            context.startActivity(Intent.createChooser(sendIntent, null))
                        }
                    }
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
fun ButtonCommentate(listSelectedMedia: List<Picture>, commentateDialogVisible: MutableState<Boolean>) {
    val context = LocalContext.current
    Column(
        modifier =
            Modifier
                .padding(8.dp, 16.dp)
                .clickable(
                    onClick = {
                        commentateDialogVisible.value = true
                    }
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
fun ButtonDelete(deleteDialogVisible: MutableState<Boolean>, mediaViewModel: MediaViewModel) {
    val context = LocalContext.current
    Column(
        modifier =
            Modifier
                .padding(8.dp, 16.dp)
                .clickable(
                    onClick = {
                        deleteDialogVisible.value = true
                    }
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
fun ButtonDelete(deleteDialogVisible: MutableState<Boolean>, fullscreenViewModel: FullscreenViewModel) {
    val context = LocalContext.current
    Column(
        modifier =
            Modifier
                .padding(8.dp, 16.dp)
                .clickable(
                    onClick = {
                        deleteDialogVisible.value = true
                    }
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

    Column(
        modifier = Modifier
            .padding(8.dp, 16.dp)
            .clickable(
                onClick = {
                    if (activity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
                    if (activity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                }
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
fun ButtonToAlbum(listSelectedMedia: List<Picture>, toAlbumDialogVisible: MutableState<Boolean>) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(8.dp, 16.dp)
            .clickable(
                onClick = {
                    toAlbumDialogVisible.value = true
                }
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



