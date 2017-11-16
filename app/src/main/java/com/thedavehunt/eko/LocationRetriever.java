package com.thedavehunt.eko;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by david on 16/11/17.
 */

public class LocationRetriever extends Activity {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private  Location loc=null;

    public LocationRetriever() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    public Location getCurrentLocation(){


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                loc=location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent _intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(_intent);
            }
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);

        return loc;
    }
}
