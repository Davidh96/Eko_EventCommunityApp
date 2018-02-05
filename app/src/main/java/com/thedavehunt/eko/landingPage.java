package com.thedavehunt.eko;

import android.*;
import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;


public class landingPage extends Activity {

    List<eventDoc> eventList;
    ListAdapter tempAdapter;

    ProgressBar loadingCircle;

    FirebaseAuth auth;

private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        url=getResources().getString(R.string.serverURL);

        //views
        loadingCircle = (ProgressBar)findViewById(R.id.loadingCircle);


        eventList = new ArrayList<eventDoc>();

         String test4="{'events':[{'-L4HHHAj1-7OmJkDeCZu':{'eventTime':'1652','eventDescription':'Hshsgsgvsgsvwggwgwvwggwgwgwgw','eventLocation':'53.25876.135','eventCategory':'Sport','eventAuthorID':'TozRmqVdeVOYpT9jOXJHXHAkOXq2','eventName':'Test','eventAuthor':'DaveHunt','members':[{'id':'TozRmqVdeVOYpT9jOXJHXHAkOXq2','name':'DaveHunt'}],'id':'-L4HHHAj1-7OmJkDeCZu','eventDate':'2018-03-15'}},{'-L4HHt-nUSRJwtnthNyV':{'eventTime':'1655','eventDescription':'Sgshshsbsbsbhsjsuwiwiwisbsbxbdb','eventLocation':'53.25876.135','eventCategory':'Sport','eventAuthorID':'TozRmqVdeVOYpT9jOXJHXHAkOXq2','eventName':'Hsbsbsb','eventAuthor':'DaveHunt','members':[{'id':'TozRmqVdeVOYpT9jOXJHXHAkOXq2','name':'DaveHunt'}],'id':'-L4HHt-nUSRJwtnthNyV','eventDate':'2018-03-08'}}]}";

        getLocation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setListContents();
    }

    //retrieve event data from server and put it in the list
    void setListContents(){

        loadingCircle.setVisibility(View.VISIBLE);

        eventList= new ArrayList<eventDoc>();

        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the loading after completion
                        loadingCircle.setVisibility(View.INVISIBLE);

                        try {

                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);

                            //place events into an array
                            JSONArray result = obj.getJSONArray("events");


                            //now looping through all the elements of the json array
                            for (int i = 0; i < result.length(); i++) {
                                //indicated where id starts
                                String startString = "{\"-";
                                String temp = result.getJSONObject(i).toString();
                                int startInd = temp.indexOf(startString);
                                //indicates end of id
                                String endString ="\"";
                                int endInd = temp.indexOf(endString,temp.indexOf(endString)+1);

                                //get JSON event key
                                String key = temp.substring(startInd+startString.length(),endInd);
                                JSONObject eventObj = result.getJSONObject(i).getJSONObject("-" + key);

                                //place event details into an eventDoc
                                eventDoc event = new eventDoc(eventObj.getString("id"),eventObj.getString("eventName"),eventObj.getString("eventAuthor"),eventObj.getString("eventAuthorID"),
                                        eventObj.getString("eventDescription"),eventObj.getString("eventCategory"),eventObj.getString("eventLocation"),eventObj.getString("eventDate"),
                                        eventObj.getString("eventTime"));

                                //add event to list
                                eventList.add(event);


                            }

                            //initialise adapter
                            tempAdapter = new landingListAdapter(landingPage.this,eventList);

                            ListView list = (ListView)findViewById(R.id.list1);
                            //set adapter for list view
                            list.setAdapter(tempAdapter);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);

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

    void getLocation(){

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            //when location is updated
            @Override
            public void onLocationChanged(Location location) {

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

        //request permission
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
//        else{
//            getLocation();
//        }

        //getLocation();

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
    }

    //is called when refresh button is clicked
    public void refreshList(View v)
    {
        setListContents();
    }

    //is called when logout button is clicked
    public void logout(View v)
    {
        auth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        finish();
    }

    //called when FAB create button is clicked
    public void createEvent(View v)
    {
        //TODO
        Intent createevent = new Intent(landingPage.this,createEvent.class);
        startActivity(createevent);
    }

}
