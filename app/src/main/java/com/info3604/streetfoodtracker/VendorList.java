package com.info3604.streetfoodtracker;

import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VendorList extends AppCompatActivity {

    RecyclerView recyclerView;
    List<PlacesPOJO.CustomA> results;
    List<VendorModel> vendorModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafes);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        SearchData x = SearchData.getInstance();
        vendorModels = x.getStoreModels();
        results = x.results;
        if (vendorModels.size() == 10 || vendorModels.size() == results.size()) {
            RecyclerViewAdapter adapterStores = new RecyclerViewAdapter(results, vendorModels);
            recyclerView.setAdapter(adapterStores);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SearchData x = SearchData.getInstance();
        vendorModels = x.getStoreModels();
        results = x.results;
        if (vendorModels.size() == 10 || vendorModels.size() == results.size()) {
            RecyclerViewAdapter adapterStores = new RecyclerViewAdapter(results, vendorModels);
            recyclerView.setAdapter(adapterStores);
        }

    }
}