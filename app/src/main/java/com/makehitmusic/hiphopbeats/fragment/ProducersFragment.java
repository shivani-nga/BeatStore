package com.makehitmusic.hiphopbeats.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.adapter.ProducersAdapter;
import com.makehitmusic.hiphopbeats.model.CategoryResponse;
import com.makehitmusic.hiphopbeats.model.ProducersObject;
import com.makehitmusic.hiphopbeats.rest.ApiClient;
import com.makehitmusic.hiphopbeats.rest.ApiInterface;
import com.makehitmusic.hiphopbeats.view.MainActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProducersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProducersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProducersFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    /** Tag for log messages */
    private static final String LOG_TAG = ProducersFragment.class.getName();

    ListView producersListView;
    private int tab_position;

    private final int CATEGORY_TAB = 1;
    private final int PRODUCERS_TAB = 2;
    private final int FAVORITES_TAB = 3;
    private final int LIBRARY_TAB = 4;

    public int categoryId;
    ArrayList<ProducersObject> producersList;

    ProgressBar progressBar;

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

    SearchView searchView;

    private Handler mHandler;

    public MainActivity mActivity;

    private int positionData;
    private ProducersObject producersObject;

    public ProducersFragment() {
        // Required empty public constructor
    }

    public ProducersFragment(MainActivity activity) {
        mActivity = activity;
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProducersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProducersFragment newInstance() {
        ProducersFragment fragment = new ProducersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mHandler = new Handler();
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_producers, container, false);

        final FragmentManager fragmentManager = getFragmentManager();

        // Find the ListView which will be populated with the producers data
        producersListView = (ListView) rootView.findViewById(R.id.list_producers_record);

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

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CategoryResponse> call = apiService.getProducers("true");
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                progressBar.setVisibility(View.GONE);
                int statusCode = response.code();
                producersList = response.body().getProducersResults();

                if (producersList.isEmpty()) {
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

                producersListView.setAdapter(new ProducersAdapter(getActivity(), producersList));
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

        // Setup the item click listener
        producersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                producersObject = producersList.get(position);
                positionData = position;

                Bundle arguments = new Bundle();
                arguments.putInt("position", positionData);
                arguments.putInt("tab_position", 2);
                arguments.putString("producer_id", producersObject.getProducerId());
                arguments.putString("producer_name", producersObject.getProducerName());
                arguments.putString("producer_description", producersObject.getProducerDescription());
                arguments.putString("producer_image", producersObject.getProducerImage());

                mActivity.loadBeatsFragment(arguments);

//                Intent intent = new Intent(getActivity(), BeatsActivity.class);
//                intent.putExtra("position", position);
//                intent.putExtra("tab_position", 2);
//                intent.putExtra("producer_id", producersObject.getProducerId());
//                intent.putExtra("producer_name", producersObject.getProducerName());
//                intent.putExtra("producer_description", producersObject.getProducerDescription());
//                intent.putExtra("producer_image", producersObject.getProducerImage());
//                getActivity().startActivity(intent);

//                // Sometimes, when fragment has huge data, screen seems hanging
//                // when switching between navigation menus
//                // So using runnable, the fragment is loaded with cross fade effect
//                // This effect can be seen in GMail app
//                Runnable mPendingRunnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        Fragment fragmentSubProducers = FragmentUtil.getFragmentByTagName(fragmentManager, "list_beat_producers");
//
//                        // Because fragment two has been popup from the back stack, so it must be null.
//                        if(fragmentSubProducers == null) {
//                            fragmentSubProducers = new BeatsFragment();
//                        }
//
//                        Bundle arguments = new Bundle();
//                        arguments.putInt("position", positionData);
//                        arguments.putInt("tab_position", 2);
//                        arguments.putString("producer_id", producersObject.getProducerId());
//                        arguments.putString("producer_name", producersObject.getProducerName());
//                        arguments.putString("producer_description", producersObject.getProducerDescription());
//                        arguments.putString("producer_image", producersObject.getProducerImage());
//                        fragmentSubProducers.setArguments(arguments);
//
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
//                                android.R.anim.fade_out);
//                        // Replace ProducersFragment one with BeatsFragment, the second fragment tag name is "list_beat_producers".
//                        // This action will remove ProducersFragment and add BeatsFragment.
//                        fragmentTransaction.replace(R.id.frame, fragmentSubProducers, "list_beat_producers");
//
//                        // Add fragment one in back stack.So it will not be destroyed. Press back menu can pop it up from the stack.
//                        fragmentTransaction.addToBackStack(null);
//
//                        fragmentTransaction.commit();
//
//                        FragmentUtil.printActivityFragmentList(fragmentManager);
//                    }
//                };
//
//                // If mPendingRunnable is not null, then add to the message queue
//                if (mPendingRunnable != null) {
//                    mHandler.post(mPendingRunnable);
//                }

            }
        });
        return rootView;

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

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Producer Name");

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Search", "Expanded");
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("Search", "Collapsed");
                return false;
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

        if (newText == null || newText.trim().isEmpty()) {
            resetSearch();
            return false;
        }

        ArrayList<ProducersObject> filteredValues = new ArrayList<ProducersObject>(producersList);
        for (ProducersObject value : producersList) {
            searchView.setQueryHint("Producer Name");
            if (!value.getProducerName().toLowerCase().contains(newText.toLowerCase())) {
                filteredValues.remove(value);
            }
        }

        producersListView.setAdapter(new ProducersAdapter(getActivity(), filteredValues));

        return false;
    }

    public void resetSearch() {
        producersListView.setAdapter(new ProducersAdapter(getActivity(), producersList));
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
