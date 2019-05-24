package com.makehitmusic.hiphopbeats.view;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.adapter.PlayerAdapter;
import com.makehitmusic.hiphopbeats.adapter.BeatsAdapter;
import com.makehitmusic.hiphopbeats.model.BeatsObject;
import com.makehitmusic.hiphopbeats.model.CategoryResponse;
import com.makehitmusic.hiphopbeats.presenter.MediaPlayerHolder;
import com.makehitmusic.hiphopbeats.presenter.PlaybackInfoListener;
import com.makehitmusic.hiphopbeats.rest.ApiClient;
import com.makehitmusic.hiphopbeats.rest.ApiInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class BeatsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    /** Tag for log messages */
    private static final String LOG_TAG = BeatsActivity.class.getName();

    public int categoryId;
    public int producerId;
    ListView beatsListView;
    private int tab_position;
    private String categoryName;
    private String producerName;
    private String producerDescription;
    private String producerImage;

    private final int CATEGORY_TAB = 1;
    private final int PRODUCERS_TAB = 2;
    private final int FAVORITES_TAB = 3;
    private final int LIBRARY_TAB = 4;

    private PlayerAdapter mPlayerAdapter;
    ArrayList<BeatsObject> beatsList;

    /** Adapter for the list of beats */
    private BeatsAdapter mAdapter;

    SearchView searchView;

    Switch switchView;

    ProgressBar progressBar;

    RelativeLayout emptyView;
    ImageView emptyImage;
    TextView emptyText;

    private boolean searchByBeats = true;
    private String searchedText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beats);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("category_id")) {
            categoryId = Integer.parseInt(getIntent().getStringExtra("category_id"));
        }
        if (getIntent().hasExtra("tab_position")) {
            tab_position = getIntent().getIntExtra("tab_position", 1);
        }
        if (getIntent().hasExtra("category_name")) {
            categoryName = getIntent().getStringExtra("category_name");
        }
        if (getIntent().hasExtra("producer_id")) {
            producerId = Integer.parseInt(getIntent().getStringExtra("producer_id"));
        }
        if (getIntent().hasExtra("producer_name")) {
            producerName = getIntent().getStringExtra("producer_name");
        }
        if (getIntent().hasExtra("producer_description")) {
            producerDescription = getIntent().getStringExtra("producer_description");
        }
        if (getIntent().hasExtra("producer_image")) {
            producerImage = getIntent().getStringExtra("producer_image");
        }
        if (tab_position == CATEGORY_TAB) {
            setTitle(categoryName);
        }
        if (tab_position == PRODUCERS_TAB) {
            setTitle(producerName);
        }

        // Find the ListView which will be populated with the beats data
        beatsListView = (ListView) findViewById(R.id.list_beats_record);

        progressBar = (ProgressBar) findViewById(R.id.loading_indicator);
        emptyView = (RelativeLayout) findViewById(R.id.empty_view);
        emptyImage = (ImageView) findViewById(R.id.empty_image);
        emptyText = (TextView) findViewById(R.id.empty_text);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyImage.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
            emptyImage.setImageResource(R.drawable.no_internet);
            emptyText.setText(R.string.no_internet);
        }

//        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
//        View emptyView = (View) findViewById(R.id.empty_view);
//        beatsListView.setEmptyView(emptyView);

        if (tab_position == PRODUCERS_TAB) {

            // Initialize Header
            LayoutInflater inflater = getLayoutInflater();
            ViewGroup header = (ViewGroup) inflater.inflate(R.layout.listview_header, beatsListView, false);

            ImageView producerCover = (ImageView) header.findViewById(R.id.producer_cover);
            TextView producerTitle = (TextView) header.findViewById(R.id.producer_title);
            ReadMoreTextView producerInfo = (ReadMoreTextView) header.findViewById(R.id.producer_info);

            if (!(producerImage.equals(""))) {
                Glide.with(this).load(producerImage)
                        //.placeholder(R.drawable.twotone_library_music_24)
                        .apply(new RequestOptions().placeholder(R.drawable.highlight_color).error(R.drawable.highlight_color))
                        .transition(withCrossFade()).into(producerCover);
            }
            else {
                producerCover.setImageDrawable(this.getResources().getDrawable(R.drawable.rounded_border));
            }

            producerTitle.setText(producerName);
            producerInfo.setText(producerDescription);

            // Add a header to the ListView
            beatsListView.addHeaderView(header);

        }

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new BeatsAdapter(this, beatsList);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        // Category Tab is selected now
        if (tab_position == CATEGORY_TAB && categoryId != 0) {
            Log.d("CategoryID[TabBeatFrag]", String.valueOf(categoryId));
            Call<CategoryResponse> call = apiService.getBeatsDetails(categoryId, 114909, "false", "true");
            call.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    int statusCode = response.code();
                    beatsList = response.body().getBeatsResults();

                    if (beatsList.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyImage.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                        emptyImage.setImageResource(R.drawable.empty_view);
                        emptyText.setText(R.string.empty_view_text);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        emptyImage.setVisibility(View.GONE);
                        emptyText.setVisibility(View.GONE);
                    }

                    beatsListView.setAdapter(new BeatsAdapter(BeatsActivity.this, beatsList));
                }

                @Override
                public void onFailure(Call<CategoryResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(LOG_TAG, t.toString());

                    progressBar.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    emptyImage.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.VISIBLE);
                    emptyImage.setImageResource(R.drawable.no_internet);
                    emptyText.setText(R.string.no_internet);
                }
            });
        }
        // Producer's Beat Tab is selected now
        else if (tab_position == PRODUCERS_TAB && producerId != 0) {
            Log.d("ProducerID[BeatAct]", String.valueOf(producerId));
            Call<CategoryResponse> call = apiService.getProducersDetails(producerId, 114909, "false", "true");
            call.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    int statusCode = response.code();
                    beatsList = response.body().getBeatsResults();

                    if (beatsList.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyImage.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                        emptyImage.setImageResource(R.drawable.empty_view);
                        emptyText.setText(R.string.empty_view_text);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        emptyImage.setVisibility(View.GONE);
                        emptyText.setVisibility(View.GONE);
                    }

                    beatsListView.setAdapter(new BeatsAdapter(BeatsActivity.this, beatsList));
                }

                @Override
                public void onFailure(Call<CategoryResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(LOG_TAG, t.toString());

                    if (beatsList.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyImage.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                        emptyImage.setImageResource(R.drawable.empty_view);
                        emptyText.setText(R.string.empty_view_text);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        emptyImage.setVisibility(View.GONE);
                        emptyText.setVisibility(View.GONE);
                    }
                }
            });
        }
        // Library Tab is selected now
        else if (tab_position == LIBRARY_TAB) {
            Call<CategoryResponse> call = apiService.getBeatsDetails("true", "true");
            call.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    int statusCode = response.code();
                    beatsList = response.body().getBeatsResults();

                    if (beatsList.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyImage.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                        emptyImage.setImageResource(R.drawable.empty_view);
                        emptyText.setText(R.string.empty_view_text);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        emptyImage.setVisibility(View.GONE);
                        emptyText.setVisibility(View.GONE);
                    }

                    beatsListView.setAdapter(new BeatsAdapter(BeatsActivity.this, beatsList));
                }

                @Override
                public void onFailure(Call<CategoryResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(LOG_TAG, t.toString());

                    if (beatsList.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyImage.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                        emptyImage.setImageResource(R.drawable.empty_view);
                        emptyText.setText(R.string.empty_view_text);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        emptyImage.setVisibility(View.GONE);
                        emptyText.setVisibility(View.GONE);
                    }
                }
            });
        }
        // Favorites Tab is selected now
        else if (tab_position == FAVORITES_TAB) {
            Call<CategoryResponse> call = apiService.getBeatsDetails(114909, "false", "true");
            call.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    int statusCode = response.code();
                    beatsList = response.body().getBeatsResults();

                    if (beatsList.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyImage.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                        emptyImage.setImageResource(R.drawable.empty_view);
                        emptyText.setText(R.string.empty_view_text);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        emptyImage.setVisibility(View.GONE);
                        emptyText.setVisibility(View.GONE);
                    }

                    beatsListView.setAdapter(new BeatsAdapter(BeatsActivity.this, beatsList));
                }

                @Override
                public void onFailure(Call<CategoryResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(LOG_TAG, t.toString());

                    if (beatsList.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyImage.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                        emptyImage.setImageResource(R.drawable.empty_view);
                        emptyText.setText(R.string.empty_view_text);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        emptyImage.setVisibility(View.GONE);
                        emptyText.setVisibility(View.GONE);
                    }
                }
            });
        }

        initializePlaybackController();

        // Setup the item click listener
        beatsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                BeatsObject beatsObject = beatsList.get(position);
                mPlayerAdapter.playFromList(beatsObject.getItemSamplePath());
                //mMainActivity.changeMusicContent(beatsObject.getItemName(), beatsObject.getItemImageBig(),
                //beatsObject.getProducerName(), beatsObject.getIsLiked(), beatsObject.getItemPrice());
            }
        });

    }

    private void initializePlaybackController() {
        MediaPlayerHolder mMediaPlayerHolder = new MediaPlayerHolder(BeatsActivity.this);
        Log.d(LOG_TAG, "initializePlaybackController: created MediaPlayerHolder");
        mMediaPlayerHolder.setPlaybackInfoListener(new BeatsActivity.PlaybackListener());
        mPlayerAdapter = mMediaPlayerHolder;
        Log.d(LOG_TAG, "initializePlaybackController: MediaPlayerHolder progress callback set");
    }

    public class PlaybackListener extends PlaybackInfoListener {

        @Override
        public void onDurationChanged(int duration) {
            Log.d(LOG_TAG, String.format("setPlaybackDuration: setMax(%d)", duration));
        }

        @Override
        public void onPositionChanged(int position) {
            Log.d(LOG_TAG, String.format("setPlaybackPosition: setProgress(%d)", position));
        }

        @Override
        public void onStateChanged(@State int state) {
        }

        @Override
        public void onPlaybackCompleted() {
        }

        @Override
        public void onLogUpdated(String message) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final MenuItem switchItem = menu.findItem(R.id.action_toggle);
        searchView = (SearchView) searchItem.getActionView();
        switchView = (Switch) switchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Beat Name");

//        if (tab_position == CATEGORY_TAB) {
//            switchItem.setVisible(true);
//            switchView.setVisibility(View.VISIBLE);
//        } else if (tab_position == PRODUCERS_TAB) {
//            switchItem.setVisible(false);
//            switchView.setVisibility(View.GONE);
//        }

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Search", "Expanded");
                if (tab_position == CATEGORY_TAB) {
                    switchItem.setVisible(true);
                } else if (tab_position == PRODUCERS_TAB) {
                    switchItem.setVisible(false);
                }
                switchItem.setChecked(false);
                switchView.setChecked(false);
                searchByBeats = true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("Search", "Collapsed");
                switchItem.setVisible(false);
                switchItem.setChecked(false);
                switchView.setChecked(false);
                searchByBeats = true;
                return false;
            }
        });

        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("Switch", "On");
                    searchByBeats = false;
                    searchView.setQueryHint("Producer Name");
                    performSearch(searchedText);
                } else {
                    Log.d("Switch", "Off");
                    searchByBeats = true;
                    searchView.setQueryHint("Beat Name");
                    performSearch(searchedText);
                }
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_restore) {
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        closeKeyboard();
        return true;
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        searchedText = newText;

        if (newText == null || newText.trim().isEmpty()) {
            resetSearch();
            return false;
        }

        searchedText = newText;
        ArrayList<BeatsObject> filteredValues = new ArrayList<BeatsObject>(beatsList);
        for (BeatsObject value : beatsList) {
            if (searchByBeats) {
                searchView.setQueryHint("Beat Name");
                if (!value.getItemName().toLowerCase().contains(newText.toLowerCase())) {
                    filteredValues.remove(value);
                }
            } else if (!searchByBeats) {
                searchView.setQueryHint("Producer Name");
                if (!value.getProducerName().toLowerCase().contains(newText.toLowerCase())) {
                    filteredValues.remove(value);
                }
            }
        }

        beatsListView.setAdapter(new BeatsAdapter(this, filteredValues));

        return false;
    }

    public void resetSearch() {
        beatsListView.setAdapter(new BeatsAdapter(this, beatsList));
    }

    public boolean performSearch(String searchedText) {
        if (searchedText == null || searchedText.trim().isEmpty()) {
            resetSearch();
            return false;
        }

        ArrayList<BeatsObject> filteredValues = new ArrayList<BeatsObject>(beatsList);
        for (BeatsObject value : beatsList) {
            if (searchByBeats) {
                searchView.setQueryHint("Beat Name");
                if (!value.getItemName().toLowerCase().contains(searchedText.toLowerCase())) {
                    filteredValues.remove(value);
                }
            } else if (!searchByBeats) {
                searchView.setQueryHint("Producer Name");
                if (!value.getProducerName().toLowerCase().contains(searchedText.toLowerCase())) {
                    filteredValues.remove(value);
                }
            }
        }

        beatsListView.setAdapter(new BeatsAdapter(this, filteredValues));

        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        Log.d("Search","Expanded");
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        Log.d("Search","Collapsed");
        return true;
    }
}
