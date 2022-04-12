package com.info3604.streetfoodtracker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.info3604.streetfoodtracker.model.FoodTypeFirebaseModel;
import com.info3604.streetfoodtracker.model.LocationTypeFirebaseModel;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExploreCategoriesFragment extends Fragment {

    private RecyclerView horizontalList;
    private RecyclerView verticalList;
    private BrowseByFoodTypeListAdapter horizontalAdapter;
    private BrowseByLocationListAdapter verticalAdapter;
    DatabaseReference dbRef1, dbRef2;

    public ExploreCategoriesFragment() {
        // Required empty public constructor
    }


    public static ExploreCategoriesFragment newInstance() {
        ExploreCategoriesFragment fragment = new ExploreCategoriesFragment();
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

        // horizontalList.setHasFixedSize(true);
        //verticalList.setHasFixedSize(true);

        // Create a instance of the database and get
        // its reference
        dbRef1 = FirebaseDatabase.getInstance().getReference().child("FoodTypes");
        dbRef2 = FirebaseDatabase.getInstance().getReference().child("VendorLocations");

        // To display the Recycler view linearly
        horizontalList.setLayoutManager(
                new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));

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


        // To display the Recycler view linearly
        verticalList.setLayoutManager(
                new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false));

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
