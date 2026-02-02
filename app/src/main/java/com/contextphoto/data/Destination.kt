package com.contextphoto.data

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector


sealed class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    class ALBUMS : Destination("albums", "Альбомы", Icons.Default.Build, "Album")
    class PICTURES : Destination("pictures", "Все фото", Icons.Default.Warning, "Picture")
    class FULLSCREENIMG :
        Destination("full_screen_img", "Картинка", Icons.Default.AccountBox, "Full")
    class SEARCH_PHOTO :
        Destination("search_photo", "Поиск по комментарию", Icons.Default.Menu, "SearchPhoto")

    class SETTINGS : Destination("settings", "Настройки", Icons.Default.Menu, "Settings")
    class Login : Destination("login", "Логин", Icons.Default.Menu, "Login")
    class Registration : Destination("registration", "Регистрация", Icons.Default.Menu, "Registration")
}
