package ru.nsu.ccfit.tsd.pinmap

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
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
            bundle.putInt("type", 3)
            bundle.putInt("id", pin.id!!)
            navController.navigate(R.id.pinConstructorFragment, bundle)

            //todo запретить двигать картой пока находимся не в StartFragment (Вова сказал что надо, но я хз зачем)
            //mapBinding.map.controller.
        }

        setInfoWindow(null) // это чтобы не показывать InfoWindow при нажатии на маркер
        return super.onMarkerClickDefault(marker, mapView)
    }

}