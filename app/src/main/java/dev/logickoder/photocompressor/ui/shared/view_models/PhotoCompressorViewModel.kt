package dev.logickoder.photocompressor.ui.shared.view_models

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.logickoder.photocompressor.ui.shared.components.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    val photoSelection = mutableStateOf(PhotoSelection.Any)
    private val app = getApplication<Application>()

    fun selectPhotos(vararg uri: Uri) {
        selectedPhotos += uri.map { Photo(it, it.size(app.contentResolver).humanReadableByteCount) }
    }

    fun compressPhotos() = viewModelScope.launch(Dispatchers.IO) {
        isCompressing.value = true

        val context = getApplication<Application>().baseContext
        selectedPhotos.distinct().forEach { photo ->
            // the location for the compressed image
            val file =
                File(context.externalCacheDir, photo.uri.fileName(app.contentResolver).toString())
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
                70,
                stream
            )
        }
        isCompressing.value = false
    }

    fun fetchCompressedPhotos() = viewModelScope.launch {
        compressedPhotos.removeIf { true }
        getApplication<Application>().baseContext.externalCacheDir?.listFiles()?.forEach {
            compressedPhotos.add(Photo(it.toUri(), it.length().humanReadableByteCount))
        }
    }
}