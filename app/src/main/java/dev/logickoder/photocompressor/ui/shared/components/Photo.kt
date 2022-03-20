package dev.logickoder.photocompressor.ui.shared.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.rememberAsyncImagePainter

@Composable
fun Photo(
    uri: Uri,
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier,
        painter = rememberAsyncImagePainter(uri),
        contentDescription = null
    )
}