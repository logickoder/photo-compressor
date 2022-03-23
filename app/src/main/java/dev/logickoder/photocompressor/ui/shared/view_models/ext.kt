package dev.logickoder.photocompressor.ui.shared.view_models

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileNotFoundException
import java.text.StringCharacterIterator


val Long.humanReadableByteCount: String
    get() {
        var bytes = this
        if (-1000 < bytes && bytes < 1000) {
            return "$bytes B"
        }
        val characterIterator = StringCharacterIterator("kMGTPE")
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000
            characterIterator.next()
        }
        return "%.1f %cB".format(bytes / 1000.0, characterIterator.current())
    }

fun Uri.fileName(contentResolver: ContentResolver): String? {
    return kotlin.runCatching {
        when (scheme) {
            ContentResolver.SCHEME_CONTENT -> contentResolver.query(
                this, null, null, null, null
            )?.use { cursor ->
                cursor.moveToFirst()
                return@use cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                    .let(cursor::getString)
            }
            else -> path?.let(::File)?.name
        }
    }.getOrNull()
}

fun Uri.size(contentResolver: ContentResolver): Long {
    val assetFileDescriptor = try {
        contentResolver.openAssetFileDescriptor(this, "r")
    } catch (e: FileNotFoundException) {
        null
    }
    // uses ParcelFileDescriptor#getStatSize underneath if failed
    val length = assetFileDescriptor?.use { it.length } ?: -1L
    if (length != -1L) {
        return length
    }

    // if "content://" uri scheme, try contentResolver table
    return if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
        contentResolver.query(this, arrayOf(OpenableColumns.SIZE), null, null, null)
            ?.use { cursor ->
                // maybe shouldn't trust ContentResolver for size: https://stackoverflow.com/questions/48302972/content-resolver-returns-wrong-size
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex == -1) {
                    return@use -1L
                }
                cursor.moveToFirst()
                return try {
                    cursor.getLong(sizeIndex)
                } catch (_: Throwable) {
                    -1L
                }
            } ?: -1L
    } else -1L
}