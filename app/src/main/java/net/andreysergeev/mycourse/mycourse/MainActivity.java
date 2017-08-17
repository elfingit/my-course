package net.andreysergeev.mycourse.mycourse;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.Snackbar;

import net.andreysergeev.mycourse.mycourse.mapoverlay.StartPathButton;
import net.andreysergeev.mycourse.mycourse.mapoverlay.StopPathButton;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        LocationListener, StartPathButton.IOnTouchListener/*,
        StopPathButton.IOnTouchListener*/ {

    private final static String[] PERMISSION_LOCATION = { Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private final static long GPS_LOCATION_UPDATE_TIME = 500; //0.5 * 1000;
    private final static float GPS_LOCATION_UPDATE_DISTANCE = 10f;

    private LocationManager locationManager = null;

    private MapView map;
    private Polyline path;
    private ArrayList<GeoPoint> pathPoints = new ArrayList<>();

    private StartPathButton startPathButton;
    private StopPathButton stopPathButton;
    private boolean onCourse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_main);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        CompassOverlay compassOverlay = new CompassOverlay(getApplicationContext(),
                new InternalCompassOrientationProvider(getApplicationContext()), map);
        compassOverlay.enableCompass();

        map.getOverlays().add(compassOverlay);
        map.getController().setZoom(18);

        path = new Polyline();
        path.setGeodesic(true);
        path.setColor(Color.RED);
        path.setWidth(2f);
        map.getOverlays().add(path);

        startPathButton = new StartPathButton(getApplicationContext(), this);
        startPathButton.setCenter(35, 105);
        startPathButton.setEnabled(true);
        map.getOverlays().add(startPathButton);

        stopPathButton = new StopPathButton(getApplicationContext(), null);
        stopPathButton.setCenter(35,70);
        stopPathButton.setEnabled(false);
        map.getOverlays().add(stopPathButton);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }

    public void onResume(){
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        if (locationManager != null) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(getResources().getString(R.string.gps_settings))
                        .setMessage(getResources().getString(R.string.gps_not_enabled))
                        .setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                dialog.show();
            } else {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestGrantedAccessLocation();
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_LOCATION_UPDATE_TIME,
                            GPS_LOCATION_UPDATE_DISTANCE, this);
                }
            }
        }
    }

    private void requestGrantedAccessLocation() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            Snackbar.make(map, R.string.location_permission, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat
                                    .requestPermissions(MainActivity.this, PERMISSION_LOCATION, 0);
                        }
                    })
                    .show();

        } else {
            ActivityCompat
                    .requestPermissions(MainActivity.this, PERMISSION_LOCATION, 0);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        GeoPoint point = new GeoPoint(location);

        if (onCourse) {

            pathPoints.add(point);

            path.setPoints(pathPoints);
        }

        map.getController().setCenter(point);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onTouchStartBtn() {
        startPathButton.setEnabled(false);
        onCourse = true;
        stopPathButton.setEnabled(true);
    }

    /*@Override
    public void onTouchStopBtn() {
        startPathButton.setEnabled(true);
        stopPathButton.setEnabled(false);
        onCourse = false;
    }*/
}
