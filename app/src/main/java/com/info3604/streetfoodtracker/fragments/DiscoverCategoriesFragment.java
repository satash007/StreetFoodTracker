package com.info3604.streetfoodtracker.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.info3604.streetfoodtracker.R;
import com.info3604.streetfoodtracker.utils.WrapContentLinearLayoutManager;
import com.info3604.streetfoodtracker.adapter.BrowseByFoodTypeListAdapter;
import com.info3604.streetfoodtracker.adapter.BrowseByLocationListAdapter;
import com.info3604.streetfoodtracker.model.FoodTypeFirebaseModel;
import com.info3604.streetfoodtracker.model.LocationTypeFirebaseModel;

import java.util.Arrays;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DiscoverCategoriesFragment extends Fragment {

    private RecyclerView horizontalList;
    private RecyclerView verticalList;
    private BrowseByFoodTypeListAdapter horizontalAdapter;
    private BrowseByLocationListAdapter verticalAdapter;
    DatabaseReference dbRef1, dbRef2;
    private AdView mAdView;
    private LinearLayout progressBarLayout, linLayout;

    public DiscoverCategoriesFragment() {
        // Required empty public constructor
    }


    public static DiscoverCategoriesFragment newInstance() {
        DiscoverCategoriesFragment fragment = new DiscoverCategoriesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.activity_home_list, container, false);

        horizontalList = (RecyclerView) view.findViewById(R.id.horizontal_recycler);
        verticalList = (RecyclerView) view.findViewById(R.id.vertical_recycler);
        progressBarLayout = (LinearLayout) view.findViewById(R.id.progressBarLayout);
        linLayout = (LinearLayout) view.findViewById(R.id.contentLayout);

        // Create a instance of the database and get
        // its reference
        dbRef1 = FirebaseDatabase.getInstance().getReference().child("FoodTypes");
        dbRef2 = FirebaseDatabase.getInstance().getReference().child("VendorLocations");

        // To display the Recycler view linearly
        horizontalList.setLayoutManager(
                new WrapContentLinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));

        // It is a class provide by the FirebaseUI to make a
        // query in the database to fetch appropriate data
        FirebaseRecyclerOptions<FoodTypeFirebaseModel> optionsHoriz
                = new FirebaseRecyclerOptions.Builder<FoodTypeFirebaseModel>()
                .setQuery(dbRef1, FoodTypeFirebaseModel.class)
                .build();
        // Connecting object of required Adapter class to
        // the Adapter class itself
        horizontalAdapter = new BrowseByFoodTypeListAdapter(optionsHoriz, getActivity());
        // Connecting Adapter class with the Recycler view*/
        horizontalList.setAdapter(horizontalAdapter);

        horizontalAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                super.onChanged();
                showLayout();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                showLayout();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                showLayout();
            }

            void showLayout() {
                progressBarLayout.setVisibility(View.GONE);
                linLayout.setVisibility(View.VISIBLE);

            }
        });


        // To display the Recycler view linearly
        verticalList.setLayoutManager(
                new WrapContentLinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false));

        // It is a class provide by the FirebaseUI to make a
        // query in the database to fetch appropriate data
        FirebaseRecyclerOptions<LocationTypeFirebaseModel> optionsVert
                = new FirebaseRecyclerOptions.Builder<LocationTypeFirebaseModel>()
                .setQuery(dbRef2, LocationTypeFirebaseModel.class)
                .build();
        // Connecting object of required Adapter class to
        // the Adapter class itself
        verticalAdapter = new BrowseByLocationListAdapter(optionsVert, getActivity());
        // Connecting Adapter class with the Recycler view*/
        verticalList.setAdapter(verticalAdapter);

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        List<String> testDeviceIds = Arrays.asList("0E9F3F151309F1E042345DACC334B7F9");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);


        return view;
    }

    // Function to tell the app to start getting
    // data from database on starting of the activity
    @Override
    public void onStart()
    {
        super.onStart();
        horizontalAdapter.startListening();
        verticalAdapter.startListening();

    }

    // Function to tell the app to stop getting
    // data from database on stopping of the activity
    @Override
    public void onStop()
    {
        super.onStop();
        horizontalAdapter.stopListening();
        verticalAdapter.stopListening();

    }

}

/*
REFERENCES
https://www.nintyzeros.com/2016/07/android-recycler-cardview.html
 */
