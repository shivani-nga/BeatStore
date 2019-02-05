package com.makehitmusic.hiphopbeats.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.makehitmusic.hiphopbeats.R;

public class ProducersViewHolder extends RecyclerView.ViewHolder {

    public TextView producerTitle;
    public TextView producerAuthor;
    public ImageView producerImage;

    public ProducersViewHolder(View itemView, TextView producerTitle, TextView producerAuthor, ImageView producerImage) {
        super(itemView);
        this.producerTitle = producerTitle;
        this.producerAuthor = producerAuthor;
        this.producerImage = producerImage;
    }

    public ProducersViewHolder(View itemView) {
        super(itemView);

        producerTitle = (TextView)itemView.findViewById(R.id.producer_title);
        producerAuthor = (TextView)itemView.findViewById(R.id.producer_author);
        producerImage = (ImageView)itemView.findViewById(R.id.producer_cover);
    }

}
