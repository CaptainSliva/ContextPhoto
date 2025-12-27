package com.contextphoto.screen

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.contextphoto.data.Destination
import com.contextphoto.data.albumBid
import com.contextphoto.item.PictureItem
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.utils.FunctionsMediaStore.getAllMedia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Composable // TODO fixme При закрытии экрана и быстром нажатии на место где была картинка - открывается картинка, хотя на экране её уже нет
fun PicturesScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MediaViewModel,
) {
    viewModel.resetPicturePosition()

    val context = LocalContext.current
    viewModel.loadPictureList(albumBid)
    val listMedia by viewModel.listMedia.collectAsStateWithLifecycle()
    Log.d("Pictures", listMedia.toString())

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
    ) {
        items(items = listMedia) { media ->
            PictureItem(
                listMedia.indexOf(media),
                media,
                Modifier.padding(1.dp),
                onItemClick = { navController.navigate(Destination.FULLSCREENIMG.route) },
                viewModel,
            )
        }
    }

}

