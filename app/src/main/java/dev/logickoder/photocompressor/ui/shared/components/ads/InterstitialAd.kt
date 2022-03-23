package dev.logickoder.photocompressor.ui.shared.components.ads

import android.content.Context
import android.util.Log
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener

fun interstitialAd(
    placementId: String,
    context: Context,
): InterstitialAd {
    return InterstitialAd(context, placementId).apply {
        val tag = "InterstitialAd"
        val listener = object : InterstitialAdListener {
            override fun onError(ad: Ad?, error: AdError?) {
                Log.e(
                    tag,
                    "failed to load, error code: ${error?.errorCode}, message: ${error?.errorMessage}"
                )
            }

            override fun onAdLoaded(ad: Ad?) {}
            override fun onAdClicked(ad: Ad?) {}
            override fun onLoggingImpression(ad: Ad?) {}
            override fun onInterstitialDisplayed(ad: Ad?) {}
            override fun onInterstitialDismissed(ad: Ad?) {
                loadAd(buildLoadAdConfig().withAdListener(this).build())
            }
        }
        loadAd(buildLoadAdConfig().withAdListener(listener).build())
    }
}