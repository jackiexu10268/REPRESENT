package com.jacquelinejxu.represent;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.google.android.*;



public class HomePage extends AppCompatActivity {
    String googleApi = "AIzaSyA0WvxZt6sxE2gXS_mjyOCgo0_412HijKo";
    ArrayList<JSONObject> peopleArray = new ArrayList<JSONObject>();
    public static int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    Boolean checkzip = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button zipButton = (Button) findViewById(R.id.button5);
        Button randomizeButton = (Button) findViewById(R.id.button);
        Button locationButton = (Button) findViewById(R.id.button2);

        zipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //what happens when you click zipButton
                EditText zipcode = findViewById(R.id.editText);
                String zip = zipcode.getText().toString();
                zipResults(zip);
                                         }
                                     }
        );

        randomizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SecureRandom random = new SecureRandom();
                int num = random.nextInt(100000);
                String zip = Integer.toString(num);
                zipResults(zip);
                                               }
                                           }
        );

        //manifest package com.google.android.gms.location.sample.basiclocationsample

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //what happens when you click locationButton
                getLocation();
                                              }
                                          }
        );
    }

    public void zipResults(final String zip) {
        String api_key = "972544c550b7b4cbb2e164223ec7abe949924be";
        String url = "https://api.geocod.io/v1.3/geocode?postal_code="
                + zip + "&fields=cd&api_key=" + api_key;

        RequestQueue queue = Volley.newRequestQueue(this);
        Log.d("zipResults", "function called and request queue made");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("zipResults", "in onResponse");
                try {
                    JSONArray results = response.getJSONArray("results");

                    JSONObject info = results.getJSONObject(0); //into json object now
                    Log.d("zipResults info =", info.getString("fields"));
                    JSONObject fields = info.getJSONObject("fields");
                    JSONArray congressDist = fields.getJSONArray("congressional_districts");
                    Log.d("congressDistrict0= ", congressDist.getString(0));
                    JSONObject congressInner = congressDist.getJSONObject(0);
                    JSONArray rep = congressInner.getJSONArray("current_legislators");
                    for (int i = 0; i < rep.length(); i++) {
                        Log.d("rep=", rep.getString(i));
                        JSONObject currPerson = rep.getJSONObject(i);
                        peopleArray.add(currPerson);
                        Log.d("for loop", "added person to peoplearray");
                    }
                    Intent intent = new Intent(HomePage.this, RepresentativeList.class);
                    for (int i = 0; i < peopleArray.size(); i++) {
                        intent.putExtra(Integer.toString(i), peopleArray.get(i).toString());
                    }
                    intent.putExtra("num_people", peopleArray.size());
                    intent.putExtra("zipcode", zip);
                    Log.d("zipResults", "passed items to next activity");
                    startActivity(intent);

                } catch (JSONException e) {
                    Log.d("zipResults", "JSONException occurred");
                    e.printStackTrace();
                    Context context = getApplicationContext();
                    CharSequence text = "Invaild Zip Code!";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("zipResults", "error");

            }
        });

        queue.add(request);
        Log.d("zipResults", "After adding to request queue");
        queue.start();
        Log.d("zipresults", "after starting");

    }



    public void latlngToState(double lat, double lng) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +
                lat + "," + lng + "&key=" + googleApi;
        RequestQueue latlngReq = Volley.newRequestQueue(this);
        Log.d("latlngTOState", url);
        JsonObjectRequest latlngRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("latlngTOState", "on response");
                            JSONArray locationArray = response.getJSONArray(("results"));
                            Log.d("LocationArray", response.getString("results"));
                            JSONObject addrComp = locationArray.getJSONObject(0);


                            for (int i = 0; i < locationArray.length(); i++) {
                                JSONArray addrArray = addrComp.getJSONArray("address_components");
                                JSONObject test = addrArray.getJSONObject(i);
                                JSONArray type = test.getJSONArray("types");
                                //String compare = type.toString();
                                Log.d("addrArray", addrArray.getString(i));
                                //Log.d("TEXTTTT", text.toString());
                                Log.d("AddrArray Test", test.getString("types"));
                                Log.d("Types get0", type.get(0).toString());

                                if (type.get(0).toString().equals("postal_code")) {
                                    Log.d("PostalSuccess", "postalsuccess");
                                    String zip = test.getString("long_name");
                                    Log.d("Zipcode", zip);
                                    zipResults(zip);
                                } else if (i == locationArray.length()) {
                                    Context context = getApplicationContext();
                                    CharSequence text = "Could not receive location.";
                                    int duration = Toast.LENGTH_LONG;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        latlngReq.add(latlngRequest);
        latlngReq.start();

    }



    protected void getLocation() {
        LocationRequest locationRequest = new LocationRequest();
        final LocationCallback locationCallback = new LocationCallback();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(10 * 1000);
//        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.d("get Location", "Permission not granted");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        } else {
            Log.d("get Location", "Permission granted");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
                    Looper.myLooper()).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("get Location", "onSuccess");
                    getLastLocation();
                }
            });
        }
    }

    public void getLastLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d("getLocation", "OnSuccess");
                // Got last known location. In some rare situations this can be null.
                // get the latitude and longitude, translate that to a district

                if (location == null ) {
                    Context context = getApplicationContext();
                    CharSequence text = "Could not receive location.";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    latlngToState(lat, lng);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        LocationRequest locationRequest = new LocationRequest();
        LocationCallback locationCallback = new LocationCallback();
        Log.d("onrequestperm", "called");
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.d("onrequest", "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Permission was granted. Kick off the process of building and connecting
                // GoogleApiClient.
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
                        null);
                mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d("getLocation", "OnSuccess");
                        // Got last known location. In some rare situations this can be null.
                        // get the latitude and longitude, translate that to a district

                        if (location == null ) {
                            double lat = 33.9938;
                            double lng = -117.75888;
                            Log.d("getLocation", "location is null");
                            latlngToState(lat, lng);
                        } else {
                            double lat = location.getLatitude();
                            double lng = location.getLongitude();
                            latlngToState(lat, lng);
                        }


                    }
                });
            } else {
                // Permission denied.
                Log.d("Sad", "Permission denied");

            }
        }


    }
}
