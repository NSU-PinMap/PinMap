package ru.nsu.ccfit.tsd.pinmap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import ru.nsu.ccfit.tsd.pinmap.databinding.ActivityMainBinding

class MapActivity : AppCompatActivity() {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map: MapView

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val res = this.resources
        val pinImage = ResourcesCompat.getDrawable(res, R.drawable.pin_marker, null)

        /* перед тем как отрисовывать карту рекомендуется сначала получить разрешения
         ACCESS_FINE_LOCATION нужен чтобы показывать текущее местоположение
         WRITE_EXTERNAL_STORAGE нужен чтобы показывать карту (требование osmdroid) */
        val permissions: Array<String> =
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissionsIfNecessary(permissions)

        setAddMarkerFabClickListener()
        setConfirmMarkerFabClickListener()
        setCancelMarkerFabClickListener()

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(binding.root)

        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)

        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        map.setMultiTouchControls(true)

        val mapController = map.controller
        mapController.setZoom(9.5)
        val startPoint = GeoPoint(48.8583, 2.2944)
        mapController.setCenter(startPoint)

        val testMarker = PinMarker(map)
        testMarker.position = GeoPoint(48.7583, 2.1944)
        testMarker.icon = pinImage
        testMarker.setAnchor(
            Marker.ANCHOR_CENTER,
            Marker.ANCHOR_BOTTOM
        )
        map.overlays.add(testMarker)
    }

    private fun setCancelMarkerFabClickListener() {
        binding.cancelMarkerFab.setOnClickListener { view ->
            Toast.makeText(view.context, "Создание маркера отменено!", Toast.LENGTH_SHORT).show()
            binding.addMarkerFab.visibility = View.VISIBLE
            view.visibility = View.GONE
            binding.confirmMarkerFab.visibility = View.GONE
        }
    }

    private fun setConfirmMarkerFabClickListener() {
        binding.confirmMarkerFab.setOnClickListener { view ->
            binding.addMarkerFab.visibility = View.VISIBLE
            view.visibility = View.GONE
            binding.cancelMarkerFab.visibility = View.GONE

            /* создать перманентный маркер тут не получается, т.к. случается onCreate() у мэйн активити, и всё обнуляется
            на это можно забить, если в конструкторе добавить маркер в базу, а в onCreate() вытащить его из базы
            текущий код просто покажет маркер за момент до того как откроется активити по созданию маркера */
            /*todo заменить активити фрагментами чтобы onCreate() не вызывать каждый раз;
                либо можно как-то поменять поведение активити при запуске интента, не уверен
                использовать кэширование чтобы не кидать кучу запросов каждый раз; однако это если остальное успеем*/
            val testMarker = PinMarker(binding.map)
            testMarker.position = map.mapCenter as GeoPoint
            testMarker.icon = ResourcesCompat.getDrawable(this.resources, R.drawable.pin_marker, null)
            testMarker.setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )
            binding.map.overlays.add(testMarker)
            binding.map.invalidate()

            // TODO: Вызывать PinConstructorActivity
            Toast.makeText(
                view.context,
                "TODO: Вызывать PinConstructorActivity",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setAddMarkerFabClickListener() {
        binding.addMarkerFab.setOnClickListener { view ->
            view.visibility = View.GONE
            binding.confirmMarkerFab.visibility = View.VISIBLE
            binding.cancelMarkerFab.visibility = View.VISIBLE
        }
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

}