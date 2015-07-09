package finder.khmer.sdbs.caminfo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import finder.khmer.sdbs.caminfo.adapter.Helper;
import finder.khmer.sdbs.caminfo.adapter.JSONParser;
import finder.khmer.sdbs.caminfo.image_loader.ImageLoader;

public class MapsActivity extends FragmentActivity implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private LocationManager locationManager;

    Helper helper = new Helper();

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    private JSONParser jsonParser = new JSONParser();

    private int[] id;
    private double[] lat;
    private double[] lng;
    private String locationName[];
    private double myLat,myLng;
    private String imageUrl[];

    String serverURL = "http://krg.sdbstechnology.com/responses/";

    private String provider;

    private Marker myMarker;

    HashMap <Marker, MarkerObject> mMarkers = new HashMap<Marker, MarkerObject>();

    class MarkerObject{
        int markerId;
        String markerName;
        String markerImage;

        public MarkerObject(int m, String n, String i){
            this.markerId = m;
            this.markerName = n;
            this.markerImage = i;
        }

        public int getMarkerId(){return this.markerId;}
        public String getMarkerName(){return this.markerName;}
        public String getMarkerImage(){return this.markerImage;}

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        /********** get Gps location service LocationManager object ***********/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000,   // 3 sec
                10, this);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {

            myLat = location.getLatitude();
            myLng = location.getLongitude();

        } else {
           helper.showToast(MapsActivity.this, "Could not get your current location");

        }


        new GettingList().execute("");
    }

    @Override
    protected void onResume() {
        super.onResume();

        try{
            if ( myMarker != null ){
                myMarker.setPosition(new LatLng(myLat, myLng));
            }
        }catch(Exception e){
            Log.d("Error", e.getMessage());
        }
    }


    private void setUpMapIfNeeded(int[] i, double[] x , double[] y, String[] name) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap(i, x, y, name);
            }

        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(int ids[], double[] x, double[] y, String[] name) {
        try{

            for(int i =0;i< x.length;i++){
               Marker mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(x[i], y[i])).title(name[i]).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
               MarkerObject mObject = new MarkerObject(ids[i],name[i], imageUrl[i]);
                mMarkers.put(mMarker ,mObject );
            }

            String current_loc = _getLocation();
            String spl_loc[] = current_loc.split(",");
            LatLng TutorialsPoint;

            if (mMap == null) {
                mMap = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map)).getMap();
            }
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            if ( Double.parseDouble(spl_loc[0]) == 0.0){

                TutorialsPoint = new LatLng(11.5625, 104.916);
            }else {
                TutorialsPoint = new LatLng(Double.parseDouble(spl_loc[0]), Double.parseDouble(spl_loc[1]));
                myMarker = mMap.addMarker(new MarkerOptions().
                        position(TutorialsPoint).title("Your Location"));
            }




            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(TutorialsPoint, 17);
            mMap.animateCamera(yourLocation);

            // Setting a custom info window adapter for the google map
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                // Defines the contents of the InfoWindow
                @Override
                public View getInfoContents(Marker arg0) {

                    // Getting view from the layout file info_window_layout
                    View v = getLayoutInflater().inflate(R.layout.activity_map_info, null);

                  try{
                      if (mMarkers.containsKey(arg0)){

                          MarkerObject mObject = (MarkerObject)mMarkers.get(arg0);

                          // Getting the position from the marker
                          LatLng latLng = arg0.getPosition();

                          // Getting reference to the TextView to set latitude
                          TextView txtCompanyName = (TextView) v.findViewById(R.id.tv_name);

                          // Getting reference to the TextView to set longitude
                          ImageView imageView = (ImageView) v.findViewById(R.id.imageView);

                          // Setting the latitude
                          txtCompanyName.setText(mObject.getMarkerName());

                          //ImageLoader imageLoader = new ImageLoader(MapsActivity.this);
                          //imageLoader.DisplayImage("http://krg.sdbstechnology.com/images/200x200/552_1432800623_4639.jpeg", imageView);

                          Picasso.with(MapsActivity.this).load(mObject.getMarkerImage()).into(imageView, new InfoWindowRefresher(arg0));
                      }else{
                          //helper.showToast(MapsActivity.this, "key not found", true);
                      }


                  }catch (Exception e){
                      //helper.showToast(MapsActivity.this, e.getMessage());
                  }

                    // Returning the view containing InfoWindow contents
                    return v;

                }
            });

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                MarkerObject mObject = (MarkerObject)mMarkers.get(marker);
                //helper.dialog(MapsActivity.this, "message" , mObject.getMarkerName());
                Intent intent = new Intent(MapsActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_id", mObject.getMarkerId());
                startActivity(intent);
                }
            });


        }catch(Exception e){
           Toast.makeText(MapsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private class InfoWindowRefresher implements Callback {
        private Marker markerToRefresh;

        private InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            markerToRefresh.showInfoWindow();
        }

        @Override
        public void onError() {}
    }

    @Override
    public void onLocationChanged(Location location) {
        String str = "Latitude: "+location.getLatitude()+" \nLongitude: "+location.getLongitude();
        myLat = location.getLatitude();
        myLng = location.getLongitude();

        try{
            if ( myMarker != null ){
                myMarker.setPosition(new LatLng(myLat, myLng));
            }
        }catch(Exception e){
            Log.d("Error", e.getMessage());
        }

        /*LatLng TutorialsPoint = new LatLng(myLat, myLng);

        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().
                    findFragmentById(R.id.map)).getMap();
        }
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //Marker TP = mMap.addMarker(new MarkerOptions().
        //        position(TutorialsPoint).title("Your Location"));

        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(TutorialsPoint, 17);
        mMap.animateCamera(yourLocation);

        myMarker.setPosition(new LatLng(myLat, myLng));



        try{
            if ( myMarker == null ){
                myMarker = mMap.addMarker(new MarkerOptions().
                        position(new LatLng(myLat, myLng)).title("Your Location"));
            }else{
                myMarker.setPosition(new LatLng(myLat, myLng));
            }
        }catch(Exception e){
            Log.d("Error", e.getMessage());
        }

         */


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
       Toast.makeText(getBaseContext(), "Location Service has been enabled", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(getBaseContext(), "Please enable location service", Toast.LENGTH_LONG).show();
        helper.showSettingsAlert(MapsActivity.this);
    }

    private String _getLocation() {
        // Get the location manager
        LocationManager locationManager = (LocationManager)
                getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        Double lat,lon;
        try {
            lat = location.getLatitude();
            lon = location.getLongitude();
        } catch (NullPointerException e) {
            lat = 0.0;
            lon = 0.0;
            Toast.makeText(MapsActivity.this,
                    "Could not determine your location" ,
                    Toast.LENGTH_LONG)
                    .show();
        }

        return lat + " , " + lon;
    }

    class GettingList extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(MapsActivity.this);
            pDialog.setMessage("Please wait ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Saving product
         */
        protected String doInBackground(String... args) {


            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("user_id", userId + ""));

            // sending modified data through http request
            // Notice that update product url accepts POST method


            // check json success tag
            try {
                JSONObject json = jsonParser.makeHttpRequest(serverURL + "company_map_list.php",
                        "POST", params);
                int success = json.getInt("success");

                String image[] = null;
                if (success == 1) {

                    JSONArray arr = json.getJSONArray("coords");

                    lat = new double[arr.length()];
                    lng = new double[arr.length()];
                    locationName = new String[arr.length()];
                    id = new int[arr.length()];
                    imageUrl = new String[arr.length()];

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject r = arr.getJSONObject(i);
                        lat[i] = r.getDouble("x");
                        lng[i] = r.getDouble("y");
                        locationName[i] = r.getString("name");
                        id[i] = r.getInt("id");
                        imageUrl[i] = r.getString("image");
                    }


                } else {
                    // failed to update product
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (helper.isNetworkAvailable(MapsActivity.this)) {
                        setUpMapIfNeeded(id, lat, lng, locationName);

                    } else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);

                        // Setting Dialog Title
                        alertDialog.setTitle("Message");

                        // Setting Dialog Message
                        alertDialog.setMessage("No internet connection");

                        // on pressing cancel button
                        alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        // Showing Alert Message
                        alertDialog.show();




                    }

                }
            });
            // dismiss the dialog once product uupdated
            pDialog.dismiss();

        }
    }

}
