package com.contextphoto.data

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination( // TODO refactor sealed interface вместо enum class Destination
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    ALBUMS("albums", "Альбомы", Icons.Default.Build, "Album"),
    PICTURES("pictures", "Все фото", Icons.Default.Warning, "Picture"),
    FULLSCREENIMG("full_screen_img", "Картинка", Icons.Default.AccountBox, "Full"),
    SEARCH_PHOTO("search_photo", "Поиск по комментарию", Icons.Default.Menu, "SearchPhoto"),
    SETTINGS("settings", "Настройки", Icons.Default.Menu, "Settings"),
}