package com.makehitmusic.hiphopbeats.view;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.adapter.PlayerAdapter;
import com.makehitmusic.hiphopbeats.fragment.ProducersFragment;
import com.makehitmusic.hiphopbeats.fragment.CategoryFragment;
import com.makehitmusic.hiphopbeats.fragment.FavoritesFragment;
import com.makehitmusic.hiphopbeats.fragment.LibraryFragment;
import com.makehitmusic.hiphopbeats.fragment.MoreFragment;
import com.makehitmusic.hiphopbeats.presenter.MediaPlayerHolder;
import com.makehitmusic.hiphopbeats.presenter.PlaybackInfoListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CategoryFragment.OnFragmentInteractionListener, ProducersFragment.OnFragmentInteractionListener,
        FavoritesFragment.OnFragmentInteractionListener,
        LibraryFragment.OnFragmentInteractionListener,
        MoreFragment.OnFragmentInteractionListener {

    public static final String TAG = "MainActivity";
    public static final int MEDIA_RES_ID = R.raw.jazz_in_paris;

    private SeekBar mSeekbarAudio;
    private PlayerAdapter mPlayerAdapter;
    private boolean mUserIsSeeking = false;

    ImageView mBeatCover;
    TextView mBeatName;
    ImageView mPlayButton;
    ImageView mPauseButton;
    ImageView mNextButton;

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

    private SlidingUpPanelLayout mSlidingLayout;
    LinearLayout mBottomBar;
    LinearLayout mNowPlaying;

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
        fab.hide();

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

        mBottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
        mNowPlaying = (LinearLayout) findViewById(R.id.now_playing);
        mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mSlidingLayout.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
                if (newState == PanelState.EXPANDED) {
                    mBottomBar.animate().alpha(0.0f);
                    mBottomBar.setVisibility(View.GONE);
                    mNowPlaying.animate().alpha(1.0f);
                    mNowPlaying.setVisibility(View.VISIBLE);
                }
                else if (newState == PanelState.COLLAPSED) {
                    mNowPlaying.animate().alpha(0.0f);
                    mNowPlaying.setVisibility(View.GONE);
                    mBottomBar.animate().alpha(1.0f);
                    mBottomBar.setVisibility(View.VISIBLE);
                }
            }
        });
        mSlidingLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingLayout.setPanelState(PanelState.COLLAPSED);
            }
        });

        initializeUI();
        initializeSeekbar();
        initializePlaybackController();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // mPlayerAdapter.loadMedia(MEDIA_RES_ID);
        Log.d(TAG, "onStart: create MediaPlayer");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isChangingConfigurations() && mPlayerAdapter.isPlaying()) {
            Log.d(TAG, "onStop: don't release MediaPlayer as screen is rotating & playing");
        } else {
            mPlayerAdapter.release();
            Log.d(TAG, "onStop: release MediaPlayer");
        }
    }

    private void initializeUI() {
        mBeatCover = (ImageView) findViewById(R.id.current_beat_cover);
        mBeatName = (TextView) findViewById(R.id.current_beat_name);
        mPlayButton = (ImageView) findViewById(R.id.button_play_pause);
        mNextButton = (ImageView) findViewById(R.id.button_next);
        mSeekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);

        mPlayButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //mPlayerAdapter.play();
                    }
                });
        mNextButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //mPlayerAdapter.reset();
                    }
                });
    }

    private void initializePlaybackController() {
        MediaPlayerHolder mMediaPlayerHolder = new MediaPlayerHolder(this);
        Log.d(TAG, "initializePlaybackController: created MediaPlayerHolder");
        mMediaPlayerHolder.setPlaybackInfoListener(new PlaybackListener());
        mPlayerAdapter = mMediaPlayerHolder;
        Log.d(TAG, "initializePlaybackController: MediaPlayerHolder progress callback set");
    }

    private void initializeSeekbar() {
        mSeekbarAudio.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int userSelectedPosition = 0;

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = true;
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            userSelectedPosition = progress;
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = false;
                        mPlayerAdapter.seekTo(userSelectedPosition);
                    }
                });
    }

    public class PlaybackListener extends PlaybackInfoListener {

        @Override
        public void onDurationChanged(int duration) {
            mSeekbarAudio.setMax(duration);
            Log.d(TAG, String.format("setPlaybackDuration: setMax(%d)", duration));
        }

        @Override
        public void onPositionChanged(int position) {
            if (!mUserIsSeeking) {
                mSeekbarAudio.setProgress(position);
                Log.d(TAG, String.format("setPlaybackPosition: setProgress(%d)", position));
            }
        }

        @Override
        public void onStateChanged(@State int state) {
            String stateToString = PlaybackInfoListener.convertStateToString(state);
            onLogUpdated(String.format("onStateChanged(%s)", stateToString));
        }

        @Override
        public void onPlaybackCompleted() {
        }

        @Override
        public void onLogUpdated(String message) {

        }
    }

    public void changeMusicContent(String beatName, String beatImageUrl, String producerName,
                                   String isLiked, double beatPrice) {
        mBeatName.setText(beatName);
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
                CategoryFragment categoryFragment = new CategoryFragment();
                return categoryFragment;
            case 1:
                // beat producers
                ProducersFragment producersFragment = new ProducersFragment();
                return producersFragment;
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
                return new CategoryFragment();
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

//        // close search view on back button pressed
//        if (!searchView.isIconified()) {
//            searchView.setIconified(true);
//            return;
//        }

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
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_search) {
//            return true;
//        }

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
        fab.hide();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
