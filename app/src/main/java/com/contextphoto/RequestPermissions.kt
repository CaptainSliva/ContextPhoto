package com.contextphoto

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
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
            checkSelfPermission(
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
                requestPermissions(
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
    fun ComposePermissions() {
        val context = LocalContext.current

        // Register and remember the permission state
        val callPermissionState = rememberPermissionState(android.Manifest.permission.READ_MEDIA_IMAGES)

        // Utility function to calculate the state based on the PermissionState
        fun getScreenState(state: PermissionState) = when (state.status) {
            is PermissionStatus.Denied -> PermissionScreenState(
                title = "Call a phone", buttonText = "Grant permission"
            )

            PermissionStatus.Granted -> PermissionScreenState(
                title = "You can now call!", buttonText = "Call"
            )
        }

        // Defines the PermissionScreen UI based on the permission state and user interactions
        var screenState by remember(callPermissionState.status) {
            mutableStateOf(getScreenState(callPermissionState))
        }

        PermissionScreen(
            state = screenState,
            onClick = {
                // Always request permissions in-context, provide a rationale if needed and check its status
                // before using an API that requires a permission.
                when (callPermissionState.status) {
                    PermissionStatus.Granted -> {
                        Toast.makeText(context, "Faking a call...", Toast.LENGTH_SHORT).show()
                    }

                    is PermissionStatus.Denied -> {
                        if (callPermissionState.status.shouldShowRationale) {
                            // Update our UI based on the user interaction by showing a rationale
                            screenState = PermissionScreenState(
                                title = "Call a phone",
                                buttonText = "Grant permission",
                                rationale = "In order to perform the call you need to grant access by accepting the next permission dialog.\n\nWould you like to continue?"
                            )
                        } else {
                            // Directly launch the system permission dialog
                            callPermissionState.launchPermissionRequest()
                        }
                    }
                }
            },
            onRationaleReply = { accepted ->
                if (accepted) {
                    callPermissionState.launchPermissionRequest()
                }

                // Reset the state after user interaction
                screenState = getScreenState(callPermissionState)
            }
        )
    }

}