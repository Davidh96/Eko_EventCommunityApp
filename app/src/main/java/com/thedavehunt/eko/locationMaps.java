package com.thedavehunt.eko;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class locationMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    public static double locLong;
    public static double locLat;

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
        //display user loation
        mMap.setMyLocationEnabled(true);
        //display button to show user location
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Add an event location marker and move the camera
        LatLng eventLoc = new LatLng(locLat, locLong);
        mMap.addMarker(new MarkerOptions().position(eventLoc).title("Event Location"));
        //set camera position and zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLoc,15));


    }

}
