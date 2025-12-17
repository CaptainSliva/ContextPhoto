package com.contextphoto.menu

import android.content.Intent
import android.graphics.Bitmap
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Delete
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
import com.contextphoto.data.Album
import com.contextphoto.data.AlbumViewModel
import com.contextphoto.data.MediaViewModel
import com.contextphoto.data.Picture
import com.contextphoto.dialog.CommentateDialog
import com.contextphoto.dialog.DeleteAlbumDialog
import com.contextphoto.dialog.RenameAlbumDialog
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import kotlin.collections.isNotEmpty
import kotlin.collections.map
import androidx.core.graphics.createBitmap
import androidx.navigation.NavController
import com.contextphoto.data.Destination


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
                Icon(Icons.Default.MoreVert, contentDescription = "More options")
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

@Composable// TODO add? ListAlbums переименовать, удалить
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

@Composable// TODO add? ListMedia поделиться, в альбом, комментировать, удалить
fun BottomMenuPictureScreen(mediaViewModel: MediaViewModel) {
    val toAlbumDialogVisible = rememberSaveable { mutableStateOf(false) }
    val commentateDialogVisible = rememberSaveable { mutableStateOf(false) }
    val deleteDialogVisible = rememberSaveable { mutableStateOf(false) }
    val listMedia by mediaViewModel.listSelectedMedia.collectAsStateWithLifecycle()

    // TODO add share

    AnimatedVisibility(
        visible = toAlbumDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {

    }
    AnimatedVisibility(
        visible = commentateDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        CommentateDialog({}, commentateDialogVisible)
    }
    AnimatedVisibility(
// TODO fixme не работает удаление фото, видимо нужно их удаление в отдельную функцию вынести
        visible = deleteDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {

    }

    Row(
        modifier =
            Modifier
                .background(Color.Black)
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        ButtonShare(listMedia)
        ButtonToAlbum(listMedia, toAlbumDialogVisible)
        ButtonCommentate(listMedia, commentateDialogVisible)
        ButtonDelete(deleteDialogVisible, mediaViewModel)
    }
}

@Composable// TODO add? FullScreen add? поделиться, повернуть, комментировать, удалить
fun BottomMenuFullScreen(mediaViewModel: MediaViewModel) { // TODO add отдельное состояние для этого вида медиа меню
    val shareDialogVisible = rememberSaveable { mutableStateOf(false) }
    val rotateMedia = rememberSaveable { mutableStateOf(false) }
    val commentateDialogVisible = rememberSaveable { mutableStateOf(false) }
    val deleteDialogVisible = rememberSaveable { mutableStateOf(false) }
    val listMedia by mediaViewModel.listSelectedMedia.collectAsStateWithLifecycle() //TODO лист каждый раз очищается и в него добавляется элемент (текущаякартинка на экране)

    AnimatedVisibility(
        visible = shareDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {

    }
    //TODO add ("rotateMedia")
    AnimatedVisibility(
        visible = commentateDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        CommentateDialog({}, commentateDialogVisible)
    }
    AnimatedVisibility(
// TODO fixme не работает удаление фото, видимо нужно их удаление в отдельную функцию вынести
        visible = deleteDialogVisible.value,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {

    }

    Row(
        modifier =
            Modifier
                .background(Color.Black)
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        ButtonShare(listMedia)
        ButtonRotate(rotateMedia)
        ButtonCommentate(listMedia, commentateDialogVisible)
        ButtonDelete(deleteDialogVisible, mediaViewModel)

    }
}


// Элементы меню

@Composable
fun ButtonShare(listSelectedMedia: List<Picture>) {
    val context = LocalContext.current
    Column(
        modifier =
            Modifier.padding(8.dp, 16.dp).combinedClickable(
                onClick = {
                    if (listSelectedMedia.isNotEmpty()) {
                        // val sendCommentText = db.findImageByHash(md5(it.thumbnail))
                        val sendIntent = Intent()
                        sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE)
                        sendIntent.setType("*/*")
                        sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(listSelectedMedia.map { it.uri }))
//                        sendIntent.putExtra(Intent.EXTRA_TEXT, sendCommentText)
                        // context.startActivity(sendIntent)
                        context.startActivity(Intent.createChooser(sendIntent, null))
                    }
                },
                onLongClick = {

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
fun ButtonCommentate(listSelectedMedia: List<Picture>, commentateDialogVisible: MutableState<Boolean>) {
    val context = LocalContext.current
    Column(
        modifier =
            Modifier.padding(8.dp, 16.dp).combinedClickable(
                onClick = {
                    TODO("Запись комментария в БД")
                    commentateDialogVisible.value = true
                },
                onLongClick = {},
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
    val listSelectedMedia by mediaViewModel.listSelectedMedia.collectAsStateWithLifecycle()
    Column(
        modifier =
            Modifier.padding(8.dp, 16.dp).combinedClickable(
                onClick = {
                    TODO("диалог удаления медиа")
                    deleteDialogVisible.value = true
                },
                onLongClick = {},
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
fun ButtonRotate(rotateDialogVisible: MutableState<Boolean>) {
    val context = LocalContext.current
    TODO("реализовать поворот")
    Column(
        modifier = Modifier.padding(8.dp, 16.dp).combinedClickable(
            onClick = {
                TODO("Запуск экрана со списком альбомов, в нём выбирается один альбом, вызывается диалог копировать/переместить и пользователя возвращаю на экран с фотками в котором он был")
                rotateDialogVisible.value = true
            },
            onLongClick = {},
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

@Composable
fun ButtonToAlbum(listSelectedMedia: List<Picture>, toAlbumDialogVisible: MutableState<Boolean>) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.padding(8.dp, 16.dp).combinedClickable(
            onClick = {
                TODO("Запуск экрана со списком альбомов, в нём выбирается один альбом, вызывается диалог копировать/переместить и пользователя возвращаю на экран с фотками в котором он был")
                toAlbumDialogVisible.value = true
            },
            onLongClick = {},
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
