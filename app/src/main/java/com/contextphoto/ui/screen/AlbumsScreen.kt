package com.contextphoto.ui.screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.contextphoto.data.navigation.Destination
import com.contextphoto.dialog.CreateAlbumDialog
import com.contextphoto.item.AlbumItem
import com.contextphoto.menu.MainDropdownMenu
import com.contextphoto.ui.AlbumViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsScreenWithScaffold(
    navController: NavHostController,
    albumViewModel: AlbumViewModel = hiltViewModel(),
) {
    val loadAlbums = albumViewModel.loadAlbums.collectAsStateWithLifecycle()
    Log.d("LOAD", loadAlbums.value.toString())
    LaunchedEffect(loadAlbums.value) {
        albumViewModel.loadAlbumList()
    }

    val albumList by albumViewModel.albumList.collectAsStateWithLifecycle()
    val listState = rememberLazyGridState()

    val createAlbumDialogVisible = rememberSaveable { mutableStateOf(false) }
    Log.d("Albums", albumList.toString())

    val isSystemInDarkTheme = isSystemInDarkTheme()

    // Используем цвета в зависимости от темы

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        Destination.Albums().label,
                    )
                },
//                navigationIcon = {
//                    IconButton(onClick = {
//                        navController.navigateUp()
//                    }) {
//                        Icon(
//                            Icons.Default.ArrowBack, // Кнопка назад
//                            contentDescription = null,
//                        )
//                    }
//                },
            )
            MainDropdownMenu(navController)
        },
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                listOf(Destination.Albums(), Destination.Pictures()).forEach { destination ->
                    NavigationBarItem(
                        selected = destination is Destination.Albums,
                        onClick = {
                            when (destination) {
//                                is Destination.Albums -> {
//                                    navController.navigate(route = destination.route)
//                                }

                                is Destination.Pictures -> {
                                    navController.navigate(Destination.Pictures().route + "/" + "/1")
                                }

                                else -> {}
                            }
                        },
                        icon = {
                            Icon(
                                painterResource(destination.icon),
                                contentDescription = destination.contentDescription,
                            )
                        },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FloatingActionButton(onClick = {
                    createAlbumDialogVisible.value = true
                }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                    )
                }
            }
        },
        content = { paddingValues ->
            LazyVerticalGrid(
                state = listState,
                columns = GridCells.Fixed(1),
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(bottom = 80.dp),
            ) {
                items(
                    items = albumList,
                    key = { album -> album.hashCode() },
                ) { album ->
                    AlbumItem(
                        album,
                        Modifier.padding(0.dp, 2.dp).animateItem(),
                        onItemClick = {
                            navController.navigate(
                                Destination.Pictures().route + "/${album.bID}" + "/${album.itemsCount ?: 1}",
                            )
                        },
                        albumViewModel,
                    )
                }
            }

            if (createAlbumDialogVisible.value) {
                CreateAlbumDialog({}, createAlbumDialogVisible)
            }
        },
    )
}
