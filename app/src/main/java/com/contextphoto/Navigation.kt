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
import com.contextphoto.data.Destination
import kotlinx.serialization.Serializable

// TODO ask Этот файл зачем нужен и делают ли так?
//@Serializable
//object Albums
//
//@Serializable
//object Pictures
//
//@Serializable
//object FullScreenImg
//
//data class NavigationRoutes<T : Any>(val name: String, val route: T, val icon: ImageVector)
//
//val navigationRoutes = listOf(
//    NavigationRoutes(Destination.ALBUMS.label, Albums, Destination.ALBUMS.icon),
//    NavigationRoutes(Destination.PICTURES.label, Pictures, Destination.PICTURES.icon),
//    NavigationRoutes(Destination.FULLSCREENIMG.label, FullScreenImg, Destination.FULLSCREENIMG.icon),
//    NavigationRoutes(Destination.SEARCH_PHOTO.label, FullScreenImg, Destination.SEARCH_PHOTO.icon),
//    NavigationRoutes(Destination.SETTINGS.label, FullScreenImg, Destination.SETTINGS.icon),
//)