package ru.nsu.ccfit.tsd.pinmap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ru.nsu.ccfit.tsd.pinmap.R
import ru.nsu.ccfit.tsd.pinmap.databinding.PinItemBinding
import ru.nsu.ccfit.tsd.pinmap.pins.Pin
import java.util.Collections

class PinAdapter(private val pins: MutableList<Pin>) : RecyclerView.Adapter<PinAdapter.PinHolder>() {
    private var pinList : MutableList<Pin>

    init {
        pinList = pins
    }

    class PinHolder(item : View) : RecyclerView.ViewHolder(item) {
        private val binding = PinItemBinding.bind(item)

        fun bind(pin: Pin) = with(binding) {
            val name = pin.name
            val date = "дата: " + pin.date
            val latitude = "широта: " + pin.latitude
            val longitude = "долгота: " + pin.longitude

            pinNameTextView.text = name
            pinDateTextView.text = date
            latitudeTextView.text= latitude
            longitudeTextView.text= longitude
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
        holder.bind(pinList[position])
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
       // TODO
    }

    fun sortedDescByDate() {
        // TODO
    }
}