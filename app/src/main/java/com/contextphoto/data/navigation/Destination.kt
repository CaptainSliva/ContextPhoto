package com.contextphoto.data.navigation

import android.support.annotation.DrawableRes
import com.contextphoto.R

sealed class Destination(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int,
    val contentDescription: String,
) {
    class Albums : Destination("albums", "Альбомы", R.drawable.outline_photo_album_24, "Album")

    class Pictures : Destination("pictures", "Все фото", R.drawable.outline_image_24, "Picture")

    class FullScreenImg : Destination("full_screen_img", "Картинка", R.drawable.ic_launcher_background, "Full")

    class SearchPhoto : Destination("search_photo", "Поиск по комментарию", R.drawable.ic_launcher_background, "SearchPhoto")

    class Settings : Destination("settings", "Настройки", R.drawable.ic_launcher_background, "Settings")

    class Login : Destination("login", "Логин", R.drawable.ic_launcher_background, "Login")

    class Registration : Destination("registration", "Регистрация", R.drawable.ic_launcher_background, "Registration")
}
