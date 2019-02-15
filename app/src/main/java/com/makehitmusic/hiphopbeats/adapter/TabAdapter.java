package com.makehitmusic.hiphopbeats.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.makehitmusic.hiphopbeats.fragment.TabBeatsFragment;
import com.makehitmusic.hiphopbeats.fragment.TabProducersFragment;

public class TabAdapter extends FragmentPagerAdapter {

    private static final String LOG_TAG = TabAdapter.class.getSimpleName();

    private static final int FRAGMENT_COUNT = 2;

    public int tab_position;
    public int categoryId  = 0;

    public TabAdapter(FragmentManager fm, int position) {
        super(fm);
        this.tab_position = position;
        Log.d("TabPosition[1]", String.valueOf(tab_position));
    }

    public TabAdapter(FragmentManager fm, int position, int category_id) {
        super(fm);
        this.tab_position = position;
        categoryId = category_id;
        Log.d("TabPosition[1]", String.valueOf(tab_position));
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TabBeatsFragment(tab_position, categoryId);
            case 1:
                return new TabProducersFragment(tab_position, categoryId);
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
