package ru.nsu.ccfit.tsd.pinmap.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import ru.nsu.ccfit.tsd.pinmap.R

class StartFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val controller = findNavController()

        val bs_button = view.findViewById<Button>(R.id.bs_button)
        bs_button.setOnClickListener { controller.navigate(R.id.bottomSheetFragment) }

        val newPinImage = view.findViewById<ImageView>(R.id.newPinIamgeView)
        newPinImage.setOnClickListener { controller.navigate(R.id.pinConstructorFragment) }
    }
}