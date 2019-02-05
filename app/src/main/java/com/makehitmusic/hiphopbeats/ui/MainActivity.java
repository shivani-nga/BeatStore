package com.makehitmusic.hiphopbeats.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.fragment.BeatProducersFragment;
import com.makehitmusic.hiphopbeats.fragment.BeatsFragment;
import com.makehitmusic.hiphopbeats.fragment.FavoritesFragment;
import com.makehitmusic.hiphopbeats.fragment.LibraryFragment;
import com.makehitmusic.hiphopbeats.fragment.MoreFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BeatsFragment.OnFragmentInteractionListener, BeatProducersFragment.OnFragmentInteractionListener,
        FavoritesFragment.OnFragmentInteractionListener,
        LibraryFragment.OnFragmentInteractionListener,
        MoreFragment.OnFragmentInteractionListener {

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FloatingActionButton fab;

    // tags used to attach the fragments
    private static final String TAG_BEATS = "beats";
    private static final String TAG_BEAT_PRODUCERS = "beat_producers";
    private static final String TAG_FAVORITES = "favorites";
    private static final String TAG_LIBRARY = "library";
    private static final String TAG_ACCOUNT_SETTINGS = "account_settings";
    public static String CURRENT_TAG = TAG_BEATS;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_titles);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_BEATS;
            loadHomeFragment();
        }
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // beats
                BeatsFragment beatsFragment = new BeatsFragment();
                return beatsFragment;
            case 1:
                // beat producers
                BeatProducersFragment beatProducersFragment = new BeatProducersFragment();
                return beatProducersFragment;
            case 2:
                // favorites
                FavoritesFragment favoritesFragment = new FavoritesFragment();
                return favoritesFragment;
            case 3:
                // library
                LibraryFragment libraryFragment = new LibraryFragment();
                return libraryFragment;

            case 4:
                // more options
                MoreFragment moreFragment = new MoreFragment();
                return moreFragment;
            default:
                return new BeatsFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_BEATS;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_beats) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_BEATS;
        } else if (id == R.id.nav_beat_producers) {
            navItemIndex = 1;
            CURRENT_TAG = TAG_BEAT_PRODUCERS;
        } else if (id == R.id.nav_favorites) {
            navItemIndex = 2;
            CURRENT_TAG = TAG_FAVORITES;
        } else if (id == R.id.nav_library) {
            navItemIndex = 3;
            CURRENT_TAG = TAG_LIBRARY;
        } else if (id == R.id.nav_account_settings) {
            navItemIndex = 4;
            CURRENT_TAG = TAG_ACCOUNT_SETTINGS;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        loadHomeFragment();
        return true;
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0||navItemIndex == 1)
            fab.show();
        else
            fab.hide();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
