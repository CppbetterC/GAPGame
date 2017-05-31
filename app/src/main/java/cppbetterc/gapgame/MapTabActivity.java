package cppbetterc.gapgame;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Path;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.VolumeProviderCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;


import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import static android.content.Context.LOCATION_SERVICE;


public class MapTabActivity extends Fragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnMapClickListener {
    private FragmentActivity fragmentActivity;
    private GoogleMap mMap;
    private LocationManager lms;
    private Marker currentMarker, fistmarker;
    private Location location;
    private LatLng reLatLng,regulate, now;
    private float CameraPosition;
    private Button button;

    static final LatLng FCU = new LatLng(24.178581,120.648063);
    private ArrayList markerPoints;


    private Boolean getservice = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_tab, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
            View v = getView();
//        Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        button = (Button) v.findViewById(R.id.nowlocation);
        mapFragment.getMapAsync(this);
        LocationManager status = (LocationManager) (getActivity().getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lms = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

        location = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        else {
            Toast.makeText(getActivity(), "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        lms.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,0,this);
        button.setOnClickListener(listen);
}


    @Override
    public void onPause() {
        super.onPause();
        if(getservice){
            lms.removeUpdates(this);
        }
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
    //爛爛畫直線方法
//    private void setPolyLine(LatLng prevLatLng){
//            if(reLatLng == null){
//                reLatLng  = new LatLng(location.getLatitude(),location.getLongitude());
//                mMap.addPolyline(new PolylineOptions().add(reLatLng, prevLatLng).width(5).color(Color.BLUE));
//                reLatLng = prevLatLng;
//            }
//            else{
//                mMap.addPolyline(new PolylineOptions().add(prevLatLng, reLatLng).width(5).color(Color.BLUE));
//                reLatLng = prevLatLng;
//            }
//    }


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
//             Add a marker in Sydney and move the camera
//            暫緩載入經緯度太慢，暫緩3秒
            if(location == null){
                try
                {
                    Thread.sleep(3000);
                }
                catch (InterruptedException e)
                {
                // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            LatLng now = new LatLng (24.1788,120.5766646);
            if(getservice){
                now = new LatLng(location.getLatitude(), location.getLongitude());
            }
            fistmarker = mMap.addMarker(new MarkerOptions().position(now).title("Lat: " + now.latitude + " Long:" + now.longitude));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(now,17));
            mMap.setOnMapClickListener(this);
//            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);換成衛星圖
            }
    @Override
    public void onLocationChanged(Location location) {
            if(mMap != null){
                fistmarker.remove();
                setMarker(location);
            }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onMapClick(LatLng latLng) {
            if(regulate == null){
                regulate  = new LatLng(location.getLatitude(),location.getLongitude());
            }
            mMap.addMarker(new MarkerOptions().position(latLng).title("Lat: " + latLng.latitude + " Long:" + latLng.longitude));
            CameraPosition = mMap.getCameraPosition().zoom;
            //setPolyLine(latLng);
            String url = getDirectionsUrl(regulate, latLng);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
            double distance = SphericalUtil.computeDistanceBetween(regulate,latLng)/1000;
            if(distance<5){
                Toast.makeText(getActivity(),String.valueOf(distance)+"Km",Toast.LENGTH_LONG).show();
            }
            regulate = latLng;
    }
    ////完全不能理解
    private String getDirectionsUrl(LatLng regulate, LatLng latLng) {
        String origin = "origin=" + regulate.latitude + "," + regulate.longitude;
        String last = "destination=" + latLng.latitude + "," + latLng.longitude;
        String parameters = origin + "&" + last + "&" + "sensor=false";
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters;
        return url;
    }
    private String downloadUrl(String Url) throws IOException{
        String data ="";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(Url);
            httpURLConnection = (HttpURLConnection)  url.openConnection();
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String line="";
            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }
            data = stringBuffer.toString();
            bufferedReader.close();
        }catch (Exception e){
        }finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return data;
    }
    private class DownloadTask extends AsyncTask<String ,Void, String>{
        @Override
        protected String doInBackground(String... params) {
            String data = "";
            try {
                data = downloadUrl(params[0]);
            }catch (Exception e){

            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(s);
        }
    }
    private class ParserTask extends AsyncTask<String, Integer,List<List<HashMap<String, String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... params) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> path = null;
            try{
                jsonObject = new JSONObject(params[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                path = parser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return path;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            //super.onPostExecute(lists);
            ArrayList<LatLng> points = null;
            PolylineOptions polylineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            for(int i = 0;i < result.size();i++){
                points = new ArrayList<LatLng>();
                polylineOptions = new PolylineOptions();
                List<HashMap<String , String >> path = result.get(i);
                for(int j=0;j<path.size();j++){
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                polylineOptions.addAll(points);
                polylineOptions.width(5);
                polylineOptions.color(Color.BLUE);
            }
            mMap.addPolyline(polylineOptions);
        }
    }


}