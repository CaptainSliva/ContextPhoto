package com.contextphoto.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.contextphoto.data.Destination
import com.contextphoto.menu.MainDropdownMenu

@Composable
fun SettingsScreen(modifier: Modifier = Modifier,
                   navController: NavController
) {
    Column(modifier = modifier) {
        Text("Импорт из файла")
        Text("Экспорт в файл")
        Divider(modifier = Modifier.fillMaxWidth().padding(0.dp, 8.dp))
        Text("Авторизация/регистрация/logout",
            modifier = Modifier.clickable(
                onClick = {
                    navController.navigate(
                        Destination.Login().route
                    )
                }
            ))
        Text("Импорт из Firebase")
        Text("Экспорт в Firebase")
    }
    MainDropdownMenu(navController)
}

//@Preview(showBackground = true)
//@Composable
//fun GreetngPreview() {
//    ContextPhotoTheme {
//        SettingsScreen(modifier = Modifier.fillMaxSize())
//    }
//}