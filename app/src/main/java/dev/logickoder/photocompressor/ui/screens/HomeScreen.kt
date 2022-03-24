package dev.logickoder.photocompressor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import dev.logickoder.photocompressor.ui.shared.view_models.PhotoCompressionViewModel.Companion.DEFAULT_COMPRESSION_QUALITY
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
    navigateToCompressedScreen: () -> Unit,
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(horizontal = padding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BannerAd(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = padding),
                placementId = BannerId,
            )

            if (selectedPhotos.isEmpty())
                Text(
                    text = stringResource(id = R.string.no_selected_image),
                    style = Theme.typography.h6,
                )
            else
                PhotoGrid(
                    modifier = Modifier
                        .padding(bottom = padding)
                        .fillMaxWidth(),
                    photos = selectedPhotos,
                )

            val buttonTextStyle = Theme.typography.button.run {
                copy(fontSize = fontSize * 1.2)
            }
            Button(
                onClick = { showGallery = false },
                modifier = Modifier
                    .padding(vertical = padding)
                    .fillMaxWidth()
                    .height(TextFieldDefaults.MinHeight),
                shape = Theme.shapes.medium,
            ) {
                Text(
                    text = stringResource(id = R.string.take_photo),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = buttonTextStyle,
                )
            }

            Box(
                modifier = Modifier
                    .padding(vertical = padding)
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
                        .align(Alignment.CenterEnd)
                        .height(TextFieldDefaults.MinHeight),
                    onClick = { showGallery = true },
                    shape = Theme.shapes.medium,
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.select_from_gallery,
                            photoSelection.value.name
                        ),
                        style = buttonTextStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(vertical = padding)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .fillMaxHeight(),
                    value = compressionQuality.value.toString(),
                    label = { Text(stringResource(id = R.string.quality)) },
                    onValueChange = {
                        compressionQuality.value =
                            if (it.isBlank()) 0 else it.toIntOrNull()?.let { input ->
                                if (input > 100) 100 else if (input < 0) 0 else input
                            } ?: DEFAULT_COMPRESSION_QUALITY
                    },
                    textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Medium),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    shape = Theme.shapes.medium,
                )
                Button(
                    onClick = {
                        interstitialAd(InterstitialId, context)
                        compressPhotos(interstitialAd, navigateToCompressedScreen)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .align(Alignment.CenterEnd)
                        .height(TextFieldDefaults.MinHeight),
                    enabled = !isCompressing.value,
                    shape = Theme.shapes.medium,
                ) {
                    Text(
                        text = stringResource(
                            id = if (isCompressing.value) R.string.compressing else R.string.compress
                        ),
                        style = buttonTextStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            NativeAd(
                modifier = Modifier.fillMaxWidth(),
                placementId = NativeId,
            )
        }
    }
}