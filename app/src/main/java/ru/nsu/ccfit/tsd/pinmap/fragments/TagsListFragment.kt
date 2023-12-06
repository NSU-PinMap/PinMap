package ru.nsu.ccfit.tsd.pinmap.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.nsu.ccfit.tsd.pinmap.R
import ru.nsu.ccfit.tsd.pinmap.adapters.TagListAdapter
import ru.nsu.ccfit.tsd.pinmap.databinding.FragmentTagsListBinding
import ru.nsu.ccfit.tsd.pinmap.pins.PinController

class TagsListFragment : Fragment() {
    private var _binding: FragmentTagsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var pinController: PinController
    private lateinit var tagListAdapter: TagListAdapter
    private lateinit var tagsList: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTagsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pinController = PinController.getController(requireContext())

        // Поскольку у нас пока нельзя проверить работу с тегами через pinController, я проверяла на списке строк
        /**/
        tagsList = mutableListOf("tag1", "tag2", "tag5", "tag3", "tag4", "tag6", "tag7", "tag8", "tag9", "tag10")
        tagListAdapter = TagListAdapter(tagsList)

        //tagListAdapter = TagListAdapter(pinController.getAllTags())

        val recyclerView = binding.rcTags
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = tagListAdapter

        val controller = findNavController()

        val options = resources.getStringArray(R.array.tags_sort_dropdown_options)
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
                    tagListAdapter.sortAlphabetically()

                if (value == "алфавиту [Я->А]")
                    tagListAdapter.sortDescAlphabetically()

                // в конце назначаем текущий номер опции предыдущим
                previousOptionsPosition = position
            }
        }

        val searchView = view.findViewById<SearchView>(R.id.search)
        searchView.clearFocus()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var filteredTags = mutableListOf<String>()

                for (tag in tagsList) {
                    if (tag.contains("" + newText)) {
                        filteredTags.add(tag)
                    }
                }

                tagListAdapter.filter(filteredTags)

                return false
            }
        })
    }
}