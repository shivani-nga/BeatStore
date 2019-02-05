package com.makehitmusic.hiphopbeats.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.model.BeatsObject;
import com.makehitmusic.hiphopbeats.model.BeatsViewHolder;
import com.makehitmusic.hiphopbeats.model.ProducersObject;
import com.makehitmusic.hiphopbeats.model.ProducersViewHolder;

import java.util.List;

public class TabbedProducersAdapter extends RecyclerView.Adapter<ProducersViewHolder> {

    private Context context;
    private List<ProducersObject> allProducers;

    public TabbedProducersAdapter(Context context, List<ProducersObject> allProducers) {
        this.context = context;
        this.allProducers = allProducers;
    }

    @Override
    public ProducersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.producers_list_layout, parent, false);
        return new ProducersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProducersViewHolder holder, int position) {
        ProducersObject producers = allProducers.get(position);
        holder.producerTitle.setText(producers.getProducerTitle());
        holder.producerAuthor.setText(producers.getProducerAuthor());
    }

    @Override
    public int getItemCount() {
        return allProducers.size();
    }
}
