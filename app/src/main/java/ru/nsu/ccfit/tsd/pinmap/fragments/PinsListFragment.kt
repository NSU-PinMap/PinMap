package ru.nsu.ccfit.tsd.pinmap.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.nsu.ccfit.tsd.pinmap.R
import ru.nsu.ccfit.tsd.pinmap.adapters.PinAdapter
import ru.nsu.ccfit.tsd.pinmap.databinding.FragmentPinsListBinding
import ru.nsu.ccfit.tsd.pinmap.filter.Filter
import ru.nsu.ccfit.tsd.pinmap.filter.FilterDialog
import ru.nsu.ccfit.tsd.pinmap.pins.Pin
import ru.nsu.ccfit.tsd.pinmap.pins.PinController
import java.util.Date


class PinsListFragment : Fragment(), FilterDialog.Filterable {
    private var _binding: FragmentPinsListBinding? = null
    private val binding get() = _binding!!

    private lateinit var pinController: PinController

    private lateinit var pinAdapter: PinAdapter
    private lateinit var pinsList: MutableList<Pin>

    private lateinit var sortOptions: Array<String>
    private lateinit var sortAdapter: ArrayAdapter<String>
    private lateinit var sortMenu: AutoCompleteTextView

    private var previousSortPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPinsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        sortAdapter = ArrayAdapter(requireContext(), R.layout.sort_dropdown_item, sortOptions)
        sortMenu.setAdapter(sortAdapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // NavController нужен, чтобы позже из списка по нажатию переходить в конструктор
        val controller = findNavController()

        pinController = PinController.getController(requireContext())

        // Передаём пины из pinController в PinAdapter
        pinsList = mutableListOf()
        pinsList.addAll(pinController.getAllPins())
        pinAdapter = PinAdapter(pinsList, controller)

        val recyclerView = binding.rcPins
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = pinAdapter

        sortOptions = resources.getStringArray(R.array.pins_sort_dropdown_options)
        sortAdapter = ArrayAdapter(requireContext(), R.layout.sort_dropdown_item, sortOptions)
        sortMenu = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        sortMenu.setAdapter(sortAdapter)

        sortMenu.setOnItemClickListener { _, _, position, _ ->
            // value -- это String-значение выбранной опции, а position -- её int-номер в менюшке
            val value = sortAdapter.getItem(position) ?: ""

            // TODO: сделать что-нибудь с этой страшной чередой if'ов
            if (position != previousSortPosition) {
                sort(value)

                // в конце назначаем текущий номер опции предыдущим
                previousSortPosition = position
            }
        }

        binding.search.setOnClickListener {
            val filterDialog = FilterDialog()
            filterDialog.setFilterListener(this)
            filterDialog.show(childFragmentManager, "list")
        }

        val returnArrow = binding.returnToAllPinsView
        returnArrow.setOnClickListener {
            pinAdapter.setPins(pinsList)
            returnArrow.visibility = View.INVISIBLE
            updateOrder()
        }

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
                var filter = Filter()
                if (tag != null) {
                    filter.hasTags.add(tag)
                    onFilter(filter)
                }
            }
        }
    }

    private fun sort(value: String) {
        val optionSortAlphabetically = resources.getText(R.string.optionSortAlphabetically).toString()
        val optionSortDescAlphabetically = resources.getText(R.string.optionSortDescAlphabetically).toString()
        val optionDateSort = resources.getText(R.string.optionDateSort).toString()
        val optionDescDateSort = resources.getText(R.string.optionDescDateSort).toString()

        if (value == optionSortAlphabetically)
            pinAdapter.sortAlphabetically()

        if (value == optionSortDescAlphabetically)
            pinAdapter.sortDescAlphabetically()

        if (value == optionDateSort)
            pinAdapter.sortByDate()

        if (value == optionDescDateSort)
            pinAdapter.sortedDescByDate()
    }

    private fun updateOrder() {
        if (-1 == previousSortPosition)
            sort(sortOptions[0])
        else
            sort(sortOptions[previousSortPosition])
    }

    override fun onFilter(filter: Filter) {
        val returnArrow = binding.returnToAllPinsView
        returnArrow.visibility = View.VISIBLE

        val filteredPins = pinController.getFilteredPins(filter)

        pinAdapter.setPins(filteredPins)

        updateOrder()
    }
}