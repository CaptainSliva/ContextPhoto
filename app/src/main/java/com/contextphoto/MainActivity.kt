package com.contextphoto

import android.app.Activity
import android.content.pm.ActivityInfo
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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.contextphoto.RequestPermissions.ComposePermissions
import com.contextphoto.data.Destination
import com.contextphoto.dialog.CreateAlbumDialog
import com.contextphoto.menu.BottomMenuFullScreen
import com.contextphoto.menu.BottomMenuPictureScreen
import com.contextphoto.menu.MainDropdownMenu
import com.contextphoto.ui.FullscreenViewModel
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.ui.screen.AlbumsScreen
import com.contextphoto.ui.screen.AlbumsScreenWithScaffold
import com.contextphoto.ui.screen.FullScreenViewPager
import com.contextphoto.ui.screen.FullScreenViewPagerWithScaffold
import com.contextphoto.ui.screen.PicturesScreen
import com.contextphoto.ui.screen.PicturesScreenWithScaffold
import com.contextphoto.ui.screen.SearchPhotoScreen
import com.contextphoto.ui.screen.SettingsScreen
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
            val context = LocalContext.current
            val activity = context as Activity
            var orientation = rememberSaveable { mutableStateOf(activity.requestedOrientation) }
            //var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
            //val albumViewModel = hiltViewModel<AlbumViewModel>()
            //val mediaViewModel = hiltViewModel<MediaViewModel>()

//            firebaseRealTimeDatabaseTest()
//            firebaseFirestoreDatabaseTest()
//            firebasePasswordAuth()


            ContextPhotoTheme {
                Scaffold(
                    bottomBar = {
                        val currentDestination =
                            navController
                                .currentBackStackEntryAsState()
                                .value
                                ?.destination
                                ?.route
                        if (currentDestination == Destination.ALBUMS.route) {
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
                                                if (destination == Destination.PICTURES) navController.navigate(Destination.PICTURES.route + "/")
                                                else navController.navigate(route = destination.route)
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
                    },
//                    floatingActionButton = {
//                        AnimatedVisibility(
//                            visible = navController.currentBackStackEntryAsState().value?.destination?.route == Destination.ALBUMS.route,
//                            enter = fadeIn(),
//                            exit = fadeOut(),
//                        ) {
//                            FloatingActionButton(onClick = {
//                                createAlbumDialogVisible.value = true
//                            }) {
//                                Icon(
//                                    Icons.Default.Add,
//                                    contentDescription = null,
//                                )
//                            }
//                        }
//                    },
                    content = { paddingValues ->
                        AppNavHost(
                            navController,
                            startDestination,
                            modifier = Modifier.padding(paddingValues)
                        )
//                        if (createAlbumDialogVisible.value) {
//                            CreateAlbumDialog({}, createAlbumDialogVisible)
//                        }
                    },
                )
                MainDropdownMenu(navController)
            }
        }
    }
}

@Composable
fun ShowBottomMenu(
    currentDestination: String,
    mediaViewModel: MediaViewModel = hiltViewModel(),
    fullScreenViewModel: FullscreenViewModel = hiltViewModel(),
    commentText: String? = null,
) {

    when (currentDestination) {
        Destination.PICTURES.route -> {
            FunBottomMenu(mediaViewModel.bottomMenuVisible.collectAsStateWithLifecycle().value,
                { BottomMenuPictureScreen(mediaViewModel) }
            )
        }

        Destination.FULLSCREENIMG.route -> {
            val visible = fullScreenViewModel.bottomMenuFullScreenVisible.collectAsStateWithLifecycle().value
            if (commentText != null) InfinityScrollableText(visible, commentText, { fullScreenViewModel.changeStateBottomMenuFullScreen() })
            FunBottomMenu(visible,
                { BottomMenuFullScreen(fullScreenViewModel) }
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
fun InfinityScrollableText(
    visible: Boolean,
    commentText: String,
    onClick: () -> Unit)
{
    val freeSpace = 167
    val brush = Brush.verticalGradient(listOf(colorResource(R.color.medium_transparant_black), colorResource(R.color.dark_black_overlay), Color.Black))
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var size by remember { mutableStateOf(Size.Zero) }
    Column(modifier = Modifier.fillMaxHeight().alpha(alpha = if (visible) 1f else 0f),
        verticalArrangement = Arrangement.Bottom) {

        println(offsetY.value)
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(unbounded=true, align = Bottom)
            .onSizeChanged { size = it.toSize() }
            .background(brush)
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    val original = Offset(offsetX.value, offsetY.value)
                    val summed = original + dragAmount
                    val newValue =
                        Offset(
                            x = summed.x.coerceIn(0f, size.width),
                            y = (original.y-dragAmount.y/3.3f).coerceIn(0f, Constraints.Infinity.toFloat()),
                        )
                    offsetX.value = newValue.x
                    offsetY.value = newValue.y
                }
            }
            .clickable(onClick = {
                onClick()
                offsetY.value = 0f
            })
            .height(freeSpace.dp+offsetY.value.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(text = commentText, modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(freeSpace.dp+offsetY.value.dp),
                color = Color.White,
            )
        }
    }

}


@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController,
        startDestination = startDestination.route,
    ) {
        composable(Destination.ALBUMS.route) {
            AlbumsScreenWithScaffold(modifier, navController)
        }

        composable(Destination.PICTURES.route + "/{bID}") { stackEntry ->
            val bID = stackEntry.arguments?.getString("bID").toString()
            PicturesScreenWithScaffold(modifier, navController, bID)
        } // TODO fixme нижнее меню перекрывает часть картинок, как-то надо на его высоту картинки приподнять

        composable(Destination.FULLSCREENIMG.route + "/{bID}/{mediaPosition}",
            arguments = listOf(
                navArgument("bID") {type = NavType.StringType},
                navArgument("mediaPosition") {type = NavType.IntType}
            )) { stackEntry ->
            val bID = stackEntry.arguments?.getString("bID").toString()
            val mediaPosition = stackEntry.arguments?.getInt("mediaPosition")
            FullScreenViewPagerWithScaffold(modifier, navController, bID, mediaPosition!!)
        }

        composable(Destination.SEARCH_PHOTO.route) {
            SearchPhotoScreen(modifier, navController)
        }

        composable(Destination.SETTINGS.route) {
            SettingsScreen(modifier, navController)
        }
    }
}
