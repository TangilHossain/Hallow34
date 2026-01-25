package com.shawonshagor0.hallow34.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun rememberImagePicker(
    context: Context,
    onImagePicked: (Uri) -> Unit
): () -> Unit {

    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri?.let { onImagePicked(it) }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onImagePicked(it) }
    }

    return {
        val imageFile = File.createTempFile(
            "camera_image",
            ".jpg",
            context.cacheDir
        )

        cameraImageUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        }

        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }

        val chooser = Intent.createChooser(galleryIntent, "Select Image").apply {
            putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                arrayOf(cameraIntent)
            )
        }

        context.startActivity(chooser)

        // fallback for activity result
        galleryLauncher.launch("image/*")
    }
}
