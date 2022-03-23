package dev.logickoder.photocompressor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.logickoder.photocompressor.R
import dev.logickoder.photocompressor.ui.shared.components.PhotoGrid
import dev.logickoder.photocompressor.ui.shared.view_models.PhotoCompressionViewModel
import dev.logickoder.photocompressor.ui.theme.padding

@Composable
fun CompressedScreen(
    modifier: Modifier = Modifier,
    viewModel: PhotoCompressionViewModel = viewModel(),
) = with(viewModel) {

    LaunchedEffect(key1 = Unit) {
        fetchCompressedPhotos()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = padding * 2)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(padding))
        PhotoGrid(
            modifier = Modifier.fillMaxWidth(),
            photos = compressedPhotos,
        )
        if (compressedPhotos.isNotEmpty()) {
            Spacer(modifier = Modifier.height(padding))
            Button(
                onClick = ::downloadCompressedPhotos,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isDownloading.value,
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