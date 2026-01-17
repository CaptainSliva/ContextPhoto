package com.contextphoto.ui.screen

import android.app.Activity
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.contextphoto.ShowBottomMenu
import com.contextphoto.data.Destination
import com.contextphoto.db.CommentDatabase
import com.contextphoto.item.PictureItem
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.utils.FunctionsBitmap.md5
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable // TODO fixme При закрытии экрана и быстром нажатии на место где была картинка - открывается картинка, хотя на экране её уже нет
fun PicturesScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    bID: String,
    mediaViewModel: MediaViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val db = CommentDatabase.getDatabse(context).commentDao()
    val haveComment = rememberSaveable { mutableStateOf(0) }
    //mediaViewModel.loadPictureList(albumBid)
    mediaViewModel.loadPictureList(bID)
    val listMedia by mediaViewModel.listMedia.collectAsStateWithLifecycle()
    Log.d("Pictures", listMedia.toString())

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
    ) {
        items(items = listMedia) { media ->
            CoroutineScope(Dispatchers.IO).launch {
                db.findImageByHash(md5(media.thumbnail))?.image_comment != ""
            }
            PictureItem(
                listMedia.indexOf(media),
                media,
                Modifier.padding(1.dp),
                onItemClick = { navController.navigate(Destination.FULLSCREENIMG.route + "/${bID}/${listMedia.indexOf(media)}") },
                mediaViewModel,
                true

            )
        }
    }
    ShowBottomMenu(Destination.PICTURES.route, mediaViewModel =  mediaViewModel)
}
