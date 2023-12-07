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
import ru.nsu.ccfit.tsd.pinmap.databinding.ActivityMainBinding
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

    // Код для дефолтного toolbar
/*
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerViewConstructorUI)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
*/
}