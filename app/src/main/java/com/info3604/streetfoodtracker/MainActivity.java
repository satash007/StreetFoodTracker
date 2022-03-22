package com.info3604.streetfoodtracker;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.info3604.streetfoodtracker.ImageHandling.CircleTransform;
import com.info3604.streetfoodtracker.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    FirebaseAuth mAuth;
    private static final String DBUserRec = "Userbase";
    private String name="", email, password, accountType, phoneNumber, address, img;
    DrawerLayout drawer;

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
                //startActivity(new Intent(MainActivity.this, VendorList.class));


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



        //check the current user
        if (mAuth.getCurrentUser() != null) {
            //Snackbar.make(binding.getRoot(), "Signed in as " + mAuth.getCurrentUser().getDisplayName(), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            navUsername.setText(mAuth.getCurrentUser().getDisplayName());

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

                Picasso.get()
                        .load(mAuth.getCurrentUser().getPhotoUrl())
                        .placeholder(R.drawable.logo_streetfoodtracker)
                        .resize(200, 200)
                        .transform(new CircleTransform())
                        //.centerCrop()
                        .into(navImage);

            }
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
        super.onBackPressed();
    }




}