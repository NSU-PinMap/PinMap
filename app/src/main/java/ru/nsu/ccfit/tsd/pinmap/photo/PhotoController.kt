package ru.nsu.ccfit.tsd.pinmap.photo

import android.content.Context

class PhotoController private constructor(context: Context) {
    companion object {
        private lateinit var instance: PhotoController

        fun getController(context: Context): PhotoController {
            if (instance == null)
                instance = PhotoController(context)

            return instance
        }
    }

    private var photos: List<Photo> = listOf()

    var selectedPhotos: List<Photo>
        get() = photos
        set(value) {
            photos = value
        }
}