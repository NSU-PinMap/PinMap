package ru.nsu.ccfit.tsd.pinmap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import ru.nsu.ccfit.tsd.pinmap.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map: MapView

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val res = this.resources
        val pinImage = ResourcesCompat.getDrawable(res, R.drawable.pin_marker, null)

        /* рекомендуется сначала получить разрешения
         ACCESS_FINE_LOCATION нужен чтобы показывать текущее местоположение
         WRITE_EXTERNAL_STORAGE нужен чтобы показывать карту */
        val permissions: Array<String>
        permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissionsIfNecessary(permissions)

        /* проинициализировать кнопку добавления маркера */
        binding.addMarkerFab.setOnClickListener { view ->
            view.visibility = View.GONE /* убираем кнопку на время */
            binding.confirmMarkerFab.visibility = View.VISIBLE /* зато показываем кнопку-подтверждение */
            binding.cancelMarkerFab.visibility = View.VISIBLE /* и кнопку-отмену */
        }

        /* проинициализировать кнопку подтверждения добавления маркера */
        binding.confirmMarkerFab.setOnClickListener { view ->
            binding.addMarkerFab.visibility = View.VISIBLE /* снова показываем кнопку добавления маркера */
            view.visibility = View.GONE /* а эту снова убираем */
            binding.cancelMarkerFab.visibility = View.GONE /* и cancel тоже */

            /* создать перманентный маркер тут не получается, т.к. случается onCreate() у мэйн активити, и всё обнуляется
            на это можно забить, если в конструкторе добавить маркер в базу, а в onCreate() вытащить его из базы
            текущий код просто покажет маркер за момент до того как откроется активити по созданию маркера */
            /*todo заменить активити фрагментами чтобы onCreate() не вызывать каждый раз;
            todo либо можно как-то поменять поведение активити при запуске интента, не уверен
            todo использовать кэширование чтобы не кидать кучу запросов каждый раз; однако это если остальное успеем*/
            val testMarker = PinMarker(binding.map)
            testMarker.position = map.mapCenter as GeoPoint
            testMarker.icon = pinImage /* используем свою картинку */
            testMarker.setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )/* это указание, какое место
            на картинке соответствует указанной координате
            в нашем случае это центр низа, т.е. кончик пина */
            binding.map.overlays.add(testMarker)
            binding.map.invalidate()

            /* вызываем активити с созданием маркера */
            val context = map.context
            val intent = Intent(context, PinCreateActivity::class.java)
            context.startActivity(intent)
        }

        /* проинициализировать кнопку отмены добавления маркера */
        binding.cancelMarkerFab.setOnClickListener { view ->
            Toast.makeText(view.context, "Создание маркера отменено!", Toast.LENGTH_SHORT).show()
            binding.addMarkerFab.visibility = View.VISIBLE /* снова показываем кнопку добавления маркера */
            view.visibility = View.GONE /* а эту снова убираем */
            binding.confirmMarkerFab.visibility = View.GONE /* и confirm тоже убираем */
        }

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(binding.root)

        /* дефолтный сурс тайлов это мапник
        https://wiki.openstreetmap.org/wiki/Mapnik */
        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        /* такое тоже есть, легко и бесплатно, зато выглядит плохо и грузится долго */
        /*final ITileSource tileSource = TileSourceFactory.USGS_TOPO;
        map.setTileSource(tileSource);*/

        /* это для зума двумя пальцами */
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        /* дефолтная локация при загрузке приложения
        она будет появляться всегда при onCreate, а onCreate появляется при смене активити!
         */
        val mapController = map.controller
        mapController.setZoom(9.5)
        val startPoint = GeoPoint(48.8583, 2.2944)
        mapController.setCenter(startPoint)

        /* стартовый тестовый маркер
        https://stackoverflow.com/questions/55705988/how-to-add-marker-in-osmdroid */
        val testMarker = PinMarker(map)
        testMarker.position = GeoPoint(48.7583, 2.1944)
        testMarker.icon = pinImage
        testMarker.setAnchor(
            Marker.ANCHOR_CENTER,
            Marker.ANCHOR_BOTTOM
        )
        map.overlays.add(testMarker)

        /* это листенер нажатия на маркер
        всё можно прописать в классе PinMarker, так что листенер ставить не надо */
        /*testMarker.setOnMarkerClickListener { marker, mapView ->
            // делаем интент и вызываем активити по редактировании
            val context = map.context
            val intent = Intent(context, PinEditActivity::class.java)
            context.startActivity(intent)
            true
        }*/

        /* получение координат по тыку + установка маркера -- это не нужно, поскольку установка маркера:
         отрисовка перекрестия (ну или чего там) и движение экрана
         я это здесь оставил, так как, возможно, оверлеи (для нажатий, например) ещё пригодятся */
        /*val touchOverlay: Overlay = object : Overlay(this) {
            var anotherItemizedIconOverlay: ItemizedIconOverlay<OverlayItem>? = null

            override fun draw(arg0: Canvas, arg1: MapView, arg2: Boolean) {}

            override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {

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

                // делаем интент и вызываем активити по созданию
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

    override fun onResume() {
        super.onResume()
        /*this will refresh the osmdroid configuration on resuming.
        if you make changes to the configuration, use
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));*/
        map.onResume() /* needed for compass, my location overlays, v6.0.0 and up */
    }

    override fun onPause() {
        super.onPause()
        /* this will refresh the osmdroid configuration on resuming.
        if you make changes to the configuration, use
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration.getInstance().save(this, prefs); */
        map.onPause() /* needed for compass, my location overlays, v6.0.0 and up */
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