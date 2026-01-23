package com.contextphoto.ui.screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.contextphoto.ShowBottomMenu
import com.contextphoto.data.Destination
import com.contextphoto.db.CommentDatabase
import com.contextphoto.item.PictureItem
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.utils.FunctionsBitmap.md5
import com.contextphoto.utils.FunctionsMediaStore.getImageDate
import com.google.android.play.integrity.internal.f
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable // TODO fixme При закрытии экрана и быстром нажатии на место где была картинка - открывается картинка, хотя на экране её уже нет
fun PicturesScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    bID: String,
    mediaViewModel: MediaViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val db = CommentDatabase.getDatabse(context).commentDao()
    val haveComment = rememberSaveable { mutableStateOf(false) }
    //mediaViewModel.loadPictureList(albumBid)
    mediaViewModel.loadPictureList(bID)
    val listMedia by mediaViewModel.listMedia.collectAsStateWithLifecycle()
    Log.d("Pictures", listMedia.toString())

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
    ) {
        items(items = listMedia) { media ->
            LaunchedEffect(Unit) {
                CoroutineScope(Dispatchers.IO).launch {
                    haveComment.value = (db.findImageByHash(md5(media.thumbnail))?.image_comment ?: "") != ""
                    Log.d("commentTag", db.findImageByHash(md5(media.thumbnail))?.image_comment?:"")
                }
            }

            PictureItem(
                listMedia.indexOf(media),
                media,
                Modifier.padding(1.dp),
                onItemClick = { navController.navigate(Destination.FULLSCREENIMG.route + "/${bID}/${listMedia.indexOf(media)}") },
                mediaViewModel,
                haveComment.value

            )
        }
    }
    ShowBottomMenu(Destination.PICTURES.route, mediaViewModel =  mediaViewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PicturesScreenWithScaffold(
    modifier: Modifier = Modifier,
    navController: NavController,
    bID: String,
    mediaViewModel: MediaViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val db = CommentDatabase.getDatabse(context).commentDao()
    val haveComment = rememberSaveable { mutableStateOf(false) }
    //mediaViewModel.loadPictureList(albumBid)
    mediaViewModel.loadPictureList(bID)
    val otboinik = 8
    val defaultTextSize = 14f
    val dateVisible = rememberSaveable {mutableStateOf(true)}
    val counterFlag = rememberSaveable {mutableStateOf(0)}
    val countOfPhotoLine = rememberSaveable {mutableStateOf(3)}
    val fontSize = rememberSaveable {mutableStateOf(defaultTextSize)}
    val listMedia by mediaViewModel.listMedia.collectAsStateWithLifecycle()

    Log.d("Pictures", listMedia.toString())
    when (countOfPhotoLine.value) {
        1 -> {fontSize.value = 28f}
        2 -> {fontSize.value = 18f}
        3 -> {fontSize.value = defaultTextSize}
        4 -> {
            fontSize.value = 11f
            dateVisible.value = true
        } //TODO visible date and divider
        5 -> {dateVisible.value = false} //TODO invisible date and divider
        6 -> {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val splitPath = File(listMedia[0].path).toString().split("/")
                    Text(
                        if (bID != "") splitPath[splitPath.size-2] else Destination.PICTURES.label
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack, // Кнопка назад
                            contentDescription = null,
                        )
                    }
                },
            )
        },
        content = { paddingValues ->
            Log.d("fontSize", fontSize.value.toString())
            Log.d("countOfPhotoLine", countOfPhotoLine.value.toString())
            LazyVerticalGrid(
                columns = GridCells.Fixed(countOfPhotoLine.value),
                modifier = modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures(panZoomLock = false) { _, _, f1, _ ->
                            if (f1 < 1) { // Увеличение масштаба
                                counterFlag.value += 1
                                if (counterFlag.value == otboinik) {
                                    counterFlag.value = 0
                                    countOfPhotoLine.value += if (countOfPhotoLine.value < 6) 1 else 0
                                }
                            }
                            else {  // Уменьшение масштаба
                                counterFlag.value += 1
                                if (counterFlag.value == otboinik) {
                                    counterFlag.value = 0
                                    countOfPhotoLine.value -= if (countOfPhotoLine.value > 1) 1 else 0
                                }
                            }
                        }
                    }
            ) {
                items(items = listMedia) { media ->
                    LaunchedEffect(Unit) {
                        CoroutineScope(Dispatchers.IO).launch {
                            haveComment.value = (db.findImageByHash(md5(media.thumbnail))?.image_comment ?: "") != ""
                            Log.d("commentTag", db.findImageByHash(md5(media.thumbnail))?.image_comment?:"")
                        }
                    }
                    Column() {
                        AnimatedVisibility(visible = dateVisible.value,
                            enter = slideInVertically(),
                            exit = slideOutVertically()
                        ) {
                            Text(text = getImageDate(LocalContext.current, media.path)[0],
                                fontSize = fontSize.value.sp)
                            HorizontalDivider()
                            Spacer(Modifier.padding(2.dp))
                        }

                        Row() {
                            PictureItem(
                                listMedia.indexOf(media),
                                media,
                                Modifier.padding(1.dp),
                                onItemClick = {
                                    navController.navigate(
                                        Destination.FULLSCREENIMG.route + "/${bID}/${
                                            listMedia.indexOf(
                                                media
                                            )
                                        }"
                                    )
                                },
                                mediaViewModel,
                                haveComment.value

                            )
                        }
                    }

                }
            }
            ShowBottomMenu(Destination.PICTURES.route, mediaViewModel =  mediaViewModel)
        }
    )

}
