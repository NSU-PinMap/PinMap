package ru.nsu.ccfit.pinmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    // умственно отсталый гайд по андроиду
    // https://www.youtube.com/watch?v=tZvjSl9dswg

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_main);

        // дефолтный сурс это мапник
        // https://wiki.openstreetmap.org/wiki/Mapnik
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        // такое есть, легко и бесплатно
        //final ITileSource tileSource = TileSourceFactory.USGS_TOPO;
        //map.setTileSource(tileSource);

        // есть альтернативы, но там нужны ключи (много бесплатных правда) (по этой ссылке всякое полезное, кэш например, плюс список pre-loaded tile sources)
        // https://osmdroid.github.io/osmdroid/Map-Sources.html#:~:text=osmdroid%20uses%20two%20components%20to,%2C%20Mapquest%2C%20Mapnik%2C%20etc.
        // using a different tile source
        /*map.setTileSource(new OnlineTileSourceBase("USGS Topo", 0, 18, 256, "",
                new String[] { "http://basemap.nationalmap.gov/ArcGIS/rest/services/USGSTopo/MapServer/tile/" }) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return getBaseUrl()
                        + MapTileIndex.getZoom(pMapTileIndex)
                        + "/" + MapTileIndex.getY(pMapTileIndex)
                        + "/" + MapTileIndex.getX(pMapTileIndex)
                        + mImageFilenameEnding;
            }
        });*/

        // теперь можно зумить двумя пальцами; хз как это проверять без подключения к физическому устройству
        //map.setBuiltInZoomControls(true);
        //map.setMultiTouchControls(true);

        // дефолтная локация
        IMapController mapController = map.getController();
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(startPoint);

        // тестовый маркер
        // https://stackoverflow.com/questions/55705988/how-to-add-marker-in-osmdroid
        Resources res = this.getResources();

        GeoPoint testPoint = new GeoPoint(48.7583, 2.1944);
        Marker testMarker = new Marker(map);
        testMarker.setPosition(testPoint);
        Drawable pinImage = ResourcesCompat.getDrawable(res, R.drawable.real_pin, null);
        testMarker.setIcon(pinImage); //используем свою картинку
        testMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(testMarker);

        GeoPoint testPoint2 = new GeoPoint(48.6583, 2.0944);
        Marker testMarker2 = new Marker(map);
        testMarker2.setPosition(testPoint2);
        testMarker2.setIcon(pinImage); //используем свою картинку
        testMarker2.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM); // это вроде просто указывает, какое место
        // на картинке соответствует указанной координате
        testMarker2.setVisible(false);
        map.getOverlays().add(testMarker2); // про оверлеи надо подумать, мб прямо сюда фрагмент добавить

        // тут можно прикрутить информационное окошко к маркеру
        // код красть отсюда
        // https://habr.com/ru/articles/224289/
        // про сам класс смотреть тут
        // https://osmdroid.github.io/osmdroid/javadocAll/org/osmdroid/views/overlay/infowindow/MarkerInfoWindow.html
        // но там скупо; тут вот есть примерчики + понятнее:
        // https://github.com/MKergall/osmbonuspack/wiki/Tutorial_2#7-customizing-the-bubble-behaviour
        // окошко отстой, используйте фрагменты
        //MarkerInfoWindow piw = new PinInfoWindow(map);
        //startMarker.setInfoWindow(piw);

        //кража координат по тыку (см тэг lnglat в логкате) + установка маркера:
        Overlay touchOverlay = new Overlay(this){
            ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = null;
            @Override
            public void draw(Canvas arg0, MapView arg1, boolean arg2) {

            }
            @Override
            public boolean onSingleTapConfirmed(final MotionEvent e, final MapView mapView) {

                final Drawable marker = getApplicationContext().getResources().getDrawable(R.drawable.real_pin);
                Projection proj = mapView.getProjection();
                GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());
                String longitude = Double.toString(((double)loc.getLongitudeE6())/1000000);
                String latitude = Double.toString(((double)loc.getLatitudeE6())/1000000);
                Log.d("lnglat", "- Latitude = " + latitude + ", Longitude = " + longitude);
                System.out.println("- Latitude = " + latitude + ", Longitude = " + longitude );
                ArrayList<OverlayItem> overlayArray = new ArrayList<OverlayItem>();
                OverlayItem mapItem = new OverlayItem("", "", new GeoPoint((((double)loc.getLatitudeE6())/1000000), (((double)loc.getLongitudeE6())/1000000)));
                mapItem.setMarker(marker);
                overlayArray.add(mapItem);
                if(anotherItemizedIconOverlay==null){
                    anotherItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(getApplicationContext(), overlayArray,null);
                    mapView.getOverlays().add(anotherItemizedIconOverlay);
                    mapView.invalidate();
                }else{
                    mapView.getOverlays().remove(anotherItemizedIconOverlay);
                    mapView.invalidate();
                    anotherItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(getApplicationContext(), overlayArray,null);
                    mapView.getOverlays().add(anotherItemizedIconOverlay);
                }
                //      dlgThread();
                return true;
            }
        };
        map.getOverlays().add(touchOverlay);

        // if you need to show the current location, add to permissions:
        // Manifest.permission.ACCESS_FINE_LOCATION
        // WRITE_EXTERNAL_STORAGE is required in order to show the map
        String permissions[];
        permissions = new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE };
        requestPermissionsIfNecessary(permissions);
    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        //why?
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}