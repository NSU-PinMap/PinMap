package ru.nsu.ccfit.tsd.pinmap.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import ru.nsu.ccfit.tsd.pinmap.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.nsu.ccfit.tsd.pinmap.MapActivity
import ru.nsu.ccfit.tsd.pinmap.filter.FilterDialog

class BottomSheetFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Контроллер для навигации
        val controller = findNavController()

        // Кнопки (и их листенеры) выдвигающегося меню на экране с картой
        val themes_button = view.findViewById<Button>(R.id.buttonTheme)
        themes_button.setOnClickListener { controller.navigate(R.id.themesFragment) }

        val searchButton = view.findViewById<Button>(R.id.buttonSearch)
        searchButton.setOnClickListener {
            findNavController().popBackStack()
            val filterDialog = FilterDialog()
            filterDialog.setFilterListener(activity as MapActivity)
            filterDialog.show(parentFragmentManager, "map")
        }

        val mainMenuButton = view.findViewById<Button>(R.id.buttonMainMenu)
        mainMenuButton.setOnClickListener { controller.navigate(R.id.mainMenuFragment) }

        val galleryButton = view.findViewById<Button>(R.id.buttonGallery)
        galleryButton.setOnClickListener {  controller.navigate(R.id.galleryFragment) }
    }
}