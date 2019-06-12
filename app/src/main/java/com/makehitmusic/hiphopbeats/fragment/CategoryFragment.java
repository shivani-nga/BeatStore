package com.makehitmusic.hiphopbeats.fragment;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.joooonho.SelectableRoundedImageView;
import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.adapter.CategoryAdapter;
import com.makehitmusic.hiphopbeats.model.Category;
import com.makehitmusic.hiphopbeats.model.CategoryResponse;
import com.makehitmusic.hiphopbeats.presenter.JsonResponse;
import com.makehitmusic.hiphopbeats.rest.ApiClient;
import com.makehitmusic.hiphopbeats.rest.ApiInterface;
import com.makehitmusic.hiphopbeats.view.MainActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.makehitmusic.hiphopbeats.utils.Url.BASE_URL;
import static com.makehitmusic.hiphopbeats.utils.Url.YOUTUBE_IMAGE_LINK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    /** Tag for log messages */
    private static final String LOG_TAG = CategoryFragment.class.getName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageView youtubeBanner;
    ProgressBar progressBar;

    RelativeLayout emptyView;
    ImageView emptyImage;
    TextView emptyText;

    private OnFragmentInteractionListener mListener;
    private Context mContext;

    List<Category> categoryList;
    RecyclerView recyclerView;
    CategoryAdapter.RecyclerViewClickListener listener;

    SearchView searchView;

    public MainActivity mActivity;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public CategoryFragment(MainActivity activity) {
        mActivity = activity;
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance() {
        CategoryFragment fragment = new CategoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.loading_indicator);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        emptyView = (RelativeLayout) view.findViewById(R.id.empty_view);
        emptyImage = (ImageView) view.findViewById(R.id.empty_image);
        emptyText = (TextView) view.findViewById(R.id.empty_text);

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

        RecyclerViewHeader header = (RecyclerViewHeader) view.findViewById(R.id.header);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);

        header.attachTo(recyclerView);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        // Get YouTube video link and image and open video in YouTube App
        youtubeBanner = view.findViewById(R.id.banner);

        Glide.with(mContext).load(BASE_URL+YOUTUBE_IMAGE_LINK)
                //.placeholder(R.drawable.twotone_library_music_24)
                .apply(new RequestOptions().placeholder(R.drawable.youtube_logo).error(R.drawable.youtube_logo))
                .transition(withCrossFade()).into(youtubeBanner);

        youtubeBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call<JsonResponse> call = apiService.getYoutubeLink();
                call.enqueue(new Callback<JsonResponse>() {
                    @Override
                    public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                        int statusCode = response.code();
                        String youtubeLink = response.body().getYoutubeLink();
                        Uri youtubeUri = Uri.parse(youtubeLink);
                        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, youtubeUri);
                        startActivity(youtubeIntent);
                    }

                    @Override
                    public void onFailure(Call<JsonResponse> call, Throwable t) {
                        // Log error here since request failed
                        Log.e(LOG_TAG, t.toString());
                        Toast.makeText(getActivity(), "YouTube video can not be played", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Call<CategoryResponse> call = apiService.getCategory("true");
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                progressBar.setVisibility(View.GONE);
                int statusCode = response.code();
                categoryList = response.body().getCategoryResults();

                if (categoryList.isEmpty()) {
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

                listener = new CategoryAdapter.RecyclerViewClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Category clickedCategory = categoryList.get(position);

                        Bundle arguments = new Bundle();
                        arguments.putInt("position", position);
                        arguments.putInt("tab_position", 1);
                        arguments.putString("category_id", String.valueOf(clickedCategory.getCategoryId()));
                        arguments.putString("category_name", clickedCategory.getCategoryName());

                        mActivity.loadBeatsFragment(arguments);
//                        Intent intent = new Intent(getActivity(), BeatsActivity.class);
//                        intent.putExtra("position", position);
//                        intent.putExtra("tab_position", 1);
//                        intent.putExtra("category_name", clickedCategory.getCategoryName());
//                        intent.putExtra("category_id", String.valueOf(clickedCategory.getCategoryId()));
//                        Log.d("CategoryID[CatAdpt]", String.valueOf(clickedCategory.getCategoryId()));
//                        getActivity().startActivity(intent);
                    }
                };

                recyclerView.setAdapter(new CategoryAdapter(categoryList, R.layout.category_list_item, getActivity(), listener));
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());

                progressBar.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyImage.setVisibility(View.VISIBLE);
                emptyText.setVisibility(View.VISIBLE);
                emptyImage.setImageResource(R.drawable.no_internet);
                emptyText.setText(R.string.no_internet);
            }
        });

        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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
        searchView.setQueryHint("Category Name");

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

        List<Category> filteredValues = new ArrayList<Category>(categoryList);
        for (Category value : categoryList) {
            if (!value.getCategoryName().toLowerCase().contains(newText.toLowerCase())) {
                filteredValues.remove(value);
            }
        }

        recyclerView.setAdapter(new CategoryAdapter(filteredValues, R.layout.category_list_item, getActivity(), listener));

        return false;
    }

    public void resetSearch() {
        recyclerView.setAdapter(new CategoryAdapter(categoryList, R.layout.category_list_item, getActivity(), listener));
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
