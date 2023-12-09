package ru.nsu.ccfit.tsd.pinmap.filter

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import ru.nsu.ccfit.tsd.pinmap.R
import ru.nsu.ccfit.tsd.pinmap.databinding.FilterDialogBinding
import ru.nsu.ccfit.tsd.pinmap.databinding.FragmentPinsListBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FilterDialog: BottomSheetDialogFragment() {

    private var _binding: FilterDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FilterDialogBinding.inflate(inflater, container, false)

        binding.rangePicker.setOnClickListener {
            val dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select dates")
                    .setSelection(
                        androidx.core.util.Pair(
                            MaterialDatePicker.thisMonthInUtcMilliseconds(),
                            MaterialDatePicker.todayInUtcMilliseconds()
                        )
                    )
                    .build()

            dateRangePicker.addOnPositiveButtonClickListener {
                binding.dateStart.text = dateRangePicker.selection?.let {
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        .format(Date(it.component1()))}

                binding.dateEnd.text = dateRangePicker.selection?.let {
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        .format(Date(it.component2()))}
            }

            dateRangePicker.show(parentFragmentManager, "dates")
        }

        binding.searchButton.setOnClickListener {
            this.findNavController().popBackStack()
        }

        return binding.root
    }
}