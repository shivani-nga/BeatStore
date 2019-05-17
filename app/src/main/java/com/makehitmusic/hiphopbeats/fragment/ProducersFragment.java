package com.makehitmusic.hiphopbeats.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ListView;
import android.widget.Switch;

import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.adapter.BeatsAdapter;
import com.makehitmusic.hiphopbeats.adapter.PlayerAdapter;
import com.makehitmusic.hiphopbeats.adapter.ProducersAdapter;
import com.makehitmusic.hiphopbeats.model.BeatsObject;
import com.makehitmusic.hiphopbeats.model.CategoryResponse;
import com.makehitmusic.hiphopbeats.model.ProducersObject;
import com.makehitmusic.hiphopbeats.presenter.MediaPlayerHolder;
import com.makehitmusic.hiphopbeats.presenter.PlaybackInfoListener;
import com.makehitmusic.hiphopbeats.rest.ApiClient;
import com.makehitmusic.hiphopbeats.rest.ApiInterface;
import com.makehitmusic.hiphopbeats.view.BeatsActivity;

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

    public ProducersFragment() {
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

        // Find the ListView which will be populated with the producers data
        producersListView = (ListView) rootView.findViewById(R.id.list_producers_record);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = rootView.findViewById(R.id.empty_view);
        producersListView.setEmptyView(emptyView);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CategoryResponse> call = apiService.getProducers("true");
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                int statusCode = response.code();
                producersList = response.body().getProducersResults();

                producersListView.setAdapter(new ProducersAdapter(getActivity(), producersList));
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(LOG_TAG, t.toString());
            }
        });

        // Setup the item click listener
        producersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ProducersObject producersObject = producersList.get(position);
                Intent intent = new Intent(getActivity(), BeatsActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("tab_position", 2);
                intent.putExtra("producer_id", producersObject.getProducerId());
                intent.putExtra("producer_name", producersObject.getProducerName());
                getActivity().startActivity(intent);
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
