package com.makehitmusic.hiphopbeats.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.model.Category;

import java.util.List;

import static com.makehitmusic.hiphopbeats.utils.Url.BASE_URL;
import static com.makehitmusic.hiphopbeats.utils.Url.CATEGORY_IMAGE_DEFAULT;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context mContext;
    private List<Category> categoryList;
    private int gridLayout;

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryTitle;
        public ImageView thumbnail;

        public CategoryViewHolder(View view) {
            super(view);
            categoryTitle = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }


    public CategoryAdapter(List<Category> categoryList, int gridLayout, Context mContext) {
        this.mContext = mContext;
        this.gridLayout = gridLayout;
        this.categoryList = categoryList;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_list_item, parent, false);

        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryTitle.setText(category.getCategoryName());

        if (!(category.getCategoryImageLarge().equals(BASE_URL))) {
            Glide.with(mContext).load(category.getCategoryImageLarge())
                    //.placeholder(R.drawable.twotone_library_music_24)
                    .animate(android.R.anim.fade_in).into(holder.thumbnail);
        }
        else if (!(category.getCategoryImage().equals(BASE_URL))) {
            // loading cover using Glide library
            Glide.with(mContext).load(category.getCategoryImage())
                    //.placeholder(R.drawable.twotone_library_music_24)
                    .animate(android.R.anim.fade_in).into(holder.thumbnail);
        }
        else {
            Glide.with(mContext).load(CATEGORY_IMAGE_DEFAULT)
                    .animate(android.R.anim.fade_in).into(holder.thumbnail);
        }

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

}
