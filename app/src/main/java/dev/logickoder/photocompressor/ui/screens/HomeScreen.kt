package dev.logickoder.photocompressor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.logickoder.photocompressor.R
import dev.logickoder.photocompressor.ui.shared.components.DropdownField
import dev.logickoder.photocompressor.ui.shared.components.PhotoGrid
import dev.logickoder.photocompressor.ui.shared.components.ads.BannerAd
import dev.logickoder.photocompressor.ui.shared.components.ads.NativeAd
import dev.logickoder.photocompressor.ui.shared.components.ads.interstitialAd
import dev.logickoder.photocompressor.ui.shared.components.camera.CameraCapture
import dev.logickoder.photocompressor.ui.shared.components.gallery.GallerySelect
import dev.logickoder.photocompressor.ui.shared.view_models.PhotoCompressionViewModel
import dev.logickoder.photocompressor.ui.shared.view_models.PhotoSelection
import dev.logickoder.photocompressor.ui.theme.padding
import dev.logickoder.photocompressor.util.BannerId
import dev.logickoder.photocompressor.util.InterstitialId
import dev.logickoder.photocompressor.util.NativeId
import androidx.compose.material.MaterialTheme as Theme


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: PhotoCompressionViewModel = viewModel(),
) = with(viewModel) {

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showGallery: Boolean? by remember { mutableStateOf(null) }
    val interstitialAd = remember { interstitialAd(InterstitialId, context) }

    showGallery?.let { show ->
        if (show)
            GallerySelect(
                modifier = modifier,
                type = photoSelection.value.type,
                onImageUri = { uri ->
                    selectPhotos(*uri.toTypedArray())
                    showGallery = null
                }
            )
        else
            Dialog(
                onDismissRequest = { showGallery = null },
                properties = DialogProperties(dismissOnClickOutside = false)
            ) {
                CameraCapture(
                    modifier = modifier,
                    onImageFile = { file ->
                        selectPhotos(file.toUri())
                        showGallery = null
                    }
                )
            }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = padding)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BannerAd(
            modifier = Modifier.fillMaxWidth(),
            placementId = BannerId,
        )
        if (selectedPhotos.isEmpty())
            Text(
                text = stringResource(id = R.string.no_image),
                style = Theme.typography.h5,
            )
        else {
            Spacer(modifier = Modifier.height(padding))
            PhotoGrid(
                modifier = Modifier.fillMaxWidth(),
                photos = selectedPhotos,
            )
        }
        Spacer(modifier = Modifier.height(padding))
        Button(
            onClick = { showGallery = false },
            modifier = Modifier.fillMaxWidth(),
            shape = Theme.shapes.medium,
        ) {
            Text(
                text = stringResource(id = R.string.take_photo),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.height(padding))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            DropdownField(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .fillMaxHeight(),
                suggested = photoSelection.value,
                suggestions = PhotoSelection.values().toList(),
                onSuggestionSelected = { photoSelection.value = it }
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .align(Alignment.CenterEnd),
                onClick = { showGallery = true },
                shape = Theme.shapes.medium,
            ) {
                Text(
                    text = stringResource(
                        id = R.string.select_from_gallery,
                        photoSelection.value.name
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.height(padding))
        Button(
            onClick = {
                compressPhotos()
                if (interstitialAd.isAdLoaded) interstitialAd.show()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isCompressing.value,
            shape = Theme.shapes.medium,
        ) {
            Text(
                text = stringResource(
                    id = if (isCompressing.value) R.string.compressing else R.string.compress
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.height(padding))
        NativeAd(
            modifier = Modifier.fillMaxWidth(),
            placementId = NativeId,
        )
    }
}