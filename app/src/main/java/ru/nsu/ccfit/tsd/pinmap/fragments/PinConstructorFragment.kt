package ru.nsu.ccfit.tsd.pinmap.fragments

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.nsu.ccfit.tsd.pinmap.R
import ru.nsu.ccfit.tsd.pinmap.databinding.FragmentPinConstructorBinding

class PinConstructorFragment() : Fragment() {
    private var _binding: FragmentPinConstructorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPinConstructorBinding.inflate(inflater, container, false)
        val view = binding.root
        val bundle = arguments

        //todo сделать обработку случаев когда некоторые поля null или не имеют осмысленных значений
        if (bundle != null) {

            if (bundle.getBoolean("new")) { // конструктор вызван по созданию нового пина

                binding.nameText.setText(bundle.getString("name"))
                binding.latitudeText.text = bundle.getFloat("latitude").toString()
                binding.longitudeText.text = bundle.getFloat("longitude").toString()

            } else { // конструктор вызван по нажатию на существующий пин

                binding.nameText.setText(bundle.getString("name"))
                binding.latitudeText.text = bundle.getFloat("latitude").toString()
                binding.longitudeText.text = bundle.getFloat("longitude").toString()
                binding.descriptionText.setText(bundle.getString("desc"))

                //todo я планировал использовать recyclerView для тегов и фотографий, мб это не лучшая идея
                // сейчас однако в xml фрагмента лежат они

                //todo тут осталось вытащить и показать настроение, тэги, дату и фотографии
                //todo ещё надо придумать как красиво показывать локацию (сейчас это просто две координаты...)
            }
        }

        setBackButtonListener()
        setEditButtonListener()
        setCancelButtonListener()
        setSaveButtonListener()
        setDeleteButtonListener()

        // чтобы нельзя было редактировать при открытии фрагмента
        disableEdit(binding.nameText)
        disableEdit(binding.dateText)
        disableEdit(binding.moodText)
        disableEdit(binding.descriptionText)
        //todo выключить редактирование остального


        return view
    }

    private fun disableEdit(view : EditText) {
        view.setInputType(InputType.TYPE_NULL)
        view.setTextIsSelectable(false)
        //todo убрать клавиатуру; она убирается на встроенную кнопку "назад" кстати
    }
    private fun enableEdit(view : EditText, inputType : Int) {
        view.setInputType(inputType)
        view.setTextIsSelectable(true)
    }

    private fun setDeleteButtonListener() {
        binding.deleteButton.setOnClickListener { v ->
            //todo сюда подключить контроллер

            Toast.makeText(context, "TODO: добавить удаление пина", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setSaveButtonListener() {
        binding.saveButton.setOnClickListener { v ->
            //todo сюда подключить контроллер
            Toast.makeText(context, "TODO: добавить сохранение пина", Toast.LENGTH_SHORT).show()
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
        binding.backButton.setOnClickListener { v ->
            val navController = findNavController()
            navController.navigate(R.id.startFragment)
        }
    }

    // это нужно чтобы вообще иметь возможность создать фрагмент?
    // вроде и без него правда что-то создаётся
    // но через эту хрень явно прокидываются аргументы для создания фрагмента
    // если это удалить то программа жалуется вроде :(
    companion object {
        private const val NAME = "name"

        fun newInstance(string: String) = PinConstructorFragment().apply {
            arguments = Bundle(1).apply {
                putString(NAME, string)
            }
        }
    }

}