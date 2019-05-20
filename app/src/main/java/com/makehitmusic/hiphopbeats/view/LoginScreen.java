package com.makehitmusic.hiphopbeats.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;

import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.adapter.CategoryAdapter;
import com.makehitmusic.hiphopbeats.model.Category;
import com.makehitmusic.hiphopbeats.model.CategoryResponse;
import com.makehitmusic.hiphopbeats.model.LoginResponse;
import com.makehitmusic.hiphopbeats.rest.ApiClient;
import com.makehitmusic.hiphopbeats.rest.ApiInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreen extends Activity implements
        View.OnClickListener {

    Button facebookButton, googleButton, skipButton;
    View loadingIndicator;
    Bitmap bm;

    private static final String TAG = "IdTokenActivity";
    private static final int RC_GET_TOKEN = 9002;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        facebookButton = (Button) findViewById(R.id.facebook_login);
        googleButton = (Button) findViewById(R.id.google_login);
        skipButton = (Button) findViewById(R.id.btn_not_now);

        loadingIndicator = findViewById(R.id.loading_indicator);

        googleButton.setOnClickListener(this);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);
                Intent i = new Intent(LoginScreen.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);
                Intent i = new Intent(LoginScreen.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        });

        // [START configure_signin]
        // Request only the user's ID token, which can be used to identify the
        // user securely to your backend. This will contain the user's basic
        // profile (name, profile picture URL, etc) so you should not need to
        // make an additional call to personalize your application.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestEmail()
                .build();
        // [END configure_signin]

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void getIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    private void refreshIdToken() {
        // Attempt to silently refresh the GoogleSignInAccount. If the GoogleSignInAccount
        // already has a valid token this method may complete immediately.
        //
        // If the user has not previously signed in on this device or the sign-in has expired,
        // this asynchronous branch will attempt to sign in the user silently and get a valid
        // ID token. Cross-device single sign on will occur in this branch.
        mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        try {
                            handleSignInResult(task);
                        } catch (IOException e) {
                            Log.e(TAG, "Problem making the HTTP request.", e);
                        }
                    }
                });
    }

    // [START handle_sign_in_result]
    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) throws IOException {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String emailId = account.getEmail();
            String userName = account.getDisplayName();

            String firstName= "";
            String lastName = "";
            if(userName.split("\\w+").length > 1) {
                lastName = userName.substring(userName.lastIndexOf(" ") + 1);
                firstName = userName.substring(0, userName.lastIndexOf(' '));
            }
            else {
                firstName = userName;
            }

            String userId = account.getId();
            String idToken = account.getIdToken();
            String loginType = "Google";
            String photoUrl = String.valueOf(account.getPhotoUrl());
            String encodedImage = "";

            // Loading image using Glide
            Glide.with(this)
                    .asBitmap()
                    .load(photoUrl)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            bm = resource;
                        }
                    });

//            HttpURLConnection con = null;
//            InputStream is = null;
//            try {
//                URL urlPhoto = new URL(photoUrl);
//
//                con = (HttpURLConnection)urlPhoto.openConnection();
//                con.setReadTimeout(10000 /* milliseconds */);
//                con.setConnectTimeout(15000 /* milliseconds */);
//                con.setRequestMethod("GET");
//                con.connect();
//
//                // If the request was successful (response code 200),
//                // then read the input stream and parse the response.
//                if (con.getResponseCode() == 200) {
//                    is = con.getInputStream();
//                    Bitmap bm = BitmapFactory.decodeStream(is);
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
//                    byte[] byteArrayImage = baos.toByteArray();
//                    encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
//                } else {
//                    Log.e(TAG, "Error response code: " + con.getResponseCode());
//                }
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//                Log.e(TAG, "Problem retrieving the earthquake JSON results.", e);
//            } finally {
//                if (con != null) {
//                    con.disconnect();
//                }
//                if (is != null) {
//                    // Closing the input stream could throw an IOException, which is why
//                    // the makeHttpRequest(URL url) method signature specifies than an IOException
//                    // could be thrown.
//                    is.close();
//                }
//            }

            Log.d("EmailId", emailId);
            Log.d("UserName", userName);
            Log.d("FirstName", firstName);
            Log.d("LastName", lastName);
            Log.d("UserId", userId);
            Log.d("TokenId", idToken);
            Log.d("PhotoURL", photoUrl);
            Log.d("LoginType", loginType);
            Log.d("EncodedImage", encodedImage);

            // TODO(developer): send ID Token to server and validate

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<LoginResponse> call = apiService.postUserLogin(emailId, userName, firstName, lastName,
                    userId, idToken, loginType, encodedImage);
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    int statusCode = response.code();
                    final int userCode = response.body().getUserCode();
                    final int userId = response.body().getUserId();
                    Log.d("UserCode", String.valueOf(userCode));
                    Log.d("UserId", String.valueOf(userId));
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(TAG, t.toString());
                }
            });

            updateUI(account);
        } catch (ApiException e) {
            Log.w(TAG, "handleSignInResult:error", e);
            updateUI(null);
        }
    }
    // [END handle_sign_in_result]

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateUI(null);
            }
        });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("RequestCode", String.valueOf(requestCode));
        if (requestCode == RC_GET_TOKEN) {
            // [START get_id_token]
            // This task is always completed immediately, there is no need to attach an
            // asynchronous listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                handleSignInResult(task);
            } catch (IOException e) {
                Log.e(TAG, "Problem making the HTTP request.", e);
            }
            // [END get_id_token]
        }
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            String idToken = account.getIdToken();
            Toast.makeText(this, "Signed in as: " + account.getDisplayName(), Toast.LENGTH_SHORT).show();
            googleButton.setFocusable(false);
            googleButton.setClickable(false);
            facebookButton.setFocusable(false);
            facebookButton.setClickable(false);
            launchApp();
        } else {
            Toast.makeText(this,"Unable to Sign In. Please try again after some time.", Toast.LENGTH_SHORT).show();
            googleButton.setFocusable(true);
            googleButton.setClickable(true);
            facebookButton.setFocusable(true);
            facebookButton.setClickable(true);
        }
    }

    public void launchApp() {
        // This method will be executed when the user is successfully logged in.
        // Start your app main activity
        Intent i = new Intent(LoginScreen.this, MainActivity.class);
        startActivity(i);

        // close this activity
        finish();
    }

    /**
     * Validates that there is a reasonable server client ID in strings.xml, this is only needed
     * to make sure users of this sample follow the README.
     */
    private void validateServerClientID() {
        String serverClientId = getString(R.string.server_client_id);
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in strings.xml, must end with " + suffix;

            Log.w(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_login:
                getIdToken();
                break;
//            case R.id.sign_out_button:
//                signOut();
//                break;
//            case R.id.disconnect_button:
//                revokeAccess();
//                break;
//            case R.id.button_optional_action:
//                refreshIdToken();
//                break;
        }
    }
}
