package cppbetterc.gapgame;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class StartPageActivity extends AppCompatActivity implements LocationListener{

    private TextView textStatus1, textStatus2;
    private LocationManager lms;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lms = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            getLocation(location);
        }
        else{
            Toast.makeText(this,"請開啟定位服務",Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }
    public void clickMe(View argc){
        getLocation(location);
    }

    public void clear(View argc){
        textStatus1.setText("null");
        textStatus2.setText("null");
        Toast.makeText(this,"清除目前座標位置",Toast.LENGTH_LONG).show();
    }

    public void getLocation(Location loc) {
        if (loc != null) {
            //經度 longitude; 緯度 latitude
            Double longitude = loc.getLongitude();
            Double latitude = location.getLatitude();
            textStatus1.setText("Longitude " + String.valueOf(longitude));
            textStatus2.setText("Latitude " + String.valueOf(latitude));
        } else {
            Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        getLocation(location);
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
}
