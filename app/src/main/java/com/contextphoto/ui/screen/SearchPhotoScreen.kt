package com.contextphoto.ui.screen

import android.R.attr.label
import android.R.attr.onClick
import android.R.attr.text
import android.R.attr.textStyle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.any
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.contextphoto.R
import com.contextphoto.data.Destination
import com.contextphoto.db.CommentDatabase
import com.contextphoto.item.PictureItem
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.ui.theme.ContextPhotoTheme
import com.contextphoto.utils.FunctionsMediaStore.getPictureFromUri
import com.google.common.base.CharMatcher.any
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import java.time.format.TextStyle

@Composable
fun SearchPhotoScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    mediaViewModel: MediaViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val db = CommentDatabase.getDatabse(context).commentDao()
    var commentText by rememberSaveable { mutableStateOf("") }
    val checkRegister = rememberSaveable { mutableStateOf(false) }
    val brush = remember {
        Brush.linearGradient(
            colors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Magenta)
        )
    }
    val listMedia by mediaViewModel.listMedia.collectAsStateWithLifecycle()
    val numberFind = rememberSaveable {mutableStateOf(0)}

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        )
        {
            TextField(
                value = commentText,
                onValueChange = {commentText = it
                    CoroutineScope(Dispatchers.IO+SupervisorJob()).launch {
                        numberFind.value = 0
                        if (it.trim() != "") {
                            db.findImageByComment(it).collect {
                                it.forEach { el ->
                                    numberFind.value+=1
                                    mediaViewModel.addPicture(getPictureFromUri(context, el.image_uri.toUri()))
                                }
                            }
                        }
                    }
                },
                label = { "Enter text" },
                placeholder = { "Найти" },
                supportingText = {
                    Text("Найдено: ${numberFind.value}")
                },
                textStyle = androidx.compose.ui.text.TextStyle(brush = brush),
                modifier = Modifier.width(200.dp)// TODO костыль, исправить так, что бы поле ввода текста кнопку справа не сдвигало за экран
            )

            Text("Отмена",
                modifier = Modifier
                    .clickable(
                        onClick = {
                            commentText = ""
                            mediaViewModel.clearPictureList()
                            // TODO add очистить EditText и MediaList
                        }
                    ),
            )

        }
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = checkRegister.value,
                onCheckedChange = {
                    checkRegister.value = !checkRegister.value
                }
            )
            Text(text = context.getString(R.string.register_check),
                textAlign = TextAlign.End)
        }


//        LazyVerticalGrid(
//            columns = GridCells.Fixed(3),
//            modifier = modifier
//        ) {
//            items(items = listMedia) { media ->
//                PictureItem(
//                    listMedia.indexOf(media),
//                    media,
//                    Modifier.padding(1.dp),
//                    onItemClick = { navController.navigate(Destination.FULLSCREENIMG.route + "/${bID}/${listMedia.indexOf(media)}") },
//                    mediaViewModel,
//                    true
//                )
//            }
//        }
    }
}