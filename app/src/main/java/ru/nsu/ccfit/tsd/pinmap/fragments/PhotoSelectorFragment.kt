package ru.nsu.ccfit.tsd.pinmap.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.launch
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.nsu.ccfit.tsd.pinmap.R
import ru.nsu.ccfit.tsd.pinmap.photo.Photo
import ru.nsu.ccfit.tsd.pinmap.photo.PhotoController

class PhotoSelectorFragment : Fragment() {
    private lateinit var photoController: PhotoController


    private val pickImageLauncher =
        registerForActivityResult(PickImageContract()) { photos ->
            photoController.selectedPhotos = photos
            close()
        }

    private fun close() {
        findNavController().popBackStack()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        openGallery()
        photoController = context?.let { PhotoController.getController(it) }!!
        return inflater.inflate(R.layout.fragment_photo_selector, container, false)
    }

    private fun openGallery() {
        pickImageLauncher.launch()
    }

}

class PickImageContract : ActivityResultContract<Unit, List<Photo>>() {

    override fun createIntent(context: Context, input: Unit): Intent {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        return galleryIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Photo> {
        val photosList = mutableListOf<Photo>()

        when {
            resultCode == Activity.RESULT_OK && intent != null -> {
                if (intent.clipData != null) {
                    // Handle multiple selected images
                    for (i in 0 until intent.clipData!!.itemCount) {
                        val selectedImageUri: Uri = intent.clipData!!.getItemAt(i).uri
                        photosList.add(Photo(selectedImageUri))
                    }
                } else if (intent.data != null) {
                    // Handle single selected image
                    val selectedImageUri: Uri = intent.data!!
                    photosList.add(Photo(selectedImageUri))
                }
            }

            else -> return listOf()
        }

        return photosList
    }
}