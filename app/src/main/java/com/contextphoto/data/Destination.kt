package com.contextphoto.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    ALBUMS("albums", "Альбомы", Icons.Default.Build, "Album"),
    PICTURES("pictures", "Все фото", Icons.Default.Warning, "Playlist")
}