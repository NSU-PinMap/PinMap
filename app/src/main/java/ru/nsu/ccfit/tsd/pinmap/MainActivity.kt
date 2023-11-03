package ru.nsu.ccfit.tsd.pinmap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import ru.nsu.ccfit.tsd.pinmap.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map: MapView

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("idiot", "oncreate called")

        binding = ActivityMainBinding.inflate(layoutInflater)

        val res = this.resources
        val pinImage = ResourcesCompat.getDrawable(res, R.drawable.real_pin, null)

        // рекомендуется сначала получить разрешения
        // ACCESS_FINE_LOCATION нужен чтобы показывать текущее местоположение
        // WRITE_EXTERNAL_STORAGE нужен чтобы показывать карту
        val permissions: Array<String>
        permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissionsIfNecessary(permissions)

        //проинициализировать кнопку добавления маркера
        binding.addMarkerFab.setOnClickListener { view ->
            //Toast.makeText(view.context, "you add marker", Toast.LENGTH_SHORT).show()
            view.visibility = View.GONE // убираем кнопку на время
            binding.confirmMarkerFab.visibility = View.VISIBLE // зато показываем кнопку-подтверждение
            binding.cancelMarkerFab.visibility = View.VISIBLE // и кнопку-отмену
        }

        //проинициализировать кнопку подтверждения добавления маркера
        binding.confirmMarkerFab.setOnClickListener { view ->
            //Toast.makeText(view.context, "you confirm marker", Toast.LENGTH_SHORT).show()
            Toast.makeText(view.context, map.overlays.size.toString(), Toast.LENGTH_SHORT).show()
            binding.addMarkerFab.visibility = View.VISIBLE // снова показываем кнопку добавления маркера
            view.visibility = View.GONE // а эту снова убираем
            binding.cancelMarkerFab.visibility = View.GONE // и cancel тоже

            //TODO создать маркер тут не получается, т.е. случается onCreate() у мэйн активити, и всё обнуляется
            //TODO на это можно забить, если в конструкторе добавить маркер в базу, а в onCreate()
            //TODO вытащить его из базы
            val testMarker = PinMarker(binding.map)
            testMarker.position = map.mapCenter as GeoPoint
            testMarker.icon = pinImage //используем свою картинку
            testMarker.setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            ) // это вроде просто указывает, какое место
            // на картинке соответствует указанной координате
            binding.map.overlays.add(testMarker)
            binding.map.invalidate()

            val context = map.context
            val intent = Intent(context, PinCreateActivity::class.java)
            context.startActivity(intent) // вызываем активити с созданием маркера
        }

        //проинициализировать кнопку отмены добавления маркера
        binding.cancelMarkerFab.setOnClickListener { view ->
            Toast.makeText(view.context, "Создание маркера отменено!", Toast.LENGTH_SHORT).show()
            binding.addMarkerFab.visibility = View.VISIBLE // снова показываем кнопку добавления маркера
            view.visibility = View.GONE // а эту снова убираем
            binding.confirmMarkerFab.visibility = View.GONE // и confirm тоже
        }

        //load/initialize the osmdroid configuration, this can be done
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        //inflate and create the map
        //setContentView(R.layout.activity_main)
        setContentView(binding.root)

        // дефолтный сурс это мапник
        // https://wiki.openstreetmap.org/wiki/Mapnik
        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        // такое есть, легко и бесплатно
        //final ITileSource tileSource = TileSourceFactory.USGS_TOPO;
        //map.setTileSource(tileSource);

        // это для зума двумя пальцами
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        // дефолтная локация
        val mapController = map.controller
        mapController.setZoom(9.5)
        val startPoint = GeoPoint(48.8583, 2.2944)
        mapController.setCenter(startPoint)

        // тестовый маркер
        // https://stackoverflow.com/questions/55705988/how-to-add-marker-in-osmdroid
        val testMarker = PinMarker(map)
        testMarker.position = GeoPoint(48.7583, 2.1944)
        testMarker.icon = pinImage //используем свою картинку
        testMarker.setAnchor(
            Marker.ANCHOR_CENTER,
            Marker.ANCHOR_BOTTOM
        ) // это вроде просто указывает, какое место
        // на картинке соответствует указанной координате
        map.overlays.add(testMarker)

        // всё можно прописать в классе пинмаркер, так что листенер ставить не надо
        /*testMarker.setOnMarkerClickListener { marker, mapView ->
            // делаем интент и вызываем активити по редактировании
            val context = map.context
            val intent = Intent(context, PinEditActivity::class.java)
            context.startActivity(intent)
            true
        }*/

        // кража координат по тыку + установка маркера -- это не нужно, поскольку установка маркера:
        // отрисовка перекрестия (ну или чего там) и движение экрана
        /*val touchOverlay: Overlay = object : Overlay(this) {
            var anotherItemizedIconOverlay: ItemizedIconOverlay<OverlayItem>? = null

            override fun draw(arg0: Canvas, arg1: MapView, arg2: Boolean) {}

            override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {

                //TODO only do this if not clicked on a marker

                // по тыку получаем координаты тыка
                val marker = applicationContext.resources.getDrawable(R.drawable.real_pin)
                val proj = mapView.projection
                val loc = proj.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint
                val longitude = java.lang.Double.toString(loc.longitudeE6.toDouble() / 1000000)
                val latitude = java.lang.Double.toString(loc.latitudeE6.toDouble() / 1000000)

                // установка маркера по тыку
                val overlayArray = ArrayList<OverlayItem>()
                val mapItem = OverlayItem(
                    "", "", GeoPoint(
                        loc.latitudeE6.toDouble() / 1000000,
                        loc.longitudeE6.toDouble() / 1000000
                    )
                )
                mapItem.setMarker(marker)
                overlayArray.add(mapItem)

                // делаем интент и вызываем активити по созданию TODO а остальной код выполнится?
                /*val context = map.context
                val intent = Intent(context, PinCreateActivity::class.java)
                context.startActivity(intent)*/

                // не знаю что это; вроде просто обновляет оверлеи по-человечески
                /*if (anotherItemizedIconOverlay == null) {
                    anotherItemizedIconOverlay =
                        ItemizedIconOverlay(applicationContext, overlayArray, null)
                    mapView.overlays.add(anotherItemizedIconOverlay)
                    mapView.invalidate()
                } else {
                    mapView.overlays.remove(anotherItemizedIconOverlay)
                    mapView.invalidate()
                    anotherItemizedIconOverlay =
                        ItemizedIconOverlay(applicationContext, overlayArray, null)
                    mapView.overlays.add(anotherItemizedIconOverlay)
                }*/
                //      dlgThread();
                return true
            }
        }
        map.overlays.add(touchOverlay)*/
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause() //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        // не знаю что это
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
            ) { // разрешения не дали
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