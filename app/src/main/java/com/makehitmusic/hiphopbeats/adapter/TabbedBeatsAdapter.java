package com.makehitmusic.hiphopbeats.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.model.BeatsObject;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import java.util.ArrayList;

import static com.makehitmusic.hiphopbeats.utils.Url.BASE_URL;

public class TabbedBeatsAdapter extends ArrayAdapter<BeatsObject> {

    /** Tag for log messages */
    private static final String LOG_TAG = TabbedBeatsAdapter.class.getName();

    private Context context;
    private ArrayList<BeatsObject> allBeats;

    public TabbedBeatsAdapter(Context context, ArrayList<BeatsObject> allBeats) {
        super(context, 0, allBeats);
        this.context = context;
        this.allBeats = allBeats;
    }

    /**
     * Returns a list item view that displays information about the beat at the given position
     * in the list of beats.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.beats_list_item, parent, false);
        }

        // Find the beat at the given position in the list of beats
        BeatsObject currentBeat = getItem(position);

        // Find the TextView with view ID beat_name
        TextView beatName = (TextView) listItemView.findViewById(R.id.beat_name);
        // Display the beat_name of the current beat in that TextView
        beatName.setText(currentBeat.getItemName());

        // Find the TextView with view ID producer_name
        TextView producerName = (TextView) listItemView.findViewById(R.id.producer_name);
        // Display the producer_name of the current beat in that TextView
        producerName.setText(currentBeat.getProducerName());

        // Find the TextView with view ID genre_name
        TextView genreName = (TextView) listItemView.findViewById(R.id.genre_name);
        // Display the genre_name of the current beat in that TextView
        genreName.setText(currentBeat.getItemDescription());

        // Find the TextView with view ID duration
        TextView duration = (TextView) listItemView.findViewById(R.id.duration);
        // Display the duration of the current beat in that TextView
        duration.setText(currentBeat.getItemDuration());

        // Find the TextView with view ID beat_price
        TextView beatPrice = (TextView) listItemView.findViewById(R.id.beat_price);
        // Display the beat_price of the current beat in that TextView
        beatPrice.setText("$" + String.valueOf(currentBeat.getItemPrice()));

        // Find the ImageView with view ID beat_cover
        ImageView beatCover = (ImageView) listItemView.findViewById(R.id.beat_cover);
        // Display the beat_cover of the current beat in that ImageView
        if (!(currentBeat.getItemImageBig().equals(BASE_URL))) {
            Glide.with(context).load(currentBeat.getItemImageBig())
                    //.placeholder(R.drawable.twotone_library_music_24)
                    .transition(withCrossFade()).into(beatCover);
        }
        else if (!(currentBeat.getItemImageSmall().equals(BASE_URL))) {
            Glide.with(context).load(currentBeat.getItemImageSmall())
                    //.placeholder(R.drawable.twotone_library_music_24)
                    .transition(withCrossFade()).into(beatCover);
        }
        else {
            beatCover.setImageDrawable(context.getResources().getDrawable(R.drawable.rounded_border));
        }

        // Find the ImageView with view ID is_liked
        ImageView isLiked = (ImageView) listItemView.findViewById(R.id.is_liked);
        // Display the is_liked of the current beat in that ImageView
        if ("true".equals(currentBeat.getIsLiked())) {
            isLiked.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite_24));
        }
        else if("false".equals(currentBeat.getIsLiked())) {
            isLiked.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite_border_24));
        }

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}
