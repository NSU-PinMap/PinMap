package ru.nsu.ccfit.tsd.pinmap

import android.content.Context
import android.content.Intent
import android.widget.Toast
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class PinMarker : Marker {

    //val lateinit pin: Pin

    constructor(mapView: MapView) : super(mapView) //TODO override it?
    constructor(mapView: MapView, ctx: Context) : super(mapView, ctx)

    //TODO disable infowindow

    override fun onMarkerClickDefault(marker: Marker?, mapView: MapView?): Boolean {
        val context = mapView!!.context
        val intent = Intent(context, PinEditActivity::class.java)
        context.startActivity(intent)
        Toast.makeText(mapView!!.context, "clicked!", Toast.LENGTH_SHORT).show()
        setInfoWindow(null) // чтобы не показывать эту штуку при тыке
        return super.onMarkerClickDefault(marker, mapView)
    }

}