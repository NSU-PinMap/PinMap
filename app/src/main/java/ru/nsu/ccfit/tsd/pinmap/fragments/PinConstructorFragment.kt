package ru.nsu.ccfit.tsd.pinmap.fragments

import android.app.AlertDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.nsu.ccfit.tsd.pinmap.databinding.FragmentPinConstructorBinding
import ru.nsu.ccfit.tsd.pinmap.fragments.adapters.ImageAdapter
import ru.nsu.ccfit.tsd.pinmap.pins.Pin
import ru.nsu.ccfit.tsd.pinmap.pins.PinController
import java.util.Date

class PinConstructorFragment() : Fragment() {
    private var _binding: FragmentPinConstructorBinding? = null
    private val binding get() = _binding!!
    private lateinit var pinController: PinController

    private lateinit var pin: Pin
    private var isPinNew: Boolean = true

    private lateinit var alertBuilder: AlertDialog.Builder

    private var imageDataset = mutableListOf<Uri>()
    private val imageAdapter = ImageAdapter(imageDataset)

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) {  photos->
            for (photo in photos) {
                if (photo != null) {
                    context?.contentResolver?.takePersistableUriPermission(
                        photo,
                        FLAG_GRANT_READ_URI_PERMISSION
                    )
                    if (!imageDataset.contains(photo)) {
                        imageDataset.add(photo)
                        imageAdapter.updateList(imageDataset)
                    }
                }
            }
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

        if (bundle != null) {

            /*bundle содержит:
            1. локацию (широта и долгота), если конструктор открыт по созданию нового пина из стартФрагмента
            2. Uri + локацию (широта и долгота), если конструктор открыт из картинки из галереи
            3. id, если конструктор открыт по тыку на существующий пин

            плюс он всегда содержит поле type = номер пункта, указанные сверху,
            в зависимости от того, откуда бандл пришёл*/

            pinController = context?.let { PinController.getController(it) }!!

            when (bundle.getInt("type")){
                1 -> { // новый пин из карты, режим редактирования
                    val latitude = bundle.getFloat("latitude")

                    val longitude = bundle.getFloat("longitude")

                    pin = Pin("Новое воспоминание", latitude.toDouble(), longitude.toDouble())

                    // конструктор вызван по созданию нового пина; его нельзя удалять из базы, ибо его там ещё нет!
                    binding.deleteButton.visibility = View.GONE

                    // чтобы можно было редактировать сразу при открытии фрагмента
                    enableEdit()
                    binding.editButton.visibility = View.GONE
                    binding.clearImageButton.visibility = View.GONE
                    binding.deleteButton.visibility = View.GONE
                    binding.saveButton.visibility = View.VISIBLE
                    binding.cancelButton.visibility = View.VISIBLE
                }
                2 -> { // новый пин из галереи, режим редактирования
                    val latitude = bundle.getFloat("latitude")

                    val longitude = bundle.getFloat("longitude")

                    val uri = bundle.getString("uri")
                    pin.photos = mutableListOf(Uri.parse(uri))

                    pin = Pin("Новое воспоминание", latitude.toDouble(), longitude.toDouble())

                    // конструктор вызван по созданию нового пина; его нельзя удалять из базы, ибо его там ещё нет!
                    binding.deleteButton.visibility = View.GONE

                    // чтобы можно было редактировать сразу при открытии фрагмента
                    enableEdit()
                    binding.editButton.visibility = View.GONE
                    binding.clearImageButton.visibility = View.VISIBLE
                    binding.deleteButton.visibility = View.GONE
                    binding.saveButton.visibility = View.VISIBLE
                    binding.cancelButton.visibility = View.VISIBLE
                }
                3 -> { // существующий пин, режим просмотра

                    isPinNew = false

                    val id = bundle.getInt("id")

                    pin = pinController.getPinById(id)!!

                    // чтобы нельзя было редактировать сразу при открытии фрагмента
                    disableEdit()

                    binding.editButton.visibility = View.VISIBLE
                    binding.clearImageButton.visibility = View.GONE
                    binding.deleteButton.visibility = View.VISIBLE
                    binding.saveButton.visibility = View.GONE
                    binding.cancelButton.visibility = View.GONE
                }
            }

            binding.nameText.text.insert(0, pin.name)

            binding.latitudeText.text = pin.latitude.toString()

            binding.longitudeText.text = pin.longitude.toString()

            val date = pin.date
            if (date != null) {
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val dateString: String = sdf.format(date)
                binding.dateText.setText(dateString)
            }

            binding.descriptionText.setText(pin.description)

            binding.moodSlider.value = pin.mood.toFloat()

            val tagsText = pin.tags.joinToString(separator = ";")
            binding.tagsText.setText(tagsText)

            imageAdapter.updateList(pin.photos)
            imageDataset = pin.photos

            //todo надо придумать как красиво показывать локацию (сейчас это просто две координаты...)

        }

        alertBuilder = AlertDialog.Builder(context)

        setImageUpdateButtonListener()
        setImageClearButtonListener()
        setBackButtonListener()
        setEditButtonListener()
        setCancelButtonListener()
        setSaveButtonListener()
        setDeleteButtonListener()

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

        binding.tagsText.inputType = InputType.TYPE_NULL

        binding.updateImageButton.visibility = View.GONE
        binding.clearImageButton.visibility = View.GONE

        // скрываем клавиатуру
        val inputMethodManager =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)

        //todo выключить редактирование локации когда редактирование будет красивым

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

        binding.tagsText.inputType = InputType.TYPE_CLASS_TEXT

        binding.updateImageButton.visibility = View.VISIBLE
        binding.clearImageButton.visibility = View.VISIBLE

        //todo включить редактирование локации когда редактирование будет красивым

        binding.moodSlider.isEnabled = true
    }

    private fun setImageUpdateButtonListener() {
        binding.updateImageButton.setOnClickListener{
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setImageClearButtonListener() {
        binding.clearImageButton.setOnClickListener{

            alertBuilder.setMessage("Очистить выбор фотографий?")
                .setCancelable(false)
                .setPositiveButton("Да") { _, _ ->
                    imageAdapter.updateList(listOf())
                    imageDataset = mutableListOf()
                }
                .setNegativeButton(
                    "Нет"
                ) { _, _ ->
                }
            val alert: AlertDialog = alertBuilder.create()
            alert.setTitle("")
            alert.show()

        }
    }

    private fun setDeleteButtonListener() {
        binding.deleteButton.setOnClickListener { v ->
            alertBuilder.setMessage("Удалить воспоминание?")
                .setCancelable(false)
                .setPositiveButton("Да") { _, _ ->
                    val isDeleted = pinController.delete(pin)
                    if (isDeleted) {
                        Toast.makeText(context, "Воспоминание удалено", Toast.LENGTH_SHORT).show()
                        val navController = findNavController()
                        navController.popBackStack()
                    } else {
                        Toast.makeText(
                            context,
                            "Не удалось удалить воспоминание",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton(
                    "Нет"
                ) { _, _ ->
                    Toast.makeText(context, "Удаление воспоминания отменено", Toast.LENGTH_SHORT).show()
                }
            val alert: AlertDialog = alertBuilder.create()
            alert.setTitle("")
            alert.show()

        }
    }

    private fun setSaveButtonListener() {
        binding.saveButton.setOnClickListener { v ->
            // надо заново считать всё с полей ввода и сунуть в пин, и потом уже вызывать save

            pin.name = binding.nameText.text.toString()
            pin.latitude = binding.latitudeText.text.toString().toDouble()
            pin.longitude = binding.longitudeText.text.toString().toDouble()
            pin.mood = binding.moodSlider.value.toInt().toUByte()
            pin.description = binding.descriptionText.text.toString()

            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val text = binding.dateText.text.toString()
            var date : Date?
            try {
                date = formatter.parse(text)
            } catch (e : Exception) {
                date = null
            }
            pin.date = date

            pin.tags = binding.tagsText.text.toString().split(";").toMutableList()

            pin.photos = imageDataset.toMutableList()

            val isSaved = pinController.save(pin)
            if (isSaved) {
                Toast.makeText(context, "Воспоминание сохранено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Не удалось сохранить воспоминание", Toast.LENGTH_SHORT)
                    .show()
            }

            val navController = findNavController()
            navController.popBackStack()

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
            navController.popBackStack()
        }
    }

}