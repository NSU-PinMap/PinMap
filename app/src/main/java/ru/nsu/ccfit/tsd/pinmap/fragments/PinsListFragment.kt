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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Retrieve and inflate the layout for this fragment
        _binding = FragmentPinsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pinController = PinController.getController(requireContext())

        // Проверка корректности выгрузки пинов и появления их в recycler view
/*
                //val pin1 = Pin("pin1", 47.6, 2.1944)
                val pin4 = Pin("pin4", 47.0, 2.1970)
                pin4.date = Date(11, 5, 7)


                //pinController.delete(pin1)
                //pinController.delete(pin2)

                pinController.save(pin4)
                //pinController.save(pin2)
*/
        // Проверка корректности добавления пина в recycler view (появляется при переоткрытии фрагмента)
        // !!!!!!!!!!!!!!! Этой кнопки уже нет в xml-файле
        // TODO: перепроверить в связи с внесёнными в адаптер изменениями
/*
        binding.button3.setOnClickListener{
            val pin3 = Pin("pin3", 47.0, 2.1945)
            pinController.save(pin3)
        }
         */

        val recyclerView = binding.rcPins
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Передаём пины из pinController в PinAdapter
        recyclerView.adapter = PinAdapter(pinController.getAllPins())

        val controller = findNavController()


        val options = resources.getStringArray(R.array.pins_sort_dropdown_options)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.sort_dropdown_item, options)
        val sortMenu = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        sortMenu.setAdapter(arrayAdapter)

        var previousOptionsPosition = 0

        sortMenu.setOnItemClickListener { _, _, position, _ ->
            // value -- это String-значение выбранной опции, а position -- её int-номер в менюшке
            val value = arrayAdapter.getItem(position) ?: ""

            if (position != previousOptionsPosition) {
                val adapter = PinAdapter(pinController.getAllPins())

                if (value == "алфавиту [А->Я]")
                    adapter.sortAlphabetically()

                if (value == "алфавиту [Я->А]")
                    adapter.sortDescAlphabetically()

                if (value == "дате [старые->новые]")
                    adapter.sortByDate()

                if (value == "дате [новые->старые]")
                    adapter.sortedDescByDate()

                // в конце назначаем текущий номер опции предыдущим
                previousOptionsPosition = position
            }
        }

        val searchView = view.findViewById<SearchView>(R.id.search)
        searchView.clearFocus()

        //TODO: когда дойдёшь до списков, раскомментируй и замени list и listAdapter на соответствующие переменные
        //TODO: upd: Вова напомнил, что у нас вообще-то отдельная логика для поиска должна быть -> исправить/убрать
        /*
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (list.contains(query)) {
                    listAdapter.filter.filter(query)
                    //TODO: notify?
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listAdapter.filter.filter(newText)
                //TODO: notify?
                return false
            }
        })
         */
    }
}