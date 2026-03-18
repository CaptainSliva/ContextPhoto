package com.contextphoto

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.contextphoto.data.navigation.Destination
import com.contextphoto.menu.BottomMenuFullScreen
import com.contextphoto.menu.BottomMenuFullScreenVideo
import com.contextphoto.menu.BottomMenuPictureScreen
import com.contextphoto.menu.BottomMenuSearchPictureScreen
import com.contextphoto.ui.screen.AlbumsScreenWithScaffold
import com.contextphoto.ui.screen.FullScreenViewPagerWithScaffold
import com.contextphoto.ui.screen.LoginScreen
import com.contextphoto.ui.screen.PicturesScreenWithScaffold
import com.contextphoto.ui.screen.RegisterScreen
import com.contextphoto.ui.screen.SearchPhotoScreenWithScaffold
import com.contextphoto.ui.screen.SettingsScreenWithScaffold
import com.contextphoto.ui.theme.ContextPhotoTheme
import com.contextphoto.ui.vm.FullscreenViewModel
import com.contextphoto.ui.vm.MediaViewModel
import com.contextphoto.utils.RequestPermissions.ComposePermissions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePermissions()
            val navController = rememberNavController()
            val startDestination = Destination.Albums()
            val context = LocalContext.current
            val activity = context as Activity

//            for (i in 1..3) {
//                val count = i*10
//                generatePictures(5000, 10000,count, delete = false, (count).toString())
//            }

            // var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

//            firebaseFirestoreDatabaseTest()

//            espWrire(context, "myFirst secret name")
//            val espName = espRead(context)
//            Log.d("ESP", espName)

            //    val MIGRATION_1_2 = object : Migration(1, 2) {
            //        override fun migrate(db: SupportSQLiteDatabase) {
            //            db.execSQL("ALTER TABLE User ADD COLUMN email TEXT")
            //        }
            //    }
            //    val db = Room.databaseBuilder(context, CommentDatabase::class.java, "comment_database").addMigrations(MIGRATION_1_2).build()


            ContextPhotoTheme {
                enableEdgeToEdge(
//                    statusBarStyle = SystemBarStyle.dark(
//                        Color.Transparent.toArgb()
//                    ),
//                    navigationBarStyle = SystemBarStyle.dark(
//                        Color.Transparent.toArgb()
//                    )

                    statusBarStyle = SystemBarStyle.auto(
                        Color.Transparent.toArgb(),
                        Color.Transparent.toArgb()
                    ),
                    navigationBarStyle = SystemBarStyle.auto(
                        Color.Transparent.toArgb(),
                        Color.Transparent.toArgb()
                    )
                )

                Scaffold(
                    content = { paddingValues ->
                        AppNavHost(
                            navController,
                            startDestination,
                        )
                    },
                )
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
    isVideo: Boolean = false,
) {
    when (currentDestination) {
        Destination.Pictures().route -> {
            FunBottomMenu(
                mediaViewModel.bottomMenuVisible.collectAsStateWithLifecycle().value,
                { BottomMenuPictureScreen(mediaViewModel) },
            )
        }

        Destination.SearchPhoto().route -> {
            FunBottomMenu(
                mediaViewModel.bottomMenuVisible.collectAsStateWithLifecycle().value,
                { BottomMenuSearchPictureScreen(mediaViewModel) },
            )
        }

        Destination.FullScreenImg().route -> {
            val visible = fullScreenViewModel.bottomMenuFullScreenVisible.collectAsStateWithLifecycle().value

            when (isVideo) {
                true -> {
                    FunBottomMenu(
                        visible,
                        { BottomMenuFullScreenVideo(fullScreenViewModel) },
                    )
                }

                else -> {
                    InfinityScrollableText(visible, commentText, { fullScreenViewModel.changeStateBottomMenuFullScreen() })

                    FunBottomMenu(
                        visible,
                        { BottomMenuFullScreen(fullScreenViewModel) },
                    )
                }
            }
        }
    }
}

@Composable
fun FunBottomMenu(
    visible: Boolean,
    bootmFun: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible,
        enter =
            slideInVertically(initialOffsetY = { 500 }) +
                fadeIn(initialAlpha = 0.3f),
        exit =
            slideOutVertically(targetOffsetY = { 600 }) +
                fadeOut(),
    ) {
        // Box(modifier = Modifier.fillMaxSize().background(Color.Red))
        bootmFun()
    }
}

@Composable
fun InfinityScrollableText(
    visible: Boolean,
    commentText: String? = null,
    onClick: () -> Unit,
    offset: Int = 0,
) {
    if (commentText != null && commentText != "") {
        val freeSpace = 167 + offset
        val brush =
            Brush.verticalGradient(
                listOf(
                    colorResource(R.color.medium_transparant_black),
                    colorResource(R.color.dark_black_overlay),
                    Color.Black,
                ),
            )
        val offsetX = remember { mutableStateOf(0f) }
        val offsetY = remember { mutableStateOf(0f) }
        var size by remember { mutableStateOf(Size.Zero) }
        Column(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .alpha(alpha = if (visible) 1f else 0f),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Log.d("println", offsetY.value.toString())
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(unbounded = true, align = Bottom)
                        .onSizeChanged { size = it.toSize() }
                        .background(brush)
                        .pointerInput(Unit) {
                            detectDragGestures { _, dragAmount ->
                                val original = Offset(offsetX.value, offsetY.value)
                                val summed = original + dragAmount
                                val newValue =
                                    Offset(
                                        x = summed.x.coerceIn(0f, size.width),
                                        y =
                                            (original.y - dragAmount.y / 3.3f).coerceIn(
                                                0f,
                                                Constraints.Infinity.toFloat(),
                                            ),
                                    )
                                offsetX.value = newValue.x
                                offsetY.value = newValue.y
                            }
                        }.clickable(onClick = {
                            onClick()
                            offsetY.value = 0f
                        })
                        .height(freeSpace.dp + offsetY.value.dp),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Text(
                    text = commentText,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .padding(horizontal = 8.dp)
                            .height(freeSpace.dp + offsetY.value.dp),
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
) {
    NavHost(
        navController,
        startDestination = startDestination.route,
    ) {
        composable(Destination.Albums().route) {
            AlbumsScreenWithScaffold(navController)
        }

        composable(Destination.Pictures().route + "/{bID}" + "/{itemsCount}") { stackEntry ->
            val bID = stackEntry.arguments?.getString("bID").toString()
            val itemsCount = stackEntry.arguments?.getString("itemsCount")?.toInt() ?: 1
            PicturesScreenWithScaffold(navController, bID, itemsCount)
        }

        composable(Destination.FullScreenImg().route) { stackEntry ->
            val mediaPosition = stackEntry.arguments?.getInt("mediaPosition")
            FullScreenViewPagerWithScaffold(navController)
        }

        composable(Destination.SearchPhoto().route) {
            SearchPhotoScreenWithScaffold(navController)
        }

        composable(Destination.Settings().route) {
            SettingsScreenWithScaffold(navController)
        }
        composable(Destination.Login().route) {
            LoginScreen(navController)
        }
        composable(Destination.Registration().route) {
            RegisterScreen(navController)
        }
    }
}
