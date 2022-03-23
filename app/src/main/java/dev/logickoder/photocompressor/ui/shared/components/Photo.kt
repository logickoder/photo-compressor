package dev.logickoder.photocompressor.ui.shared.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import coil.compose.rememberAsyncImagePainter
import dev.logickoder.photocompressor.ui.theme.padding

data class Photo(val uri: Uri, val size: String)

@Composable
fun Photo(
    photo: Photo,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val corner = MaterialTheme.shapes.large
        Image(
            modifier = Modifier
                .clip(corner)
                .fillMaxSize(),
            painter = rememberAsyncImagePainter(photo.uri),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .alpha(0.7f)
                .clip(
                    RoundedCornerShape(
                        CornerSize(0), CornerSize(0), corner.bottomStart, corner.bottomEnd
                    )
                )
                .background(MaterialTheme.colors.onBackground)
                .padding(vertical = padding)
                .width(maxWidth)
                .align(Alignment.BottomCenter),
            text = photo.size,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.background,
        )
    }
}

@Composable
fun PhotoGrid(
    photos: List<Photo>,
    modifier: Modifier = Modifier,
    spanCount: Int = 2,
) {
    BoxWithConstraints(modifier = modifier) {
        val gridGap = padding * 2
        val photoSize = (maxWidth / spanCount) - (gridGap * (spanCount - 1))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(gridGap),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            photos.chunked(spanCount).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    row.forEach { photo ->
                        Photo(
                            modifier = Modifier.requiredSize(photoSize),
                            photo = photo
                        )
                    }
                }
            }
        }
    }
}