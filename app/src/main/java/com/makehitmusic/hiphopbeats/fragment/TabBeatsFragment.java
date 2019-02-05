package com.makehitmusic.hiphopbeats.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.adapter.TabbedBeatsAdapter;
import com.makehitmusic.hiphopbeats.model.BeatsObject;

import java.util.ArrayList;
import java.util.List;

public class TabBeatsFragment extends Fragment {

    public TabBeatsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab_beats, container, false);

        getActivity().setTitle("Beats");
        RecyclerView songRecyclerView = (RecyclerView)view.findViewById(R.id.beats_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        songRecyclerView.setLayoutManager(linearLayoutManager);
        songRecyclerView.setHasFixedSize(true);

        TabbedBeatsAdapter mAdapter = new TabbedBeatsAdapter(getActivity(), getTestData());
        songRecyclerView.setAdapter(mAdapter);
        return view;
    }

    public List<BeatsObject> getTestData() {
        List<BeatsObject> recentBeats = new ArrayList<BeatsObject>();
        recentBeats.add(new BeatsObject("Adele", "Someone Like You", ""));
        recentBeats.add(new BeatsObject("Adele", "Someone Like You", ""));
        recentBeats.add(new BeatsObject("Adele", "Someone Like You", ""));
        recentBeats.add(new BeatsObject("Adele", "Someone Like You", ""));
        recentBeats.add(new BeatsObject("Adele", "Someone Like You", ""));
        recentBeats.add(new BeatsObject("Adele", "Someone Like You", ""));
        return recentBeats;
    }

}
