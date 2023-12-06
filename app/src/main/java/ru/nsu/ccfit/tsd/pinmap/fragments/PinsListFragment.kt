package ru.nsu.ccfit.tsd.pinmap.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.SearchView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.nsu.ccfit.tsd.pinmap.R
import ru.nsu.ccfit.tsd.pinmap.adapters.PinAdapter
import ru.nsu.ccfit.tsd.pinmap.databinding.FragmentPinsListBinding
import ru.nsu.ccfit.tsd.pinmap.pins.Pin
import ru.nsu.ccfit.tsd.pinmap.pins.PinController
import java.util.Collections
import java.util.Date

class PinsListFragment : Fragment() {
    private var _binding: FragmentPinsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var pinController: PinController
    private lateinit var pinAdapter: PinAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPinsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pinController = PinController.getController(requireContext())

        // Проверка корректности выгрузки пинов и появления их в recycler view
        // TODO: протестировать с добавлением пина на карте
        /*
        val pin1 = Pin("pin1", 47.6, 2.1944)
        pin1.date = Date(9, 5, 7)
        pinController.save(pin1)
        */

        // Передаём пины из pinController в PinAdapter
        pinAdapter = PinAdapter(pinController.getAllPins())

        val recyclerView = binding.rcPins
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = pinAdapter

        val controller = findNavController()

        val options = resources.getStringArray(R.array.pins_sort_dropdown_options)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.sort_dropdown_item, options)
        val sortMenu = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        sortMenu.setAdapter(arrayAdapter)

        var previousOptionsPosition = -1

        sortMenu.setOnItemClickListener { _, _, position, _ ->
            // value -- это String-значение выбранной опции, а position -- её int-номер в менюшке
            val value = arrayAdapter.getItem(position) ?: ""

            // TODO: сделать что-нибудь с этой страшной чередой if'ов
            if (position != previousOptionsPosition) {
                if (value == "алфавиту [А->Я]")
                    pinAdapter.sortAlphabetically()

                if (value == "алфавиту [Я->А]")
                    pinAdapter.sortDescAlphabetically()

                if (value == "дате [старые->новые]")
                    pinAdapter.sortByDate()

                if (value == "дате [новые->старые]")
                    pinAdapter.sortedDescByDate()

                // в конце назначаем текущий номер опции предыдущим
                previousOptionsPosition = position
            }
        }

        val searchView = view.findViewById<SearchView>(R.id.search)
        searchView.clearFocus()

        //TODO: добавить доступ к окну поиска, когда оно будет готово
/*
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
*/
    }
}