package com.contextphoto

import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.media.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import kotlinx.serialization.Serializable

@Serializable
object Albums

@Serializable
object Pictures

data class NavigationRoutes<T : Any>(val name: String, val route: T, val icon: ImageVector)

val navigationRoutes = listOf(
    NavigationRoutes("Альбомы", Albums, Icons.Outlined.Build),
    NavigationRoutes("Все фото", Pictures, Icons.Outlined.Warning)
)