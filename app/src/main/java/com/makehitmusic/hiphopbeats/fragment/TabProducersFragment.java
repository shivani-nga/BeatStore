package com.makehitmusic.hiphopbeats.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.adapter.TabbedBeatsAdapter;
import com.makehitmusic.hiphopbeats.adapter.TabbedProducersAdapter;
import com.makehitmusic.hiphopbeats.model.BeatsObject;
import com.makehitmusic.hiphopbeats.model.CategoryResponse;
import com.makehitmusic.hiphopbeats.model.ProducersObject;
import com.makehitmusic.hiphopbeats.rest.ApiClient;
import com.makehitmusic.hiphopbeats.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TabProducersFragment extends Fragment {

    /** Tag for log messages */
    private static final String LOG_TAG = TabProducersFragment.class.getName();

    ListView beatsListView;

    private int tab_position;

    private final int CATEGORY_TAB = 1;
    private final int PRODUCERS_TAB = 2;
    private final int FAVORITES_TAB = 3;
    private final int LIBRARY_TAB = 4;

    public int categoryId;

    public TabProducersFragment() {
        // Required empty public constructor
    }

    // Constructor for getting tab position
    public TabProducersFragment(int tab_position, int category_id) {
        this.tab_position = tab_position;
        categoryId = category_id;
        Log.d("TabPosition[3]", String.valueOf(tab_position));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tab_beats, container, false);

        // Find the ListView which will be populated with the beats data
        beatsListView = (ListView) rootView.findViewById(R.id.list_beats_record);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = rootView.findViewById(R.id.empty_view);
        beatsListView.setEmptyView(emptyView);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        // Category Tab is selected now
        if (tab_position == CATEGORY_TAB && categoryId != 0) {
            Call<CategoryResponse> call = apiService.getBeatsDetails(categoryId, 114909, "false");
            call.enqueue(new Callback<CategoryResponse>() {
                @Override
                public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                    int statusCode = response.code();
                    ArrayList<BeatsObject> beatsList = response.body().getBeatsResults();

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
                    ArrayList<BeatsObject> beatsList = response.body().getBeatsResults();

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
                    ArrayList<BeatsObject> beatsList = response.body().getBeatsResults();

                    beatsListView.setAdapter(new TabbedBeatsAdapter(getActivity(), beatsList));
                }

                @Override
                public void onFailure(Call<CategoryResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(LOG_TAG, t.toString());
                }
            });
        }

        // Setup the item click listener
        beatsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getActivity(), "Music will be played when music functionality is added", Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

}
