package com.info3604.streetfoodtracker;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "";
    private EditText inputEmail, inputPassword;
    private Button btnLogin, btnContinueAsGuest;
    private TextView textRegister;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    SignInButton btnSignInGoogle;
    private final static int RC_SIGN_IN = 123;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth.AuthStateListener mAuthListener;
    boolean permissionGranted = false;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuthListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                finish();
            }

        };

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnContinueAsGuest = (Button) findViewById(R.id.btnContinueAsGuest);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textRegister = (TextView) findViewById(R.id.sign_in_button);
        btnSignInGoogle = (SignInButton) findViewById(R.id.sign_in_google);

        permissionGranted = requestSinglePermission(); //request permission for map usage in MainActivity

        btnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(permissionGranted) {
                    progressBar.setVisibility(View.VISIBLE);
                    signIn();
                }else{
                    progressBar.setVisibility(View.GONE);
                    requestSinglePermission();
                }
            }
        });

        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, RegistrationActivity.class));
            }
        });

        // Checking the email id and password is Empty
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (permissionGranted) {
                    inputEmail.setError(null);
                    inputPassword.setError(null);

                    // Variables for validation of inputted data
                    boolean cancel = false;
                    View focusView = null;

                    String email = inputEmail.getText().toString();
                    String password = inputPassword.getText().toString();

                    if (TextUtils.isEmpty(email)) {
                        inputEmail.setError(getString(R.string.error_field_required));
                        focusView = inputEmail;
                        cancel = true;
                    }
                    if (TextUtils.isEmpty(password)) {
                        inputPassword.setError(getString(R.string.error_field_required));
                        focusView = inputPassword;
                        cancel = true;
                    }

                    if (cancel) {
                        // On error register process is not completed and focus returns to first area of error
                        focusView.requestFocus();

                    } else {

                        progressBar.setVisibility(View.VISIBLE);

                        //authenticate user
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        progressBar.setVisibility(View.GONE);

                                        if (task.isSuccessful()) {
                                            // there was an error
                                            Log.d(TAG, "signInWithEmail:success");
                                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();

                                        } else {
                                            Log.d(TAG, "singInWithEmail:Fail");
                                            Toast.makeText(SignInActivity.this, getString(R.string.failed), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                });
                    }
                }else{
                    requestSinglePermission();
                }
            }

        });

        btnContinueAsGuest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        });

    }

    @Override
    public void onBackPressed() {
        if (mAuth.getCurrentUser() == null) {
            SignInActivity.super.onBackPressed();
            finish();
        }
    }

    private void signIn() {
        // clearing previous signin caches
        mGoogleSignInClient.signOut();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            //Toast.makeText(getApplicationContext(), " Starting activity", Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Auth Failed", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private boolean requestSinglePermission() {

        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        permissionGranted = true;
                        btnSignInGoogle.setEnabled(true);
                        btnLogin.setEnabled(true);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            permissionGranted = false;
                            btnSignInGoogle.setEnabled(false);
                            btnLogin.setEnabled(false);
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
