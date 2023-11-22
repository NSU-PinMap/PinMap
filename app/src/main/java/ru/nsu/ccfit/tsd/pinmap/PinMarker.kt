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
            //todo передавать информацию с пина в конструктор пинов?
            val bundle = Bundle()
            //todo добавить id пина чтобы фрагмент отправил запрос в базу на редактирование пина?
            bundle.putString("name", pin.name)
            bundle.putFloat("latitude", pin.latitude.toFloat())
            bundle.putFloat("longitude", pin.longitude.toFloat())
            bundle.putString("desc", pin.description)
            navController.navigate(R.id.pinConstructorFragment, bundle)
            //todo запретить двигать картой пока сидим во фрагменте или понять что это не надо
        }

        setInfoWindow(null) // это чтобы не показывать InfoWindow при нажатии на маркер
        return super.onMarkerClickDefault(marker, mapView)
    }

}