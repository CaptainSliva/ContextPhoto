package com.contextphoto.ui.screen

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.contextphoto.R
import com.contextphoto.data.Destination
import com.contextphoto.db.CommentDao
import com.contextphoto.db.CommentDatabase
import com.contextphoto.item.PictureItem
import com.contextphoto.menu.MainDropdownMenu
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.utils.FunctionsMediaStore.getPictureFromUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPhotoScreenWithScaffold(
    modifier: Modifier = Modifier,
    navController: NavController,
    mediaViewModel: MediaViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val db = CommentDatabase.getDatabse(context).commentDao()
    var commentText by rememberSaveable { mutableStateOf("") }
    val checkRegister = rememberSaveable { mutableStateOf(false) }
    val listMedia by mediaViewModel.listMedia.collectAsStateWithLifecycle()
    val numberFind = rememberSaveable { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(commentText, checkRegister.value) {
        if (commentText.isNotBlank()) {
            delay(300)
            withContext(coroutineScope.coroutineContext) {
                searchPhotoOnComment(mediaViewModel, numberFind, commentText, checkRegister.value, db, context)
            }
        }
        else {
            mediaViewModel.clearPictureList()
            numberFind.value = 0
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
                        navController.navigateUp()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack, // Кнопка назад
                            contentDescription = null,
                        )
                    }
                },
            )
            //MainDropdownMenu(navController)
        },
        content = { paddingValues ->
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
//                            searchPhotoOnComment(mediaViewModel, numberFind, it, checkRegister.value, db, context)
                        },
                        label = { "Enter text" },
                        placeholder = { "Найти" },
                        supportingText = {
                            Text("Найдено: ${numberFind.value}")
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
                                        numberFind.value = 0
                                        mediaViewModel.clearPictureList()
                                    },
                                ),
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
//                            searchPhotoOnComment(mediaViewModel, numberFind, commentText, checkRegister.value, db, context)
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
                    columns = GridCells.Fixed(3),
                    modifier = modifier,
                ) {
                    items(items = listMedia, key = { media -> media.path }) { media ->
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
        },
    )
}

private suspend fun searchPhotoOnComment(
    mediaViewModel: MediaViewModel,
    numberFind: MutableState<Int>,
    comment: String,
    checkRegister: Boolean,
    db: CommentDao,
    context: Context,
) {
    CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
        mediaViewModel.clearPictureList()
        numberFind.value = 0
        if (comment.trim() != "") {
            db.findImageByComment(comment).collect {
                it.forEach {
                    when (checkRegister) {
                        true -> {
                            if (it.image_comment.contains(comment)) {
                                numberFind.value += 1
                                mediaViewModel.addPicture(
                                    getPictureFromUri(
                                        context,
                                        it.image_uri.toUri(),
                                    ),
                                )
                            }
                        }

                        false -> {
                            numberFind.value += 1
                            mediaViewModel.addPicture(
                                getPictureFromUri(
                                    context,
                                    it.image_uri.toUri(),
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}
