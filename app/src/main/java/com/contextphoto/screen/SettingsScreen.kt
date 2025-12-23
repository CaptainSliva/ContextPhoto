package com.contextphoto.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.room.util.TableInfo
import com.contextphoto.data.MediaViewModel
import com.contextphoto.ui.theme.ContextPhotoTheme

@Composable
fun SettingsScreen(modifier: Modifier = Modifier,
                   navController: NavController
) {
    Column(modifier = modifier) {
        Text("Импорт из файла")
        Text("Экспорт в файл")
        Divider(modifier = Modifier.fillMaxWidth().padding(0.dp, 8.dp))
        Text("Авторизация/регистрация")
        Text("Импорт из Firebase")
        Text("Экспорт в Firebase")
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetngPreview() {
//    ContextPhotoTheme {
//        SettingsScreen(modifier = Modifier.fillMaxSize())
//    }
//}