package com.makehitmusic.hiphopbeats.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.model.BeatsObject;
import com.makehitmusic.hiphopbeats.model.ProducersObject;
import com.makehitmusic.hiphopbeats.rest.GlideApp;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.makehitmusic.hiphopbeats.utils.Url.BASE_URL;

public class ProducersAdapter extends ArrayAdapter<ProducersObject> {

    /** Tag for log messages */
    private static final String LOG_TAG = ProducersAdapter.class.getName();

    private Context context;
    private ArrayList<ProducersObject> allProducers;

    public ProducersAdapter(Context context, ArrayList<ProducersObject> allProducers) {
        super(context, 0, allProducers);
        this.context = context;
        this.allProducers = allProducers;
    }

    /**
     * Returns a list item view that displays information about the producer at the given position
     * in the list of producers.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.producers_list_item, parent, false);
        }

        // Find the producer at the given position in the list of producers
        ProducersObject currentProducer = getItem(position);

        // Find the TextView with view ID producer_name
        TextView producerName = (TextView) listItemView.findViewById(R.id.producer_name);
        // Display the producer_name of the current producer in that TextView
        producerName.setText(currentProducer.getProducerName());

        // Find the TextView with view ID producer_description
        TextView producerDescription = (TextView) listItemView.findViewById(R.id.producer_description);
        // Display the producer_description of the current producer in that TextView
        producerDescription.setText(currentProducer.getProducerDescription());

        // Find the ImageView with view ID producer_cover
        ImageView producerCover = (ImageView) listItemView.findViewById(R.id.producer_cover);
        // Display the producer_cover of the current producer in that ImageView
        if (!(currentProducer.getProducerImage().equals(""))) {
            GlideApp.with(context).load(currentProducer.getProducerImage())
                    //.placeholder(R.drawable.twotone_library_music_24)
                    .apply(new RequestOptions().placeholder(R.drawable.highlight_color).error(R.drawable.highlight_color))
                    .transition(withCrossFade()).into(producerCover);
        }
        else {
            producerCover.setImageDrawable(context.getResources().getDrawable(R.drawable.default_cover));
        }

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}
