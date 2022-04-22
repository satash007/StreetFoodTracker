package com.info3604.streetfoodtracker.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

//import com.daimajia.androidanimations.library.Techniques;
//import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.info3604.streetfoodtracker.API.APIClient;
import com.info3604.streetfoodtracker.API.ApiInterface;

import com.info3604.streetfoodtracker.R;
import com.info3604.streetfoodtracker.datahandling.SearchData;
import com.info3604.streetfoodtracker.activities.SignInActivity;
import com.info3604.streetfoodtracker.activities.UserCommentsActivity;
import com.info3604.streetfoodtracker.model.PlacesPOJO;
import com.info3604.streetfoodtracker.model.VendorModel;
import com.info3604.streetfoodtracker.model.maproutes.DirectionResponses;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.INTERNET;

public class VendorMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private GoogleMap mMap;

    private static final String TAG = "VendorMapFragment";
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 10000000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000000000; /* 20 sec */

    private LocationManager locationManager;
    private boolean isPermission;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private List<VendorModel> vendorModels;
    private ApiInterface apiService;

    private String latLngString, vendorLoc, currentLoc;
    private LatLng latLng;
    private EditText editText;
    private Button btnSearch, closeBtn, addReviewBtn;
    private List<PlacesPOJO.CustomA> results;
    private ProgressBar progressBar;

    private TextView vendorNameText, vendorAddressText, vendorLatitudeText, vendorLongitudeText, vendorSourceText, vendorDistanceText;
    private RatingBar rbVendorRat;

    //private FloatingActionButton fab;


    private FloatingActionButton viewMapLegendFab, goToMyLocFab;

    // Use the ExtendedFloatingActionButton to handle the
    // parent FAB
    private ExtendedFloatingActionButton actionFab;

    // These TextViews are taken to make visible and
    // invisible along with FABs except parent FAB's action
    // name
    private TextView viewMapLegendText, goToLocText;

    // to check whether sub FABs are visible or not
    private Boolean isAllFabsVisible;


    private View root;

    private FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    private DatabaseReference databaseReference;
    private SupportMapFragment mapFragment;

    private int vendorCount = 0;

    private PolylineOptions polyline;
    private Polyline mPolyline;
    private Marker currentMarker = null, vendorMarker;

    private Circle radiusCircle;

    public Toolbar mToolbar;

    private AdView mAdView;

    private SlidingUpPanelLayout mLayout;

    private String[] querySplitText = null;

    public VendorMapFragment() {

    }

    public static VendorMapFragment newInstance() {
        VendorMapFragment fragment = new VendorMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void searchVendors(String searchQuery, String queryType) {
        progressBar.setVisibility(View.VISIBLE);

        CountDownTimer countDownTimer =
                new CountDownTimer(1 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        progressBar.setVisibility(View.GONE);

                    }
                };
        countDownTimer.start();
        fetchVendors(searchQuery, queryType);
    }

    public static VendorMapFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(TAG, page);
        VendorMapFragment fragment = new VendorMapFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private List<PlacesPOJO.CustomA> getResults(List<PlacesPOJO.CustomA> results) {
        return results;
    }

    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        EventBus.getDefault().register(this);

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        apiService = APIClient.getClient().create(ApiInterface.class);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                //getActivity().finish();
            }

        };

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        setupMap();

        mToolbar = getActivity().findViewById(R.id.toolbar);
        mToolbar.setTitle("Street Food Tracker");
        mToolbar.setSubtitle("Discover, track and review street foods and their vendors");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.red_200));
        mToolbar.setSubtitleTextColor(getResources().getColor(R.color.cardview_dark_background));
        mToolbar.setSubtitleTextAppearance(getContext(), R.style.ToolbarSubtitleTextAppearance);

    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.activity_maps, container, false);

        editText = (EditText) root.findViewById(R.id.editTextSearch);
        btnSearch = (Button) root.findViewById(R.id.btnSearch);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        //fab = (FloatingActionButton) root.findViewById(R.id.fab);

        //Vendor details
        vendorNameText = (TextView) root.findViewById(R.id.vendor_name);
        vendorAddressText = (TextView) root.findViewById(R.id.vendor_address);
        vendorLatitudeText = (TextView) root.findViewById(R.id.vendor_lat);
        vendorLongitudeText = (TextView) root.findViewById(R.id.vendor_lng);
        vendorSourceText = (TextView) root.findViewById(R.id.vendor_source);
        vendorDistanceText = (TextView) root.findViewById(R.id.vendor_distance);

        rbVendorRat = (RatingBar) root.findViewById(R.id.current_rating);
        addReviewBtn = (Button) root.findViewById(R.id.add_review);

        closeBtn = (Button) root.findViewById(R.id.closeBtn);


        btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String s = editText.getText().toString().trim();
                if (s == null || s.length() == 0) {
                    showSnackBar("Please enter something into the search bar.", true);

                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    CountDownTimer countDownTimer =
                            new CountDownTimer(1 * 1000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    progressBar.setVisibility(View.GONE);

                                }
                            };
                    countDownTimer.start();
                    fetchVendors(s, "");
                }
            }
        });

        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Loading your current location...", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                if(currentMarker == null) {
                    currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                }else{
                    if (latLng != null) {
                        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

                        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                        CameraPosition cameraPosition = new CameraPosition.Builder( )
                                .target(latLng)      // Sets the center of the map to the vendor selected
                                .zoom(16)                   // Sets the zoom
                                //.bearing(90)                // Sets the orientation of the camera to east
                                //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                .build();                   // Creates a CameraPosition from the builder
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback(){

                            @Override
                            public void onFinish(){
                                currentMarker.showInfoWindow();

                            }

                            @Override
                            public void onCancel(){


                            }
                        });
                    }
                }

            }
        });*/

        mAdView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        List<String> testDeviceIds = Arrays.asList("0E9F3F151309F1E042345DACC334B7F9");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);


        mLayout = (SlidingUpPanelLayout) root.findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        closeBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                    }
                });



        setupFabButton();

        return root;
    }

    private void setupFabButton() {
        // Register all the FABs with their appropriate IDs
        // This FAB button is the Parent
        actionFab = root.findViewById(R.id.action_fab);
        // FAB button
        viewMapLegendFab = root.findViewById(R.id.view_map_legend_fab);
        goToMyLocFab = root.findViewById(R.id.go_to_my_location_fab);

        // Also register the action name text, of all the
        // FABs. except parent FAB action name text
        viewMapLegendText = root.findViewById(R.id.view_map_legend_text);
        goToLocText = root.findViewById(R.id.go_to_my_location_text);

        hideFab();

        // We will make all the FABs and action name texts
        // visible only when Parent FAB button is clicked So
        // we have to handle the Parent FAB button first, by
        // using setOnClickListener you can see below
        actionFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {

                            showFab();
                            //actionFab.setIcon(getResources().getDrawable(R.drawable.leku_ic_close));
                        } else {
                            hideFab();
                            //actionFab.setIcon(getResources().getDrawable(R.drawable.ic_view_list_white));
                        }
                    }
                });

        // below is the sample action to handle add person
        // FAB. Here it shows simple Toast msg. The Toast
        // will be shown only when they are visible and only
        // when user clicks on them
        goToMyLocFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "Loading your current location...", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                        if (currentMarker == null) {
                            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                        } else {
                            if (latLng != null) {
                                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

                                // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(latLng)      // Sets the center of the map to the vendor selected
                                        .zoom(16)                   // Sets the zoom
                                        //.bearing(90)                // Sets the orientation of the camera to east
                                        //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                        .build();                   // Creates a CameraPosition from the builder
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback() {

                                    @Override
                                    public void onFinish() {
                                        currentMarker.showInfoWindow();

                                    }

                                    @Override
                                    public void onCancel() {


                                    }
                                });
                            }
                        }
                        hideFab();
                    }
                });

        // below is the sample action to handle add alarm
        // FAB. Here it shows simple Toast msg The Toast
        // will be shown only when they are visible and only
        // when user clicks on them
        viewMapLegendFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        LayoutInflater factory = LayoutInflater.from(getActivity());
                        final View legendView = factory.inflate(R.layout.map_legend, null);
                        alert.setView(legendView);
                        alert.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int action) {

                            }
                        });

                        alert.show();

                    }
                });
    }

    private void showFab() {
        // when isAllFabsVisible becomes
        // true make all the action name
        // texts and FABs VISIBLE.
        viewMapLegendFab.show();
        goToMyLocFab.show();
        viewMapLegendText.setVisibility(View.VISIBLE);
        goToLocText.setVisibility(View.VISIBLE);

        // Now extend the parent FAB, as
        // user clicks on the shrinked
        // parent FAB
        actionFab.extend();

        // make the boolean variable true as
        // we have set the sub FABs
        // visibility to GONE
        isAllFabsVisible = true;
    }

    private void hideFab() {
        // when isAllFabsVisible becomes
        // true make all the action name
        // texts and FABs GONE.
        viewMapLegendFab.hide();
        goToMyLocFab.hide();
        viewMapLegendText.setVisibility(View.GONE);
        goToLocText.setVisibility(View.GONE);

        // Set the FAB to shrink after user
        // closes all the sub FABs
        actionFab.shrink();

        // make the boolean variable false
        // as we have set the sub FABs
        // visibility to GONE
        isAllFabsVisible = false;
    }


    public void setupMap() {
        int permissionFINELOC = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionFINELOC == PackageManager.PERMISSION_GRANTED) {
            if (mapFragment == null) {
                mapFragment = SupportMapFragment.newInstance();

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        if (latLng != null && currentMarker == null) {
                            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        }
                    }
                });

            }


            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            checkLocation(); //check whether location service is enable or not in your  phone
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:  {
                // navigate to settings screen
                Toast.makeText(getActivity(), "Settings", Toast.LENGTH_SHORT).show();

                return true;
            }
            case R.id.action_logout: {
                mAuth.signOut();
                startActivity(new Intent(getActivity(), SignInActivity.class));

                Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }
*/


    public ArrayList<String> getPermissionList() {
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissions.add(INTERNET);
        permissions.add(ACCESS_WIFI_STATE);
        permissions.add(ACCESS_NETWORK_STATE);
        return permissions;
    }

    private boolean checkForPendingPermission() {
        permissionsToRequest = findUnAskedPermissions(getPermissionList());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                Log.d("checkPendingPermission", "All permissions not available.");
                return true;
            }
        }
        Log.d("checkPendingPermission", "All permissions available.");
        return false;
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();
        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {
                    startLocationUpdates();
                    //Restart activity to get location permissions

                }

                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    //it was pre written
    @Override
    public void onMapReady(GoogleMap googleMap) {


        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference().child("Userbase").getRef();

        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setPadding(0, 0, 0, 150); //move the controls above the AdMob banner


        /*
        TODO: Add feature to use profile photo in marker icon
        ImageView navImage = (ImageView) getActivity().findViewById(R.id.nav_ImageView);

        BitmapDrawable bitmapDrawable = ((BitmapDrawable) navImage.getDrawable());
        Bitmap bitmap = bitmapDrawable .getBitmap();

    */

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            public void onMapLoaded() {

                currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                if (vendorMarker != null) {
                    if (querySplitText != null) {
                        if (querySplitText[1].equals("location")) {

                            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                            //mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

                            // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(vendorMarker.getPosition())      // Sets the center of the map to the vendor selected
                                    .zoom(11)                   // Sets the zoom
                                    //.bearing(90)                // Sets the orientation of the camera to east
                                    //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                    .build();                   // Creates a CameraPosition from the builder
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback() {

                                @Override
                                public void onFinish() {
                                    currentMarker.showInfoWindow();
                                }

                                @Override
                                public void onCancel() {


                                }
                            });
                        }
                    }

                } else {
                    // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

                    // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)      // Sets the center of the map to the vendor selected
                            .zoom(11)                   // Sets the zoom
                            //.bearing(90)                // Sets the orientation of the camera to east
                            //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback() {

                        @Override
                        public void onFinish() {
                            currentMarker.showInfoWindow();
                        }

                        @Override
                        public void onCancel() {


                        }
                    });
                }
            }
        });


        //Load search history
        loadSeachHistory();


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!marker.getTitle().equals("You are here!")) {
                    Location locationA = new Location("Origin");

                    locationA.setLatitude(latLng.latitude);
                    locationA.setLongitude(latLng.longitude);

                    Location locationB = new Location("Destination");

                    locationB.setLatitude(marker.getPosition().latitude);
                    locationB.setLongitude(marker.getPosition().longitude);

                    float distance = locationA.distanceTo(locationB) / 1000; //divide by 1000 to convert from m to km

                    double roundOff = Math.round(distance * 10.0) / 10.0;

                    marker.setSnippet(roundOff + "km away"); //in km

                    vendorMarker = marker;
                    currentLoc = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
                    vendorLoc = String.valueOf(marker.getPosition().latitude) + "," + String.valueOf(marker.getPosition().longitude);
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 12f));

                    ApiServices apiServices = RetrofitClient.apiServices(getActivity());
                    apiServices.getDirection(currentLoc, vendorLoc, getString(R.string.google_maps_key))
                            .enqueue(new Callback<DirectionResponses>() {
                                @Override
                                public void onResponse(@NonNull Call<DirectionResponses> call, @NonNull Response<DirectionResponses> response) {
                                    drawPolyline(response);

                                    // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

                                    // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(marker.getPosition())      // Sets the center of the map to the vendor selected
                                            .zoom(16)                   // Sets the zoom
                                            //.bearing(90)                // Sets the orientation of the camera to east
                                            //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                            .build();                   // Creates a CameraPosition from the builder
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                    Log.d("Success", response.message());
                                }

                                @Override
                                public void onFailure(@NonNull Call<DirectionResponses> call, @NonNull Throwable t) {
                                    Log.e("Failed to get data", t.getLocalizedMessage());
                                }
                            });

                }

                return false;
            }
        });



        /*
         OnClick Listener for Markers info windows on the map.
         Clicking will open a new activity with details of the marker.
         */
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (!marker.getTitle().equals("You are here!")) {


                    vendorNameText.setText(marker.getTitle().toString());

                    for (VendorModel vM : vendorModels) {
                        if (vM.lat.equals(String.valueOf(marker.getPosition().latitude)) && vM.lng.equals(String.valueOf(marker.getPosition().longitude))) {
                            vendorAddressText.setText(vM.address);
                            vendorLatitudeText.setText(vM.lat);
                            vendorLongitudeText.setText(vM.lng);
                            rbVendorRat.setRating(Float.parseFloat(vM.rating));
                            vendorSourceText.setText(vM.type);
                            vendorDistanceText.setText(vM.distance + "km away");

                        }
                    }


                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);

                    addReviewBtn.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if(mAuth.getCurrentUser() != null) {
                                        //fetch firebase vendors
                                        databaseReference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                //Iterate through all the child nodes of users
                                                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                                                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                                                boolean flag = false;
                                                while (iterator.hasNext()) {
                                                    DataSnapshot next = (DataSnapshot) iterator.next();

                                                    String name = (String) next.child("name").getValue();
                                                    String uid = (String) next.child("uid").getValue();

                                                    if (marker.getTitle().equals(name)) {
                                                        flag = true;
                                                        Intent intent = new Intent(getActivity(), UserCommentsActivity.class);
                                                        intent.putExtra("user_key", mAuth.getCurrentUser().getUid()); // the id of the user that executed the action //
                                                        intent.putExtra("Vendor_Uid", uid); // the id of the vendor to which the user selected //
                                                        startActivity(intent);
                                                    }

                                                }

                                                if(!flag)
                                                    Toast.makeText(getActivity(), "Only Firebase vendors supported.", Toast.LENGTH_SHORT).show();

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Log.d(TAG, databaseError.getMessage());
                                                Toast.makeText(getActivity(), "Fail to get data.", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }else{
                                        Intent intent = new Intent(getActivity(), SignInActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(getActivity(), "Please register/sign in and try again.", Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });


                }
            }
        });

    }

    private void loadSeachHistory() {
        SearchData x = SearchData.getInstance();
        vendorModels = x.getStoreModels();

        if (vendorModels != null && vendorModels.size() > 0) {
            addMarkers(vendorModels);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultReceived(String result) {
        if (result != null) {

            querySplitText = result.split("%");

            editText.setText(querySplitText[0]);
            searchVendors(querySplitText[0], querySplitText[1]);
        }
    }


    private void drawCircle(LatLng latLng, Double radius) {
        radiusCircle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeWidth(1.0f)
                .strokeColor(ContextCompat.getColor(getContext(), R.color.red_200)).fillColor(ContextCompat.getColor(getContext(), R.color.browser_actions_divider_color)));

    }

    private void drawPolyline(@NonNull Response<DirectionResponses> response) {
        if (mPolyline != null) {
            clearPolylines();
        }
        Log.e(TAG, response.toString());
        if (response.body() != null) {

            String shape = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
            polyline = new PolylineOptions()
                    .addAll(PolyUtil.decode(shape))
                    .width(8f)
                    .geodesic(true)
                    .color(Color.RED);
            mPolyline = mMap.addPolyline(polyline);
        }

    }


    private void clearPolylines() {
        mPolyline.remove();
    }


    private interface ApiServices {
        @GET("maps/api/directions/json")
        Call<DirectionResponses> getDirection(@Query("origin") String origin,
                                              @Query("destination") String destination,
                                              @Query("key") String apiKey);
    }

    private static class RetrofitClient {
        static ApiServices apiServices(Context context) {
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(context.getResources().getString(R.string.base_url))
                    .build();

            return retrofit.create(ApiServices.class);
        }
    }

    public void addMarkers(List<VendorModel> stores) {
        //mMap.clear();

        if (latLng != null) {
            mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            drawCircle(latLng, 5000.00); //TODO: Pass value by parameter for radius

           /* // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

            // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
            CameraPosition cameraPosition = new CameraPosition.Builder( )
                    .target(vendorMarker.getPosition())      // Sets the center of the map to the vendor selected
                    .zoom(11)                   // Sets the zoom
                    //.bearing(90)                // Sets the orientation of the camera to east
                    //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback(){

                @Override
                public void onFinish(){

                }

                @Override
                public void onCancel(){


                }
            });*/

        }

        for (VendorModel s : stores) {
            //vendorMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(s.lat), Double.parseDouble(s.lng))).title(s.name));
            if (s.type.equals("Firebase"))
                vendorMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(s.lat), Double.parseDouble(s.lng))).title(s.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            else
                vendorMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(s.lat), Double.parseDouble(s.lng))).title(s.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            //Log.e(TAG, "MAP: " + s.toString());
        }


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation == null) {
            showSnackBar("Please wait...detecting your current location", true);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {

        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        latLngString = location.getLatitude() + "," + location.getLongitude();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //it was pre written
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    /*
        YoYo.with(Techniques.Flash)
                .duration(700)
                .repeat(2)
                .playOn(root.findViewById(R.id.fab));

     */
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        mAuth.removeAuthStateListener(mAuthListener);
        EventBus.getDefault().unregister(this);

    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Enable Location")
                .setMessage("Your Location Settings is turned 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

/*
    private boolean requestSinglePermission() {

        Dexter.withContext(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        //Single Permission is granted
                        //Toast.makeText(MapsActivity.this, "Single permission is granted!", Toast.LENGTH_SHORT).show();
                        //showSnackBar("Location permission available.", false);
                        isPermission = true;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            isPermission = false;
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        return isPermission;
    }
*/


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void fetchVendors(String searchQuery, String queryType) {

        mMap.clear();
        vendorModels.clear();

        if (!isNetworkConnected()) {
            showSnackBar("Check network connection.", true);
            return;
        }

        if (checkLocation()) {

            //fetch firebase vendors
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //Iterate through all the child nodes of users
                    Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> iterator = snapshotIterator.iterator();


                    while (iterator.hasNext()) {
                        DataSnapshot next = (DataSnapshot) iterator.next();
                        Double latitude = (Double) next.child("latitude").getValue();
                        Double longitude = (Double) next.child("longitude").getValue();
                        String name = (String) next.child("name").getValue();
                        String address = (String) next.child("address").getValue();
                        //String rating = (String) next.child("rating").getValue();
                        String rating = "0"; //TODO: Access ratings separately after narin is finished


                        if (latitude != null && longitude != null) {
                            //search both address and name for the each vendor
                            if (queryType.equals("food")) {
                                if (name.toLowerCase(Locale.ROOT).contains(searchQuery.toLowerCase(Locale.ROOT))) {
                                    LatLng latLng = new LatLng(latitude, longitude);
                                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(name).snippet(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                                    vendorMarker = mMap.addMarker(markerOptions);

                                    vendorModels.add(new VendorModel(name, address, String.valueOf(latitude), String.valueOf(longitude), rating, "Firebase", ""));

                                }
                            } else if (queryType.equals("location")) {
                                if (address.toLowerCase(Locale.ROOT).contains(searchQuery.toLowerCase(Locale.ROOT))) {
                                    LatLng latLng = new LatLng(latitude, longitude);
                                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(name).snippet(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                                    vendorMarker = mMap.addMarker(markerOptions);

                                    vendorModels.add(new VendorModel(name, address, String.valueOf(latitude), String.valueOf(longitude), rating, "Firebase", ""));

                                }
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage());
                    Toast.makeText(getActivity(), "Fail to get data.", Toast.LENGTH_SHORT).show();

                }
            });


            Call<PlacesPOJO.Root> call = apiService.doPlaces("restaurant", latLngString, searchQuery, true, "distance", APIClient.GOOGLE_PLACE_API_KEY);
            call.enqueue(new Callback<PlacesPOJO.Root>() {
                @Override
                public void onResponse(Call<PlacesPOJO.Root> call, Response<PlacesPOJO.Root> response) {
                    PlacesPOJO.Root root = response.body();

                    if (response.isSuccessful()) {

                        if (root.status.equals("OK")) {
                            SearchData x = SearchData.getInstance();
                           x.setSearchTerm(searchQuery);

                            results = root.customA;

                            x.results = results;

                            //vendorModels = new ArrayList<>();
                            for (int i = 0; i < results.size(); i++) {

                                //if (i == 10)
                                //    break;
                                Log.d(TAG, "RESULTS: " + results.get(i));

                                PlacesPOJO.CustomA info = results.get(i);


                                vendorModels.add(new VendorModel(info.name, info.vicinity, info.geometry.locationA.lat, info.geometry.locationA.lng, info.rating, "GooglePlaces", ""));

                               /* if (vendorModels.size() == 10 || vendorModels.size() == results.size()) {

                                }*/
                            }

                            //Calculate distance of each vendor from current location
                            for (VendorModel vM : vendorModels) {

                                Location locationA = new Location("Origin");

                                locationA.setLatitude(latLng.latitude);
                                locationA.setLongitude(latLng.longitude);

                                Location locationB = new Location("Destination");

                                locationB.setLatitude(Double.parseDouble(vM.lat));
                                locationB.setLongitude(Double.parseDouble(vM.lng));

                                float distance = locationA.distanceTo(locationB) / 1000; //divide by 1000 to convert from m to km

                                double roundOff = Math.round(distance * 10.0) / 10.0;

                                vM.distance = String.valueOf(roundOff);

                            }

                            x.setStores(vendorModels, "rating");
                            addMarkers(vendorModels);

                            if (vendorModels.size() > 0)
                                showSnackBar("Found " + vendorModels.size() + " " + searchQuery + " vendors.", false);
                            else
                                showSnackBar("No matches found near you.", true);

                            // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(currentMarker.getPosition())      // Sets the center of the map to the vendor selected
                                    .zoom(11)                   // Sets the zoom
                                    //.bearing(90)                // Sets the orientation of the camera to east
                                    //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                    .build();                   // Creates a CameraPosition from the builder
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback() {

                                @Override
                                public void onFinish() {

                                }

                                @Override
                                public void onCancel() {


                                }
                            });


                        } else {
                            if (vendorModels.size() > 0)
                                showSnackBar("Found " + vendorModels.size() + " " + searchQuery + " vendors.", false);
                            else
                                showSnackBar("No matches found near you.", true);

                            if (latLng != null) {
                                mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                            }
                        }

                    } else if (response.code() != 200) {
                        showSnackBar("Error fetching vendors.", true);

                    }
                }

                @Override
                public void onFailure(Call<PlacesPOJO.Root> call, Throwable t) {
                    // Log error here since request failed
                    call.cancel();
                }
            });


        } else {
            showSnackBar("Enable Location and try again.", true);
        }


    }

    public void showSnackBar(String message, boolean error) {
        hideKeyboard(getActivity());

        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}

/*
REFERENCES

https://stackoverflow.com/questions/8049612/calculating-distance-between-two-geographic-locations/8050255#8050255
 https://stackoverflow.com/questions/50950863/preventing-two-markers-for-current-location-in-google-maps
 https://androidwave.com/fragment-communication-using-eventbus/
 https://github.com/greenrobot/EventBus
 */