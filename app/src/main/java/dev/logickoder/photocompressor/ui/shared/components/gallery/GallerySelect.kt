package dev.logickoder.photocompressor.ui.shared.components.gallery

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.logickoder.photocompressor.util.Permission

@Composable
fun GallerySelect(
    modifier: Modifier = Modifier,
    onImageUri: (List<Uri>) -> Unit = { }
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uri: List<Uri>? ->
            onImageUri(uri ?: emptyList())
        }
    )

    @Composable
    fun LaunchGallery() {
        SideEffect {
            launcher.launch("image/*")
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Permission(
            permission = Manifest.permission.ACCESS_MEDIA_LOCATION,
            rationale = "You want to read from photo gallery, so I'm going to have to ask for permission.",
            permissionNotAvailableContent = {
                Column(modifier) {
                    Text("O noes! No Photo Gallery!")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(
                            modifier = Modifier.padding(4.dp),
                            onClick = {
                                context.startActivity(
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                )
                            }
                        ) {
                            Text("Open Settings")
                        }
                        // If they don't want to grant permissions, this button will result in going back
                        Button(
                            modifier = Modifier.padding(4.dp),
                            onClick = {
                                onImageUri(emptyList())
                            }
                        ) {
                            Text("Use Camera")
                        }
                    }
                }
            },
        ) {
            LaunchGallery()
        }
    } else {
        LaunchGallery()
    }
}

val EMPTY_IMAGE_URI: Uri = Uri.parse("file://dev/null")