package lees.tubetracker;

import android.animation.ObjectAnimator;
import android.app.DownloadManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.Plus;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import pl.polidea.view.ZoomView;


public class MainActivity extends ActionBarActivity  {

    GoogleMap myMap;
    String stationId = "central";

    OkHttpClient client = new OkHttpClient();

    String url = "https://api.tfl.gov.uk/Line/%7Bids%7D/Arrivals?ids="+stationId+"&app_id=89d1f922&app_key=b8e8baab76a04ea8a70a9f35c2994e79";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager myFragmentManager = getSupportFragmentManager();
        SupportMapFragment myMapFragment
                = (SupportMapFragment) myFragmentManager.findFragmentById(R.id.map);
        myMap = myMapFragment.getMap();

        Location locationA = new Location("point A");
        locationA.setLatitude(latA);
        locationA.setLongitude(lngA);
        Location locationB = new Location("point B");
        locationB.setLatitude(latB);
        locationB.setLongitude(lngB);
        distance = locationA.distanceTo(locationB) ;

       // myMap.setOnMapClickListener(this);



       // myMap.setOnMarkerClickListener(this);

        myMap.getUiSettings().setMapToolbarEnabled(false);
        myMap.getUiSettings().setZoomControlsEnabled(false);
        myMap.getUiSettings().setCompassEnabled(false);
        myMap.getUiSettings().setMyLocationButtonEnabled(false);

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("object");


            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                Log.d("Details-->", jo_inside.getString("Station"));
                String station = jo_inside.getString("Station");

                LatLng point = new LatLng(Double.parseDouble(jo_inside.getString("Latitude")), Double.parseDouble(jo_inside.getString("Longitude")));
                CameraPosition myPosition = new CameraPosition.Builder()
                        .target(new LatLng(51.504674357,-0.085991192281661)).zoom(15).bearing(90).build();
                myMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(myPosition));

                myMap.addMarker(new MarkerOptions()
                        .position(point)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.rail))
                        .title(station) );
            }

        }catch (JSONException e){

        }



    }

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("tubeLocation.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
