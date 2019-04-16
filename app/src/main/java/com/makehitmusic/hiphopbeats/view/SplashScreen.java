package com.makehitmusic.hiphopbeats.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;

import com.makehitmusic.hiphopbeats.R;

public class SplashScreen extends Activity {

    // Identifier to check whether user is logged in or not
    public boolean isLoggedIn = false;

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                if (isLoggedIn) {
                    // User is already logged in so load MainActivity
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                } else if (!isLoggedIn) {
                    // User is not logged in so load LoginScreen
                    Intent i = new Intent(SplashScreen.this, LoginScreen.class);
                    startActivity(i);
                }

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if the user is already signed in and all required scopes are granted
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null && GoogleSignIn.hasPermissions(account, new Scope(Scopes.DRIVE_APPFOLDER))) {
            isLoggedIn = true;
        } else {
            isLoggedIn = false;
        }
    }

}
