package com.contextphoto.utils

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.contextphoto.ui.AlbumViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

object RequestPermissions {
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun ComposePermissions(albumViewModel: AlbumViewModel = hiltViewModel()) {
        val mediaPermissionState =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                    ),
                )
            } else {
                rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ),
                )
            }

        val allPermissionsGranted = mediaPermissionState.permissions.all { it.status == PermissionStatus.Granted }
        val anyPermanentlyDenied =
            mediaPermissionState.permissions.any {
                it.status is PermissionStatus.Denied && !it.status.shouldShowRationale
            }

        LaunchedEffect(allPermissionsGranted) {
            if (allPermissionsGranted) {
                albumViewModel.loadAlbumsStateChange(true)
            }
        }

        if (allPermissionsGranted) {
            // Разрешения уже есть, ничего не делаем
            // (loadAlbumsStateChange уже вызван в LaunchedEffect)
        } else if (anyPermanentlyDenied) {
            LaunchedEffect(Unit) {
                mediaPermissionState.launchMultiplePermissionRequest()
            }
        } else {
            if (!mediaPermissionState.shouldShowRationale) {
                val context = LocalContext.current
                val intent =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                context.startActivity(intent)
            }
        }
    }
}
