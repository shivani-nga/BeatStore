package com.makehitmusic.hiphopbeats.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.makehitmusic.hiphopbeats.fragment.TabBeatsFragment;
import com.makehitmusic.hiphopbeats.fragment.TabProducersFragment;

public class TabAdapter extends FragmentPagerAdapter {

    private static final String TAG = TabAdapter.class.getSimpleName();

    private static final int FRAGMENT_COUNT = 2;

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TabBeatsFragment();
            case 1:
                return new TabProducersFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Beats";
            case 1:
                return "Producers";
        }
        return null;
    }
}
