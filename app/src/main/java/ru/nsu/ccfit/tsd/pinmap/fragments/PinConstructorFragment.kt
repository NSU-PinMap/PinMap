package ru.nsu.ccfit.tsd.pinmap.fragments

import android.app.AlertDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.nsu.ccfit.tsd.pinmap.R
import ru.nsu.ccfit.tsd.pinmap.contract.PickImageContract
import ru.nsu.ccfit.tsd.pinmap.databinding.FragmentPinConstructorBinding
import ru.nsu.ccfit.tsd.pinmap.fragments.adapters.ImageAdapter
import ru.nsu.ccfit.tsd.pinmap.fragments.adapters.TagAdapter
import ru.nsu.ccfit.tsd.pinmap.pins.Pin
import ru.nsu.ccfit.tsd.pinmap.pins.PinController

class PinConstructorFragment() : Fragment() {
    private var _binding: FragmentPinConstructorBinding? = null
    private val binding get() = _binding!!
    private lateinit var pinController: PinController

    private lateinit var pin: Pin
    private var imageDataset = listOf<Uri>()
    private var isPinNew: Boolean = true

    private lateinit var alertBuilder: AlertDialog.Builder

    private val imageAdapter = ImageAdapter(imageDataset)

    private val pickImageLauncher =
        registerForActivityResult(PickImageContract()) { photos ->
            imageDataset = photos
            imageAdapter.updateList(photos)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPinConstructorBinding.inflate(inflater, container, false)
        val view = binding.root
        val bundle = arguments

        binding.imagesRecyclerView.adapter = imageAdapter
        binding.imagesRecyclerView.layoutManager = LinearLayoutManager(context)

        val tagDataset = arrayListOf<String?>(null, null, null)
        val tagAdapter = TagAdapter(tagDataset)
        binding.tagsRecyclerView.adapter = tagAdapter
        binding.tagsRecyclerView.layoutManager = LinearLayoutManager(context)
        //todo тут нужно доработать получение тэгов

        if (bundle != null) {

            val name = bundle.getString("name")
            val safeName: String
            if (name != null) {
                safeName = name
            } else {
                safeName = ""
            }
            binding.nameText.setText(safeName)

            val latitude = bundle.getFloat("latitude")
            binding.latitudeText.text = latitude.toString()

            val longitude = bundle.getFloat("longitude")
            binding.longitudeText.text = longitude.toString()

            pin = Pin(safeName, latitude.toDouble(), longitude.toDouble())

            if (!(bundle.getBoolean("new"))) { // конструктор вызван по нажатию на существующий пин

                isPinNew = false
                binding.deleteButton.visibility = View.VISIBLE

                // инициализация объекта на случай его удаления
                pin.id = bundle.getInt("id")

                binding.descriptionText.setText(bundle.getString("desc"))

                binding.moodSlider.value = bundle.getByte("mood").toFloat()

                //todo сделать обработку случаев когда некоторые поля null или не имеют осмысленных значений

                //todo я планировал использовать recyclerView для тегов и фотографий, мб это не лучшая идея
                // сейчас однако в xml фрагмента лежат они
                // тут бы какой-то другой ViewGroup поставить

                //todo тут осталось вытащить и показать настроение, тэги, дату и фотографии
                //todo надо придумать как красиво показывать локацию (сейчас это просто две координаты...)

                // чтобы нельзя было редактировать сразу при открытии фрагмента
                disableEdit()

                binding.editButton.visibility = View.VISIBLE
                binding.deleteButton.visibility = View.VISIBLE
                binding.saveButton.visibility = View.GONE
                binding.cancelButton.visibility = View.GONE

            } else { // конструктор вызван по созданию нового пина; его нельзя удалять из базы, ибо его там ещё нет!
                binding.deleteButton.visibility = View.GONE

                // чтобы можно было редактировать сразу при открытии фрагмента
                enableEdit()
                binding.editButton.visibility = View.GONE
                binding.deleteButton.visibility = View.GONE
                binding.saveButton.visibility = View.VISIBLE
                binding.cancelButton.visibility = View.VISIBLE
            }

        }

        alertBuilder = AlertDialog.Builder(context)

        setImageUpdateButtonListener()
        setBackButtonListener()
        setEditButtonListener()
        setCancelButtonListener()
        setSaveButtonListener()
        setDeleteButtonListener()

        pinController = context?.let { PinController.getController(it) }!!

        return view
    }

    private fun disableEdit() {
        binding.latitudeText.inputType = InputType.TYPE_NULL
        binding.latitudeText.setTextIsSelectable(false)

        binding.longitudeText.inputType = InputType.TYPE_NULL
        binding.longitudeText.setTextIsSelectable(false)

        binding.descriptionText.inputType = InputType.TYPE_NULL
        binding.descriptionText.setTextIsSelectable(false)

        binding.nameText.inputType = InputType.TYPE_NULL
        binding.nameText.setTextIsSelectable(false)

        binding.dateText.inputType = InputType.TYPE_NULL
        binding.dateText.setTextIsSelectable(false)

        binding.updateImageButton.visibility = View.GONE

        // скрываем клавиатуру
        val inputMethodManager =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)

        //todo выключить редактирование локации (когда редактирование будет красивым), тегов и картинок

        binding.moodSlider.isEnabled = false
    }

    private fun enableEdit() {
        binding.latitudeText.inputType = InputType.TYPE_CLASS_NUMBER
        binding.latitudeText.setTextIsSelectable(true)

        binding.longitudeText.inputType = InputType.TYPE_CLASS_NUMBER
        binding.longitudeText.setTextIsSelectable(true)

        binding.descriptionText.inputType = InputType.TYPE_CLASS_TEXT
        binding.descriptionText.setTextIsSelectable(true)

        binding.nameText.inputType = InputType.TYPE_CLASS_TEXT
        binding.nameText.setTextIsSelectable(true)

        binding.dateText.inputType = InputType.TYPE_CLASS_DATETIME
        binding.dateText.setTextIsSelectable(true)

        binding.updateImageButton.visibility = View.VISIBLE

        //todo включить редактирование локации (когда редактирование будет красивым), тегов и картинок

        binding.moodSlider.isEnabled = true
    }

    private fun setImageUpdateButtonListener() {
        binding.updateImageButton.setOnClickListener{

            pickImageLauncher.launch(imageDataset)

        }
    }

    private fun setDeleteButtonListener() {
        binding.deleteButton.setOnClickListener { v ->

            alertBuilder.setMessage("Delete pin?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    val isDeleted = pinController.delete(pin)
                    if (isDeleted) {
                        Toast.makeText(context, "Воспоминание удалено", Toast.LENGTH_SHORT).show()
                        val navController = findNavController()
                        navController.navigate(R.id.startFragment)
                    } else {
                        Toast.makeText(
                            context,
                            "Не удалось удалить воспоминание",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton(
                    "No"
                ) { _, _ ->
                    Toast.makeText(context, "Удаление отменено", Toast.LENGTH_SHORT).show()
                }
            val alert: AlertDialog = alertBuilder.create()
            alert.setTitle("Confirm pin deletion")
            alert.show()

        }
    }

    private fun setSaveButtonListener() {
        binding.saveButton.setOnClickListener { v ->

            pinController.delete(pin)

            // надо заново считать всё с полей ввода и сунуть в пин, и потом уже сохранить
            val name = binding.nameText.text.toString()
            val latitude = binding.latitudeText.text.toString().toDouble()
            val longitude = binding.longitudeText.text.toString().toDouble()
            val pin = Pin(name, latitude, longitude)

            pin.mood = binding.moodSlider.value.toInt().toUByte()
            //pin.tags //todo
            //pin.id //todo
            pin.description = binding.descriptionText.text.toString()
            //pin.date = binding.dateText.text. //todo
            //todo картинки ещё

            val isSaved = pinController.save(pin)
            if (isSaved) {
                Toast.makeText(context, "Воспоминание сохранено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Не удалось сохранить воспоминание", Toast.LENGTH_SHORT)
                    .show()
            }

            val navController = findNavController()
            navController.navigate(R.id.startFragment)

        }
    }

    private fun setCancelButtonListener() {
        binding.cancelButton.setOnClickListener { v ->
            binding.editButton.visibility = View.VISIBLE
            if (!isPinNew) binding.deleteButton.visibility = View.VISIBLE
            binding.saveButton.visibility = View.GONE
            binding.cancelButton.visibility = View.GONE
            disableEdit()
        }
    }

    private fun setEditButtonListener() {
        binding.editButton.setOnClickListener { v ->
            binding.editButton.visibility = View.GONE
            binding.deleteButton.visibility = View.GONE
            binding.saveButton.visibility = View.VISIBLE
            binding.cancelButton.visibility = View.VISIBLE
            enableEdit()
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