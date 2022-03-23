package dev.logickoder.photocompressor.ui.shared.components.ads

import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.facebook.ads.*

@Composable
fun BannerAd(
    placementId: String,
    modifier: Modifier = Modifier,
    height: AdSize = AdSize.BANNER_HEIGHT_50,
) {
    val tag = remember { "BannerAd" }
    var adLoaded by remember { mutableStateOf(false) }
    val listener = remember {
        object : AdListener {
            override fun onError(ad: Ad?, error: AdError?) {
                Log.e(tag, "code: ${error?.errorCode}, message: ${error?.errorMessage}")
            }

            override fun onAdLoaded(ad: Ad?) {
                adLoaded = true
            }

            override fun onAdClicked(p0: Ad?) {}
            override fun onLoggingImpression(p0: Ad?) {}
        }
    }
    AndroidView(
        factory = { ctx ->
            AdView(ctx, placementId, height).apply {
                loadAd(buildLoadAdConfig().withAdListener(listener).build())
            }
        },
        modifier = modifier,
    )
    if (!adLoaded) Spacer(modifier = Modifier.height(height.height.dp))
}