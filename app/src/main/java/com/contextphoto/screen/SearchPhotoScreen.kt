package com.contextphoto.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.contextphoto.R
import com.contextphoto.data.MediaViewModel
import com.contextphoto.ui.theme.ContextPhotoTheme

@Composable
fun SearchPhotoScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MediaViewModel,
) {
    var commentText by rememberSaveable { mutableStateOf("") }
    val checkRegister = rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth( )
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = { "Enter text" },
                placeholder = { "Найти" },
                supportingText = {
                    Text("Минимум 6 символов")
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Box() {
                Column() {
                    Button(
                        onClick = {

                        }
                    ) {

                    }
                    Text(text = "Найдено: n")
                }


            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Checkbox(
                checked = checkRegister.value,
                onCheckedChange = {

                }
            )
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

@Composable
fun Greeting() { // TODO fixme render problem
    val context = LocalContext.current
    var commentText by rememberSaveable { mutableStateOf("") }
    val checkRegister = rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth( )
    ) {
        Row() {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = { "Enter text" },
                placeholder = { "Найти" },
                supportingText = {
                    Text("Найдено: n")
                },
            )
            OutlinedButton(
                modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp),
                onClick = {

                }
            ) { // TODO fixme надо кнопку убрать и на текст сразу поставить слушатель
                Text("Отмена")
            }

        }
        Row() {
            Checkbox(
                checked = checkRegister.value,
                onCheckedChange = {

                }
            )
            Text(text = "context.getString(R.string.register_check)",
                textAlign = TextAlign.Center) // TODO fixme надо понять как центрировать нармально
        } // И на 125 строке в ListItem тоже // наверное надо в Box обернуть
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ContextPhotoTheme {
        Greeting()
    }
}