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

import java.util.List;

public class TabbedBeatsAdapter extends RecyclerView.Adapter<BeatsViewHolder> {

    private Context context;
    private List<BeatsObject> allBeats;

    public TabbedBeatsAdapter(Context context, List<BeatsObject> allBeats) {
        this.context = context;
        this.allBeats = allBeats;
    }

    @Override
    public BeatsViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.beats_list_layout, parent, false);
        return new BeatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BeatsViewHolder holder, int position) {
        BeatsObject beats = allBeats.get(position);
        holder.beatTitle.setText(beats.getBeatTitle());
        holder.beatAuthor.setText(beats.getBeatAuthor());
    }

    @Override
    public int getItemCount() {
        return allBeats.size();
    }
}
