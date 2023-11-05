package ru.nsu.ccfit.tsd.pinmap

import android.content.Context
import android.widget.Toast
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class PinMarker : Marker {

    constructor(mapView: MapView) : super(mapView)
    constructor(mapView: MapView, ctx: Context) : super(mapView, ctx)

    override fun onMarkerClickDefault(marker: Marker?, mapView: MapView?): Boolean {
        // этот листенер вызывает окно редактирования маркера по нажатию
        val context = mapView!!.context
        // TODO: Вызывать PinConstructorActivity
        Toast.makeText(context, "TODO: Вызывать PinConstructorActivity", Toast.LENGTH_SHORT).show()
        setInfoWindow(null) // это чтобы не показывать InfoWindow при нажатии на маркер
        return super.onMarkerClickDefault(marker, mapView)
    }

}