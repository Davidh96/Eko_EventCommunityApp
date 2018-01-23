package com.thedavehunt.eko;

import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class landingPage extends Activity {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    LocationManager locationManager;
    LocationListener locationListener;

    List<eventDoc> eventList;
    ListAdapter tempAdapter;

    public double lati;
    public double longi;

    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        Button logoutBtn = (Button) findViewById(R.id.logoutBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                finish();
            }
        });

        //FAB, used to allow creation of new events
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                Intent createevent = new Intent(landingPage.this,createEvent.class);
                startActivity(createevent);

            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            //when location is updated
            @Override
            public void onLocationChanged(Location location) {
                getLocalEvent(location.getLatitude(),location.getLongitude());
//                landingPage.this.longi =
//                landingPage.this.lati=l
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            //if gps is disabled
            @Override
            public void onProviderDisabled(String s) {
                Intent _intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(_intent);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.INTERNET
                },10);
                return;
            }
        }
        else{
            getLocation();
        }

        getLocation();



        eventList= new ArrayList<eventDoc>();

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear();

                for(DataSnapshot eventSnapshop: dataSnapshot.getChildren()){
                    //retrieve data from db and place it in a eventDoc structure
                    eventDoc evnt = eventSnapshop.getValue(eventDoc.class);
                    //add event to list
                    eventList.add(evnt);
                }
                //initialise adapter
                tempAdapter = new landingListAdapter(landingPage.this,eventList);

                ListView list = (ListView)findViewById(R.id.list1);
                //set adapter for list view
                list.setAdapter(tempAdapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent viewTask = new Intent(landingPage.this,viewEvent.class);

                        eventDoc evnt = (eventDoc)eventList.get(i);
                        viewTask.putExtra("id",evnt.getId());
                        startActivity(viewTask);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    void getLocation(){
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
    }


    private void getLocalEvent(double lati, double longi){

//        Toast.makeText(getApplicationContext(),"" + longi + ", " + lati,Toast.LENGTH_SHORT).show();
//        for(int i =0;i <eventList.size();i++) {
//            eventLat
//            double dLat = Math.toRadians(lat2 - lat1);
//            double dLon = Math.toRadians(lon2 - lon1);
//            lat1 = Math.toRadians(lat1);
//            lat2 = Math.toRadians(lat2);
//
//            double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
//            double c = 2 * Math.asin(Math.sqrt(a));
//            return R * c;
//        }
    }

}
