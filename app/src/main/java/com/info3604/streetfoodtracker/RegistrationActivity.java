package com.info3604.streetfoodtracker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adevinta.leku.LocationPickerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.info3604.streetfoodtracker.model.User;

import java.util.ArrayList;


public class RegistrationActivity extends AppCompatActivity {
    private EditText inputUsername, inputEmail, inputPassword, inputPhone;
    private TextView txtUserAddress;
    private RadioGroup rgAccountType;
    private RadioButton rbAccountType;
    private FirebaseAuth mAuth;
    private static final String TAG = "";
    private ProgressBar progressBar;
    private View registrationView;

    // creating a variable for our
    // Firebase Database.
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;

    // creating a variable for
    // our object class
    User user;

    private ArrayList<User> userList = new ArrayList<User>();
    private boolean emailInUse = false;
    private String mImage = "";

    private String LATITUDE = "latitude";
    private String LONGITUDE = "longitude";
    private String LOCATION_ADDRESS = "location_address";
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);


        TextView txtLogin = (TextView) findViewById(R.id.login_page);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });



        mAuth = FirebaseAuth.getInstance();

        inputUsername = (EditText) findViewById(R.id.input_username);
        inputPhone = (EditText) findViewById(R.id.input_phone);
        inputEmail = (EditText) findViewById(R.id.input_email);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        inputPassword = (EditText) findViewById(R.id.input_password);
        Button btnSignUp = (Button) findViewById(R.id.btn_signup);
        rgAccountType = (RadioGroup) findViewById(R.id.rgAccountType);
        txtUserAddress = (TextView) findViewById(R.id.text_user_address);

        registrationView = findViewById(R.id.registrationForm);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              register();

            }
        });

        // below line is used to get the
        // instance of our Firebase database.
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference().child("Userbase");

        // initializing our object
        // class variable.
        user = new User();

        Button btnSetLoc = (Button) findViewById(R.id.btnChooseLoc);

        ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {

                                latitude = result.getData().getDoubleExtra(LATITUDE, 0.0);
                                Log.d("LATITUDE****", latitude.toString());
                                longitude = result.getData().getDoubleExtra(LONGITUDE, 0.0);
                                Log.d("LONGITUDE****", longitude.toString());
                                String address = result.getData().getStringExtra(LOCATION_ADDRESS);
                                Log.d("ADDRESS****", address.toString());

                                if(!address.isEmpty()) {
                                    txtUserAddress.setText(address);
                                }else{
                                    txtUserAddress.setText("Latitude: " + latitude.toString() + "\n" + "Longitude: " + longitude.toString());
                                }

                        }
                    }
                });


        btnSetLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent locationPickerIntent = new LocationPickerActivity.Builder()
                        .withLocation(10.536421, -61.311951)
                        .withGeolocApiKey(getResources().getString(R.string.google_maps_key))
                        .withSearchZone("en-EN")
                        .withDefaultLocaleSearchZone()
                        .shouldReturnOkOnBackPressed()
                        .withStreetHidden()
                        .withCityHidden()
                        .withZipCodeHidden()
                        .withSatelliteViewHidden()
                        .withGoogleTimeZoneEnabled()
                        .withVoiceSearchHidden()
                        .withUnnamedRoadHidden()
                        .build(RegistrationActivity.this);

                mStartForResult.launch(locationPickerIntent);

            }
        });


    }

    // Invoked when user data passes validation process
    private void createAccount(){

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String username = inputUsername.getText().toString();
        String phone = inputPhone.getText().toString();
        String userAddress = txtUserAddress.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            addDatatoFirebase(mAuth.getCurrentUser().getUid(), email, username, phone, userAddress, latitude, longitude, rbAccountType.getText().toString(), mImage);
                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }


                });


    }

    private void register(){
        // Reset errors, if there are any previous errors
        inputEmail.setError(null);
        inputPassword.setError(null);
        inputUsername.setError(null);
        inputPhone.setError(null);

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String username = inputUsername.getText().toString();
        String phone = inputPhone.getText().toString();
        String userAddress = txtUserAddress.getText().toString();

        int selectedID = rgAccountType.getCheckedRadioButtonId();
        rbAccountType = findViewById(selectedID);

        progressBar.setVisibility(View.VISIBLE);


        // Variables for validation of inputted data
        boolean cancel = false;
        View focusView = null;

        // Check if email is in use
        for(User u: userList){
            if(u.getEmail().equalsIgnoreCase(email)) {
                emailInUse = true;
            }
        }

        // Validates supplied registration data
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.error_field_required));
            focusView = inputEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            inputEmail.setError(getString(R.string.error_invalid_email));
            focusView = inputEmail;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            inputPassword.setError(getString(R.string.error_field_required));
            focusView = inputPassword;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            inputPassword.setError(getString(R.string.error_invalid_password));
            focusView = inputPassword;
            cancel = true;
        } else if (TextUtils.isEmpty(username)) {
            inputUsername.setError(getString(R.string.error_field_required));
            focusView = inputUsername;
            cancel = true;
        } else if (TextUtils.isEmpty(phone)) {
            inputPhone.setError(getString(R.string.error_field_required));
            focusView = inputPhone;
            cancel = true;
        } else if (emailInUse) {
            inputEmail.setError(getString(R.string.duplicate_email));
            focusView = inputEmail;
            cancel = true;
            emailInUse = false;
            focusView.requestFocus();
        }

        if (cancel) {
            // On error register process is not completed and focus returns to first area of error
            focusView.requestFocus();
            showProgress(false);

        } else {
            // Show loading screen for two seconds before createAccount Method is invoked
            showProgress(true);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    createAccount();
                }
            }, 2000);
        }

    }





    // Check if email is valid, must be in format digits@digits.digits,
    // digits after . must be greater than 2 characters wide
    private boolean isEmailValid(String email) {
        String pattern = "^[_A-Za-z0-9-\\+]*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if(email.matches(pattern)){
            return true;
        } else {
            return false;
        }
    }

    // Check if password is valid, i.e must be greater than 6 characters
    // & contain upper case, lower case & numeric characters, Regular Expression
    // from https://www.mkyong.com/regular-expressions
    private boolean isPasswordValid(String password) {
        String pattern = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,})";
        if(password.matches(pattern)){
            return true;
        } else {
            return false;
        }
    }

    // Shows the loading screen progress bar.  This Method is taken from built in Android Studio
    // Login Activity
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            registrationView.setVisibility(show ? View.GONE : View.VISIBLE);
            registrationView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    registrationView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            registrationView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void addDatatoFirebase(String uID, String userEmail, String username, String phone, String userAddress, Double latitudeVal, Double longitudeVal, String accountType, String mImage) {
        // below 3 lines of code is used to set
        // data in our object class.


        user.setUID(uID);
        user.setEmail(userEmail);
        user.setName(username);
        user.setPhoneNumber(phone);
        user.setAddress(userAddress);
        user.setLatitude(latitudeVal);
        user.setLongitude(longitudeVal);
        user.setAccountType(accountType);


        // we are use add value event listener method
        // which is called with database reference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // inside the method of on Data change we are setting
                // our object class to our database reference.
                // data base reference will sends data to firebase.
                //databaseReference.setValue(user);

                // Specify unique value, i.e. Primary Key, for User record and add this user to Firebase Database
                //String key = databaseReference.push().getKey();
               //String userName = userEmail.substring(0, userEmail.indexOf("."));
                databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(user);


                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User userSnapshotValue = userSnapshot.getValue(User.class);
                    userList.add(userSnapshotValue);
                }

                // after adding this data we are showing toast message.
                //Toast.makeText(RegistrationActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                Toast.makeText(RegistrationActivity.this, "Failed to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });


    }
}
