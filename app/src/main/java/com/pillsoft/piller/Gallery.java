/*
This file is part of Piller.
Piller is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
Piller is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with Piller. If not, see <http://www.gnu.org/licenses/>.
Copyright 2015, Giulio Fagioli, Lorenzo Salani
*/
package com.pillsoft.piller;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.ref.WeakReference;

import com.pillsoft.piller.Adapter.CustomPagerAdapter;
import com.pillsoft.piller.Fragment.ImageFragment;
import com.pillsoft.piller.Fragment.OnFragmentInteractionListener;

public class Gallery extends AppCompatActivity implements OnFragmentInteractionListener {

    private String themeName = "";
    private int index;
    private int [] resources = null;
    ViewPager pager;
    Gallery main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        main = this;
        themeName = getIntent().getStringExtra("themeName");
        index = getIntent().getIntExtra("index", -1);
        resources = getIntent().getIntArrayExtra("resources");
//        mCustomPagerAdapter = new CustomPagerAdapter(this,s.galleryList);
//        mViewPager = (ViewPager) findViewById(R.id.pager);
//        mViewPager.setAdapter(mCustomPagerAdapter);
//        mViewPager.setCurrentItem(index,true);

        MyPagerAdapter adapter = null;
        if (resources != null) {
             adapter = new MyPagerAdapter(
                    this.getSupportFragmentManager(), resources);
        }
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setCurrentItem(index);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {

        int[] resources = null;

        public MyPagerAdapter(android.support.v4.app.FragmentManager fm,int[] res) {
            super(fm);
            resources = res;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f;
            System.out.println(position);
            f = ImageFragment.newInstance(main, resources[position]);
            return f;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return resources[position]+"";
        }

        @Override
        public int getCount() {
            return resources.length;
        }
    }

}
