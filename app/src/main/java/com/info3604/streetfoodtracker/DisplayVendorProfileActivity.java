package com.info3604.streetfoodtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class DisplayVendorProfileActivity extends AppCompatActivity {

    String vendorId, vendorLoc, vendorRat;
    private Button add_review;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserId = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_details_activity);

        TextView txtVendorName = (TextView) findViewById(R.id.vendor_name);
        TextView txtVendorLoc = (TextView) findViewById(R.id.vendor_location);
        RatingBar rbVendorRat = (RatingBar) findViewById(R.id.current_rating);
        add_review = (Button) findViewById(R.id.add_review);

        // gets the uid of the vendor which the user selects
        vendorId = getIntent().getStringExtra("VENDOR_UID");
        txtVendorName.setText(vendorId);

        /*vendorLoc = getIntent().getStringExtra("VENDOR_ADDRESS");
        txtVendorLoc.setText("Location: " + vendorLoc);

        vendorRat = getIntent().getStringExtra("VENDOR_RATING");
        rbVendorRat.setRating(Float.parseFloat(vendorRat));*/

        // on click listener that redirects to the comments views
        add_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DisplayVendorProfileActivity.this,UserCommentsActivity.class);
                intent.putExtra("user_key",currentUserId); // the id of the user that executed the action //
                intent.putExtra("Vendor_Uid",vendorId); // the id of the vendor to which the user selected //
                startActivity(intent);
            }
        });
    }

}

/*REFERENCES
    https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android-application

 */