package com.contextphoto.screen

import android.R.attr.onClick
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.contextphoto.R
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.ui.theme.ContextPhotoTheme

@Composable
fun SearchPhotoScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MediaViewModel,
) {
    val context = LocalContext.current
    var commentText by rememberSaveable { mutableStateOf("") }
    val checkRegister = rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        )
        {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = { "Enter text" },
                placeholder = { "Найти" },
                supportingText = {
                    Text("Найдено: n")
                }
            )

            Text("Отмена",
                modifier = Modifier
                    .clickable(
                        onClick = {
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


//        LazyVerticalGrid( // TODO add сдесь гружу то, что из БД по комменту подошло
//            columns = GridCells.Fixed(3),
//            modifier = modifier
//        ) {
//            items(items = listMedia) { media ->
//                println("\n\nPRIIIINT\n${listMedia.size}\nIIITT\n$media\n")
//                PictureItem(
//                    listMedia.indexOf(media),
//                    media,
//                    Modifier.padding(1.dp),
//                    onItemClick = { navController.navigate(Destination.FULLSCREENIMG.route) },
//                    viewModel,
//                )
//            }
//        }
    }
}