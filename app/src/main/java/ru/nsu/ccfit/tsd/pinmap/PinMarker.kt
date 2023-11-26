package ru.nsu.ccfit.tsd.pinmap

import android.os.Bundle
import android.view.View
import androidx.fragment.app.add
import androidx.fragment.app.findFragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import ru.nsu.ccfit.tsd.pinmap.pins.Pin

class PinMarker(mapView: MapView, mapActivity_: MapActivity, pin_: Pin) : Marker(mapView) {

    private val mapActivity = mapActivity_
    private val mapBinding = mapActivity_.getBinding()

    private val pin = pin_

    public override fun onMarkerClickDefault(marker: Marker?, mapView: MapView?): Boolean {
        // этот листенер вызывает окно редактирования маркера по нажатию
        val constructorFragment = mapActivity.supportFragmentManager.findFragmentById(R.id.fragmentContainerViewConstructorUI)
        if (constructorFragment != null) {
            val navController = findNavController(constructorFragment)

            val bundle = Bundle()

            //todo сделать обработку случаев когда некоторых полей нет
            bundle.putBoolean("new", false)
            bundle.putString("name", pin.name)
            bundle.putFloat("latitude", pin.latitude.toFloat())
            bundle.putFloat("longitude", pin.longitude.toFloat())
            if (pin.id != null) bundle.putInt("id", pin.id!!)//todo это норм?
            bundle.putStringArray("tags", pin.tags.toTypedArray())//todo это работает?
            bundle.putString("desc", pin.description)
            bundle.putByte("mood", pin.mood.toByte())//todo это норм?
            bundle.putSerializable("date", pin.date)//todo это работает?

            navController.navigate(R.id.pinConstructorFragment, bundle)

            //todo запретить двигать картой
        }

        setInfoWindow(null) // это чтобы не показывать InfoWindow при нажатии на маркер
        return super.onMarkerClickDefault(marker, mapView)
    }

}