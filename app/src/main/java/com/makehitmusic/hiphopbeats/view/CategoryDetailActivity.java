package com.makehitmusic.hiphopbeats.view;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;

import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.adapter.TabAdapter;

public class CategoryDetailActivity extends AppCompatActivity {

    /** Tag for log messages */
    private static final String LOG_TAG = CategoryDetailActivity.class.getName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    TabAdapter tabAdapter;
    private int[] tabIcons = {
            R.drawable.twotone_music_note_black_24,
            R.drawable.twotone_person_black_24
    };
    public int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        if (getIntent().hasExtra("category_id")) {
            categoryId = Integer.parseInt(getIntent().getStringExtra("category_id"));
        }

        tabLayout = (TabLayout)findViewById(R.id.tabs);
        viewPager = (ViewPager)findViewById(R.id.viewpager);

        tabAdapter = new TabAdapter(getSupportFragmentManager(), 1, categoryId);

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_restore) {
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }
}
