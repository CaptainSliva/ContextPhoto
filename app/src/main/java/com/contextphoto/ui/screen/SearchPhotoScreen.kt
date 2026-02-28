package com.contextphoto.ui.screen

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.contextphoto.R
import com.contextphoto.ShowBottomMenu
import com.contextphoto.data.navigation.Destination
import com.contextphoto.db.CommentDao
import com.contextphoto.db.CommentDatabase
import com.contextphoto.item.PictureItem
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.utils.FunctionsMediaStore.getPictureFromUri
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPhotoScreenWithScaffold(navController: NavHostController,
                                  mediaViewModel: MediaViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val db = CommentDatabase.getDatabse(context).commentDao()
    var commentText by rememberSaveable { mutableStateOf("") }
    val checkRegister = rememberSaveable { mutableStateOf(false) }
    val listMedia by mediaViewModel.listMedia.collectAsStateWithLifecycle()
    val numberFind by mediaViewModel.numberFind.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val clearFlag = rememberSaveable { mutableStateOf(false) }
    val listState = rememberLazyGridState()

    LaunchedEffect(commentText, checkRegister.value) {
        clearFlag.value = true
        delay(300)
        withContext(coroutineScope.coroutineContext) {
            searchPhotoOnComment(mediaViewModel, commentText, checkRegister.value, db, context)
        }
    }

    LaunchedEffect(clearFlag.value) {
        if (clearFlag.value) {
            mediaViewModel.clearPictureList()
            clearFlag.value = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        Destination.SearchPhoto().label,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        backActions(mediaViewModel, navController)
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
            BackHandler {
                backActions(mediaViewModel, navController)
            }
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(paddingValues),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                )
                {
                    TextField(
                        value = commentText,
                        onValueChange = {
                            commentText = it
                        },
                        label = { "Enter text" },
                        placeholder = { "Найти" },
                        supportingText = {
                            Text("Найдено: ${numberFind}")
                        },
                        modifier = Modifier.weight(8f),
                    )

                    Text(
                        "Отмена",
                        modifier =
                            Modifier
                                .weight(2f)
                                .clickable(
                                    onClick = {
                                        commentText = ""
                                    },
                                ),
                        color = colorResource(R.color.light_blue)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = checkRegister.value,
                        onCheckedChange = {
                            checkRegister.value = !checkRegister.value
                            clearFlag.value = true
                        },
                        colors =
                            CheckboxDefaults.colors(
                                checkedColor = colorResource(R.color.light_blue),
                                disabledCheckedColor = Color.White,
                            ),
                    )
                    Text(
                        text = context.getString(R.string.register_check),
                        textAlign = TextAlign.End,
                    )
                }

                LazyVerticalGrid(
                    state = listState,
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(items = listMedia, key = { media -> media.hashCode() }) { media ->
                        val mediaIndex = listMedia.indexOf(media)
                        LaunchedEffect(Unit) {
                            mediaViewModel.changeStatePictureComment(mediaIndex, media.thumbnail)
                        }
                        PictureItem(
                            listMedia.indexOf(media),
                            media,
                            Modifier.padding(1.dp).animateItem(),
                            onItemClick = {
                                navController.navigate(
                                    Destination.FullScreenImg().route,
                                )
                            },
                            mediaViewModel,
                        )
                    }
                }
            }
            ShowBottomMenu(Destination.SearchPhoto().route, mediaViewModel = mediaViewModel)
        },
    )
}

private suspend fun searchPhotoOnComment(
    mediaViewModel: MediaViewModel,
    comment: String,
    checkRegister: Boolean,
    db: CommentDao,
    context: Context,
) {
    if (comment.trim() != "") {
        db.findImageByComment(comment).collect {
            it.forEach { commentCurrent ->
                println(commentCurrent.image_comment)
                val imageByUri = getPictureFromUri(context, commentCurrent.image_uri.toUri())
                if (imageByUri.path != "") {
                    println(imageByUri.path)
                    when (checkRegister) {
                        true -> {
                            if (commentCurrent.image_comment.contains(comment)) {
                                mediaViewModel.addFoundedPicture(
                                    imageByUri,
                                )
                            }
                        }

                        false -> {
                            mediaViewModel.addFoundedPicture(
                                imageByUri,
                            )
                        }
                    }
                }
                else {
                    db.delete(commentCurrent)
                }
            }
        }
    }
}


private fun backActions(
    mediaViewModel: MediaViewModel,
    navController: NavHostController
) {
    mediaViewModel.clearPictureList()
    mediaViewModel.loadPicturesStateChange(true)
    navController.navigateUp()
}
