package dev.logickoder.photocompressor

import android.app.Application
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds


class PhotoCompressorApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initAds()
    }

    private fun initAds() {
        MobileAds.initialize(this) {}
        // Example for setting the SDK to crash when in debug mode
        AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE);
        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);
    }
}