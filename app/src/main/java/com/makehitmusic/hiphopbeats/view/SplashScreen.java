package com.makehitmusic.hiphopbeats.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.makehitmusic.hiphopbeats.R;

public class SplashScreen extends Activity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button buttonGetStarted;

    // Identifier to check whether user is logged in or not
    public boolean isLoggedIn = false;

    // Identifier to check whether user is logged in using Google or not
    public boolean isGoogleLoggedIn = false;

    // Identifier to check whether user is logged in using Facebook or not
    public boolean isFacebookLoggedIn = false;

    // Identifier to indicate that LoginScreen has to to be loaded
    public boolean launceLoginScreen = false;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_splash);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestEmail()
                .build();

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        buttonGetStarted = (Button) findViewById(R.id.btn_get_started);

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        buttonGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        // Check if the user is already signed in using Facebook

//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        if (accessToken != null && !accessToken.isExpired()) {
//            isFacebookLoggedIn = accessToken != null && !accessToken.isExpired();
//            launchHomeScreen();
//        } else {
//            isFacebookLoggedIn = false;
//        }

    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences sharedPref = SplashScreen.this.getSharedPreferences(
                getString(R.string.preference_login), Context.MODE_PRIVATE);
        int loginTypeInt = sharedPref.getInt("LoginType", 0);
        int userCode = sharedPref.getInt("UserCode", 0);
        int userId = sharedPref.getInt("UserId", 0);
        String userName = sharedPref.getString("UserName", "null");
        String userEmail = sharedPref.getString("UserEmail", "null");

        if (loginTypeInt != 0 && userCode != 0 && userId != 0) {
            isLoggedIn = true;
            if (loginTypeInt == 1) {
                checkGoogleLogin();
            } else if (loginTypeInt == 2) {
                //checkFacebookLogin();
                launchHomeScreen();
            }
            Log.d("LoginTypeInt", String.valueOf(loginTypeInt));
            Log.d("UserCode", String.valueOf(userCode));
            Log.d("UserId", String.valueOf(userId));
            Log.d("UserName", String.valueOf(userName));
            Log.d("UserEmail", String.valueOf(userEmail));
//            launchHomeScreen();
        } else {
            isLoggedIn = false;
            revokeAccess();
        }

//        // Check if the user is already signed in and all required scopes are granted
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account != null && GoogleSignIn.hasPermissions(account, new Scope(Scopes.DRIVE_APPFOLDER))) {
//            isGoogleLoggedIn = true;
//            launchHomeScreen();
//        } else {
//            isGoogleLoggedIn = false;
//            revokeAccess();
//        }
//
//        // Check if the user is already signed in using Facebook
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        if (accessToken != null && !accessToken.isExpired()) {
//            isFacebookLoggedIn = accessToken != null && !accessToken.isExpired();
//            launchHomeScreen();
//        } else {
//            isFacebookLoggedIn = false;
//            revokeAccess();
//        }

    }

    private void checkGoogleLogin() {
        // Check if the user is already signed in and all required scopes are granted
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null && GoogleSignIn.hasPermissions(account, new Scope(Scopes.DRIVE_APPFOLDER))) {
            isGoogleLoggedIn = true;
            launchHomeScreen();
        } else {
            isGoogleLoggedIn = false;
            revokeAccess();
        }
    }

    private void checkFacebookLogin() {
        // Check if the user is already signed in using Facebook
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            isFacebookLoggedIn = accessToken != null && !accessToken.isExpired();
            launchHomeScreen();
        } else {
            isFacebookLoggedIn = false;
            revokeAccess();
        }
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        if (isLoggedIn || isGoogleLoggedIn || isFacebookLoggedIn) {
            // User is already logged in so load MainActivity
            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(i);

            // close this activity
            finish();
        } else {
            // User is not logged in so load LoginScreen
            launceLoginScreen = true;
            revokeAccess();
        }
    }

    public void revokeAccess() {
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
                        resetEverything();
                    }
                });
    }

    public void resetEverything() {
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_login), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("LoginType", 0);
        editor.putInt("UserCode", 0);
        editor.putInt("UserId", 0);
        editor.putString("UserName", "null");
        editor.putString("UserEmail", "null");
        editor.apply();

        if (launceLoginScreen) {
            Intent i = new Intent(SplashScreen.this, LoginScreen.class);
            startActivity(i);

            // close this activity
            finish();
        }
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}
