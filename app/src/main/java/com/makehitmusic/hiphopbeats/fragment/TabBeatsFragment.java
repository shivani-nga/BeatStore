package com.makehitmusic.hiphopbeats.fragment;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.adapter.CategoryAdapter;
import com.makehitmusic.hiphopbeats.adapter.PlayerAdapter;
import com.makehitmusic.hiphopbeats.adapter.TabbedBeatsAdapter;
import com.makehitmusic.hiphopbeats.model.BeatsObject;
import com.makehitmusic.hiphopbeats.model.Category;
import com.makehitmusic.hiphopbeats.model.CategoryResponse;
import com.makehitmusic.hiphopbeats.presenter.MediaPlayerHolder;
import com.makehitmusic.hiphopbeats.presenter.PlaybackInfoListener;
import com.makehitmusic.hiphopbeats.rest.ApiClient;
import com.makehitmusic.hiphopbeats.rest.ApiInterface;
import com.makehitmusic.hiphopbeats.view.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;

public class TabBeatsFragment extends Fragment {

    /** Tag for log messages */
    private static final String LOG_TAG = TabBeatsFragment.class.getName();

    ListView beatsListView;
    private int tab_position;

    public static final String TAG = "TabBeatsFragment";

    private final int CATEGORY_TAB = 1;
    private final int PRODUCERS_TAB = 2;
    private final int FAVORITES_TAB = 3;
    private final int LIBRARY_TAB = 4;

    private PlayerAdapter mPlayerAdapter;

    private MainActivity mMainActivity;

    public int categoryId;
    ArrayList<BeatsObject> beatsList;

    public TabBeatsFragment() {
        // Required empty public constructor
    }

    // Constructor for getting tab position
    public TabBeatsFragment(int tab_position, int category_id) {
        this.tab_position = tab_position;
        categoryId = category_id;
        Log.d("TabPosition[2]", String.valueOf(tab_position));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_tab_beats, container, false);

        // Find the ListView which will be populated with the beats data
        beatsListView = (ListView) rootView.findViewById(R.id.list_beats_record);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = rootView.findViewById(R.id.empty_view);
        beatsListView.setEmptyView(emptyView);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        // Category Tab is selected now
        if (tab_position == CATEGORY_TAB && categoryId != 0) {
            Log.d("CategoryID[TabBeatFrag]", String.valueOf(categoryId));
            Call<CategoryResponse> call = apiService.getBeatsDetails(categoryId, 114909, "false");
            call.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                    int statusCode = response.code();
                    beatsList = response.body().getBeatsResults();

                    beatsListView.setAdapter(new TabbedBeatsAdapter(getActivity(), beatsList));
                }

                @Override
                public void onFailure(Call<CategoryResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(LOG_TAG, t.toString());
                }
            });
        }
        // Library Tab is selected now
        else if (tab_position == LIBRARY_TAB) {
            Call<CategoryResponse> call = apiService.getBeatsDetails("true");
            call.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                    int statusCode = response.code();
                    beatsList = response.body().getBeatsResults();

                    beatsListView.setAdapter(new TabbedBeatsAdapter(getActivity(), beatsList));
                }

                @Override
                public void onFailure(Call<CategoryResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(LOG_TAG, t.toString());
                }
            });
        }
        // Favorites Tab is selected now
        else if (tab_position == FAVORITES_TAB) {
            Call<CategoryResponse> call = apiService.getBeatsDetails(114909, "false");
            call.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                    int statusCode = response.code();
                    beatsList = response.body().getBeatsResults();

                    beatsListView.setAdapter(new TabbedBeatsAdapter(getActivity(), beatsList));
                }

                @Override
                public void onFailure(Call<CategoryResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(LOG_TAG, t.toString());
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
        return rootView;
    }

    private void initializePlaybackController() {
        MediaPlayerHolder mMediaPlayerHolder = new MediaPlayerHolder(getActivity());
        Log.d(TAG, "initializePlaybackController: created MediaPlayerHolder");
        mMediaPlayerHolder.setPlaybackInfoListener(new PlaybackListener());
        mPlayerAdapter = mMediaPlayerHolder;
        Log.d(TAG, "initializePlaybackController: MediaPlayerHolder progress callback set");
    }

    public class PlaybackListener extends PlaybackInfoListener {

        @Override
        public void onDurationChanged(int duration) {
            Log.d(TAG, String.format("setPlaybackDuration: setMax(%d)", duration));
        }

        @Override
        public void onPositionChanged(int position) {
                Log.d(TAG, String.format("setPlaybackPosition: setProgress(%d)", position));
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

}
