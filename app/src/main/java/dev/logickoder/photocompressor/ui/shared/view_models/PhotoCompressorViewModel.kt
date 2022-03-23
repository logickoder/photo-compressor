package dev.logickoder.photocompressor.ui.shared.view_models

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.ads.InterstitialAd
import dev.logickoder.photocompressor.ui.shared.components.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

enum class PhotoSelection(val type: String) {
    Any("*"),
    Png("png"),
}

class PhotoCompressionViewModel(application: Application) : AndroidViewModel(application) {
    val selectedPhotos = mutableStateListOf<Photo>()
    val compressedPhotos = mutableStateListOf<Photo>()
    val isCompressing = mutableStateOf(false)
    val isDownloading = mutableStateOf(false)
    val photoSelection = mutableStateOf(PhotoSelection.Any)
    val compressionQuality = mutableStateOf(DEFAULT_COMPRESSION_QUALITY)
    private val app = getApplication<Application>()

    fun selectPhotos(vararg uri: Uri) {
        selectedPhotos += uri.map { Photo(it, it.size(app.contentResolver).humanReadableByteCount) }
    }

    fun compressPhotos(
        interstitialAd: InterstitialAd,
        navigateToCompressedScreen: () -> Unit,
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (selectedPhotos.isNotEmpty()) {
            isCompressing.value = true
            val context = getApplication<Application>().baseContext
            selectedPhotos.distinct().forEach { photo ->
                // the location for the compressed image
                val file =
                    File(
                        context.externalCacheDir,
                        photo.uri.fileName(app.contentResolver).toString()
                    )
                if (!file.exists()) file.createNewFile()
                // get the bitmap from the uri
                // initialize a file output stream to store the compressed image
                val stream = FileOutputStream(file)
                val bitmap =
                    BitmapFactory.decodeStream(context.contentResolver.openInputStream(photo.uri))
                // compress the bitmap
                bitmap.compress(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        Bitmap.CompressFormat.WEBP_LOSSLESS
                    else
                        Bitmap.CompressFormat.WEBP,
                    compressionQuality.value,
                    stream
                )
            }
            isCompressing.value = false
            selectedPhotos.removeAll { true }
            withContext(Dispatchers.Main) {
                navigateToCompressedScreen()
                if (interstitialAd.isAdLoaded) interstitialAd.show()
            }
        }
    }

    fun fetchCompressedPhotos() = viewModelScope.launch {
        compressedPhotos.removeIf { true }
        getApplication<Application>().baseContext.externalCacheDir?.listFiles()?.forEach {
            compressedPhotos.add(Photo(it.toUri(), it.length().humanReadableByteCount))
        }
    }

    fun downloadCompressedPhotos() = viewModelScope.launch(Dispatchers.IO) {
        if (compressedPhotos.isNotEmpty()) {
            isDownloading.value = true
            val context = getApplication<Application>().baseContext
            val downloadFolder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "compressed"
            )
            while (compressedPhotos.isNotEmpty()) {
                try {
                    val photo = compressedPhotos.first()
                    val source = context.contentResolver.openInputStream(photo.uri)!!
                    val destination = FileOutputStream(
                        File(
                            downloadFolder,
                            photo.uri.fileName(app.contentResolver).toString()
                        ).also { if (!it.exists()) it.createNewFile() }
                    )
                    val buf = ByteArray(1024)
                    var len: Int
                    while (Unit.let { len = source.read(buf); len } > 0) {
                        destination.write(buf, 0, len)
                    }
                    source.close()
                    destination.close()
                } catch (e: Exception) {
                    Log.e(TAG, "downloadCompressedPhotos: ${e.message}")
                } finally {
                    compressedPhotos.removeAt(0)
                }
            }
            isDownloading.value = false
        }
    }

    companion object {
        const val TAG = "PhotoCompressorViewModel"
        const val DEFAULT_COMPRESSION_QUALITY = 80
    }
}