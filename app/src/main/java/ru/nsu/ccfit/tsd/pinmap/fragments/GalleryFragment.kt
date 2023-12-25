package ru.nsu.ccfit.tsd.pinmap.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.nsu.ccfit.tsd.pinmap.R
import ru.nsu.ccfit.tsd.pinmap.databinding.FragmentGalleryBinding
import ru.nsu.ccfit.tsd.pinmap.utils.uriToPath

class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                // Получаем координаты изображения
                val location = context?.let { it1 -> getLocationFromImage(it1, uri) }
                if (location != null) {
                    val latitude = location[0]
                    val longitude = location[1]
                        //Log.println(Log.ERROR, "LOCATION", "Latitude: $latitude Longitude: $longitude")
                    navigateToConstructor(uri, latitude, longitude)
                } else {
                    showNoLocationDialog()
                }
            } else {
                findNavController().navigateUp()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showGallery()
    }

    private fun navigateToConstructor(uri: Uri, lat: Float, lon: Float) {

        val navController = findNavController()

        val bundle = Bundle()
        bundle.putInt("type", 2)
        bundle.putString("uri", uri.toString())
        bundle.putFloat("latitude", lat)
        bundle.putFloat("longitude", lon)
        // Чтобы потом из конструктора сюда не попадать
        navController.popBackStack()
        navController.navigate(R.id.pinConstructorFragment, bundle)

    }

    private fun getLocationFromImage(context: Context, imageUri: Uri): FloatArray? {
//        val rawId = DocumentsContract.getDocumentId(imageUri)
//        val id = if (rawId.contains(':')) rawId.split(':')[1] else rawId
//        val mediaUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

        val exifInterface = ExifInterface(context.uriToPath(imageUri).toString())

        val f = FloatArray(2)
        return if (exifInterface.getLatLong(f))
            floatArrayOf(f[0], f[1])
        else
            null
    }

    private fun showGallery() {
        pickImage.launch("image/jpeg")
    }

    private fun showNoLocationDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Локация не найдена!")
            .setMessage("Эта фотография не содержит данные о локации.Попробовать выбрать другую?")
            .setPositiveButton("Да") { _, _ ->
                showGallery()
            }
            .setNegativeButton("Нет") { _, _ ->
                findNavController().navigateUp()
            }
            .create()
        dialog.show()
    }

}
