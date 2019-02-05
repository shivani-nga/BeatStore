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
import com.makehitmusic.hiphopbeats.adapter.TabbedProducersAdapter;
import com.makehitmusic.hiphopbeats.model.BeatsObject;
import com.makehitmusic.hiphopbeats.model.ProducersObject;

import java.util.ArrayList;
import java.util.List;

public class TabProducersFragment extends Fragment {

    public TabProducersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab_producers, container, false);

        getActivity().setTitle("Producers");
        RecyclerView songRecyclerView = (RecyclerView)view.findViewById(R.id.producers_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        songRecyclerView.setLayoutManager(linearLayoutManager);
        songRecyclerView.setHasFixedSize(true);

        TabbedProducersAdapter mAdapter = new TabbedProducersAdapter(getActivity(), getTestData());
        songRecyclerView.setAdapter(mAdapter);
        return view;
    }

    public List<ProducersObject> getTestData() {
        List<ProducersObject> recentProducers = new ArrayList<ProducersObject>();
        recentProducers.add(new ProducersObject("Adele", "Someone Like You", ""));
        recentProducers.add(new ProducersObject("Adele", "Someone Like You", ""));
        recentProducers.add(new ProducersObject("Adele", "Someone Like You", ""));
        recentProducers.add(new ProducersObject("Adele", "Someone Like You", ""));
        recentProducers.add(new ProducersObject("Adele", "Someone Like You", ""));
        recentProducers.add(new ProducersObject("Adele", "Someone Like You", ""));
        return recentProducers;
    }

}
