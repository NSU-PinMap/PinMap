package ru.nsu.ccfit.tsd.pinmap

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import org.osmdroid.views.MapView
import ru.nsu.ccfit.tsd.pinmap.databinding.FragmentPinConstructorBinding

class PinConstructorFragment() : Fragment() {
    private var _binding: FragmentPinConstructorBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPinConstructorBinding.inflate(inflater, container, false)
        val view = binding.root
        val bundle = arguments // получение аргументов из replace
        if (bundle != null) {
            binding.nameText.setText(bundle.getString("name"))
            binding.descriptionText.setText(bundle.getString("desc"))
            //todo остальное из пина тоже протащить
        }

        setBackButtonListener()
        setEditButtonListener()
        setCancelButtonListener()
        setSaveButtonListener()
        setDeleteButtonListener()

        //todo временное решение чтобы нельзя было редактировать при открытии фрагмента
        disableEdit(binding.nameText)
        disableEdit(binding.dateText)
        disableEdit(binding.moodText)
        disableEdit(binding.descriptionText)
        //todo выключить редактирование позиции и картинки

        //todo ещё локацию добавить надо

        return view
    }

    private fun disableEdit(view : EditText) {
        view.setInputType(InputType.TYPE_NULL)
        view.setTextIsSelectable(false)
        //todo как-то надо убрать клавиатуру тут
    }
    private fun enableEdit(view : EditText, inputType : Int) {
        view.setInputType(inputType)
        view.setTextIsSelectable(true)
    }

    private fun setDeleteButtonListener() {
        binding.deleteButton.setOnClickListener { v ->
            //todo сюда Вова подключит контроллер
        }
    }

    private fun setSaveButtonListener() {
        binding.saveButton.setOnClickListener { v ->
            //todo сюда Вова подключит контроллер
        }
    }

    private fun setCancelButtonListener() {
        binding.cancelButton.setOnClickListener { v ->
            binding.editButton.visibility = View.VISIBLE
            binding.deleteButton.visibility = View.VISIBLE
            binding.saveButton.visibility = View.GONE
            binding.cancelButton.visibility = View.GONE
            disableEdit(binding.nameText)
            disableEdit(binding.dateText)
            disableEdit(binding.moodText)
            disableEdit(binding.descriptionText)
            //todo выключить редактирование локации, тегов и картинки
        }
    }

    private fun setEditButtonListener() {
        binding.editButton.setOnClickListener { v ->
            binding.editButton.visibility = View.GONE
            binding.deleteButton.visibility = View.GONE
            binding.saveButton.visibility = View.VISIBLE
            binding.cancelButton.visibility = View.VISIBLE
            enableEdit(binding.nameText, InputType.TYPE_CLASS_TEXT)
            enableEdit(binding.dateText, InputType.TYPE_CLASS_DATETIME)
            enableEdit(binding.moodText, InputType.TYPE_CLASS_TEXT)
            enableEdit(binding.descriptionText, InputType.TYPE_CLASS_TEXT)
            //todo включить редактирование локации, тегов и картинки
        }
    }

    private fun setBackButtonListener() {
        //todo пока что кнопка back просто убирает фрагмент
        // но по идее она должна откатывать edit на view, а вот уже со view возвращаться на активити,
        // т.е. убирать фрагмент
        binding.backButton.setOnClickListener { v ->
            //Toast.makeText(v.context, "back pressed", Toast.LENGTH_SHORT).show()
            val constructorFragment =
                parentFragmentManager.findFragmentById(R.id.fragmentContainerView)
            if (constructorFragment != null)
                parentFragmentManager.beginTransaction()
                    .hide(constructorFragment)
                    .commit()
            val root = requireView()
            if (root.findViewById<ImageView>(R.id.createdPin) != null)
                root.findViewById<ImageView>(R.id.createdPin).visibility = View.GONE
        }
        //todo как-то надо убрать клавиатуру тут тоже
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    // это нужно чтобы вообще иметь возможность создать фрагмент?
    // вроде и без него правда что-то создаётся
    // но через эту хрень явно прокидываются аргументы для создания фрагмента
    companion object {
        private const val NAME = "name"

        fun newInstance(string: String) = PinConstructorFragment().apply {
            arguments = Bundle(1).apply {
                putString(NAME, string)
            }
        }
    }

}