package com.info3604.streetfoodtracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.info3604.streetfoodtracker.R;
import com.info3604.streetfoodtracker.utils.RecyclerTouchListener;
import com.info3604.streetfoodtracker.datahandling.SearchData;
import com.info3604.streetfoodtracker.activities.SignInActivity;
import com.info3604.streetfoodtracker.activities.UserCommentsActivity;
import com.info3604.streetfoodtracker.adapter.VendorListAdapter;
import com.info3604.streetfoodtracker.model.PlacesPOJO;
import com.info3604.streetfoodtracker.model.VendorModel;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VendorListFragment extends Fragment {
    private static final String TAG = "VendorListFragment";

    RecyclerView recyclerView;
    List<PlacesPOJO.CustomA> results;
    List<VendorModel> vendorModels;
    private LinearLayout emptyViewLayout, search_title_layout, sortByLayout;
    private AdView mAdView;
    private SlidingUpPanelLayout mLayout;
    private Button closeBtn, addReviewBtn;
    private TextView vendorNameText, vendorAddressText, vendorLatitudeText, vendorLongitudeText, vendorSourceText, vendorDistanceText, searchResultTitle;
    private RatingBar rbVendorRat;

    private RadioGroup radioGroup;
    private RadioButton radioButton;

    private FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    private DatabaseReference databaseReference;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public VendorListFragment() {
        // Required empty public constructor
    }


    public static VendorListFragment newInstance() {
        VendorListFragment fragment = new VendorListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_vendor_list, container, false);

        emptyViewLayout = (LinearLayout) view.findViewById(R.id.empty_layout);
        search_title_layout = (LinearLayout) view.findViewById(R.id.search_title_layout);
        sortByLayout = (LinearLayout) view.findViewById(R.id.sortByLayout);

        closeBtn = (Button) view.findViewById(R.id.closeBtn);

        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        addReviewBtn = (Button) view.findViewById(R.id.add_review);


        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        VendorListAdapter adapterStores = new VendorListAdapter(getActivity(), vendorModels);
        recyclerView.setAdapter(adapterStores);

        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference().child("Userbase").getRef();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                //getActivity().finish();
            }

        };


        SearchData x = SearchData.getInstance();

        vendorModels = x.getStoreModels();

        results = x.results;

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rbRating:
                        // do operations specific to this selection
                        x.setStores(vendorModels, "rating");
                        vendorModels = x.getStoreModels();

                        results = x.results;
                        adapterStores.notifyDataSetChanged();
                        recyclerView.setAdapter(adapterStores);
                        break;
                    case R.id.rbDistance:
                        // do operations specific to this selection
                        x.setStores(vendorModels, "distance");
                        vendorModels = x.getStoreModels();

                        results = x.results;
                        adapterStores.notifyDataSetChanged();
                        recyclerView.setAdapter(adapterStores);
                        break;
                }
            }
        });




        if (vendorModels.isEmpty()) {
            emptyViewLayout.setVisibility(View.VISIBLE);
            search_title_layout.setVisibility(View.GONE);
            sortByLayout.setVisibility(View.GONE);
        }
        else {
            emptyViewLayout.setVisibility(View.GONE);
            search_title_layout.setVisibility(View.VISIBLE);
            sortByLayout.setVisibility(View.VISIBLE);

        }


        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        List<String> testDeviceIds = Arrays.asList("0E9F3F151309F1E042345DACC334B7F9");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        //Vendor details
        vendorNameText = (TextView) view.findViewById(R.id.vendor_name);
        vendorAddressText = (TextView) view.findViewById(R.id.vendor_address);
        vendorLatitudeText = (TextView) view.findViewById(R.id.vendor_lat);
        vendorLongitudeText = (TextView) view.findViewById(R.id.vendor_lng);
        vendorSourceText = (TextView) view.findViewById(R.id.vendor_source);
        rbVendorRat = (RatingBar) view.findViewById(R.id.vendor_rating);
        vendorDistanceText = (TextView) view.findViewById(R.id.vendor_distance);
        searchResultTitle = (TextView) view.findViewById(R.id.search_title);

        searchResultTitle.setText("Showing search results for: " + x.getSearchTerm());

        mLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
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

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final VendorModel vM = vendorModels.get(position);
                vendorNameText.setText(vM.name);
                vendorAddressText.setText(vM.address);
                vendorLatitudeText.setText(vM.lat);
                vendorLongitudeText.setText(vM.lng);
                rbVendorRat.setRating(Float.parseFloat(vM.rating));
                vendorSourceText.setText(vM.type);
                vendorDistanceText.setText(vM.distance + "km away");

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

                                                if (vM.name.equals(name)) {
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

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

    }


    @Override
    public void onResume() {
        super.onResume();

        mAuth.addAuthStateListener(mAuthListener);

        SearchData x = SearchData.getInstance();
        vendorModels = x.getStoreModels();
        results = x.results;
        //if (vendorModels.size() == 10 || vendorModels.size() == results.size()) {
            VendorListAdapter adapterStores = new VendorListAdapter(getActivity(), vendorModels);
            recyclerView.setAdapter(adapterStores);
        //}

    }
}