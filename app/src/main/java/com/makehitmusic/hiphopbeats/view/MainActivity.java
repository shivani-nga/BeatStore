package com.makehitmusic.hiphopbeats.view;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.fragment.BeatsFragment;
import com.makehitmusic.hiphopbeats.fragment.ProducersFragment;
import com.makehitmusic.hiphopbeats.fragment.CategoryFragment;
import com.makehitmusic.hiphopbeats.fragment.FavoritesFragment;
import com.makehitmusic.hiphopbeats.fragment.LibraryFragment;
import com.makehitmusic.hiphopbeats.fragment.MoreFragment;
import com.makehitmusic.hiphopbeats.model.BeatsObject;
import com.makehitmusic.hiphopbeats.model.FavouriteRequest;
import com.makehitmusic.hiphopbeats.model.FavouriteResponse;
import com.makehitmusic.hiphopbeats.rest.ApiClient;
import com.makehitmusic.hiphopbeats.rest.ApiInterface;
import com.makehitmusic.hiphopbeats.utils.MediaPlayerService;
import com.makehitmusic.hiphopbeats.utils.StorageUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.makehitmusic.hiphopbeats.utils.MediaPlayerService.CHANNEL_ID;
import static com.makehitmusic.hiphopbeats.utils.Url.BASE_URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CategoryFragment.OnFragmentInteractionListener, ProducersFragment.OnFragmentInteractionListener,
        FavoritesFragment.OnFragmentInteractionListener,
        LibraryFragment.OnFragmentInteractionListener,
        MoreFragment.OnFragmentInteractionListener,
        BeatsFragment.OnFragmentInteractionListener {

    public static final String TAG = "MainActivity";
    //public static final int MEDIA_RES_ID = R.raw.jazz_in_paris;

    //private PlayerAdapter mPlayerAdapter;
    private boolean mUserIsSeeking = false;

    private MediaPlayerService player;
    private Intent playIntent;
    boolean serviceBound = false;

    ArrayList<BeatsObject> audioList;

    ImageView mBeatCover;
    TextView mBeatName;
    TextView mProducerName;
    ImageView mPlayButton;
    ImageView mNextButton;

    ImageView mBeatCoverBig;
    TextView mBeatNameBig;
    TextView mProducerNameBig;
    ImageView mVolumeDown;
    SeekBar mVolumeSeekbar;
    ImageView mVolumeUp;
    ImageView mFavourite;
    TextView mBeatPrice;
    SeekBar mAudioSeekbar;
    TextView mCurrentTime;
    TextView mFullTime;
    ImageView mPreviousButtonBig;
    ImageView mPlayButtonBig;
    ImageView mNextButtonBig;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FloatingActionButton fab;

    // tags used to attach the fragments
    private static final String TAG_BEATS = "beats";
    private static final String TAG_SUB_BEATS = "sub_beats";
    private static final String TAG_BEAT_PRODUCERS = "beat_producers";
    private static final String LIST_BEAT_PRODUCERS = "list_beat_producers";
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

    public boolean isBeatsFragment = false;

    public boolean isPlaying = false;
    public BeatsObject currentBeat;
    private AudioManager audioManager = null;

    public static Bundle beatsData;
    public static int position;
    public static int tabPosition;
    public static int categoryId;
    public static String categoryName;
    public static int producerId;
    public static String producerName;
    public static String producerDescription;
    public static String producerImage;

    // Change to your package name
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.makehitmusic.hiphopbeats.PlayNewAudio";
    public static final String Broadcast_PLAY_AUDIO = "com.makehitmusic.hiphopbeats.PlayAudio";
    public static final String Broadcast_PAUSE_AUDIO = "com.makehitmusic.hiphopbeats.PauseAudio";
    public static final String Broadcast_PLAY_NEXT_AUDIO = "com.makehitmusic.hiphopbeats.PlayNextAudio";
    public static final String Broadcast_PLAY_PREVIOUS_AUDIO = "com.makehitmusic.hiphopbeats.PlayPreviousAudio";

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            //Toast.makeText(MainActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        createNotificationChannel();


//        //play the first audio in the ArrayList
//        playAudio(audioList.get(0).getData());

        //playAudio("https://upload.wikimedia.org/wikipedia/commons/6/6c/Grieg_Lyric_Pieces_Kobold.ogg");

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
            loadHomeFragment(false);
        }

        mBottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
        mNowPlaying = (LinearLayout) findViewById(R.id.now_playing);
        mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mSlidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    mBottomBar.animate().alpha(0.0f);
                    mBottomBar.setVisibility(View.GONE);
                    mNowPlaying.animate().alpha(1.0f);
                    mNowPlaying.setVisibility(View.VISIBLE);
                }
                else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
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
                mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        initializeUI();
        initializeSeekbar();
        //initializePlaybackController();
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
//        if (isChangingConfigurations() && mPlayerAdapter.isPlaying()) {
//            Log.d(TAG, "onStop: don't release MediaPlayer as screen is rotating & playing");
//        } else {
//            mPlayerAdapter.release();
//            Log.d(TAG, "onStop: release MediaPlayer");
//        }
    }

//    private void loadAudio(ArrayList<BeatsObject> queue) {
//        ContentResolver contentResolver = getContentResolver();
//
//        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
//        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
//        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);
//
//        if (cursor != null && cursor.getCount() > 0) {
//            audioList = new ArrayList<>();
//            while (cursor.moveToNext()) {
//                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
//                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
//                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
//                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
//
//                // Save to audioList
//                audioList.add(new BeatsObject(data, title, album, artist));
//            }
//        }
//        cursor.close();
//    }

    private void initializeUI() {

        final SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_login), Context.MODE_PRIVATE);
        int loginTypeInt = sharedPref.getInt("LoginType", 0);
        int userCode = sharedPref.getInt("UserCode", 0);
        final int userId = sharedPref.getInt("UserId", 0);

        mBeatCover = (ImageView) findViewById(R.id.current_beat_cover);
        mBeatName = (TextView) findViewById(R.id.current_beat_name);
        mProducerName = (TextView) findViewById(R.id.current_producer_name);
        mPlayButton = (ImageView) findViewById(R.id.button_play_pause);
        mNextButton = (ImageView) findViewById(R.id.button_next);
        mBeatCoverBig = (ImageView) findViewById(R.id.current_beat_cover_big);
        mBeatNameBig = (TextView) findViewById(R.id.current_beat_name_big);
        mProducerNameBig = (TextView) findViewById(R.id.current_producer_name_big);
        mVolumeDown = (ImageView) findViewById(R.id.volume_down);
        mVolumeUp = (ImageView) findViewById(R.id.volume_up);
        mVolumeSeekbar = (SeekBar) findViewById(R.id.volume_seekbar);
        mFavourite = (ImageView) findViewById(R.id.make_favorite);
        mBeatPrice = (TextView) findViewById(R.id.beat_price);
        mAudioSeekbar = (SeekBar) findViewById(R.id.audio_seekbar);
        mCurrentTime = (TextView) findViewById(R.id.current_time);
        mFullTime = (TextView) findViewById(R.id.full_time);
        mPreviousButtonBig = (ImageView) findViewById(R.id.previous_button);
        mPlayButtonBig = (ImageView) findViewById(R.id.play_button);
        mNextButtonBig = (ImageView) findViewById(R.id.next_button);

        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mVolumeSeekbar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            mVolumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));

            mVolumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onStopTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        mPlayButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (serviceBound) {
                            if (isPlaying) {
                                mPlayButton.setImageResource(R.drawable.round_play_arrow_white_36);
                                mPlayButtonBig.setImageResource(R.drawable.round_play_arrow_white_48);
                                isPlaying = false;

                                //Send a broadcast to the service -> PLAY_PAUSE_AUDIO
                                Intent broadcastIntent = new Intent(Broadcast_PAUSE_AUDIO);
                                sendBroadcast(broadcastIntent);
                            } else {
                                mPlayButton.setImageResource(R.drawable.round_pause_white_36);
                                mPlayButtonBig.setImageResource(R.drawable.round_pause_white_48);
                                isPlaying = true;

                                //Send a broadcast to the service -> PLAY_PAUSE_AUDIO
                                Intent broadcastIntent = new Intent(Broadcast_PLAY_AUDIO);
                                sendBroadcast(broadcastIntent);
                            }
                        }
                    }
                });
        mPlayButtonBig.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (serviceBound) {
                            if (isPlaying) {
                                mPlayButton.setImageResource(R.drawable.round_play_arrow_white_36);
                                mPlayButtonBig.setImageResource(R.drawable.round_play_arrow_white_48);
                                isPlaying = false;

                                //Send a broadcast to the service -> PLAY_PAUSE_AUDIO
                                Intent broadcastIntent = new Intent(Broadcast_PAUSE_AUDIO);
                                sendBroadcast(broadcastIntent);
                            } else {
                                mPlayButton.setImageResource(R.drawable.round_pause_white_36);
                                mPlayButtonBig.setImageResource(R.drawable.round_pause_white_48);
                                isPlaying = true;

                                //Send a broadcast to the service -> PLAY_PAUSE_AUDIO
                                Intent broadcastIntent = new Intent(Broadcast_PLAY_AUDIO);
                                sendBroadcast(broadcastIntent);
                            }
                        }
                    }
                });
        mNextButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Send a broadcast to the service -> PLAY_NEXT_AUDIO
                        Intent broadcastIntent = new Intent(Broadcast_PLAY_NEXT_AUDIO);
                        sendBroadcast(broadcastIntent);
                    }
                });
        mNextButtonBig.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Send a broadcast to the service -> PLAY_NEXT_AUDIO
                        Intent broadcastIntent = new Intent(Broadcast_PLAY_NEXT_AUDIO);
                        sendBroadcast(broadcastIntent);
                    }
                });
        mPreviousButtonBig.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Send a broadcast to the service -> PLAY_NEXT_AUDIO
                        Intent broadcastIntent = new Intent(Broadcast_PLAY_PREVIOUS_AUDIO);
                        sendBroadcast(broadcastIntent);
                    }
                });

        mFavourite.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                        FavouriteRequest favouriteRequest = null;

                        if (userId == 0) {
                            Toast.makeText(MainActivity.this, "Oops, you need to sign in to do that", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putInt("LoginType", 0);
                            editor.putInt("UserCode", 0);
                            editor.putInt("UserId", 0);
                            editor.apply();

                            // Take the user to Login Screen
                            Intent i = new Intent(MainActivity.this, LoginScreen.class);
                            startActivity(i);
                            //Toast.makeText(getActivity(), "Signed Out: Successfully", Toast.LENGTH_SHORT).show();

                            finish();
                        } else {
                            if ("false".equals(currentBeat.getIsLiked())) {
                                favouriteRequest = new FavouriteRequest(String.valueOf(currentBeat.getItemId()), String.valueOf(userId), true);
                            } else if ("true".equals(currentBeat.getIsLiked())) {
                                favouriteRequest = new FavouriteRequest(String.valueOf(currentBeat.getItemId()), String.valueOf(userId), false);
                            }

                            Call<FavouriteResponse> call = apiService.postFavoiritingBeat(favouriteRequest);
                            call.enqueue(new Callback<FavouriteResponse>() {
                                @Override
                                public void onResponse(Call<FavouriteResponse> call, Response<FavouriteResponse> response) {
                                    int statusCode = response.code();
                                    final String message = response.body().getMessage();

                                    Log.d("Message", message);
                                    if (message.equals("Successfully Added")) {
                                        currentBeat.setIsLiked(true);
                                        mFavourite.setImageDrawable(getResources().getDrawable(R.drawable.favorite_24));
                                    }
                                    else if (message.equals("Successfully Removed")) {
                                        currentBeat.setIsLiked(false);
                                        mFavourite.setImageDrawable(getResources().getDrawable(R.drawable.favorite_border_24));
                                    }
                                }

                                @Override
                                public void onFailure(Call<FavouriteResponse> call, Throwable t) {
                                    // Log error here since request failed
                                    Log.e(TAG, t.toString());
                                }
                            });

                        }
                    }
                });
    }

    private void initializeNowPlaying() {
        mPlayButton.setImageResource(R.drawable.round_pause_white_36);
        mPlayButtonBig.setImageResource(R.drawable.round_pause_white_48);
        mBeatName.setText(currentBeat.getItemName());
        mProducerName.setText(currentBeat.getProducerName());
        mBeatNameBig.setText(currentBeat.getItemName());
        mProducerNameBig.setText(currentBeat.getProducerName());

        if (!(currentBeat.getItemImageBig().equals(BASE_URL))) {
            Glide.with(this).load(currentBeat.getItemImageBig())
                    //.placeholder(R.drawable.twotone_library_music_24)
                    .apply(new RequestOptions().placeholder(R.drawable.highlight_color).error(R.drawable.highlight_color))
                    .transition(withCrossFade()).into(mBeatCover);

            Glide.with(this).load(currentBeat.getItemImageBig())
                    //.placeholder(R.drawable.twotone_library_music_24)
                    .apply(new RequestOptions().placeholder(R.drawable.highlight_color).error(R.drawable.highlight_color))
                    .transition(withCrossFade()).into(mBeatCoverBig);
        }
        else if (!(currentBeat.getItemImageSmall().equals(BASE_URL))) {
            Glide.with(this).load(currentBeat.getItemImageSmall())
                    //.placeholder(R.drawable.twotone_library_music_24)
                    .apply(new RequestOptions().placeholder(R.drawable.highlight_color).error(R.drawable.highlight_color))
                    .transition(withCrossFade()).into(mBeatCover);

            Glide.with(this).load(currentBeat.getItemImageSmall())
                    //.placeholder(R.drawable.twotone_library_music_24)
                    .apply(new RequestOptions().placeholder(R.drawable.highlight_color).error(R.drawable.highlight_color))
                    .transition(withCrossFade()).into(mBeatCoverBig);
        }
        else if (!(currentBeat.getProducerImage().equals(BASE_URL))) {
            Glide.with(this).load(currentBeat.getProducerImage())
                    //.placeholder(R.drawable.twotone_library_music_24)
                    .apply(new RequestOptions().placeholder(R.drawable.highlight_color).error(R.drawable.highlight_color))
                    .transition(withCrossFade()).into(mBeatCover);

            Glide.with(this).load(currentBeat.getProducerImage())
                    //.placeholder(R.drawable.twotone_library_music_24)
                    .apply(new RequestOptions().placeholder(R.drawable.highlight_color).error(R.drawable.highlight_color))
                    .transition(withCrossFade()).into(mBeatCoverBig);
        }
        else {
            mBeatCover.setImageDrawable(this.getResources().getDrawable(R.drawable.default_cover));
        }
    }

    private void initializeSeekbar() {
        mAudioSeekbar.setOnSeekBarChangeListener(
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
                        //mPlayerAdapter.seekTo(userSelectedPosition);
                    }
                });
    }

    public void playAudio(ArrayList<BeatsObject> audioList) {
        //Check if service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudio(audioList);
            storage.storeAudioIndex(0);

            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioList and audioIndex to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudio(audioList);
            storage.storeAudioIndex(0);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
        isPlaying = true;
        currentBeat = audioList.get(0);
        initializeNowPlaying();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }

//    private void initializePlaybackController() {
//        MediaPlayerHolder mMediaPlayerHolder = new MediaPlayerHolder(this);
//        Log.d(TAG, "initializePlaybackController: created MediaPlayerHolder");
//        mMediaPlayerHolder.setPlaybackInfoListener(new PlaybackListener());
//        mPlayerAdapter = mMediaPlayerHolder;
//        Log.d(TAG, "initializePlaybackController: MediaPlayerHolder progress callback set");
//    }

//    public class PlaybackListener extends PlaybackInfoListener {
//
//        @Override
//        public void onDurationChanged(int duration) {
//            Log.d(TAG, String.format("setPlaybackDuration: setMax(%d)", duration));
//        }
//
//        @Override
//        public void onPositionChanged(int position) {
//            if (!mUserIsSeeking) {
//                Log.d(TAG, String.format("setPlaybackPosition: setProgress(%d)", position));
//            }
//        }
//
//        @Override
//        public void onStateChanged(@State int state) {
//            String stateToString = PlaybackInfoListener.convertStateToString(state);
//            onLogUpdated(String.format("onStateChanged(%s)", stateToString));
//        }
//
//        @Override
//        public void onPlaybackCompleted() {
//        }
//
//        @Override
//        public void onLogUpdated(String message) {
//        }
//    }

//    public void changeMusicContent(String beatName, String beatImageUrl, String producerName,
//                                   String isLiked, double beatPrice) {
//        mBeatName.setText(beatName);
//    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment(boolean flag) {
        // selecting appropriate nav menu item
        selectNavMenu();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if ((getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) && !isBeatsFragment) {
            drawer.closeDrawers();
            // show or hide the fab button
            toggleFab();
            return;
        }

        isBeatsFragment = flag;

        // set toolbar title
        setToolbarTitle();

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = null;
                if (isBeatsFragment) {
                    if (tabPosition == 1) {
                        fragment = new BeatsFragment(MainActivity.this);
                        fragment.setArguments(beatsData);
                    } else if (tabPosition == 2 || tabPosition == 5) {
                        fragment = new BeatsFragment(MainActivity.this);
                        fragment.setArguments(beatsData);
                    }
                } else {
                    fragment = getHomeFragment();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
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

    public void loadBeatsFragment(Bundle parameters) {
        isBeatsFragment = true;

        beatsData = parameters;
        position = parameters.getInt("position");
        tabPosition = parameters.getInt("tab_position");
        if (tabPosition == 1) {
            categoryId = Integer.parseInt(parameters.getString("category_id"));
            categoryName = parameters.getString("category_name");
        } else if (tabPosition == 2) {
            producerId = Integer.parseInt(parameters.getString("producer_id"));
            producerName = parameters.getString("producer_name");
            producerDescription = parameters.getString("producer_description");
            producerImage = parameters.getString("producer_image");
        } else if (tabPosition == 5) {
            producerId = parameters.getInt("producer_id");
            producerName = parameters.getString("producer_name");
            producerDescription = parameters.getString("producer_description");
            producerImage = parameters.getString("producer_image");
        }

        loadHomeFragment(true);
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // beats
                CategoryFragment categoryFragment = new CategoryFragment(MainActivity.this);
                return categoryFragment;
            case 1:
                // beat producers
                ProducersFragment producersFragment = new ProducersFragment(MainActivity.this);
                return producersFragment;
            case 2:
                // favorites
                FavoritesFragment favoritesFragment = new FavoritesFragment(MainActivity.this);
                return favoritesFragment;
            case 3:
                // library
                LibraryFragment libraryFragment = new LibraryFragment(MainActivity.this);
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
        if (isBeatsFragment) {
            if (tabPosition == 1) {
                getSupportActionBar().setTitle(categoryName);
            } else if (tabPosition == 2 || tabPosition == 5) {
                getSupportActionBar().setTitle(producerName);
            }
        } else {
            getSupportActionBar().setTitle(activityTitles[navItemIndex]);
        }
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
                if (isBeatsFragment) {
                    if (tabPosition == 2 || tabPosition == 5) {
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_BEAT_PRODUCERS;
                        loadHomeFragment(false);
                        isBeatsFragment = false;
                        return;
                    } else if (tabPosition == 1) {
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_BEATS;
                        loadHomeFragment(false);
                        isBeatsFragment = false;
                        return;
                    }
                } else {
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_BEATS;
                    loadHomeFragment(false);
                    return;
                }
            } else if (navItemIndex == 0 && isBeatsFragment) {
                if (tabPosition == 2 || tabPosition == 5) {
                    navItemIndex = 1;
                    CURRENT_TAG = TAG_BEAT_PRODUCERS;
                    loadHomeFragment(false);
                    isBeatsFragment = false;
                    return;
                } else if (tabPosition == 1) {
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_BEATS;
                    loadHomeFragment(false);
                    isBeatsFragment = false;
                    return;
                }
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
        int id = item.getItemId();
//
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_restore) {
            Toast.makeText(this, "Purchases have been restored", Toast.LENGTH_SHORT).show();
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

        loadHomeFragment(false);
        return true;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
