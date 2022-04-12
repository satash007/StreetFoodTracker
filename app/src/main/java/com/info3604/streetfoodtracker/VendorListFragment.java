package com.info3604.streetfoodtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Arrays;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VendorListFragment extends Fragment {

    RecyclerView recyclerView;
    List<PlacesPOJO.CustomA> results;
    List<VendorModel> vendorModels;
    private LinearLayout emptyViewLayout;
    private AdView mAdView;

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

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        SearchData x = SearchData.getInstance();
        vendorModels = x.getStoreModels();
        results = x.results;
        //if (vendorModels.size() == 10 || vendorModels.size() == results.size()) {
            VendorListAdapter adapterStores = new VendorListAdapter(getActivity(), vendorModels);
            recyclerView.setAdapter(adapterStores);
        //}

        if (vendorModels.isEmpty()) {
            emptyViewLayout.setVisibility(View.VISIBLE);
        }
        else {
            emptyViewLayout.setVisibility(View.GONE);
        }

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final VendorModel vM = vendorModels.get(position);
                Intent i = new Intent(getActivity(), DisplayVendorProfileActivity.class);
                i.putExtra("VENDOR_NAME", vM.name);
                i.putExtra("VENDOR_ADDRESS", vM.address);
                i.putExtra("VENDOR_RATING", vM.rating);

                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        List<String> testDeviceIds = Arrays.asList("0E9F3F151309F1E042345DACC334B7F9");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        SearchData x = SearchData.getInstance();
        vendorModels = x.getStoreModels();
        results = x.results;
        //if (vendorModels.size() == 10 || vendorModels.size() == results.size()) {
            VendorListAdapter adapterStores = new VendorListAdapter(getActivity(), vendorModels);
            recyclerView.setAdapter(adapterStores);
        //}

    }
}