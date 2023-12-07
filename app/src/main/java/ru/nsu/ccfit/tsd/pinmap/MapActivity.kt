package ru.nsu.ccfit.tsd.pinmap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import ru.nsu.ccfit.tsd.pinmap.databinding.ActivityMainBinding
import ru.nsu.ccfit.tsd.pinmap.pins.Pin
import ru.nsu.ccfit.tsd.pinmap.pins.PinController

class MapActivity : AppCompatActivity() {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map: MapView
    private lateinit var pinController: PinController

    private lateinit var binding: ActivityMainBinding
    fun getBinding() : ActivityMainBinding{
        return binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val res = this.resources
        val pinImage = ResourcesCompat.getDrawable(res, R.drawable.pin_marker, null)

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

        val mapController = map.controller
        //todo стартовые точки настраивайте как хотите
        mapController.setZoom(9.5)
        val startPoint = GeoPoint(48.8583, 2.2944)
        mapController.setCenter(startPoint)

        //todo это тестовый пин, его надо будет удалить
        val pin = Pin("test", 48.7583, 2.1944)
        pin.description = "description of test"
        pin.mood = 2u
        pin.tags.add("test tag")
        val testMarker = PinMarker(
            binding.map,
            this,
            pin
        )
        testMarker.position = GeoPoint(48.7583, 2.1944)
        testMarker.icon = pinImage
        testMarker.setAnchor(
            Marker.ANCHOR_CENTER,
            Marker.ANCHOR_BOTTOM
        )
        map.overlays.add(testMarker)

        //todo надо на карту загружать все маркеры из базы вот тут;
        // надо будет породить для каждого маркера по объекту PinMarker и Pin (Pin внутри PinMarker)
        pinController = PinController.getController(applicationContext)

        // Код для дефолтного тулбара (чтобы это работало, нужно поменять themes.xml)
/*
        // Если вы решите передвинуть эти две строчки кода, имейте в виду, что от их расположения зависит работоспособность приложения
        // (буквально, что-то должно проинициализироваться до вызова setupActionBarWithNavController, но я не знаю, в чём именно дело)
        val navController = findNavController(R.id.fragmentContainerViewConstructorUI)
        //setupActionBarWithNavController(navController)
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

    // Код для дефолтного тулбара
/*
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerViewConstructorUI)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
*/
}