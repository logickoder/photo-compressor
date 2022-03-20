package dev.logickoder.photocompressor.ui.screens.home

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val selectedPhotos = mutableStateListOf<Uri>()
    val isCompressing = mutableStateOf(false)

    fun compress() = viewModelScope.launch(Dispatchers.IO) {
        isCompressing.value = true
        val context = getApplication<Application>().baseContext
        selectedPhotos.distinct().forEachIndexed { index, uri ->
            // the location for the compressed image
            val file = File(context.externalCacheDir, "compressed-$index.png")
            if (!file.exists()) file.createNewFile()
            Log.d(
                "HomeViewModel",
                "compressImage: Saving compressed image in ${file.absolutePath}"
            )
            // get the bitmap from the uri
            // initialize a file output stream to store the compressed image
            val stream = FileOutputStream(file)
            val bitmap =
                BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
            // compress the bitmap
            bitmap.compress(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    Bitmap.CompressFormat.WEBP_LOSSLESS
                else
                    Bitmap.CompressFormat.WEBP,
                80,
                stream
            )
        }
        isCompressing.value = false
    }
}