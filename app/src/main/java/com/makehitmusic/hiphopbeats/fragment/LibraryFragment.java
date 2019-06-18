package com.makehitmusic.hiphopbeats.fragment;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
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

import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.adapter.BeatsAdapter;
import com.makehitmusic.hiphopbeats.model.BeatsObject;
import com.makehitmusic.hiphopbeats.model.CategoryResponse;
import com.makehitmusic.hiphopbeats.rest.ApiClient;
import com.makehitmusic.hiphopbeats.rest.ApiInterface;
import com.makehitmusic.hiphopbeats.view.MainActivity;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.os.Environment.DIRECTORY_MUSIC;
import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LibraryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    /** Tag for log messages */
    private static final String LOG_TAG = LibraryFragment.class.getName();

    private static final int MY_PERMISSIONS_REQUEST_READ = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE = 2;
    private boolean READ_PERMISSION = false;
    private boolean WRITE_PERMISSION = false;
    public int count = 0;
    public boolean isDownloaded = false;
    public BeatsObject beatHolder;
    public File fileHolder;

    public ProgressDialog dialog;

    ListView beatsListView;
    private int tab_position;

    private final int CATEGORY_TAB = 1;
    private final int PRODUCERS_TAB = 2;
    private final int FAVORITES_TAB = 3;
    private final int LIBRARY_TAB = 4;

//    private PlayerAdapter mPlayerAdapter;

    public int categoryId;
    ArrayList<BeatsObject> beatsList;

    private long downloadID;

    ProgressBar progressBar;

    private int positionData;

    RelativeLayout emptyView;
    ImageView emptyImage;
    TextView emptyText;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Context mContext;

    private BeatsAdapter mAdapter;

    SearchView searchView;

    Switch switchView;

    public MainActivity mActivity;

    private boolean searchByBeats = true;
    private String searchedText = "";

    public LibraryFragment() {
        // Required empty public constructor
    }

    public LibraryFragment(MainActivity activity) {
        mActivity = activity;
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibraryFragment newInstance() {
        LibraryFragment fragment = new LibraryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setHasOptionsMenu(true);
        mContext.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                //Toast.makeText(mContext, "Download Completed", Toast.LENGTH_SHORT).show();
                isDownloaded = true;
                if (isDownloaded) {
                    dialog.dismiss();
                    startEmailIntent();
                } else if (!isDownloaded) {
                    Toast.makeText(mContext, "File could not be attached, try again later", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        long viewId = view.getId();
//
//        if (viewId == R.id.options) {
//            Toast.makeText(mContext, "Options clicked", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(mContext, "ListView clicked" + id, Toast.LENGTH_SHORT).show();
//        }
//    }

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

        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.preference_login), Context.MODE_PRIVATE);
        int loginTypeInt = sharedPref.getInt("LoginType", 0);
        int userCode = sharedPref.getInt("UserCode", 0);
        int userId = sharedPref.getInt("UserId", 0);
        Log.d("UserID", String.valueOf(userId));

        if (loginTypeInt != 0 && userCode != 0 && userId != 0) {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

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
                    beatsListView.setAdapter(new BeatsAdapter(getActivity(), beatsList, 1));
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
        } else {
            beatsList = null;
            progressBar.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyImage.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
            emptyImage.setImageResource(R.drawable.empty_view);
            String message = getString(R.string.empty_view_text) + "\n" + getString(R.string.empty_view_text_head);
            emptyText.setGravity(Gravity.CENTER_HORIZONTAL);
            emptyText.setText(message);

            beatsListView.setAdapter(null);
        }

//        initializePlaybackController();

        // Setup the item click listener
        beatsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                long viewId = view.getId();
                positionData = position;

                // Find the beat at the given position in the list of beats
                BeatsObject beatsObject = beatsList.get(position);

                if (viewId == R.id.options) {
                    //Toast.makeText(mContext, "Options clicked", Toast.LENGTH_SHORT).show();
                    openPopupMenu(beatsObject, view);
                } else if (viewId == R.id.producer_name) {
                    Bundle arguments = new Bundle();
                    arguments.putInt("position", positionData);
                    arguments.putInt("tab_position", 5);
                    arguments.putInt("producer_id", beatsObject.getProducerId());
                    arguments.putString("producer_name", beatsObject.getProducerName());
                    arguments.putString("producer_description", beatsObject.getProducerDescription());
                    arguments.putString("producer_image", beatsObject.getProducerImage());

                    mActivity.loadBeatsFragment(arguments);
                } else {
                    //Toast.makeText(mContext, "ListView clicked" + id, Toast.LENGTH_SHORT).show();

                    ArrayList<BeatsObject> playingQueue = new ArrayList<BeatsObject>(beatsList.subList(position, beatsList.size()));
                    mActivity.playAudio(playingQueue);

//                    mPlayerAdapter.playFromList(beatsObject.getItemSamplePath());
                    //mMainActivity.changeMusicContent(beatsObject.getItemName(), beatsObject.getItemImageBig(),
                    //beatsObject.getProducerName(), beatsObject.getIsLiked(), beatsObject.getItemPrice());
                }
            }
        });
        return rootView;

    }

    public void openPopupMenu(BeatsObject currentBeat, View v) {

        final BeatsObject clickedBeat = currentBeat;
        beatHolder = clickedBeat;
        checkReadPermission();
//        //creating a popup menu
//        PopupMenu popup = new PopupMenu(mContext, v);
//        //inflating menu from xml resource
//        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.library_menu, popup.getMenu());
//        //displaying the popup
//        popup.show();
//        beatHolder = clickedBeat;
//        //adding click listener
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.email_option:
//                        //handle email click
//                        checkReadPermission();
//                        break;
//                    case R.id.drive_option:
//                        //handle drive click
//                        checkReadPermission();
//                        break;
//                }
//                return false;
//            }
//        });

    }

    private void continueToOptions() {
        final String samplePath = beatHolder.getItemSamplePath();
        if (count >= 2) {
            try {
                File imageStorageDir = new File(getExternalStoragePublicDirectory(DIRECTORY_MUSIC), "MHMBeats");


                if (!imageStorageDir.exists()) {

                    imageStorageDir.mkdirs();
                }


                String imgExtension = ".mp3";

                String file = beatHolder.getItemName() + " - " + beatHolder.getProducerName() + imgExtension;

                File filePath = new File(getExternalStoragePublicDirectory(DIRECTORY_MUSIC)
                        + "/" + file);
                Log.d("FileExists", String.valueOf(filePath.exists()));
                fileHolder = filePath;

                if (!filePath.exists()) {
                    DownloadManager dm = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
                    Uri downloadUri = Uri.parse(samplePath);
                    DownloadManager.Request request = new DownloadManager.Request(downloadUri);

                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                            .setDestinationInExternalPublicDir(DIRECTORY_MUSIC + File.separator, file)
                            .setTitle(file).setDescription("BeatStore")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    downloadID = dm.enqueue(request);

                    dialog = ProgressDialog.show(mContext,"BeatStore", "Please wait....",true);

                    fileHolder = filePath;
                    //Toast.makeText(mContext, "Downloading...", Toast.LENGTH_LONG).show();

                } else {
                    startEmailIntent();
                }


            } catch (IllegalStateException ex) {
                Toast.makeText(mContext.getApplicationContext(), "Storage Error", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            } catch (Exception ex) {
                // just in case, it should never be called anyway
                Toast.makeText(mContext.getApplicationContext(), "Unable to attach beat", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(mContext, "Please grant access to use this functionality", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkReadPermission() {
        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // No explanation needed; request the permission
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ);
        } else {
            READ_PERMISSION = true;
            count += 1;
            checkWritePermission();
        }
    }

    private void checkWritePermission() {
        if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // No explanation needed; request the permission
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE);
        }
        else {
            WRITE_PERMISSION = true;
            count += 1;
            continueToOptions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    READ_PERMISSION = true;
                    count += 1;
                } else {
                    // permission denied, boo!
                    READ_PERMISSION = false;
                }
                checkWritePermission();
                return;
            }
            case MY_PERMISSIONS_REQUEST_WRITE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    WRITE_PERMISSION = true;
                    count += 1;
                } else {
                    // permission denied, boo!
                    WRITE_PERMISSION = false;
                }
                continueToOptions();
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void startEmailIntent() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
        emailIntent.setType("message/rfc822");
        //emailIntent.setDataAndType(Uri.parse("mailto:"), "message/rfc822");
        //emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"xxx@xxx.xxx"});
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Sent from my Android Device.");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                beatHolder.getItemName() + " from Beat Store");
        Uri beatURI = FileProvider.getUriForFile(getActivity(),
                mContext.getString(R.string.file_provider_authority),
                fileHolder);
        emailIntent.putExtra(Intent.EXTRA_STREAM, beatURI);
        mContext.startActivity(Intent.createChooser(emailIntent, "Send Beat"));
    }

//    private void initializePlaybackController() {
//        MediaPlayerHolder mMediaPlayerHolder = new MediaPlayerHolder(getActivity());
//        Log.d(LOG_TAG, "initializePlaybackController: created MediaPlayerHolder");
//        mMediaPlayerHolder.setPlaybackInfoListener(new LibraryFragment.PlaybackListener());
//        mPlayerAdapter = mMediaPlayerHolder;
//        Log.d(LOG_TAG, "initializePlaybackController: MediaPlayerHolder progress callback set");
//    }
//
//    public class PlaybackListener extends PlaybackInfoListener {
//
//        @Override
//        public void onDurationChanged(int duration) {
//            Log.d(LOG_TAG, String.format("setPlaybackDuration: setMax(%d)", duration));
//        }
//
//        @Override
//        public void onPositionChanged(int position) {
//            Log.d(LOG_TAG, String.format("setPlaybackPosition: setProgress(%d)", position));
//        }
//
//        @Override
//        public void onStateChanged(@State int state) {
//        }
//
//        @Override
//        public void onPlaybackCompleted() {
//        }
//
//        @Override
//        public void onLogUpdated(String message) {
//
//        }
//    }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(onDownloadComplete);
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
                switchItem.setVisible(true);
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

        beatsListView.setAdapter(new BeatsAdapter(getActivity(), filteredValues, 1));

        return false;
    }

    public void resetSearch() {
        beatsListView.setAdapter(new BeatsAdapter(getActivity(), beatsList, 1));
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

        beatsListView.setAdapter(new BeatsAdapter(getActivity(), filteredValues, 1));

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
