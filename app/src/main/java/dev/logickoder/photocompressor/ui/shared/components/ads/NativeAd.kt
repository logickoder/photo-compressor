package dev.logickoder.photocompressor.ui.shared.components.ads

import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.facebook.ads.*

@Composable
fun NativeAd(
    modifier: Modifier = Modifier,
    placementId: String,
) {
    val tag = remember { "NativeAd" }
    val context = LocalContext.current
    var adLoaded by remember { mutableStateOf(false) }
    val nativeAd = remember { NativeAd(context, placementId) }
    val listener = remember {
        object : NativeAdListener {
            override fun onError(ad: Ad?, error: AdError?) {
                Log.e(tag, "code: ${error?.errorCode}, message: ${error?.errorMessage}")
            }

            override fun onAdLoaded(ad: Ad?) {
                // Race condition, load() called again before last ad was displayed
                adLoaded = true
            }

            override fun onAdClicked(p0: Ad?) {}
            override fun onLoggingImpression(p0: Ad?) {}
            override fun onMediaDownloaded(p0: Ad?) {}
        }
    }
    nativeAd.loadAd(
        nativeAd
            .buildLoadAdConfig()
            .withAdListener(listener)
            .withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL)
            .build()
    )
    if (adLoaded) AndroidView(
        factory = { ctx ->
            nativeAd.unregisterView()
            NativeAdLayout(ctx).apply {
                addView(
                    NativeAdView.render(ctx, nativeAd),
                    FrameLayout.LayoutParams(MATCH_PARENT, 800)
                )
            }
        },
        modifier = modifier,
    )
}