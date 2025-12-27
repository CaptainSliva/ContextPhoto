package com.contextphoto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.contextphoto.RequestPermissions.ComposePermissions
import com.contextphoto.ui.AlbumViewModel
import com.contextphoto.data.Destination
import com.contextphoto.data.albumBid
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.dialog.CreateAlbumDialog
import com.contextphoto.menu.BottomMenuFullScreen
import com.contextphoto.menu.BottomMenuPictureScreen
import com.contextphoto.screen.AlbumsScreen
import com.contextphoto.screen.FullScreenViewPager
import com.contextphoto.screen.PicturesScreen
import com.contextphoto.screen.SearchPhotoScreen
import com.contextphoto.screen.SettingsScreen
import com.contextphoto.ui.theme.ContextPhotoTheme
import dagger.hilt.android.AndroidEntryPoint

// Виды todo
// TODO add
// TODO fixme
//TODO ask
@AndroidEntryPoint
class MainActivity() : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePermissions()
            val navController = rememberNavController()
            val startDestination = Destination.ALBUMS
            //var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
            val createAlbumDialogVisible = rememberSaveable { mutableStateOf(false) }
            val albumViewModel = hiltViewModel<AlbumViewModel>()
            val mediaViewModel = hiltViewModel<MediaViewModel>()

            ContextPhotoTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            // Если поменять route на label - всегда будет null title = { Text(navController.currentBackStackEntryAsState().value?.destination?.route.toString()) },
                            title = {
                                Text(
                                    when (
                                        navController
                                            .currentBackStackEntryAsState()
                                            .value
                                            ?.destination
                                            ?.route
                                    ) {
                                        Destination.ALBUMS.route -> Destination.ALBUMS.label
                                        Destination.PICTURES.route -> Destination.PICTURES.label
                                        Destination.FULLSCREENIMG.route -> Destination.FULLSCREENIMG.label
                                        Destination.SEARCH_PHOTO.route -> Destination.SEARCH_PHOTO.label
                                        Destination.SETTINGS.route -> Destination.SETTINGS.label
                                        else -> "error in MainActivity TopAppBar"
                                    },
                                )
                            },
//                            title = {Text(startDestination.label)},
                            navigationIcon = {
                                val currentDestination =
                                    navController
                                        .currentBackStackEntryAsState()
                                        .value
                                        ?.destination
                                        ?.route
                                IconButton(onClick = {
                                    navController.navigateUp()
                                }) {
                                    Icon(
                                        Icons.Default.ArrowBack, // Кнопка назад
                                        contentDescription = null,
                                    )
                                    mediaViewModel.changeStateBottomMenu(false)
                                    mediaViewModel.changeStateCheckBox(false)
                                    mediaViewModel.changeStateBottomMenuFullScreen(false)
                                }
                            },
                        )
                    },
                    bottomBar = {
                        val currentDestination =
                            navController
                                .currentBackStackEntryAsState()
                                .value
                                ?.destination
                                ?.route
                        if (currentDestination == Destination.ALBUMS.route) {
                            mediaViewModel.changeState(false)
                            mediaViewModel.changeStateCheckBox(false)
                            mediaViewModel.clearSelectedMedia()
                            albumBid = ""
                            AnimatedVisibility(
                                currentDestination == Destination.ALBUMS.route,
                                enter =
                                    slideInVertically() +
                                            expandVertically(
                                                expandFrom = Alignment.Top,
                                            ) +
                                            fadeIn(
                                                initialAlpha = 0.3f,
                                            ),
                                exit =
                                    slideOutVertically() +
                                            shrinkVertically(
                                                shrinkTowards = Alignment.Top,
                                            ) + fadeOut(),
                            ) {
                                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                                    Destination.entries.slice(0..1).forEachIndexed { index, destination ->
                                        NavigationBarItem(
                                            selected = destination == Destination.ALBUMS,
                                            onClick = {
                                                navController.navigate(route = destination.route)
                                            },
                                            icon = {
                                                Icon(
                                                    destination.icon,
                                                    contentDescription = destination.contentDescription,
                                                )
                                            },
                                            label = { Text(destination.label) },
                                        )
                                    }
                                }
                            }
                        }
                        ShowBottomMenu(currentDestination?: Destination.ALBUMS.route, albumViewModel, mediaViewModel)

                    },
                    floatingActionButton = {
                        AnimatedVisibility(
                            visible = navController.currentBackStackEntryAsState().value?.destination?.route == Destination.ALBUMS.route,
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

                        AppNavHost(
                            navController,
                            startDestination,
                            modifier = Modifier.padding(paddingValues),
                            albumViewModel,
                            mediaViewModel,
                        )
                        if (createAlbumDialogVisible.value) {
                            CreateAlbumDialog({}, createAlbumDialogVisible, albumViewModel)
                        }
                    },
                )
                com.contextphoto.menu.MainDropdownMenu(navController)
            }
        }
    }
}

@Composable
fun ShowBottomMenu(currentDestination: String, albumViewModel: AlbumViewModel, mediaViewModel: MediaViewModel) {

    when (currentDestination) {
        Destination.ALBUMS.route -> {
//            albumViewModel.changeState(false)
//            mediaViewModel.changeState(false)
//            FunBottomMenu(albumViewModel.bottomMenuVisible.collectAsStateWithLifecycle().value,
//                { PopupMenuAlbumScreen(albumViewModel) })
        }

        Destination.PICTURES.route -> {
//            albumViewModel.changeState(false)
//            mediaViewModel.changeState(false)
            FunBottomMenu(mediaViewModel.bottomMenuVisible.collectAsStateWithLifecycle().value,
                { BottomMenuPictureScreen(mediaViewModel) }
            )
        }

        Destination.FULLSCREENIMG.route -> {
            FunBottomMenu(mediaViewModel.bottomMenuFullScreenVisible.collectAsStateWithLifecycle().value,
                { BottomMenuFullScreen(mediaViewModel) }
            )
        }
    }
}

@Composable
fun FunBottomMenu(visible: Boolean,
      bootmFun: @Composable () -> Unit) {
    AnimatedVisibility(
        visible,
        enter =
            slideInVertically() +
                    expandVertically(
                        expandFrom = Alignment.Top,
                    ) +
                    fadeIn(
                        initialAlpha = 0.3f,
                    ),
        exit =
            slideOutVertically() +
                    shrinkVertically(
                        shrinkTowards = Alignment.Top,
                    ) + fadeOut(),
    ) {
        //Box(modifier = Modifier.fillMaxSize().background(Color.Red))
        bootmFun()
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
    albumViewModel: AlbumViewModel,
    mediaViewModel: MediaViewModel,
) {
    NavHost(
        navController,
        startDestination = startDestination.route,
    ) {
        composable(Destination.ALBUMS.route) {
            AlbumsScreen(modifier, navController, albumViewModel)
        }

        composable(Destination.PICTURES.route) {
            PicturesScreen(modifier, navController, mediaViewModel) // TODO fixme пропадают фото при перекомпозиции, но альбомы не пропадают /- вьюмодель создаётся неправильно, похоже она умирает, надо почитать как создавать viewModel (не руками) в офф доке
        }

        composable(Destination.FULLSCREENIMG.route) {
            FullScreenViewPager(modifier, navController, mediaViewModel)
        }

        composable(Destination.SEARCH_PHOTO.route) {
            SearchPhotoScreen(modifier, navController, mediaViewModel)
        }

        composable(Destination.SETTINGS.route) {
            SettingsScreen(modifier, navController)
        }
    }
}
