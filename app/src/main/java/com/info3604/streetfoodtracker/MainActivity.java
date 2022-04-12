

package com.info3604.streetfoodtracker;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.info3604.streetfoodtracker.imagehandling.CircleTransform;
import com.info3604.streetfoodtracker.databinding.ActivityMainBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    FirebaseAuth mAuth;
    private static final String DBUserRec = "Userbase";
    private String name="";
    DrawerLayout drawer;
    private AdView mAdView;
    private boolean permissionGranted;
    public Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

    /*
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
                //startActivity(new Intent(MainActivity.this, VendorListFragment.class));


            }
        });
*/

        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        mAuth = FirebaseAuth.getInstance();
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nav_header_title);
        TextView navEmail = (TextView) headerView.findViewById(R.id.nav_header_email);
        ImageView navImage = (ImageView) headerView.findViewById(R.id.nav_ImageView);
        ImageView navImageLogo = (ImageView) headerView.findViewById(R.id.nav_ImageView_Logo);
        Button loginBtn = (Button) headerView.findViewById(R.id.btnLogin);


        //check the current user
        if (mAuth.getCurrentUser() != null) {
            Snackbar.make(binding.getRoot(), "Signed in as " + mAuth.getCurrentUser().getDisplayName(), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            navUsername.setText(mAuth.getCurrentUser().getDisplayName());
            loginBtn.setVisibility(View.VISIBLE);
            loginBtn.setText("Sign Out");
            navEmail.setText(mAuth.getCurrentUser().getEmail());

            if((mAuth.getCurrentUser().getPhotoUrl()) == null) {
                //Using default user profile avatar image compliments Wikipedia Commons

                Picasso.get()
                        .load("https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png")
                        .placeholder(R.drawable.logo_streetfoodtracker)
                        .resize(200, 200)
                        .transform(new CircleTransform())
                        //.centerCrop()
                        .into(navImage);



            }else{
                //loginBtn.setVisibility(View.GONE);

                Picasso.get()
                        .load(mAuth.getCurrentUser().getPhotoUrl())
                        .placeholder(R.drawable.logo_streetfoodtracker)
                        .resize(200, 200)
                        .transform(new CircleTransform())
                        //.centerCrop()
                        .into(navImage);

            }

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference userRef = rootRef.child(DBUserRec);
            Log.v("Userbase", userRef.getKey());
            // Read from the database
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot keyId: dataSnapshot.getChildren()) {
                        if(keyId.child("uid").getValue() != null && (mAuth.getCurrentUser() != null)){
                            if (keyId.child("uid").getValue().equals(mAuth.getCurrentUser().getUid())) {
                                name = keyId.child("name").getValue(String.class);
                                //profession = keyId.child("profession").getValue(String.class);
                                //workplace = keyId.child("workplace").getValue(String.class);
                                //phone = keyId.child("phone").getValue(String.class);
                                Snackbar.make(binding.getRoot(), "Signed in as " + name, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                navUsername.setText(name);
                                break;
                            }
                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", databaseError.toException());            }
            });



        }else{
            navUsername.setText("Guest User");
            loginBtn.setVisibility(View.VISIBLE);
            loginBtn.setText("Login/Register");
            navEmail.setText("Not logged in");
            navImageLogo.setVisibility(View.GONE);
            Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png")
                    .placeholder(R.drawable.logo_streetfoodtracker)
                    .resize(200, 200)
                    .transform(new CircleTransform())
                    //.centerCrop()
                    .into(navImage);
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               if(loginBtn.getText().equals("Sign Out")){
                   mAuth.signOut();
                   navUsername.setText("Guest User");
                   loginBtn.setVisibility(View.VISIBLE);
                   loginBtn.setText("Login/Register");
                   navEmail.setText("Not logged in");
                   navImageLogo.setVisibility(View.GONE);
                   Picasso.get()
                           .load("https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png")
                           .placeholder(R.drawable.logo_streetfoodtracker)
                           .resize(200, 200)
                           .transform(new CircleTransform())
                           //.centerCrop()
                           .into(navImage);
                   Snackbar.make(binding.getRoot(), "Signed out.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

               }else {
                   startActivity(new Intent(MainActivity.this, SignInActivity.class));
                   finish();
               }
            }
        });

        permissionGranted = requestSinglePermission(); //request permission for map usage in MainActivity

        if(permissionGranted){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 3s = 3000ms
                    mToolbar = findViewById(R.id.toolbar);
                    SharedPreferences pref = getSharedPreferences("TutSharedPreferences",0);
                    SharedPreferences.Editor editor= pref.edit();
                    boolean firstRun = pref.getBoolean("firstRunTutorial", true);

                    if(firstRun) {
                        final TapTargetSequence sequence = new TapTargetSequence(MainActivity.this)
                                .targets(
                                        TapTarget.forToolbarNavigationIcon(mToolbar, "Tap here to access more content!", "Here you can browse by category, view all vendors and access other application features.")
                                                // All options below are optional
                                                //.outerCircleColor(R.color.myPrimaryColorTransparent)      // Specify a color for the outer circle
                                                //.outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                                .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                                .titleTextSize(30)                  // Specify the size (in sp) of the title text
                                                .titleTextColor(R.color.black)      // Specify the color of the title text
                                                .descriptionTextSize(16)            // Specify the size (in sp) of the description text
                                                .descriptionTextColor(R.color.myColorPrimary)  // Specify the color of the description text
                                                .textColor(R.color.black)            // Specify a color for both the title and description text
                                                .textTypeface(ResourcesCompat.getFont(MainActivity.this, R.font.poppins_reg))  // Specify a typeface for the text
                                                .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                                .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                                                .tintTarget(true)                   // Whether to tint the target view's color
                                                .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                                                .targetRadius(30)                 // Specify the target radius (in dp)
                                );
                        sequence.start();

                        editor.putBoolean("firstRunTutorial",false);
                        editor.commit();
                    }
                }
            }, 2000);
        }

    }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        }
        if( getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            return;
        }


        ViewPager mviewPager = (ViewPager) findViewById(R.id.viewpager);
        int currentPagerPos = mviewPager.getCurrentItem();

        if (currentPagerPos == 1 || currentPagerPos == 2) {
            mviewPager.setCurrentItem(0);
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onStart(){

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        List<String> testDeviceIds = Arrays.asList("0E9F3F151309F1E042345DACC334B7F9");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        super.onStart();
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {

        if (mAdView != null) {
            mAdView.resume();
        }

        super.onResume();
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private boolean requestSinglePermission() {

        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        //Single Permission is granted
                        //Toast.makeText(MapsActivity.this, "Single permission is granted!", Toast.LENGTH_SHORT).show();
                        //showSnackBar("Location permission available.", false);
                        permissionGranted = true;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            permissionGranted = false;
                            Toast.makeText(MainActivity.this, "Location permissions needed. Please reopen app.", Toast.LENGTH_SHORT).show();
                            //finish();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        return permissionGranted;
    }


}