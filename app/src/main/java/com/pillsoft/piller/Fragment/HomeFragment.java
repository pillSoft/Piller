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
package com.pillsoft.piller.Fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pillsoft.piller.Adapter.MainViewCardAdapter;
import com.pillsoft.piller.MainActivity;
import com.pillsoft.piller.R;
import com.pillsoft.piller.Shared;

public class HomeFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Shared s = Shared.getIt();

    private RecyclerView mRecyclerView;
    private MainViewCardAdapter mAdapter;

    private static MainActivity main;

    private OnFragmentInteractionListener mListener;

    public static HomeFragment newInstance(MainActivity mainActivity) {
        HomeFragment fragment = new HomeFragment();
        main = mainActivity;
        return fragment;
    }

    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recycler, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerMainCard);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));

        mAdapter = new MainViewCardAdapter(s.mainCardViewList, R.layout.card);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
