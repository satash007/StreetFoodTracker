package com.info3604.streetfoodtracker;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayVendorProfileActivity extends AppCompatActivity {

    String vendorName, vendorLoc, vendorRat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_details_activity);

        TextView txtVendorName = (TextView) findViewById(R.id.vendor_name);
        TextView txtVendorLoc = (TextView) findViewById(R.id.vendor_location);
        RatingBar rbVendorRat = (RatingBar) findViewById(R.id.current_rating);

        vendorName = getIntent().getStringExtra("VENDOR_NAME");
        txtVendorName.setText(vendorName);

        vendorLoc = getIntent().getStringExtra("VENDOR_ADDRESS");
        txtVendorLoc.setText("Location: " + vendorLoc);

        vendorRat = getIntent().getStringExtra("VENDOR_RATING");
        rbVendorRat.setRating(Float.parseFloat(vendorRat));
    }

}

/*REFERENCES
    https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android-application

 */