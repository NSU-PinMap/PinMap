package ru.nsu.ccfit.tsd.pinmap.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import ru.nsu.ccfit.tsd.pinmap.R
import ru.nsu.ccfit.tsd.pinmap.databinding.PinItemBinding
import ru.nsu.ccfit.tsd.pinmap.pins.Pin

class PinAdapter(private val pins: MutableList<Pin>, private val navController: NavController)
    : RecyclerView.Adapter<PinAdapter.PinHolder>() {
    private var pinList = mutableListOf<Pin>()

    init {
        pinList.addAll(pins)
        sortAlphabetically()
    }

    class PinHolder(item : View) : RecyclerView.ViewHolder(item) {
        private val binding = PinItemBinding.bind(item)

        fun bind(pin: Pin, navController: NavController) = with(binding) {
            pinNameTextView.text = pin.name

            val coordinatesString = "широта: ${pin.latitude}; долгота: ${pin.longitude}"
            coordinatesTextView.text = coordinatesString

            val date = pin.date
            if (date != null) {
                val dateString = "дата: $date"
                pinDateTextView.text = dateString
            } else {
                pinDateTextView.text = "дата не указана"
            }

            val tagsList = pin.tags
            if (tagsList == null || tagsList.size == 0) {
                tagsListTextView.text = "теги не указаны"
            } else {
                val tagsListString = "теги: ${tagsList.joinToString ( separator = ";" )}"
                tagsListTextView.text = tagsListString
            }

            itemView.setOnClickListener {
                onPinPressed(pin, navController)
            }
        }

        private fun onPinPressed(pin: Pin, navController: NavController) {
            val bundle = Bundle()

            bundle.putInt("type", 3)
            bundle.putInt("id", pin.id!!)

            navController.navigate(R.id.pinConstructorFragment, bundle)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PinHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pin_item, parent, false)
        return PinHolder(view)
    }

    override fun getItemCount(): Int {
        return pinList.size
    }

    override fun onBindViewHolder(holder: PinHolder, position: Int) {
        val item = pinList[position]
        holder.bind(item, navController)
    }

    fun setPins(newPins: MutableList<Pin>) {
        pinList = newPins
        notifyDataSetChanged()
    }

    fun sortAlphabetically() {
        val comparator = Comparator<Pin> { pin1, pin2 -> pin1.name.compareTo(pin2.name)}
        pinList.sortWith(comparator)
        notifyDataSetChanged()
    }

    fun sortDescAlphabetically() {
        val comparator = Comparator<Pin> { pin1, pin2 -> pin2.name.compareTo(pin1.name)}
        pinList.sortWith(comparator)
        notifyDataSetChanged()
    }

    fun sortByDate() {
        val comparator = object : Comparator<Pin> {
            override fun compare(pin1: Pin, pin2: Pin): Int {
                if (pin1.date == null) {
                    if (pin2.date == null)
                        return 0

                    return 1
                }

                if (pin2.date == null)
                    return -1

                return pin1.date!!.compareTo(pin2.date)
            }
        }
        pinList.sortWith(comparator)
        notifyDataSetChanged()
    }

    fun sortedDescByDate() {
        val comparator = object : Comparator<Pin> {
            override fun compare(pin1: Pin, pin2: Pin): Int {
                if (pin1.date == null) {
                    if (pin2.date == null)
                        return 0

                    return 1
                }

                if (pin2.date == null)
                    return -1

                return pin2.date!!.compareTo(pin1.date)
            }
        }
        pinList.sortWith(comparator)
        notifyDataSetChanged()
    }
}