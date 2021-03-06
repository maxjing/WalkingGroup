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
import android.os.Handler;
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
import java.util.Date;
import java.util.List;

import ca.cmpt276.walkinggroup.app.Adapter.CustomInfoWindowAdapter;
import ca.cmpt276.walkinggroup.app.Adapter.PlaceAutocompleteAdapter;
import ca.cmpt276.walkinggroup.app.DialogFragment.JoinGroupFragment;
import ca.cmpt276.walkinggroup.app.DialogFragment.MessageFragment;
import ca.cmpt276.walkinggroup.app.DialogFragment.MyToast;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.GroupInfo;
import ca.cmpt276.walkinggroup.dataobjects.PlaceInfo;
import ca.cmpt276.walkinggroup.dataobjects.Session;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * searching the place with auto fill search bar
 * map button for pick place
 * move camera, zoom, get device location
 * creating walking group by select target place and meet place, join group
 * to create a group, first search and click the target place, then search and click a meeting place, the dialog would come out
 * to join a group, first click the blue marker, it would show the meeting place using yellow marker, click the yellow marker to join a group
 * show the child position and update the position every 30 seconds
 */


public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "GoogleMapActivity";
    private static final Handler handler = new Handler();
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int PLACE_PICKER_REQUEST = 1;
    public static final String WALKING_GROUP = "Walking Group";
    public static final String MEETING_PLACE = "Meeting Place";
    public static final String CHILD = "CHILD";
    public static final String SELECTED_PLACE = "selected place";
    private PlaceInfo mPlaceDetailsText;
    private PlaceInfo mSearchMarkerDetail = null;
    private PlaceInfo mMeetPlaceDetail = null;
    private List<PlaceInfo> mPlaceDetailsTextList = new ArrayList<>();
    private List<Marker> mSearchMarker = new ArrayList<>();
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362)
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
    private LatLng position = null;


    private List<LatLng> latLngList = new ArrayList<>();
    private List<Marker> mMarkerList = new ArrayList<>();
    private List<GroupInfo> mGroupInfoList = new ArrayList<>();
    private List<GroupInfo> mMeetingGroupInfoList = new ArrayList<>();
    private List<Marker> mMeetGroupMarkerList = new ArrayList<>();
    private List<Marker> mChildMarkerList = new ArrayList<>();
    private List<Long> mChildID = new ArrayList<>();
    private List<User> monitorsUsers = new ArrayList<>();

    //widgets
    private AutoCompleteTextView mSearchText, mSearchMeetingPlace;
    private ImageView mGps, mInfo, mClearSelectedPlace, mShowWalkingGroups, mPlacePicker;

    //transfer value

    public static final String LATITUDE = "latitude";
    public static final String LONGTITUDE = "longtitude";
    public static final String PLACENAME = "placename";
    public static final String JOINGROUP = "joinGroupID";
    public static final String USER_JOIN = "userId";
    public static final String MEETINGPLACE = "meetingPlace";
    public static final String GROUP_DES = "groupDescription";
    public static final String MEETLAT = "meetLat";
    public static final String MEETLNG = "meetLng";

    //latlnt data
    private String token;
    private WGServerProxy proxy;
    private List<Group> groupList;
    private Double[] latitudes;
    private Double[] longtitudes;
    private Double[] meetlat;
    private Double[] meetlng;
    private String[] groupDes;
    private Long[] groupId;
    private String[] meetPlace;

    private List<User> childList;
    private Double[] ChildLatitudes;
    private Double[] ChildLongtitudes;
    private String[] ChildDes;
    private Long[] ChildID;
    private Date[] ChildTimeStamp;
    private Session session;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mSearchText = findViewById(R.id.input_search);     // setting up
        mGps = findViewById(R.id.ic_gps);
        mInfo = findViewById(R.id.place_info);
        mClearSelectedPlace = findViewById(R.id.create_group);
        mShowWalkingGroups = findViewById(R.id.search_group);
        mPlacePicker = findViewById(R.id.place_picker);
        // Retrieve the TextViews that will display details and attributions of the selected place.

        //for Test
//        latLngList.add(new LatLng(49.30,-122.80));
//        latLngList.add(new LatLng(49.56, -122.78));
//        latLngList.add(new LatLng(49.2960264,-122.745591));


        //get latlnt data
        session = Session.getInstance();
        proxy = session.getProxy();
        user = session.getUser();
        userId = user.getId();


        getLocationPermission();
        setUpClearButton();

        handler.postDelayed(updateChild, 5000);
    }

    private void setOnMapClick() {
        // Setting a click event handler for the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng)
                        .snippet(SELECTED_PLACE)
                        .title(latLng.latitude + " : " + latLng.longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                Marker tempMarker = mMap.addMarker(markerOptions);
                PlaceInfo tempPlace = new PlaceInfo(tempMarker.getTitle(), tempMarker.getTitle(), null, null, null, latLng, 0, null);

                mSearchMarker.add(tempMarker);
                mPlaceDetailsTextList.add(tempPlace);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        handler.removeCallbacks(updateChild);
        return super.onKeyDown(keyCode, event);
    }

    private void response(List<Group> groups) {   // get group info from lists
        groupList = groups;
        latitudes = new Double[groupList.size()];
        longtitudes = new Double[groupList.size()];
        groupDes = new String[groupList.size()];
        groupId = new Long[groupList.size()];
        meetPlace = new String[groupList.size()];
        meetlat = new Double[groupList.size()];
        meetlng = new Double[groupList.size()];

        for (int i = 0; i < groupList.size(); i++) {
            latitudes[i] = groupList.get(i).getRouteLatArray().get(0);
            longtitudes[i] = groupList.get(i).getRouteLngArray().get(0);

            meetlat[i] = groupList.get(i).getRouteLatArray().get(1);
            meetlng[i] = groupList.get(i).getRouteLngArray().get(1);

            groupDes[i] = groupList.get(i).getGroupDescription();
            groupId[i] = groupList.get(i).getId();

        }
        for (int i = 0; i < latitudes.length; i++) {
//            Toast.makeText(this, ""+meetPlace[i], Toast.LENGTH_SHORT).show();

            // get the list for target place
            mGroupInfoList.add(new GroupInfo(new LatLng(latitudes[i], longtitudes[i]), groupDes[i], groupId[i]));
            // get the list for meeting place
            mMeetingGroupInfoList.add(new GroupInfo(new LatLng(meetlat[i], meetlng[i]), groupDes[i], groupId[i]));
        }
        walkingGroup(); // call walkingGroup function to show markers for group

    }

    private void setUpClearButton() {  // clear the text in searching bar
        ImageView btn = findViewById(R.id.clear_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchText.setText("");
            }
        });
    }

    private void init() {
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(GoogleMapsActivity.this, caller, returnedGroup -> response(returnedGroup));

        Call<List<User>> callerChild = proxy.getMonitorsUsers(userId);
        ProxyBuilder.callProxy(GoogleMapsActivity.this, callerChild, returnedList -> responseForChild(returnedList));

        Log.d(TAG, "init: initializing");


        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, LAT_LNG_BOUNDS, null);

        mSearchText.setOnItemClickListener(mAutocompleteClickerListener);       // setting auto fill for search bar

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

        mGps.setOnClickListener(new View.OnClickListener() { // move camera and zoom to device location
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        mInfo.setOnClickListener(new View.OnClickListener() { // hide or show Info Window
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked place info");
                try {
                    if (mMarker.isInfoWindowShown()) { // if window is open, hide
                        mMarker.hideInfoWindow();
                    } else { // if is close, show
                        Log.d(TAG, "onClick: place info: " + mPlaceDetailsText.toString());
                        mMarker.showInfoWindow();
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, "onClick: NullPointerException: " + e.getMessage());
                }
            }
        });

        mClearSelectedPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // clear the selected place.
                mSearchMarkerDetail = null;     // clear the selected target place
                mMeetPlaceDetail = null;        // clear the selected meeting place

            }
        });

        mShowWalkingGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            // show or hide walking groups, the blue marker
            public void onClick(View view) {
                boolean flag = false;
                if (mMarkerList != null) {
                    for (int i = 0; i < mMarkerList.size(); i++) {
                        if (!mMarkerList.get(i).isVisible()) {
                            mMarkerList.get(i).setVisible(true);
                            flag = true;
                        } else {
                            mMarkerList.get(i).setVisible(false);
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    MyToast.makeText(GoogleMapsActivity.this, R.string.show_walking_groups, Toast.LENGTH_SHORT).show();
                } else {
                    MyToast.makeText(GoogleMapsActivity.this, R.string.hide_walking_groups, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override                               // pick the near place in the map
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(GoogleMapsActivity.this), PLACE_PICKER_REQUEST);  // go to place picker api
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e(TAG, "GooglePlayServicesRepairableException: " + e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG, "GooglePlayServicesNotAvailableException: " + e.getMessage());
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override                                           // setting the click marker activity
            public boolean onMarkerClick(Marker marker) {
                for (int i = 0; i < mMeetGroupMarkerList.size(); i++) { // hide all meeting place marker
                    mMeetGroupMarkerList.get(i).setVisible(false);
                }
                if (!marker.getTitle().equals(WALKING_GROUP) && !marker.getTitle().equals(MEETING_PLACE) && !marker.getTitle().equals(CHILD)) {
                    markerID = 0;
                    if (mPlaceDetailsTextList != null) {
                        for (int i = 0; i < mPlaceDetailsTextList.size(); i++) {
                            try {
                                if (marker.equals(mSearchMarker.get(i))) {
                                    if (mSearchMarkerDetail == null) {
                                        mSearchMarkerDetail = mPlaceDetailsTextList.get(i);
                                        break;
                                    } else {
                                        mMeetPlaceDetail = mPlaceDetailsTextList.get(i);
                                    }
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                    if (mSearchMarkerDetail != null) {
                        if (mMeetPlaceDetail == null) {
                            MyToast.makeText(GoogleMapsActivity.this, R.string.select_meet_place, Toast.LENGTH_SHORT).show();
                        } else if (mSearchMarkerDetail == mMeetPlaceDetail) {
                            MyToast.makeText(GoogleMapsActivity.this, R.string.different_place, Toast.LENGTH_SHORT).show();
                        } else {
                            MyToast.makeText(GoogleMapsActivity.this, R.string.clear_selected_place, Toast.LENGTH_SHORT).show();
                            Bundle args = new Bundle();
                            final double Latitude = mSearchMarkerDetail.getLatLng().latitude;
                            final double Longtitude = mSearchMarkerDetail.getLatLng().longitude;
                            final String PlaceName = mSearchMarkerDetail.getName();
                            final double meetLat = mMeetPlaceDetail.getLatLng().latitude;
                            final double meetLng = mMeetPlaceDetail.getLatLng().longitude;
                            final String meetName = mMeetPlaceDetail.getName();
                            args.putString(PLACENAME, PlaceName);
                            args.putDouble(LONGTITUDE, Longtitude);
                            args.putDouble(LATITUDE, Latitude);
                            args.putString(MEETINGPLACE, meetName);
                            args.putDouble(MEETLAT, meetLat);
                            args.putDouble(MEETLNG, meetLng);

                            mSearchMarkerDetail = null;
                            mMeetPlaceDetail = null;

                            FragmentManager manager = getSupportFragmentManager();
                            MessageFragment dialog = new MessageFragment();
                            dialog.setArguments(args);
                            dialog.show(manager, getString(R.string.message_dialog));

                            Log.i(TAG, "show the dialog");
                        }

                    }else {
                        MyToast.makeText(GoogleMapsActivity.this, R.string.create_group_select, Toast.LENGTH_SHORT).show();
                    }
                } else if (mMarkerList != null) {
                    if (marker.getTitle().equals(MEETING_PLACE) && position != null) {
                        marker.setVisible(true);
                        Bundle args = new Bundle();
                        final long selectedID = markerID;
                        args.putLong(JOINGROUP, selectedID);
                        args.putLong(USER_JOIN, userId);
                        args.putString(GROUP_DES, groupDescription);

                        FragmentManager manager = getSupportFragmentManager();
                        JoinGroupFragment dialog = new JoinGroupFragment();
                        dialog.setArguments(args);
                        dialog.show(manager, getString(R.string.message_dialog));
                        ;
                    } else {
                        for (int i = 0; i < mMarkerList.size(); i++) {
                            try {
                                if (marker.equals(mMarkerList.get(i))) {
                                    //handle click here
                                    markerID = mGroupInfoList.get(i).getID();
                                    groupDescription = mGroupInfoList.get(i).getDes();
                                    mMeetGroupMarkerList.get(i).setVisible(true);
                                    position = mMeetingGroupInfoList.get(i).getLatLng();
                                    break;
                                }
                                markerID = 0;
                            } catch (Exception e) {
                            }
                        }
                    }
                    if (markerID != 0) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10f));
                        MyToast.makeText(GoogleMapsActivity.this, R.string.yellow_marker, Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });


        hideSoftKeyboard();
    }

    private void responseForChild(List<User> returnedList) {
        try {
            monitorsUsers = returnedList;
            ChildLatitudes = new Double[monitorsUsers.size()];
            ChildLongtitudes = new Double[monitorsUsers.size()];
            ChildDes = new String[monitorsUsers.size()];
            ChildID = new Long[monitorsUsers.size()];
            ChildTimeStamp = new Date[monitorsUsers.size()];

            for (int i = 0; i < monitorsUsers.size(); i++) {
                ChildLatitudes[i] = monitorsUsers.get(i).getLastGpsLocation().getLat();
                ChildLongtitudes[i] = monitorsUsers.get(i).getLastGpsLocation().getLng();
                ChildTimeStamp[i] = monitorsUsers.get(i).getLastGpsLocation().getTimestamp();
                ChildDes[i] = "" + monitorsUsers.get(i).getName();
                ChildID[i] = monitorsUsers.get(i).getId();
            }

            ChildMonitoring(ChildLatitudes, ChildLongtitudes, ChildDes, ChildTimeStamp, ChildDes, ChildID);
        } catch (Exception e) {
        }
    }

    private void ChildMonitoring(Double[] childLatitudes, Double[] childLongtitudes, String[] childDes, Date[] childTimeStamp, String[] childDes1, Long[] childID) {
        for (int i = 0; i < childID.length; i++) {
            String snippet = "" + ChildDes + " , " + childTimeStamp;
            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(childLatitudes[i], childLongtitudes[i]))
                    .title(CHILD)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            Marker tempMarker = mMap.addMarker(options);
            tempMarker.setVisible(true);
            tempMarker.setSnippet(snippet);
            mChildMarkerList.add(tempMarker);
            mChildID.add(childID[i]);
        }
    }

    Runnable updateChild = new Runnable() {
        @Override
        public void run() {
            Call<List<User>> callerChild = proxy.getMonitorsUsers(userId);
            ProxyBuilder.callProxy(GoogleMapsActivity.this, callerChild, returnedList -> UpdateChildMarker(returnedList));
            handler.postDelayed(this, 30000);
        }
    };


    private void UpdateChildMarker(List<User> returnedList) {
        //Toast.makeText(GoogleMapsActivity.this, "Child Update Called", Toast.LENGTH_SHORT).show();
        try {
            for (int i = 0; i < mChildMarkerList.size(); i++) {
                mChildMarkerList.get(i).remove();
                mChildMarkerList.remove(i);
                mChildID.remove(i);
            }

            for (int i = 0; i < returnedList.size(); i++) {
                String snippet = "" + returnedList.get(i).getName() + " , " + returnedList.get(i).getLastGpsLocation().getTimestamp();
                MarkerOptions options = new MarkerOptions()
                        .position(new LatLng(returnedList.get(i).getLastGpsLocation().getLat(),
                                returnedList.get(i).getLastGpsLocation().getLng()))
                        .title(CHILD)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                Marker tempMarker = mMap.addMarker(options);
                tempMarker.setVisible(true);
                tempMarker.setSnippet(snippet);
                mChildMarkerList.add(tempMarker);
                mChildID.add(returnedList.get(i).getId());
            }
        }catch (Exception e){
        }
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
                            if (currentLocation != null) {

                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                                // move to the device location and zoom
                            }
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            MyToast.makeText(GoogleMapsActivity.this, R.string.unable_get_location, Toast.LENGTH_SHORT).show();
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
        MyToast.makeText(GoogleMapsActivity.this,
                R.string.red_blue_marker,
                Toast.LENGTH_LONG).show();
        hideSoftKeyboard();
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        // walkingGroup();
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + " , lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (title != "My Location") {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .snippet(title);
            mMarker = mMap.addMarker(options);
            mSearchMarker.add(mMarker);

            PlaceInfo temPlace = new PlaceInfo(title, title, null, null, null, latLng, 0, null);
            mPlaceDetailsTextList.add(temPlace);
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

            MarkerOptions meetOptions = new MarkerOptions()
                    .position(mMeetingGroupInfoList.get(i).getLatLng())
                    .title(MEETING_PLACE)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

            Marker tempMarker = mMap.addMarker(options);
            tempMarker.setVisible(true);
            mMarkerList.add(tempMarker);

            Marker tempMeeting = mMap.addMarker(meetOptions);
            tempMeeting.setVisible(false);
            mMeetGroupMarkerList.add(tempMeeting);
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
        MyToast.makeText(this, R.string.map_is_ready, Toast.LENGTH_SHORT).show();
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

            setOnMapClick();



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
