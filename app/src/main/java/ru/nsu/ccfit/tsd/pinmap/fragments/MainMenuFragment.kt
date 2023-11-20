package ru.nsu.ccfit.tsd.pinmap.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import ru.nsu.ccfit.tsd.pinmap.R


class MainMenuFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val controller = findNavController()

        val pins_button = view.findViewById<Button>(R.id.buttonpins)
        pins_button.setOnClickListener { controller.navigate(R.id.pinsListFragment) }

        val tags_button = view.findViewById<Button>(R.id.buttontags)
        tags_button.setOnClickListener { controller.navigate(R.id.tagsListFragment) }
    }
}