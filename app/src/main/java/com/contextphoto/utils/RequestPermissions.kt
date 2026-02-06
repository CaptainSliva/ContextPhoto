package com.contextphoto.utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.contextphoto.ui.AlbumViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

object RequestPermissions {
    @Composable
    fun requestPermissions() {
        val context = LocalContext.current
        val listPermissions = arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        when {
            ContextCompat.checkSelfPermission(
                context,
                listPermissions[0],
            ) == PackageManager.PERMISSION_GRANTED -> {

            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                LocalActivity.current,
                listPermissions[0]
            ) -> {
//                    val intent = Intent().apply {
//                        action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
//                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
//                    }
//                    context.startActivity(intent)
            }

            else -> {
                ActivityCompat.requestPermissions(
                    LocalActivity.current,
                    listPermissions,
                    100,
                )
            }
        }

//        listPermissions.forEach {
//            when {
//                checkSelfPermission(
//                    context,
//                    it,
//                ) == PackageManager.PERMISSION_GRANTED -> {
//
//                }
//
////                ActivityCompat.shouldShowRequestPermissionRationale(LocalActivity.current, it) -> {
////                    val intent = Intent().apply {
////                        action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
////                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
////                    }
////                    context.startActivity(intent)
////                }
//
//                else -> {
//                    requestPermissions(
//                        LocalActivity.current,
//                        listPermissions,
//                        100,
//                    )
//                }
//            }
//        }

    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun ComposePermissions(
        albumViewModel: AlbumViewModel = hiltViewModel()
    ) {
        val mediaPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberMultiplePermissionsState(
                listOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            )
        } else {
            rememberMultiplePermissionsState(
                listOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }

        val allPermissionsGranted = mediaPermissionState.permissions.all { it.status == PermissionStatus.Granted }
        val anyPermanentlyDenied = mediaPermissionState.permissions.any {
            it.status is PermissionStatus.Denied && !it.status.shouldShowRationale
        }

        if (allPermissionsGranted) {
            albumViewModel.loadAlbumsStateChange(true)
        }
        else if (anyPermanentlyDenied) {
            LaunchedEffect(Unit) {
                mediaPermissionState.launchMultiplePermissionRequest()
            }
        }
        else {
            if (!mediaPermissionState.shouldShowRationale) {
                val context = LocalContext.current
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)

            }
        }

    }

}