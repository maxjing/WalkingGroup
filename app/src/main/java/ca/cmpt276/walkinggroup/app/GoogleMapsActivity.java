package ca.cmpt276.walkinggroup.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.GroupInfo;
import ca.cmpt276.walkinggroup.dataobjects.PlaceInfo;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "GoogleMapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int PLACE_PICKER_REQUEST = 1;
    public static final String WALKING_GROUP = "Walking Group";
    private PlaceInfo mPlaceDetailsText;
    private PlaceInfo mSearchMarkerDetail;
    private List<PlaceInfo> mPlaceDetailsTextList = new ArrayList<>();
    private List<Marker> mSearchMarker = new ArrayList<>();
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136)
    );

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GeoDataClient mGeoDataClient;
    private Marker mMarker;
    private long markerID = 0;
    private long userId = 0;
    private String groupDescription;
    private String meetingPlace;


    // private List<LatLng> latLngList = new ArrayList<>();
    private List<Marker> mMarkerList = new ArrayList<>();
    private List<GroupInfo> mGroupInfoList = new ArrayList<>();

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps, mInfo, mCreateGroup, mJoinGroup, mPlacePicker;

    //transfer value

    public static final String LATITUDE = "latitude";
    public static final String LONGTITUDE = "longtitude";
    public static final String PLACENAME = "placename";
    public static final String JOINGROUP = "joinGroupID";
    public static final String USER_JOIN = "userId";
    public static final String MEETINGPLACE = "meetingPlace";
    public static final String GROUP_DES = "groupDescription";

    //latlnt data
    private String token;
    private WGServerProxy proxy;
    private List<Group> groupList;
    private Double[] latitudes;
    private Double[] longtitudes;
    private String[] groupDes;
    private Long[] groupId;
    private String[] meetPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mSearchText = findViewById(R.id.input_search);
        mGps = findViewById(R.id.ic_gps);
        mInfo = findViewById(R.id.place_info);
        mCreateGroup = findViewById(R.id.create_group);
        mJoinGroup = findViewById(R.id.search_group);
        mPlacePicker = findViewById(R.id.place_picker);
        // Retrieve the TextViews that will display details and attributions of the selected place.

        //for Test
       /* latLngList.add(new LatLng(49.30,-122.80));
        latLngList.add(new LatLng(49.56, -122.78));
        latLngList.add(new LatLng(49.2960264,-122.745591));*/


        //get latlnt data
        SharedPreferences dataToGet = getApplicationContext().getSharedPreferences("userPref", 0);
        token = dataToGet.getString("userToken", "");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        userId = dataToGet.getLong("userId",0);

        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(GoogleMapsActivity.this, caller, returnedGroup -> response(returnedGroup));


        getLocationPermission();
        setUpClearButton();
    }


    private void response(List<Group> groups) {
        groupList = groups;
        latitudes = new Double[groupList.size()];
        longtitudes = new Double[groupList.size()];
        groupDes = new String[groupList.size()];
        groupId = new Long[groupList.size()];
        meetPlace = new String[groupList.size()];

        for (int i = 0; i < groupList.size(); i++) {
            latitudes[i] = groupList.get(i).getRouteLatArray().get(0);
            longtitudes[i] = groupList.get(i).getRouteLngArray().get(0);
            groupDes[i] = groupList.get(i).getGroupDescription();
            groupId[i] = groupList.get(i).getId();
//            meetPlace[i] = groupList.get(i).getMessages().get(0);



        }
        for (int i = 0; i < latitudes.length; i++) {
//            Toast.makeText(this, ""+meetPlace[i], Toast.LENGTH_SHORT).show();
            mGroupInfoList.add(new GroupInfo(new LatLng(latitudes[i], longtitudes[i]), groupDes[i], groupId[i]));
        }
        walkingGroup();
    }

    private void setUpClearButton() {
        ImageView btn = findViewById(R.id.clear_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchText.setText("");
            }
        });
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, LAT_LNG_BOUNDS, null);

        mSearchText.setOnItemClickListener(mAutocompleteClickerListener);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == keyEvent.ACTION_DOWN
                        || keyEvent.getAction() == keyEvent.KEYCODE_ENTER) {

                    //execute our method for searching
                    geoLocate();
                }
                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked place info");
                try {
                    if (mMarker.isInfoWindowShown()) {
                        mMarker.hideInfoWindow();
                    } else {
                        Log.d(TAG, "onClick: place info: " + mPlaceDetailsText.toString());
                        mMarker.showInfoWindow();
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, "onClick: NullPointerException: " + e.getMessage());
                }
            }
        });

        mCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSearchMarkerDetail != null) {
                    Bundle args = new Bundle();
                    final double Latitude = mSearchMarkerDetail.getLatLng().latitude;
                    final double Longtitude = mSearchMarkerDetail.getLatLng().longitude;
                    final String PlaceName = mSearchMarkerDetail.getName();
                    args.putString(PLACENAME, PlaceName);
                    args.putDouble(LONGTITUDE, Longtitude);
                    args.putDouble(LATITUDE, Latitude);


                    FragmentManager manager = getSupportFragmentManager();
                    MessageFragment dialog = new MessageFragment();
                    dialog.setArguments(args);
                    dialog.show(manager, "MessageDialog");

                    Log.i(TAG, "show the dialog");

                } else {
                    Toast.makeText(GoogleMapsActivity.this, "To create a group, please select a place specific first", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(GoogleMapsActivity.this, "should show the group lists around the selected location", Toast.LENGTH_SHORT).show();
                if (markerID != 0) {
                    Bundle args = new Bundle();
                    final long selectedID = markerID;
                    args.putLong(JOINGROUP, selectedID);
                    args.putLong(USER_JOIN,userId);
                    args.putString(GROUP_DES,groupDescription);

                    FragmentManager manager = getSupportFragmentManager();
                    JoinGroupFragment dialog = new JoinGroupFragment();
                    dialog.setArguments(args);
                    dialog.show(manager, "MessageDialog");
                }
            }
        });

        mPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(GoogleMapsActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e(TAG, "GooglePlayServicesRepairableException: " + e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG, "GooglePlayServicesNotAvailableException: " + e.getMessage());
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                markerID = 0;
                mSearchMarkerDetail = null;
                if (!marker.getTitle().equals(WALKING_GROUP)){
                    if (mPlaceDetailsTextList != null){
                        for (int i = 0; i < mPlaceDetailsTextList.size(); i++){
                            try{
                                if (marker.equals(mSearchMarker.get(i))){
                                    mSearchMarkerDetail = mPlaceDetailsTextList.get(i);
                                    break;
                                }
                            }
                            catch (Exception e){

                            }
                        }
                    }
                    if (mSearchMarkerDetail != null) {
                        Bundle args = new Bundle();
                        final double Latitude = mSearchMarkerDetail.getLatLng().latitude;
                        final double Longtitude = mSearchMarkerDetail.getLatLng().longitude;
                        final String PlaceName = mSearchMarkerDetail.getName();
                        args.putString(PLACENAME, PlaceName);
                        args.putDouble(LONGTITUDE, Longtitude);
                        args.putDouble(LATITUDE, Latitude);


                        FragmentManager manager = getSupportFragmentManager();
                        MessageFragment dialog = new MessageFragment();
                        dialog.setArguments(args);
                        dialog.show(manager, "MessageDialog");

                        Log.i(TAG, "show the dialog");

                    } else {
                        Toast.makeText(GoogleMapsActivity.this, "To create a group, please select a place specific first", Toast.LENGTH_SHORT).show();
                    }
                }
               else if (mMarkerList != null) {
                    for (int i = 0; i < mMarkerList.size(); i++) {
                        try {
                            if (marker.equals(mMarkerList.get(i))) {
                                //handle click here
                                markerID = mGroupInfoList.get(i).getID();
                                Toast.makeText(GoogleMapsActivity.this, "ID: " + markerID, Toast.LENGTH_SHORT).show();
                                groupDescription = mGroupInfoList.get(i).getDes();

                                break;
                            }
                            markerID = 0;
                        }
                        catch(Exception e){
                        }
                    }
                    if (markerID != 0) {
                        Bundle args = new Bundle();
                        final long selectedID = markerID;
                        args.putLong(JOINGROUP, selectedID);
                        args.putLong(USER_JOIN,userId);
                        args.putString(GROUP_DES,groupDescription);

                        FragmentManager manager = getSupportFragmentManager();
                        JoinGroupFragment dialog = new JoinGroupFragment();
                        dialog.setArguments(args);
                        dialog.show(manager, "MessageDialog");
                    }
                }
                return false;
            }
        });

        hideSoftKeyboard();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(place.getId());
                placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback);
            }
        }
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(GoogleMapsActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException" + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the current devices location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "on Complete: Found Location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");  // move to the device location and zoom
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(GoogleMapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException:" + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + " , lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
       // mMap.clear();

       // walkingGroup();
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(GoogleMapsActivity.this));

        if (placeInfo != null) {
            try {
                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
                        "Phone Number: : " + placeInfo.getPhoneNumber() + "\n" +
                        "Website: : " + placeInfo.getWebsiteUri() + "\n" +
                        "Price Rating: : " + placeInfo.getRating() + "\n";

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
                        .snippet(snippet);
                mMarker = mMap.addMarker(options);
                mSearchMarker.add(mMarker);
            } catch (NullPointerException e) {
                Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage());
            }
        } else {
            mSearchMarker.add(mMap.addMarker(new MarkerOptions().position(latLng)));
        }
        hideSoftKeyboard();
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
       // walkingGroup();
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + " , lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (title != "My Location") {
            mMap.clear();
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMarker = mMap.addMarker(options);
            mSearchMarker.add(mMarker);

            mSearchMarkerDetail.setLatLng(latLng);
            mSearchMarkerDetail.setName(title);
            mPlaceDetailsTextList.add(mSearchMarkerDetail);
        }
        hideSoftKeyboard();
    }

    private void walkingGroup() {
        for (int i = 0; i < mGroupInfoList.size(); i++) {
            String snippet = WALKING_GROUP;
            if (mGroupInfoList.get(i).getDes() != null) {
                snippet = mGroupInfoList.get(i).getDes();
            }
            MarkerOptions options = new MarkerOptions()
                    .position(mGroupInfoList.get(i).getLatLng())
                    .title(WALKING_GROUP)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mMarkerList.add(mMap.addMarker(options));
        }
    }

    private void intiMap() {
        Log.d(TAG, "intiMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(GoogleMapsActivity.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                intiMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    intiMap();
                }
            }
        }
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
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "OnMapReady: Map is Ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            //mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

            init();

        }


       /* // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*
    ---------------------------------------------------------------google places api auto complete suggestions-----------------------------------------------------------------
     */
    private AdapterView.OnItemClickListener mAutocompleteClickerListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeID = item.getPlaceId();
            Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeID);
            placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback);
        }
    };

    // link: https://github.com/googlesamples/android-play-places/blob/master/PlaceCompleteAdapter/Application/src/main/java/com/example/google/playservices/placecomplete/MainActivity.java
    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback
            = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(Task<PlaceBufferResponse> task) {
            try {
                PlaceBufferResponse places = task.getResult();
                // Get the Place object from the buffer.
                final Place place = places.get(0);
                Log.i(TAG, "Place details received: " + place.getName());

                mPlaceDetailsText = new PlaceInfo();
                mPlaceDetailsText.setAddress(place.getAddress().toString());
                mPlaceDetailsText.setId(place.getId());
                mPlaceDetailsText.setName(place.getName().toString());
                mPlaceDetailsText.setLatLng(place.getLatLng());
                mPlaceDetailsText.setPhoneNumber(place.getPhoneNumber().toString());
                mPlaceDetailsText.setWebsiteUri(place.getWebsiteUri());
                mPlaceDetailsText.setRating(place.getRating());
               /* if (place.getAttributions()!= null){
                    mPlaceDetailsText.setAttributions(place.getAttributions().toString());
                }
                */
                mPlaceDetailsTextList.add(mPlaceDetailsText);
                moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlaceDetailsText);

                places.release();
            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete.", e);
                return;
            }
        }
    };

    public static Intent makeIntent(Context context) {
        return new Intent(context, GoogleMapsActivity.class);
    }

}
