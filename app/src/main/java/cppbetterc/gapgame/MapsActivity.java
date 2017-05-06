package cppbetterc.gapgame;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private LocationManager lms;
    private Marker currentMarker, fistmarker;
    private Location location;
    private LatLng reLatLng,regulate;
    private float CameraPosition;
    private FloatingActionButton button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        button = (FloatingActionButton) findViewById(R.id.nowlocation);
        mapFragment.getMapAsync(this);
        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lms = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            location = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        lms.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,0,this);
        button.setOnClickListener(listen);
    }
    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            regulate = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(regulate,17));
        }
    };
    private void setMarker(Location location) {
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        if(currentMarker == null){
            currentMarker = mMap.addMarker(new MarkerOptions().position(current).title("Lat: " + location.getLatitude() + " Long:" + location.getLongitude()));
        }
        else{
            currentMarker.setPosition(current);
            currentMarker.setTitle("Lat: " + location.getLatitude() + " Long:" + location.getLongitude());
        }
    }
    private void setPolyLine(LatLng prevLatLng){
        if(reLatLng == null){
            reLatLng  = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.addPolyline(new PolylineOptions().add(reLatLng, prevLatLng).width(5).color(Color.BLUE));
            reLatLng = prevLatLng;
        }
        else{
            mMap.addPolyline(new PolylineOptions().add(prevLatLng, reLatLng).width(5).color(Color.BLUE));
            reLatLng = prevLatLng;
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     **/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        //暫緩載入經緯度太慢，暫緩3秒
        if(location == null){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        LatLng now = new LatLng(location.getLatitude(), location.getLongitude());
        fistmarker = mMap.addMarker(new MarkerOptions().position(now).title("Lat: " + now.latitude + " Long:" + now.longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(now,17));
        mMap.setOnMapClickListener(this);
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);換成衛星圖
    }
    @Override
    public void onLocationChanged(Location location) {
        if(mMap != null){
            fistmarker.remove();
            setMarker(location);
        }
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
    public void onMapClick(LatLng latLng) {
        regulate = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title("Lat: " + latLng.latitude + " Long:" + latLng.longitude));
        CameraPosition = mMap.getCameraPosition().zoom;
        setPolyLine(latLng);
        double distance = SphericalUtil.computeDistanceBetween(regulate,latLng)/1000;
        if(distance<5){
            Toast.makeText(this,String.valueOf(distance)+"Km",Toast.LENGTH_LONG).show();
        }
    }
}
