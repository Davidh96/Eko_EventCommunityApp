package com.thedavehunt.eko;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkManager {

    private static final String TAG = "NetworkManager";
    private static NetworkManager instance = null;

    Context context;
    public RequestQueue requestQueue;

    private NetworkManager(Context context)
    {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.context = context;
    }

    public static synchronized NetworkManager getInstance(Context context)
    {
        if (null == instance)
            instance = new NetworkManager(context);
        return instance;
    }

    //for when context has already been passed
    public static synchronized NetworkManager getInstance()
    {
        if (null == instance)
        {
            throw new IllegalStateException(NetworkManager.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }


    //get user details from Firebase database
    public void readUserDetails(String userID,final SomeCustomListener<JSONObject> listener){
        String url=context.getResources().getString(R.string.serverURLreadUserDetails) + userID;

        //send request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,new JSONObject(),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        if(null != response.toString())
                            listener.getResult(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (null != error.networkResponse)
                        {
                            Log.d(TAG + ": ", "Error Response code: " + error.networkResponse.statusCode);
                            listener.getResult(new JSONObject());
                        }
                    }
                });


        requestQueue.add(request);
    }


    //post an event to database
    public void postEvent(EventDoc event,final SomeCustomListener<JSONObject> listener) {

        String url = context.getResources().getString(R.string.serverURLSaveEvent);

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //set JSON info
        HashMap req = new HashMap();
        req.put("eventAuthor",user.getDisplayName() );
        req.put("eventAuthorID", user.getUid());
        req.put("eventCategory", event.getEventCategory());
        req.put("eventDate", event.getEventDate());
        req.put("eventDescription", event.getEventDescription());
        req.put("eventLocation", event.getEventLocation());
        req.put("eventName", event.getEventName());
        req.put("eventTime", event.getEventTime());

        //send request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(req),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        if(null != response.toString())
                            listener.getResult(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (null != error.networkResponse)
                        {
                            Log.d(TAG + ": ", "Error Response code: " + error.networkResponse.statusCode);
                            listener.getResult(new JSONObject());
                        }
                    }
                });
        //add request to queue
        requestQueue.add(request);
    }

    //delete an event
    public void deleteEvent(String eventID,final SomeCustomListener<String> listener) {

        String url = context.getResources().getString(R.string.serverURLDeleteEvent) + eventID;

        //send request
        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        if(null != response.toString())
                            listener.getResult(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (null != error.networkResponse)
                        {
                            Log.d(TAG + ": ", "Error Response code: " + error.networkResponse.statusCode);
                            listener.getResult("error");
                        }
                    }
                });

        //add to request queue
        requestQueue.add(request);
    }

    //delete member from an event
    public void deleteMember(String eventID,final SomeCustomListener<String> listener) {

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String url = context.getResources().getString(R.string.serverURLDeleteMember) + eventID + "/" +user.getUid();

        //send request
        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        if(null != response.toString())
                            listener.getResult(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (null != error.networkResponse)
                        {
                            Log.d(TAG + ": ", "Error Response code: " + error.networkResponse.statusCode);
                            listener.getResult("error");
                        }
                    }
                });
        //add to request queue
        requestQueue.add(request);
    }

    //add rating for an event
    public void addRating(String eventID,float Rating,final SomeCustomListener<JSONObject> listener) {

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String url = context.getResources().getString(R.string.serverURLAddRating);


        //set JSON format
        HashMap req = new HashMap();
        req.put("eventID",eventID);
        req.put("userID",user.getUid());
        req.put("Rating",Rating);

        //send request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(req),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d(TAG + ": ", "addRating Response : " + response.toString());
                        if(null != response.toString())
                            listener.getResult(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (null != error.networkResponse)
                        {
                            Log.d(TAG + ": ", "Error Response code: " + error.networkResponse.statusCode);
                            listener.getResult(new JSONObject());
                        }
                    }
                });

        //add request to queue
        requestQueue.add(request);
    }

    //add member to event
    public void addMember(String eventID,final SomeCustomListener<JSONObject> listener) {

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String url = context.getResources().getString(R.string.serverURLAddMember);

        //set JSON contents
        HashMap req = new HashMap();
        req.put("eventID",eventID);
        req.put("userID",user.getUid());

        //send request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(req),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        if(null != response.toString())
                            listener.getResult(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (null != error.networkResponse)
                        {
                            Log.d(TAG + ": ", "Error Response code: " + error.networkResponse.statusCode);
                            listener.getResult(new JSONObject());
                        }
                    }
                });

        //add request to queue
        requestQueue.add(request);
    }

    //update user details
    public void updateUser(String key, String token,final SomeCustomListener<JSONObject> listener) {

        String url = context.getResources().getString(R.string.serverURLUserDetails);

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //set JSON elements
        HashMap req = new HashMap();
        req.put("userID",user.getUid());
        req.put("userName", user.getDisplayName());
        req.put("userToken", token);
        req.put("userKey", key);

        //send request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(req),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        if(null != response.toString())
                            listener.getResult(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (null != error.networkResponse)
                        {
                            Log.d(TAG + ": ", "Error Response code: " + error.networkResponse.statusCode);
                            listener.getResult(new JSONObject());
                        }
                    }
                });
        //add request to queue
        requestQueue.add(request);
    }

    //get recommendations
    public void getRecommendedEvent(double locLat,double locLong,float distance,final SomeCustomListener<JSONObject> listener) {

        String url = context.getResources().getString(R.string.serverURLGetRec);

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //set JSON elemenrs
        HashMap req = new HashMap();
        req.put("location",locLat + "," + locLong );
        req.put("distance", distance);
        req.put("userID", user.getUid());

        //send request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(req),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        if(null != response.toString())
                            listener.getResult(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (null != error.networkResponse)
                        {
                            Log.d(TAG + ": ", "Error Response code: " + error.networkResponse.statusCode);
                            listener.getResult(new JSONObject());
                        }
                    }
                });
        //add request to queue
        requestQueue.add(request);
    }

    //get list of nearby events
    public void getRelevantEvents(String url, final SomeCustomListener<String> listener)
    {

        //send request
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        if(null != response.toString())
                            listener.getResult(response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (null != error.networkResponse)
                        {
                            Log.d(TAG + ": ", "Error Response code: " + error.networkResponse.statusCode);
                            listener.getResult("error");
                        }
                    }
                });

        //add request to queue
        requestQueue.add(request);
    }


    //used to communicate between classes
    public interface SomeCustomListener<T>
    {
        public void getResult(T object);
    }
}
