package ru.nsu.ccfit.tsd.pinmap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import ru.nsu.ccfit.tsd.pinmap.databinding.ActivityMainBinding
import ru.nsu.ccfit.tsd.pinmap.filter.Filter
import ru.nsu.ccfit.tsd.pinmap.filter.FilterDialog
import ru.nsu.ccfit.tsd.pinmap.pins.Pin
import ru.nsu.ccfit.tsd.pinmap.pins.PinController

class MapActivity : AppCompatActivity(), FilterDialog.Filterable {

    private var filter: Filter? = null

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map: MapView

    private lateinit var pinController: PinController

    private lateinit var binding: ActivityMainBinding
    fun getBinding() : ActivityMainBinding{
        return binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pinController = PinController.getController(this)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val permissions: Array<String> =
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissionsIfNecessary(permissions)

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(binding.root)

        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)

        map.setMultiTouchControls(true)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        map.minZoomLevel = 4.0
        map.maxZoomLevel = 22.0

        val mapController = map.controller
        //todo стартовые точки настраивайте как хотите
        mapController.setZoom(9.5)
        val startPoint = GeoPoint(48.8583, 2.2944)
        mapController.setCenter(startPoint)

        // Код для дефолтного toolbar
/*
        // Если вы решите передвинуть эти две строчки кода, имейте в виду, что от их расположения зависит работоспособность приложения
        // (буквально, что-то должно проинициализироваться до вызова setupActionBarWithNavController, но я не знаю, в чём именно дело)
        val navController = findNavController(R.id.fragmentContainerViewConstructorUI)
        setupActionBarWithNavController(navController)
*/

    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>()
        for (i in grantResults.indices) {
            permissionsToRequest.add(permissions[i])
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private fun requestPermissionsIfNecessary(permissions: Array<String>) {
        val permissionsToRequest = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    fun refreshMap() {
        if (filter != null)
            showPinsOnMap(pinController.getFilteredPins(filter!!))
        else
            showPinsOnMap(pinController.getAllPins())
    }

    override fun onFilter(filter: Filter) {
        Toast.makeText(this, "Вызван поиск на карте", Toast.LENGTH_SHORT).show()
        this.filter = filter
        refreshMap()
        findViewById<FloatingActionButton>(R.id.showAllMarkersFab).visibility = View.VISIBLE
    }

    fun clearFilter() {
        filter = null
        refreshMap()
    }

    fun showPinsOnMap(newPins : MutableList<Pin>){
        val res = this.resources
        val pinImage = ResourcesCompat.getDrawable(res, R.drawable.pin_marker, null)
        map.overlays.forEach {
            map.overlays.remove(it)
            map.invalidate()
        }
        for (pin in newPins){
            val marker = PinMarker(map, this, pin)
            marker.position = GeoPoint(pin.latitude, pin.longitude)
            marker.icon = pinImage
            marker.setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )
            map.overlays.add(marker)
            map.invalidate()
        }
    }

    fun isFiltered() : Boolean {
        return filter != null
    }

    // Код для дефолтного тулбара
/*
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerViewConstructorUI)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
*/
}