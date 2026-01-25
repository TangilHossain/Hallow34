package com.shawonshagor0.hallow34.presentation.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.InputStream

fun uriToFile(context: Context, uri: Uri): File {
    val inputStream: InputStream =
        context.contentResolver.openInputStream(uri)!!

    val file = File.createTempFile("upload", ".jpg", context.cacheDir)
    file.outputStream().use { inputStream.copyTo(it) }
    return file
}
