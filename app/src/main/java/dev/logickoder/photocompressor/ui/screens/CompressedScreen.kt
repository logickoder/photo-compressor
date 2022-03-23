package dev.logickoder.photocompressor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
    }
}