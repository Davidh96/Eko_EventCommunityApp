package com.thedavehunt.eko;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    public double locLong;
    public double locLat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_maps);

        //get user location
        Intent i = getIntent();
        locLat=i.getDoubleExtra("lat",0);
        locLong=i.getDoubleExtra("long",0);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        //display user loation
        mMap.setMyLocationEnabled(true);
        //display button to show user location
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Add an event location marker and move the camera
        LatLng eventLoc = new LatLng(locLat, locLong);
        mMap.addMarker(new MarkerOptions().position(eventLoc).title("Event Location"));
        //set camera position and zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLoc,15));


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //get the current zoom value
                float lastZoom=mMap.getCameraPosition().zoom;
                mMap.clear();

                LocationMaps.this.locLat=latLng.latitude;
                LocationMaps.this.locLong=latLng.longitude;

                // Add an event location marker and move the camera
                LatLng eventLoc = new LatLng(locLat, locLong);
                mMap.addMarker(new MarkerOptions().position(eventLoc).title("Event Location"));
                //set camera position and zoom
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLoc,lastZoom));

            }
        });


    }

    //return selected location
    public void returnLocation(View v){
        Intent i = new Intent();
        //pas the latitude and longitude
        i.putExtra("eLat",locLat);
        i.putExtra("eLong",locLong);
        setResult(Activity.RESULT_OK,i);
        finish();
    }

}
