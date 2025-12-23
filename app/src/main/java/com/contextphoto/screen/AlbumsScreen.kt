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
import com.contextphoto.item.AlbumItem
import com.contextphoto.data.AlbumViewModel
import com.contextphoto.data.Destination
import com.contextphoto.data.MediaViewModel
import com.contextphoto.utils.FunctionsMediaStore.getListAlbums
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Composable
fun AlbumsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    albumViewModel: AlbumViewModel,
    mediaViewModel: MediaViewModel,
) {
    mediaViewModel.changeState()

    val context = LocalContext.current
    albumViewModel.loadAlbumList(context)
    val albumList by albumViewModel.albumList.collectAsStateWithLifecycle()
    Log.d("Albums", albumList.toString())

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier,
    ) {
        items(
            items = albumList,
        ) { album ->
            AlbumItem(
                album,
                Modifier.padding(0.dp, 2.dp),
                onItemClick = { navController.navigate(Destination.PICTURES.route) },
                albumViewModel,
                mediaViewModel
            )
        }
    }
}

