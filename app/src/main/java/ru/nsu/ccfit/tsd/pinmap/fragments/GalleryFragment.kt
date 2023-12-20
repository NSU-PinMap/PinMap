package ru.nsu.ccfit.tsd.pinmap.fragments

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.nsu.ccfit.tsd.pinmap.R
import ru.nsu.ccfit.tsd.pinmap.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    val selectedImageUri: Uri? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Получаем координаты изображения
                val location = context?.let { it1 -> getLocationFromImage(it1, it) }
                if (location != null) {
                    val latitude = location[0]
                    val longitude = location[1]
                    navigateToConstructor(uri, latitude, longitude)
                } else {
                    showNoLocationDialog()
                }
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

    private fun navigateToConstructor(uri: Uri, lat: Double, lon: Double) {

        val navController = findNavController()

        val bundle = Bundle()
        bundle.putInt("type", 2)
        bundle.putString("uri", uri.toString())
        bundle.putFloat("latitude", lat.toFloat())
        bundle.putFloat("longitude", lon.toFloat())
        // Чтобы потом из конструктора сюда не попадать
        navController.popBackStack()
        navController.navigate(R.id.pinConstructorFragment, bundle)

    }

    private fun getLocationFromImage(context: Context, imageUri: Uri): DoubleArray? {
        context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            val exifInterface = ExifInterface(inputStream)

            return exifInterface.latLong
        }

        return null
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
