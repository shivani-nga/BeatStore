package com.makehitmusic.hiphopbeats.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.makehitmusic.hiphopbeats.R;

public class BeatsViewHolder extends RecyclerView.ViewHolder {

    public TextView beatTitle;
    public TextView beatAuthor;
    public ImageView beatImage;

    public BeatsViewHolder(View itemView, TextView beatTitle, TextView beatAuthor, ImageView beatImage) {
        super(itemView);
        this.beatTitle = beatTitle;
        this.beatAuthor = beatAuthor;
        this.beatImage = beatImage;
    }

    public BeatsViewHolder(View itemView) {
        super(itemView);

        beatTitle = (TextView)itemView.findViewById(R.id.beat_title);
        beatAuthor = (TextView)itemView.findViewById(R.id.beat_author);
        beatImage = (ImageView)itemView.findViewById(R.id.beat_cover);
    }

}
