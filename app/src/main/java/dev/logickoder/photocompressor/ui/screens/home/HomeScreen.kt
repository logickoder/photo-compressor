package dev.logickoder.photocompressor.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.logickoder.photocompressor.R
import dev.logickoder.photocompressor.ui.shared.components.Photo
import dev.logickoder.photocompressor.ui.shared.components.camera.CameraCapture
import dev.logickoder.photocompressor.ui.shared.components.gallery.GallerySelect
import dev.logickoder.photocompressor.ui.theme.basePadding
import dev.logickoder.photocompressor.ui.theme.padding
import androidx.compose.material.MaterialTheme as Theme


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) = with(viewModel) {

    val scrollState = rememberScrollState()
    var showGallery: Boolean? by remember { mutableStateOf(null) }

    showGallery?.let { show ->
        if (show)
            GallerySelect(
                modifier = modifier,
                onImageUri = { uri ->
                    selectedPhotos += uri
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
                        selectedPhotos += file.toUri()
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
        if (selectedPhotos.isEmpty())
            Text(
                text = stringResource(id = R.string.no_image),
                style = Theme.typography.h5,
            )
        else
            BoxWithConstraints {
                val spanCount = 2
                val photoSize = (maxWidth / spanCount) - (basePadding * 2)
                Column {
                    selectedPhotos.distinct().chunked(spanCount).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            row.forEach { uri ->
                                Photo(
                                    modifier = Modifier
                                        .padding(basePadding)
                                        .size(photoSize),
                                    uri = uri
                                )
                            }
                        }
                    }
                }
            }
        Spacer(modifier = Modifier.height(padding))
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { showGallery = false },
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(id = R.string.take_photo),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(padding))
            Button(
                onClick = { showGallery = true },
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(id = R.string.select_from_gallery),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.height(padding))
        Button(
            onClick = ::compress,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isCompressing.value,
        ) {
            Text(
                text = stringResource(
                    id = if (isCompressing.value) R.string.compressing else R.string.compress
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}