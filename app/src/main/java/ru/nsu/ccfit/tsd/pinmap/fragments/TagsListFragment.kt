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

    private lateinit var sortOptions: Array<String>
    private lateinit var optionsArrayAdapter: ArrayAdapter<String>
    private lateinit var sortMenu: AutoCompleteTextView

    // В этой переменной лежит позиция последней выбранной сортировки в массиве options
    // (см. в функции onViewCreated и tags_sort_dropdown_options в strings.xml)
    //
    // Она вынесена сюда, а не оставлена локальной, поскольку мне в функии filter требуется знать,
    // какая сортировка в данный момент выбрана пользователем
    private var previousSortPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTagsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        optionsArrayAdapter = ArrayAdapter(requireContext(), R.layout.sort_dropdown_item, sortOptions)
        sortMenu.setAdapter(optionsArrayAdapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pinController = PinController.getController(requireContext())
        val navController = findNavController()

        tagsList = mutableListOf()
        tagsList.addAll(pinController.getAllTags())
        tagListAdapter = TagListAdapter(tagsList, navController)

        val recyclerView = binding.rcTags
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = tagListAdapter

        sortOptions = resources.getStringArray(R.array.tags_sort_dropdown_options)
        optionsArrayAdapter = ArrayAdapter(requireContext(), R.layout.sort_dropdown_item, sortOptions)
        sortMenu = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        sortMenu.setAdapter(optionsArrayAdapter)

        sortMenu.setOnItemClickListener { _, _, position, _ ->
            // value -- это String-значение выбранной опции, а position -- её int-номер в менюшке
            val value = optionsArrayAdapter.getItem(position) ?: ""

            // Сортируем заново, только если вид сортировки поменяли
            if (position != previousSortPosition) {
                sort(value)
                // После сортировки назначаем текущий номер опции предыдущим
                previousSortPosition = position
            }
        }

        val searchView = view.findViewById<SearchView>(R.id.search)
        searchView.clearFocus()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Эту функцию обязательно переопределять, но проще всего оставить пустой,
            // onQueryTextChange и так отфильтрует вам поиск, когда вы допечатаете
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return false
            }
        })
    }

    private fun sort(value: String) {
        // В файле strings.xml лежат строки и задан массив, состоящий из них
        // В коде ниже мы проверяем, с какой из строк совпало выбранное в массиве значение,
        // чтобы определить, какая сортировка выбрана ползователем
        //
        // Это всё ещё колхоз, но менее ужасный, чем проверять по текстовой строке, мне кажется
        val optionSortAlphabetically = resources.getText(R.string.optionSortAlphabetically).toString()
        val optionSortDescAlphabetically = resources.getText(R.string.optionSortDescAlphabetically).toString()

        // В зависимости от выбранной сортировки, вызываем нужную функцию в адаптере
        // (см. adapters/TagListAdapter)
        if (value == optionSortAlphabetically)
            tagListAdapter.sortAlphabetically()

        if (value == optionSortDescAlphabetically)
            tagListAdapter.sortDescAlphabetically()
    }

    private fun filter(query: String?) {
        val options = resources.getStringArray(R.array.tags_sort_dropdown_options)
        val newTagsList = mutableListOf<String>()

        for (tag in tagsList) {
            if (tag.contains("" + query)) {
                newTagsList.add(tag)
            }
        }


        tagListAdapter.setTags(newTagsList)

        if (-1 == previousSortPosition)
            sort(options[0])
        else
            sort(options[previousSortPosition])

        //
        // При поиске мы передаём в адаптер отфильтрованный список и сортируем его
        // в выбранном до этого порядке (исходно - по алфавиту)
        //
        // Если не отсортировать, то сортировка "сбросится", потому что отсортированный список
        // лежит в адаптере, а tadsList здесь -  исходный список с изначальным порядком
        //
        // Можно, конечно, перенести функции сортировки сюда и сортировать tagsList,
        // а не вызывать tagListAdapter.sort[порядок сортировки], но тогда придётся
        // каждый раз пересоздавать список и при сортировке, потому что Android
        // в упор отказывается видеть изменения в списке здесь, хоть я и вызываю
        // tagListAdapter.notifyDataSetChanged()
        //
        // Другой вариант - фильтровать список в самом адаптере - я тоже пробовала,
        // но тогда приходится хранить дополнительно полный список тегов, чтобы
        // при изменении строки с запросом можно было вернуть в список те теги,
        // которые раньше не подходили, а теперь подходят
        //
        // Причём нужно будет сортировать каждый раз полный список
        //
        //
        // В общем, мне кажется, так, как сейчас, проще всего, но если, вы знаете, как сделать
        // лучше - напишите мне и я исправлю
        //
    }
}