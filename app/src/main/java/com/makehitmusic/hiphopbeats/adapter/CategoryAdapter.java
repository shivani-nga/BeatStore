package com.makehitmusic.hiphopbeats.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.model.Category;
import com.makehitmusic.hiphopbeats.rest.GlideApp;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.makehitmusic.hiphopbeats.utils.Url.BASE_URL;
import static com.makehitmusic.hiphopbeats.utils.Url.CATEGORY_IMAGE_DEFAULT;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    /** Tag for log messages */
    private static final String LOG_TAG = CategoryAdapter.class.getName();

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }

    private Context mContext;
    private List<Category> categoryList;
    private int gridLayout;
    private RecyclerViewClickListener mListener;

    public int positionGlobal;
    public Category category;

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerViewClickListener mListener;
        public TextView categoryTitle;
        public ImageView thumbnail;
        public CardView cardView;

        public CategoryViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            categoryTitle = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            cardView = (CardView) view.findViewById(R.id.card_view);
            mListener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    public CategoryAdapter(List<Category> categoryList, int gridLayout, Context mContext, RecyclerViewClickListener listener) {
        this.mContext = mContext;
        this.gridLayout = gridLayout;
        this.categoryList = categoryList;
        mListener = listener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_list_item, parent, false);

        return new CategoryViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, int position) {
        positionGlobal = position;
        category = categoryList.get(position);
        holder.categoryTitle.setText(category.getCategoryName());

        if (!(category.getCategoryImageLarge().equals(BASE_URL))) {
            GlideApp.with(mContext).load(category.getCategoryImageLarge())
                    //.placeholder(R.drawable.twotone_library_music_24)
                    .apply(new RequestOptions().placeholder(R.drawable.highlight_color).error(R.drawable.highlight_color))
                    .transition(withCrossFade()).into(holder.thumbnail);
        }
        else if (!(category.getCategoryImage().equals(BASE_URL))) {
            // loading cover using Glide library
            GlideApp.with(mContext).load(category.getCategoryImage())
                    //.placeholder(R.drawable.twotone_library_music_24)
                    .apply(new RequestOptions().placeholder(R.drawable.highlight_color).error(R.drawable.highlight_color))
                    .transition(withCrossFade()).into(holder.thumbnail);
        }
        else {
            GlideApp.with(mContext).load(CATEGORY_IMAGE_DEFAULT)
                    .transition(withCrossFade()).into(holder.thumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

}
