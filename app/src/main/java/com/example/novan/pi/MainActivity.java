package com.example.novan.pi;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicIntegerArray;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public GoogleMap mMap;
    String radius = "1000";
    String tipe = " ";
    String nama = " ";
    GoogleApiClient mGoogleApiClient;
    Toast t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        SupportMapFragment sMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        sMapFragment.getMapAsync(this);
        final TextView radius1 = (TextView)findViewById(R.id.radius);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seek_bar_radius);
        seekBar.setMax(10);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                radius = String.valueOf(progress*1000);
                Log.e("radius",radius);
                makeRequest(tipe,nama,radius);
                //Toast.makeText(getApplicationContext(),"Radius : "+radius, Toast.LENGTH_SHORT).show();
                radius1.setText(radius);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch stopped!", Toast.LENGTH_SHORT).show();
            }
        });
            // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        t.cancel();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.satelite) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        if (id == R.id.Terain) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        if (id == R.id.Hybird) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        if (id == R.id.Normal) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.mosque) {
            tipe = "mosque";
            nama = "";
            makeRequest(tipe,nama,radius);

        } else if (id == R.id.Gereja_Kristen) {
            tipe = "place_of_worship";
            nama = "kristen";
            makeRequest(tipe,nama,radius);
        } else if (id == R.id.Gereja_Katolik) {
            tipe = "church";
            nama = "katolik";
            makeRequest(tipe,nama,radius);
        } else if (id == R.id.hindu_temple) {
            tipe = "hindu_temple";
            nama = "";
            makeRequest(tipe,nama,radius);
        } else if (id == R.id.budha_temple) {
            tipe = "place_of_worship";
            nama = "vihara";
            makeRequest(tipe,nama,radius);
        }else if (id== R.id.about){
            AlertDialog.Builder about = new AlertDialog.Builder(this);
            about.setMessage
                    ("Nearby Place Of Worship is an application that used to find Place Of Worship near your location." +
                            "\n\nCreated by : \nDiki Anugerah Triya Novan")
                    .setCancelable(false).setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = about.create();
            dialog.setTitle("About");
            dialog.show();
        }else if(id==R.id.tutorial){
           tutorial();
        }else if(id==R.id.exit){
            AlertDialog.Builder exit = new AlertDialog.Builder(this);
            exit.setMessage
                    ("Do you want exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent exit = new Intent(Intent.ACTION_MAIN);
                                    exit.addCategory(Intent.CATEGORY_HOME);
                                    exit.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(exit);
                                    finish();
                                }
                            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = exit.create();
            dialog.setTitle("Exit");
            dialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        }

    private void enabledMylocationButton() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enabledMylocationButton();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied..", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private void makeRequest(String type, String name, String jarak){
        mMap.clear();
        try {
            String lat = String.valueOf(mMap.getMyLocation().getLatitude());
            String lng = String.valueOf(mMap.getMyLocation().getLongitude());
            Log.e("lokasi", lat + ", " + lng);
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                    "location=" + lat + "," + lng +
                    "&radius=" + jarak +
                    "&types=" + type +
                    "&name=" + name +
                    "&key=AIzaSyAsrcioBL4C16QsZHnqCa-_4qbikTgEHZo";

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(MainActivity.this, url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String respon = new String(responseBody);
                    Log.e("Respon", respon);
                    try {
                        JSONObject json = new JSONObject(respon);
                        JSONArray result = json.getJSONArray("results");
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject data = result.getJSONObject(i);
                            JSONObject geometry = data.getJSONObject("geometry");
                            JSONObject lokasi = geometry.getJSONObject("location");
                            String lat = lokasi.getString("lat");
                            String lng = lokasi.getString("lng");
                            String nama = data.getString("name");
                            String alamat = data.getString("vicinity");
                            LatLng kordinat = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                            mMap.addMarker(new MarkerOptions()
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker2))
                                    .snippet(alamat)
                                    .title(nama)
                                    .position(kordinat));
//                      t.makeText(getApplication(), "Ditemukan " + result.length() + " Tempat Beribadah", Toast.LENGTH_SHORT).show();
                        showToastMessage("Ditemukan " + result.length() + " Tempat Beribadah",1000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getApplication(), "koneksi gagal....!", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }
    public void tutorial(){
        Intent p = new Intent(this,Tutorial.class);
        startActivity(p);
    }
    public void showToastMessage(String text, int duration){
        final Toast toast = Toast.makeText(getApplication(),text, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, duration);
    }
}

