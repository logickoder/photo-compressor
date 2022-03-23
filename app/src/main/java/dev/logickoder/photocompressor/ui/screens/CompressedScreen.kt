package dev.logickoder.photocompressor.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.logickoder.photocompressor.R
import dev.logickoder.photocompressor.ui.shared.components.PhotoGrid
import dev.logickoder.photocompressor.ui.shared.components.ads.BannerAd
import dev.logickoder.photocompressor.ui.shared.view_models.PhotoCompressionViewModel
import dev.logickoder.photocompressor.ui.theme.padding
import dev.logickoder.photocompressor.util.BannerId
import dev.logickoder.photocompressor.util.Permission
import kotlinx.coroutines.launch

@Composable
fun CompressedScreen(
    modifier: Modifier = Modifier,
    viewModel: PhotoCompressionViewModel = viewModel(),
) = with(viewModel) {

    val context = LocalContext.current
    Permission(
        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
        rationale = "You want to download a photo, so I'm going to have to ask for permission.",
        permissionNotAvailableContent = {
            Column(modifier) {
                Text("O noes! Can't access storage!")
                Spacer(modifier = Modifier.height(8.dp))
                Button(
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
            }
        }
    )

    LaunchedEffect(key1 = Unit) {
        fetchCompressedPhotos()
    }

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    Scaffold(modifier = modifier, scaffoldState = scaffoldState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = padding * 2)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(padding))
            BannerAd(
                modifier = Modifier.fillMaxWidth(),
                placementId = BannerId,
            )
            Spacer(modifier = Modifier.height(padding))
            PhotoGrid(
                modifier = Modifier.fillMaxWidth(),
                photos = compressedPhotos,
            )
            Spacer(modifier = Modifier.height(padding))
            val savedTo = stringResource(id = R.string.saved_to)
            Button(
                onClick = {
                    downloadCompressedPhotos { dir ->
                        scope.launch {
                            scaffoldState.snackbarHostState.showSnackbar("$savedTo $dir")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = compressedPhotos.isNotEmpty() && !isDownloading.value,
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(
                    text = stringResource(
                        id = if (isCompressing.value) R.string.downloading else R.string.download,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}