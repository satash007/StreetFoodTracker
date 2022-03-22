package com.info3604.streetfoodtracker.fragments.home;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.daimajia.androidanimations.library.Techniques;
//import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.info3604.streetfoodtracker.PlacesPOJO;
import com.info3604.streetfoodtracker.R;
import com.info3604.streetfoodtracker.SearchData;
import com.info3604.streetfoodtracker.VendorList;
import com.info3604.streetfoodtracker.VendorModel;
import com.info3604.streetfoodtracker.DisplayVendorProfileActivity;
import com.info3604.streetfoodtracker.API.APIClient;
import com.info3604.streetfoodtracker.API.ApiInterface;
import com.info3604.streetfoodtracker.SignInActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.INTERNET;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    private GoogleMap mMap;

    private static final String TAG = "HomeFragment";
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
    List<VendorModel> vendorModels;
    ApiInterface apiService;

    String latLngString;
    LatLng latLng;
    EditText editText;
    Button btnSearch, btnViewVendors;
    List<PlacesPOJO.CustomA> results;

    FloatingActionButton fab;
    View root;

    FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;
    SupportMapFragment mapFragment;

    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        apiService = APIClient.getClient().create(ApiInterface.class);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser()==null)
            {
                getActivity().finish();
            }

        };

        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference().child("Userbase").getRef();

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        setupMap();
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */

        root = inflater.inflate(R.layout.activity_maps, container, false);

        editText = (EditText) root.findViewById(R.id.editText);
        btnSearch = (Button) root.findViewById(R.id.btnSearch);
        btnViewVendors = (Button) root.findViewById(R.id.btnViewVendors);

        fab = (FloatingActionButton) root.findViewById(R.id.fab);




        btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String s = editText.getText().toString().trim();
                if (s == null || s.length() == 0) {
                    showSnackBar("Please enter text.", true);

                } else
                    fetchVendors(s);
            }
        });

        btnViewVendors.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(editText.getText().toString().trim() != null){
                       startActivity(new Intent(getActivity(), VendorList.class));
                   }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Getting current location...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                if(latLng!=null) {
                    mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }

            }
        });


        return root;
    }


    public void setupMap(){
        int permissionFINELOC = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionFINELOC == PackageManager.PERMISSION_GRANTED) {
            if (mapFragment == null) {
                mapFragment = SupportMapFragment.newInstance();

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        if(latLng!=null) {
                            mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
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
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    //it was pre written
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /*
        TODO: Add feature to use profile photo in marker icon
        ImageView navImage = (ImageView) getActivity().findViewById(R.id.nav_ImageView);

        BitmapDrawable bitmapDrawable = ((BitmapDrawable) navImage.getDrawable());
        Bitmap bitmap = bitmapDrawable .getBitmap();

    */
        if (latLng != null) {
            mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        }

        SearchData x = SearchData.getInstance();
        vendorModels = x.getStoreModels();

        if(vendorModels !=null && vendorModels.size()>0){
            addMarkers(vendorModels);
        }

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

                    float distance = locationA.distanceTo(locationB)/1000; //divide by 1000 to convert from m to km

                    double roundOff = Math.round(distance * 10.0) / 10.0;

                    marker.setSnippet(roundOff + "km away"); //in km

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

                    Intent i = new Intent(getActivity(), DisplayVendorProfileActivity.class);
                    i.putExtra("VENDOR_NAME", marker.getTitle().toString());

                    int b=0;
                    for (VendorModel vM : vendorModels) {
                        if (vM.lat.equals(String.valueOf(marker.getPosition().latitude)) && vM.lng.equals(String.valueOf(marker.getPosition().longitude))) {
                            i.putExtra("VENDOR_ADDRESS", vM.address);
                            i.putExtra("VENDOR_RATING", x.results.get(b).rating);

                            b++;
                        }
                    }

                    startActivity(i);
                }
            }
        });




    }

    public void addMarkers(List<VendorModel> stores) {
        mMap.clear();
        for (VendorModel s : stores) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(s.lat), Double.parseDouble(s.lng))).title(s.name));
        }
        if(latLng!=null) {
            mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        }

        //fetchFirebaseDatabaseVendors();


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
            showSnackBar("Location not Detected", true);
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
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        mAuth.removeAuthStateListener(mAuthListener);
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

    private void fetchFirebaseDatabaseVendors(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Iterate through all the child nodes of users
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    //Log.i(TAG, "Value = " + next.child("address").getValue());
                    Double latitude = (Double) next.child("latitude").getValue();
                    Double longitude = (Double) next.child("longitude").getValue();
                    String name = (String) next.child("name").getValue();
                    String address = (String) next.child("address").getValue();

                    if(latitude!= null && longitude !=null) {
                        LatLng latLng = new LatLng(latitude, longitude);
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(name).snippet(address);
                        mMap.addMarker(markerOptions);
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
                Toast.makeText(getActivity(), "Fail to get data.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void fetchVendors(String businessName) {

        if (!isNetworkConnected()) {
            showSnackBar("Check network connection.", true);
            return;
        }

        if (checkLocation()) {
            Call<PlacesPOJO.Root> call = apiService.doPlaces("restaurant", latLngString, businessName, true, "distance", APIClient.GOOGLE_PLACE_API_KEY);
            call.enqueue(new Callback<PlacesPOJO.Root>() {
                @Override
                public void onResponse(Call<PlacesPOJO.Root> call, Response<PlacesPOJO.Root> response) {
                    PlacesPOJO.Root root = response.body();

                    if (response.isSuccessful()) {

                        if (root.status.equals("OK")) {
                            SearchData x = SearchData.getInstance();

                            results = root.customA;
                            x.results = results;
                            vendorModels = new ArrayList<>();
                            for (int i = 0; i < results.size(); i++) {

                                //if (i == 10)
                                //    break;
                                PlacesPOJO.CustomA info = results.get(i);

                                vendorModels.add(new VendorModel(info.name, info.vicinity, info.geometry.locationA.lat, info.geometry.locationA.lng, info.rating));
                                if (vendorModels.size() == 10 || vendorModels.size() == results.size()) {

                                }
                            }
                            x.setStores(vendorModels);
                            int size = vendorModels.size();
                            showSnackBar("Found " + size + " vendors.", false);
                            addMarkers(vendorModels);

                        } else {
                            showSnackBar("No matches found near you.", true);
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
 */