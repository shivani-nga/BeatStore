package com.makehitmusic.hiphopbeats.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.makehitmusic.hiphopbeats.model.LoginRequest;
import com.makehitmusic.hiphopbeats.model.LoginResponse;
import com.makehitmusic.hiphopbeats.rest.ApiClient;
import com.makehitmusic.hiphopbeats.rest.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreen extends Activity implements
        View.OnClickListener {

    Button googleButton, skipButton;
    Button facebookButton;
    View loadingIndicator;

    GoogleSignInAccount account;
    String emailIdG = "";
    String userNameG = "";
    String firstNameG = "";
    String lastNameG = "";
    String userIdG = "";
    String idTokenG = "";
    String loginTypeG = "";
    String photoUrlG = "";
    String encodedImageG = "";

    String emailIdF = "";
    String userNameF = "";
    String firstNameF = "";
    String lastNameF = "";
    String userIdF = "";
    String idTokenF = "";
    String loginTypeF = "";
    String photoUrlF;
    String encodedImageF = "";

    private AccessToken mAccessToken;
    private CallbackManager mCallbackManager;

    private int loginTypeInt = 0;

    private static final String TAG = "IdTokenActivity";
    private static final int RC_GET_TOKEN = 9002;

    private static final String EMAIL = "email";
    private static final String PUBLIC_PROFILE = "public_profile";
    private static final String USER_BIRTHDAY = "user_birthday";
    private static final String USER_PHOTOS = "user_photos";
    private static final String USER_LOCATION = "user_location";
    private static final String AUTH_TYPE = "rerequest";


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

        mCallbackManager = CallbackManager.Factory.create();

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginTypeInt = 0;

                SharedPreferences.Editor loginPreferences = getSharedPreferences("LoginPreferences", MODE_PRIVATE).edit();
                loginPreferences.putInt("LoginType", 0);
                loginPreferences.putInt("UserCode", 0);
                loginPreferences.putInt("UserId", 0);
                loginPreferences.apply();

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
                loginTypeInt = 2;
                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);
                //LoginManager.getInstance().logInWithReadPermissions(LoginScreen.this, Arrays.asList(EMAIL, PUBLIC_PROFILE, USER_BIRTHDAY, USER_PHOTOS, USER_LOCATION));
                LoginManager.getInstance().logInWithReadPermissions(LoginScreen.this, Arrays.asList(EMAIL, PUBLIC_PROFILE));
            }
        });

        // Callback registration
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                setResult(RESULT_OK);
                mAccessToken = loginResult.getAccessToken();
                getUserProfile(mAccessToken);
                Log.d("LoginSuccess", mAccessToken.toString());
            }

            @Override
            public void onCancel() {
                // App code
                setResult(RESULT_CANCELED);
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
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

    @Override
    protected void onStart() {
        super.onStart();
        revokeAccess();
    }

    private void getUserProfile(final AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            Log.d("Response1", object.toString());
                            Log.d("Response2", response.getJSONObject().toString());
                            //You can fetch user info like this…
                            emailIdF = object.getString("email");
                            userNameF = object.getString("name");
                            if(userNameF.split("\\w+").length > 1) {
                                lastNameF = userNameF.substring(userNameF.lastIndexOf(" ") + 1);
                                firstNameF = userNameF.substring(0, userNameF.lastIndexOf(' '));
                            }
                            else {
                                firstNameF = userNameF;
                            }
                            userIdF = object.getString("id");
                            idTokenF = currentAccessToken.toString();
                            loginTypeF = "Facebook";
                            photoUrlF = object.getJSONObject("picture").
                                    getJSONObject("data").getString("url");

                            // Loading image using Glide
                            Glide.with(LoginScreen.this)
                                    .asBitmap()
                                    .load(photoUrlF)
                                    .into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @NonNull Transition<? super Bitmap> transition) {
                                            encodedImageF = convertToEncode(resource);
                                            Log.d("Image", encodedImageF);
                                            makeApiCall();
                                            Toast.makeText(LoginScreen.this, "Signed in as: " + userNameF, Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {

                                        }
                                    });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        request.executeAsync();
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
            account = completedTask.getResult(ApiException.class);
            emailIdG = account.getEmail();
            userNameG = account.getDisplayName();

            if(userNameG.split("\\w+").length > 1) {
                lastNameG = userNameG.substring(userNameG.lastIndexOf(" ") + 1);
                firstNameG = userNameG.substring(0, userNameG.lastIndexOf(' '));
            }
            else {
                firstNameG = userNameG;
            }

            userIdG = account.getId();
            idTokenG = account.getIdToken();
            loginTypeG = "Google";
            photoUrlG = String.valueOf(account.getPhotoUrl());

            // Loading image using Glide
            Glide.with(this)
                    .asBitmap()
                    .load(photoUrlG)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @NonNull Transition<? super Bitmap> transition) {
                            encodedImageG = convertToEncode(resource);
                            Log.d("Image", encodedImageG);
                            makeApiCall();
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });

        } catch (ApiException e) {
            Log.w(TAG, "handleSignInResult:error", e);
            updateUI(null);
        }
    }
    // [END handle_sign_in_result]

    public void makeApiCall() {

        if (loginTypeInt == 1) {
            Log.d("EmailId", emailIdG);
            Log.d("UserName", userNameG);
            Log.d("FirstName", firstNameG);
            Log.d("LastName", lastNameG);
            Log.d("UserId", userIdG);
            Log.d("TokenId", idTokenG);
            Log.d("PhotoURL", photoUrlG);
            Log.d("LoginType", loginTypeG);
            Log.d("UserImage", encodedImageG);
        } else if (loginTypeInt == 2) {
            Log.d("EmailId", emailIdF);
            Log.d("UserName", userNameF);
            Log.d("FirstName", firstNameF);
            Log.d("LastName", lastNameF);
            Log.d("UserId", userIdF);
            Log.d("TokenId", idTokenF);
            Log.d("PhotoURL", photoUrlF);
            Log.d("LoginType", loginTypeF);
            Log.d("UserImage", encodedImageF);
        }

        // TODO(developer): send ID Token to server and validate

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        LoginRequest loginRequest = null;

        if (loginTypeInt == 1) {
            loginRequest = new LoginRequest(emailIdG, userNameG, firstNameG, lastNameG, userIdG, idTokenG,
                    loginTypeG, encodedImageG);
        } else if (loginTypeInt == 2) {
            loginRequest = new LoginRequest(emailIdF, userNameF, firstNameF, lastNameF, userIdF, idTokenF,
                    loginTypeF, encodedImageF);
        }

        Call<LoginResponse> call = apiService.postUserLogin(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                int statusCode = response.code();
                final int userCode = response.body().getUserCode();
                final int userId = response.body().getUserId();

                SharedPreferences sharedPref = LoginScreen.this.getSharedPreferences(
                        getString(R.string.preference_login), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("LoginType", loginTypeInt);
                editor.putInt("UserCode", userCode);
                editor.putInt("UserId", userId);
                editor.apply();

                Log.d("UserCode", String.valueOf(userCode));
                Log.d("UserId", String.valueOf(userId));
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });

        if (loginTypeInt == 1) {
            updateUI(account);
        } else if (loginTypeInt == 2) {
            launchApp();
        }

    }

    public String convertToEncode(Bitmap inputBitmap) {

        String outputString = "";

        try {
            ByteArrayOutputStream oStream = new ByteArrayOutputStream();
            inputBitmap.compress(Bitmap.CompressFormat.JPEG, 90, oStream);

            byte[] byteArray = oStream.toByteArray();
            outputString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputString;
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        signOut();
                    }
                });
    }

    public void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (loginTypeInt == 2) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
        else if (loginTypeInt == 1) {
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

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
                loginTypeInt = 1;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
