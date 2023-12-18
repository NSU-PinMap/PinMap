package ru.nsu.ccfit.tsd.pinmap.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.component1
import androidx.core.util.component2
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import ru.nsu.ccfit.tsd.pinmap.databinding.FilterDialogBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FilterDialog: BottomSheetDialogFragment() {

    private var _binding: FilterDialogBinding? = null
    private val binding get() = _binding!!
    private var filter = Filter()
    private lateinit var listener: Filterable

    interface Filterable {
        fun onFilter(filter: Filter)
    }

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
                if (dateRangePicker.selection == null)
                    return@addOnPositiveButtonClickListener

                filter.startDate = Date(dateRangePicker.selection!!.component1())
                filter.endDate = Date(dateRangePicker.selection!!.component2())

                binding.dateStart.text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    .format(filter.startDate!!)
                binding.dateEnd.text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    .format(filter.endDate!!)

            }

            dateRangePicker.show(parentFragmentManager, "dates")
        }

        binding.searchButton.setOnClickListener {
            filter.hasTags.clear()
            filter.hasTags.addAll(binding.tags.text.toString().split(";"))

            filter.textIncludes.clear()
            filter.textIncludes.addAll(binding.textIncludes.text.toString().split(";"))

            filter.lowestMood = binding.moodSlider.value.toInt().toByte()

            listener.onFilter(filter)

            dismiss()
        }

        return binding.root
    }

    fun setFilterListener(fragment : Filterable) {
        listener = fragment
    }
}