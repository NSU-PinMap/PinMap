package ru.nsu.ccfit.tsd.pinmap.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.nsu.ccfit.tsd.pinmap.MapActivity
import ru.nsu.ccfit.tsd.pinmap.R
import ru.nsu.ccfit.tsd.pinmap.adapters.PinAdapter
import ru.nsu.ccfit.tsd.pinmap.databinding.FragmentPinsListBinding
import ru.nsu.ccfit.tsd.pinmap.filter.Filter
import ru.nsu.ccfit.tsd.pinmap.filter.FilterDialog
import ru.nsu.ccfit.tsd.pinmap.pins.PinController


class PinsListFragment : Fragment(), FilterDialog.Filterable {
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

        // Передаём пины из pinController в PinAdapter
        pinAdapter = PinAdapter(pinController.getAllPins())

        val recyclerView = binding.rcPins
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = pinAdapter

        // При переходе в список пинов из списка тегов по нажатию на тег
        // получаем тег в виде строки, вызываем onFilter с ним
        val bundle = arguments
        if (null != bundle) {
            // На самом деле других типов переходов с аргументами нет,
            // но если оно появятся, то так не придётся исправлять аргументы в TagListAdapter
            val type = bundle.getInt("type")

            // Получаем из bundle выбранный тег и фильтруем по нему
            if (1 == type) {
                val tag = bundle.getString("tag")
                val filter = Filter()
                if (tag != null) {
                    filter.hasTags.add(tag)
                    onFilter(filter)
                }
            }
        }

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

        binding.search.setOnClickListener {
            val filterDialog = FilterDialog()
            filterDialog.setFilterListener(this)
            filterDialog.show(childFragmentManager, "list")
        }
    }

    override fun onFilter(filter: Filter) {
        // TODO: Заменить логикой поиска
        Toast.makeText(context, "Вызван поиск в списке", Toast.LENGTH_SHORT).show()
    }
}