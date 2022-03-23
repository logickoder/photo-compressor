package dev.logickoder.photocompressor.ui.shared.components.ads

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.facebook.ads.*

@Composable
fun BannerAd(
    placementId: String,
    modifier: Modifier = Modifier,
    height: AdSize = AdSize.BANNER_HEIGHT_50,
) {
    val tag = remember { "BannerAd" }
    val listener = remember {
        object : AdListener {
            override fun onError(ad: Ad?, error: AdError?) {
                Log.e(tag, "code: ${error?.errorCode}, message: ${error?.errorMessage}")
            }

            override fun onAdLoaded(ad: Ad?) {}
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
}