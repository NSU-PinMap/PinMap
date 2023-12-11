package ru.nsu.ccfit.tsd.pinmap.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract

class PickImageContract : ActivityResultContract<List<Uri>, List<Uri>>() {

    override fun createIntent(context: Context, input: List<Uri>): Intent {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        //TODO отправлять по интенту входящие фотки

        return galleryIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        val photosList = mutableListOf<Uri>()

        when {
            resultCode == Activity.RESULT_OK && intent != null -> {
                if (intent.clipData != null) {
                    // Handle multiple selected images
                    for (i in 0 until intent.clipData!!.itemCount) {
                        val selectedImageUri: Uri = intent.clipData!!.getItemAt(i).uri
                        photosList.add(selectedImageUri)
                    }
                } else if (intent.data != null) {
                    // Handle single selected image
                    val selectedImageUri: Uri = intent.data!!
                    photosList.add(selectedImageUri)
                }
            }

            else -> return listOf()
        }

        return photosList
    }
}