package com.contextphoto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.request.ImageRequest
import com.contextphoto.RequestPermissions.ComposePermissions
import com.contextphoto.data.Album
import com.contextphoto.data.AlbumListViewModel
import com.contextphoto.data.Destination
import com.contextphoto.data.FABVisible
import com.contextphoto.data.MediaViewModel
import com.contextphoto.data.albumBid
import com.contextphoto.data.bottomMenuVisible
import com.contextphoto.data.dialogVisible
import com.contextphoto.data.listpicture
import com.contextphoto.data.selectProcess
import com.contextphoto.ui.theme.ContextPhotoTheme
import com.contextphoto.utils.FunctionsMediaStore.getAllMedia
import com.contextphoto.utils.FunctionsMediaStore.getListAlbums
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.video.VideoSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File


class MainActivity() : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            ComposePermissions()
            val navController = rememberNavController()
            val startDestination = Destination.ALBUMS
            var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
            val showCreateAlbumDialog = remember { mutableStateOf(false) }


            ContextPhotoTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            // Если поменять route на label - всегда будет null title = { Text(navController.currentBackStackEntryAsState().value?.destination?.route.toString()) },
                            title = { Text(when(navController.currentBackStackEntryAsState().value?.destination?.route) {
                                "albums" -> "Альбомы"
                                "pictures" -> "Все фото"
                                "full_screen_img" -> "Картинка"
                                else -> "not! found"
                            }) },
//                            title = {Text(startDestination.label)},
                            navigationIcon = {
                                IconButton(onClick = { navController.navigateUp() }) {
                                    Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {

                        val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
                        if (currentDestination == Destination.ALBUMS.route) {
                            bottomMenuVisible.value = false
                            selectProcess.value = false
                        }
                        if (currentDestination == Destination.PICTURES.route && selectProcess.value) {
                            bottomMenuVisible.value = true
                        }

                        if (currentDestination == Destination.FULLSCREENIMG.route) {
                            FABVisible.value = false
                            bottomMenuVisible.value = false
                        }
                        else FABVisible.value = true
                        AnimatedVisibility(bottomMenuVisible.value,
                            enter = slideInVertically()
                                    + expandVertically(
                                expandFrom = Alignment.Top
                            ) + fadeIn(
                                initialAlpha = 0.3f
                            ),
                            exit = slideOutVertically() + shrinkVertically() + fadeOut())
                        {
                            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                                Destination.entries.forEachIndexed { index, destination ->
                                    NavigationBarItem(
                                        selected = selectedDestination == index,
                                        onClick = {
                                            navController.navigate(route = destination.route)
                                            selectedDestination = index
                                        },
                                        icon = {
                                            Icon(
                                                destination.icon,
                                                contentDescription = destination.contentDescription
                                            )
                                        },
                                        label = { Text(destination.label) }
                                    )
                                }
                            }
                        }
                        AnimatedVisibility(bottomMenuVisible.value,
                            enter = slideInVertically()
                                    + expandVertically(
                                expandFrom = Alignment.Top
                            ) + fadeIn(
                                initialAlpha = 0.3f
                            ),
                            exit = slideOutVertically() + shrinkVertically(
                                    shrinkTowards = Alignment.Top
                                    ) + fadeOut())
                        {
                            BottomMenu()
                        }
                    },
                    floatingActionButton = {
                        AnimatedVisibility(FABVisible.value,
                            enter = fadeIn(),
                            exit = fadeOut()
                        )
                        {
                            FloatingActionButton(onClick = {
                                FABVisible.value = false
                                dialogVisible.value = true
                            }) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    content = { paddingValues ->
                        AppNavHost(navController, startDestination, modifier = Modifier.padding(paddingValues))
                        if (dialogVisible.value) {
                            //TODO fixme Нужна viewModel, как правильно её передать?
                            CreateAlbumDialog({ dialogVisible.value = false}, showCreateAlbumDialog, viewModel)
                        }
//                        AlbumsScreen(
//                            modifier = Modifier.padding(paddingValues)
//                        )
                    }
                )

            }
        }
    }
}

@Composable
fun AlbumsScreen(modifier: Modifier = Modifier, navController: NavController, viewModel: AlbumListViewModel = AlbumListViewModel()) {

    val context = LocalContext.current
    LaunchedEffect({}) { // TODO fixme при повторном открытии повторно присылает элементы
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            getListAlbums(context, viewModel)
        }
    }
    val albumList by viewModel.albumList.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier
    )
    {
        items(
            items = albumList
        ) { album ->
            AlbumItem(
                album,
                Modifier.padding(0.dp, 2.dp),
                onItemClick = { navController.navigate(Destination.PICTURES.route) }
            )
        }
    }
}

@Composable // TODO fixme При закрытии экрана и быстром нажатии на место где была картинка - открывается картинка, хотя на экране её уже нет
fun PicturesScreen(modifier: Modifier = Modifier, navController: NavController, viewModel: MediaViewModel) {

    val context = LocalContext.current
    LaunchedEffect(Unit) { // TODO fixme при повторном открытии повторно присылает элементы
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            getAllMedia(context, albumBid, viewModel)
            viewModel.resetMediaPosition()
        }
    }

    val listMedia by viewModel.listPictures.collectAsState()

    Column(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
        ) {

            items(items = listMedia) { media ->
                println("\n\nPRIIIINT\n${listMedia.size}\nIIITT\n$media\n")
                PictureItem(
                listMedia.indexOf(media),
                    media,
                    Modifier.padding(1.dp),
                    onItemClick = { navController.navigate(Destination.FULLSCREENIMG.route) },
                    viewModel
                )
            }
        }
    }
}

@Composable
fun FullScreenViewPager(modifier: Modifier = Modifier, navController: NavController, viewModel: MediaViewModel) {

    val listMedia by viewModel.listPictures.collectAsState()
    val mediaPosotion by viewModel.mediaPosition.collectAsState()
    val pagerState = rememberPagerState(initialPage = mediaPosotion, pageCount = { listMedia.size })

    Log.d("POSITION", mediaPosotion.toString())


    HorizontalPager(state = pagerState) { page -> // TODO add ViewPager и subsampling-scale-image-view
        // Our page content
        val media = listMedia[page]
        viewModel.updateMediaPosition(pagerState.settledPage)
        Log.d("POSITION page", page.toString())
        Box(modifier = Modifier.fillMaxSize())
        {
            Log.d("POSITION page", listMedia[page].toString())
//            AsyncImage(
//                model = media.uri,
//                contentScale = ContentScale.Fit,
//                contentDescription = "Example Image",
//                modifier = Modifier.background(Color.Black).fillMaxSize(),
////                placeholder = painterResource(id = R.drawable.placeholder), // Replace with your placeholder drawable
////                error = painterResource(id = R.drawable.error)  // Replace with your error drawable
//            )
            if (media.path.contains("VID")) {
                NewVideoUI(media.uri) // TODO add контроллер и слежка за состоянием https://kotlincodes.com/kotlin/jetpack-compose-kotlin/jetpack-compose-media-player-integration/

            } else {
                ImageScreenUI(media.uri, media.path)
            }
            Text(
                textAlign = TextAlign.Center,
                text = "Page: $page",
                modifier = Modifier.fillMaxSize()
            )
        }
    }

}


@Composable
fun ExoPlayerView(uri: Uri,
                  onComplete: () -> Unit = {}) {
    val isVisible = remember { mutableStateOf(true) }
    val videoHeight = remember { mutableStateOf(0) }
    val videoWidth = remember { mutableStateOf(0) }
    val context = LocalContext.current
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.Builder().setUri(uri).build()
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            StyledPlayerView(context).apply {
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                player = exoPlayer
                setShowBuffering(StyledPlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                useController = false // Hide playback controls
                exoPlayer.addListener(object : Player.Listener {
                    override fun onVideoSizeChanged(videoSize: VideoSize) {
                        videoWidth.value = videoSize.width
                        videoHeight.value = videoSize.height
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            onComplete()
                        }
                    }
                })
            }
        }
    )
}

@Composable // https://gorkemkara.net/responsive-video-playback-jetpack-compose-exoplayer/
fun NewVideoUI(uri: Uri, onComplete: () -> Unit = {}) {

    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.Builder().setUri(uri).build()
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = false
        }
    }

    AndroidView(
        factory = {
            StyledPlayerView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                this.player = exoPlayer

                // Автоматическое скрытие контролов
                setControllerAutoShow(false)

                // Обработка касаний
                setControllerHideOnTouch(true)
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { view ->
            // Обновление при необходимости
        }
    )
}

@Composable
fun MediaPlayerControlUI(uri: Uri) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(uri)
            setMediaItem(mediaItem)
            prepare()
        }
    }
    var playbackState by remember { mutableStateOf(Player.STATE_IDLE) }

    val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            playbackState = state
        }
    }

    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(exoPlayer) {
        exoPlayer.addListener(playerListener)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "Playback State: ${
            when (playbackState) {
                Player.STATE_IDLE -> "Idle"
                Player.STATE_BUFFERING -> "Buffering"
                Player.STATE_READY -> "Ready"
                Player.STATE_ENDED -> "Ended"
                else -> "Unknown"
            }
        }")

        Spacer(modifier = Modifier.height(16.dp))

        IconButton(onClick = {
            if (isPlaying) {
                exoPlayer.pause()
            } else {
                exoPlayer.play()
            }
            isPlaying = !isPlaying
        }) {
            Icon( // ExitToApp иконка т.к иконки Pause нету
                imageVector = if (isPlaying) Icons.Filled.ExitToApp else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play"
            )
        }

        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.removeListener(playerListener)
                exoPlayer.release()
            }
        }
    }
}

@Composable
fun ImageScreenUI(uri: Uri, path: String) {
    AndroidView(
        factory = { ctx ->
            SubsamplingScaleImageView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                // Загрузка изображения через Coil
//                val imageLoader = ImageLoader(ctx)
//                val request = ImageRequest.Builder(ctx)
//                    .data(uri)
//                    .allowHardware(false)
//                    .build()
//
//                imageLoader.enqueue(request)
                // Заработает?
                setImage(ImageSource.uri(Uri.fromFile(File(path)))) // Костыль кажется
                // Загрузка изображения через Glide
//                            Glide.with(context)
//                                .asFile()
//                                .load(media.uri)
//                                .into(object : CustomTarget<File>() {
//                                    override fun onResourceReady(
//                                        resource: File,
//                                        transition: Transition<in File>?
//                                    ) {
//                                        setImage(ImageSource.uri(Uri.fromFile(resource)))
//                                    }
//
//                                    override fun onLoadCleared(placeholder: Drawable?) {
//                                        recycle()
//                                    }
//                                })
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun BottomMenu() {
    // TODO fixme при отмене диалога нажатием на пустое место (я ни где это не прописывал) - не возвращается исходный вид view
    val context = LocalContext.current
    val sendDialogVisible = remember { mutableStateOf(false) }
    val commentateDialogVisible = remember { mutableStateOf(false) }
    val toAlbumDialogVisible = remember { mutableStateOf(false) } // заготовка
    val deleteDialogVisible = remember { mutableStateOf(false) }

    // TODO add? ListMedia поделиться, повернуть, комментировать, удалить
    // TODO add? FullScreen add? поделиться, в альбом, повернуть, комментировать, удалить
    // TODO add? ListAlbums переименовать, удалить

//    AnimatedVisibility(visible = sendDialogVisible.value, enter = slideInVertically(),
//        exit = slideOutVertically()) {
//        deleteDialog({}, Album("", "", 0,listpicture[0].thumbnail, File("")), false, deleteDialogVisible)
//    }
    AnimatedVisibility(visible = commentateDialogVisible.value, enter = slideInVertically(),
        exit = slideOutVertically()) {
        CommentateDialog({}, commentateDialogVisible)
    }
//    AnimatedVisibility(visible = toAlbumDialogVisible.value, enter = slideInVertically(),
//        exit = slideOutVertically()) {
//        deleteDialog({}, Album("", "", 0,listpicture[0].thumbnail, File("")), false, deleteDialogVisible)
//    }
    AnimatedVisibility(visible = deleteDialogVisible.value, enter = slideInVertically(),
        exit = slideOutVertically()) { // TODO fixme не работает удаление фото, видимо нужно их удаление в отдельную функцию вынести
        DeleteDialog({}, Album("", "", 0,listpicture[0].thumbnail, File("")), false, deleteDialogVisible)
    }


    Row(modifier = Modifier
        .background(Color.Black)
        .fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Column(modifier = Modifier.padding(8.dp, 16.dp).combinedClickable(
            onClick = {
                if (listpicture.isNotEmpty()) {
                    // val sendCommentText = db.findImageByHash(md5(it.thumbnail))
                    val sendIntent = Intent()
                    sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE)
                    sendIntent.setType("*/*")
                    sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(listpicture.map { it.uri }))
//                        sendIntent.putExtra(Intent.EXTRA_TEXT, sendCommentText)
                    //context.startActivity(sendIntent)
                    context.startActivity(Intent.createChooser(sendIntent, null))
                }
            },
            onLongClick = {}
        ),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(Icons.Outlined.Share, contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White))
            Text(text = context.getString(R.string.share),
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.white))
        }
        Column(modifier = Modifier.padding(8.dp, 16.dp).combinedClickable(
            onClick = {
                commentateDialogVisible.value = true
            },
            onLongClick = {}
        ),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(Icons.Outlined.Create, contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White))
            Text(text = context.getString(R.string.commentate),
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.white))
        }
        Column(modifier = Modifier.padding(8.dp, 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(Icons.Outlined.Add, contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White))
            Text(text = context.getString(R.string.to_album),
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.white))
        }
        Column(modifier = Modifier.padding(8.dp, 16.dp).combinedClickable(
            onClick = {
                deleteDialogVisible.value = true
            },
            onLongClick = {}
        ),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(Icons.Outlined.Delete, contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White))
            Text(text = context.getString(R.string.delete),
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.white))
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
    mediaViewModel: MediaViewModel = viewModel(),
    albumViewModel: AlbumListViewModel = viewModel()
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {

        composable(Destination.ALBUMS.route) {
            AlbumsScreen(modifier, navController, albumViewModel)
        }

        composable(Destination.PICTURES.route) {
            PicturesScreen(modifier, navController, mediaViewModel)
        }

        composable(Destination.FULLSCREENIMG.route) {
            com.contextphoto.FullScreenViewPager(modifier, navController, mediaViewModel)
        }

    }
}

//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    ContextPhotoTheme {
//        //PicturesScreen()
//    }
//}