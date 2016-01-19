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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pillsoft.piller.Adapter.ChangelogsCardAdapter;

public class ChangelogsActivity extends AppCompatActivity {

    public static Context context;
    private DrawerLayout drawerLayout;
    private View content;
    private RecyclerView mRecyclerView;
    private ChangelogsCardAdapter mAdapter;
    private Shared s = Shared.getIt();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changelogs);

        context = getApplicationContext();
        initToolbar();
        content = findViewById(R.id.content);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerChangelogsCard);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ChangelogsCardAdapter(s.changelogList, R.layout.card_changelog);
        mRecyclerView.setAdapter(mAdapter);
        Log.d("CHANGELOG", "Elements number->" + s.changelogList.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

    }


}


