package ru.nsu.ccfit.tsd.pinmap

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.findFragment
import androidx.fragment.app.replace
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import ru.nsu.ccfit.tsd.pinmap.databinding.ActivityMainBinding
import ru.nsu.ccfit.tsd.pinmap.databinding.FragmentPinConstructorBinding
import ru.nsu.ccfit.tsd.pinmap.pins.Pin

class PinMarker(mapView: MapView, mapActivity_: MapActivity, pin_: Pin) : Marker(mapView) {

    private val mapActivity = mapActivity_
    private val mapBinding = mapActivity_.getBinding()

    private val pin = pin_

    public override fun onMarkerClickDefault(marker: Marker?, mapView: MapView?): Boolean {
        // этот листенер вызывает окно редактирования маркера по нажатию
        //todo фрагмент виден при старте приложения и я понятия не имею как это менять
        // сейчас в мапактивити стоит костыль с меткой туду, который это решает
        val constructorFragment = mapActivity.supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        if (constructorFragment != null) {
            mapBinding.createdPin.visibility = View.GONE//todo не работает
            //todo запретить двигать картой пока сидим во фрагменте
            mapActivity.supportFragmentManager.beginTransaction()
                .remove(constructorFragment)
                .commit()
        } else {
            val bundle = Bundle()
            //todo добавить id пина чтобы фрагмент отправил запрос в базу на редактирование пина
            bundle.putString("name", pin.name)
            bundle.putDouble("latitude", pin.latitude)
            bundle.putDouble("longitude", pin.longitude)
            bundle.putString("desc", pin.description)
            mapActivity.supportFragmentManager.beginTransaction()
                .add<PinConstructorFragment>(R.id.fragmentContainerView, args = bundle)
                .commit()
        }

        setInfoWindow(null) // это чтобы не показывать InfoWindow при нажатии на маркер
        return super.onMarkerClickDefault(marker, mapView)
    }

}