package com.prachigupta.noterem;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng latLng;
    private LatLng current;

    String lat;
    String lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SharedPreferences pref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
        lat = pref.getString(Constant.SHARED_PREF_LATITUDE, null);
        lng = pref.getString(Constant.SHARED_PREF_LONGITUDE, null);
        if (lat != null && lat.length() > 0
                && lng != null && lng.length() > 0) {
            EditText eTSearch = (EditText) findViewById(R.id.etSearch);
            ImageButton bmSearch = (ImageButton) findViewById(R.id.bmSearch);
            ImageButton bmSave = (ImageButton) findViewById(R.id.bmSave);

            eTSearch.setVisibility(View.GONE);
            bmSearch.setVisibility(View.GONE);
            bmSave.setVisibility(View.GONE);


        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constant.SHARED_PREF_LONGITUDE, null);
        editor.putString(Constant.SHARED_PREF_LATITUDE, null);
        editor.apply();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        mMap.setMyLocationEnabled(true);


        if (lat != null && lat.length() > 0 && lng != null
                && lng.length() > 0) {

            double latitude = Double.parseDouble(lat);
            double longitude = Double.parseDouble(lng);

            current = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(current).title(latitude + ":" + longitude));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        }
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
    }

    public void onMapSearch(View view) {

        EditText locationSearch = (EditText) findViewById(R.id.etSearch);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geoCoder = new Geocoder(this);
            try {
                addressList = geoCoder.getFromLocationName(location, 1);
            } catch (IOException e) {

                e.printStackTrace();
            }


            Address address = null;
            if (addressList != null && addressList.size() > 0) {
                address = addressList.get(0);
            } else {
                Toast.makeText(getApplicationContext(), "Please enter valid location", Toast.LENGTH_LONG).show();
            }
            if (address != null) {
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                this.latLng = latLng;
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            } else {
                Toast.makeText(this, "No Location found", Toast.LENGTH_LONG).show();
            }


        }
    }

    public void onSave(View view) {
        if (latLng != null) {
            SharedPreferences pref = getSharedPreferences(Constant.SHARED_PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            Log.i("MapActivity ", latLng.latitude + ":" + latLng.longitude);
            editor.putString(Constant.SHARED_PREF_LATITUDE, latLng.latitude + "");
            editor.putString(Constant.SHARED_PREF_LONGITUDE, latLng.longitude + "");
            editor.apply();
            finish();
        } else if (current == null) {
            Toast.makeText(getApplicationContext(), "Please enter valid location", Toast.LENGTH_LONG).show();

        }

    }

}
