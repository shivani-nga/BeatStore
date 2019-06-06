package com.makehitmusic.hiphopbeats.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import android.widget.Toast;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.adapter.BeatsAdapter;
import com.makehitmusic.hiphopbeats.adapter.CategoryAdapter;
import com.makehitmusic.hiphopbeats.adapter.PlayerAdapter;
import com.makehitmusic.hiphopbeats.model.BeatsObject;
import com.makehitmusic.hiphopbeats.model.Category;
import com.makehitmusic.hiphopbeats.model.CategoryResponse;
import com.makehitmusic.hiphopbeats.presenter.MediaPlayerHolder;
import com.makehitmusic.hiphopbeats.presenter.PlaybackInfoListener;
import com.makehitmusic.hiphopbeats.rest.ApiClient;
import com.makehitmusic.hiphopbeats.rest.ApiInterface;
import com.makehitmusic.hiphopbeats.utils.MediaPlayerService;
import com.makehitmusic.hiphopbeats.view.BeatsActivity;
import com.makehitmusic.hiphopbeats.view.MainActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BeatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BeatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BeatsFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    /** Tag for log messages */
    private static final String LOG_TAG = BeatsFragment.class.getName();

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Context mContext;

    public BeatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BeatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BeatsFragment newInstance() {
        BeatsFragment fragment = new BeatsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Bundle arguments = getArguments();
        //categoryId = Integer.parseInt(arguments.getString("category_id"));
        tab_position = arguments.getInt("tab_position");
        if (tab_position == 1) {
            categoryId = Integer.parseInt(arguments.getString("category_id"));
            categoryName = arguments.getString("category_name");
        }else if (tab_position == 2) {
            producerId = Integer.parseInt(arguments.getString("producer_id"));
            producerName = arguments.getString("producer_name");
            producerDescription = arguments.getString("producer_description");
            producerImage = arguments.getString("producer_image");
        }

        //getActivity().setTitle(producerName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.activity_beats, container, false);

        // Find the ListView which will be populated with the beats data
        beatsListView = (ListView) rootView.findViewById(R.id.list_beats_record);

        progressBar = (ProgressBar) rootView.findViewById(R.id.loading_indicator);
        emptyView = (RelativeLayout) rootView.findViewById(R.id.empty_view);
        emptyImage = (ImageView) rootView.findViewById(R.id.empty_image);
        emptyText = (TextView) rootView.findViewById(R.id.empty_text);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

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
//        View emptyView = rootView.findViewById(R.id.empty_view);
//        producersListView.setEmptyView(emptyView);

        if (tab_position == PRODUCERS_TAB) {

            // Initialize Header
            LayoutInflater inflaterH = getLayoutInflater();
            ViewGroup header = (ViewGroup) inflaterH.inflate(R.layout.listview_header, beatsListView, false);

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

        // Create a new adapter that takes an empty list of beats as input
        mAdapter = new BeatsAdapter(getActivity(), beatsList, 0);

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

                    if (beatsList == null) {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyImage.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                        emptyImage.setImageResource(R.drawable.empty_view);
                        emptyText.setText(R.string.empty_view_text);
                        return;
                    } else if (beatsList.isEmpty()) {
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

                    beatsListView.setAdapter(new BeatsAdapter(getActivity(), beatsList, 0));
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

                    if (beatsList == null) {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyImage.setVisibility(View.VISIBLE);
                        emptyText.setVisibility(View.VISIBLE);
                        emptyImage.setImageResource(R.drawable.empty_view);
                        emptyText.setText(R.string.empty_view_text);
                        return;
                    } else if (beatsList.isEmpty()) {
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

                    beatsListView.setAdapter(new BeatsAdapter(getActivity(), beatsList, 0));
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

                    beatsListView.setAdapter(new BeatsAdapter(getActivity(), beatsList, 0));
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

                    beatsListView.setAdapter(new BeatsAdapter(getActivity(), beatsList, 0));
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
        return rootView;
    }

    private void initializePlaybackController() {
        MediaPlayerHolder mMediaPlayerHolder = new MediaPlayerHolder(getActivity());
        Log.d(LOG_TAG, "initializePlaybackController: created MediaPlayerHolder");
        mMediaPlayerHolder.setPlaybackInfoListener(new BeatsFragment.PlaybackListener());
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final MenuItem switchItem = menu.findItem(R.id.action_toggle);
        searchView = (SearchView) searchItem.getActionView();
        switchView = (Switch) switchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Beat Name");

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

        super.onCreateOptionsMenu(menu, inflater);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        closeKeyboard();
        return true;
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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

        beatsListView.setAdapter(new BeatsAdapter(getActivity(), filteredValues, 0));

        return false;
    }

    public void resetSearch() {
        beatsListView.setAdapter(new BeatsAdapter(getActivity(), beatsList, 0));
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

        beatsListView.setAdapter(new BeatsAdapter(getActivity(), filteredValues, 0));

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
