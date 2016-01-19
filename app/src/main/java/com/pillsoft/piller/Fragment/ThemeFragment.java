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
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pillsoft.piller.Adapter.ThemeAdapter;
import com.pillsoft.piller.MainActivity;
import com.pillsoft.piller.R;
import com.pillsoft.piller.Shared;

public class ThemeFragment extends Fragment {

    private static final String ARG_TYPE = "Type";
    private static MainActivity main;
    private Shared s = Shared.getIt();
    private RecyclerView mRecyclerView;
    private ThemeAdapter mAdapter;
    private String type = "";
    private OnFragmentInteractionListener mListener;

    public ThemeFragment() {
    }

    public static ThemeFragment newInstance(MainActivity mainActivity, String type) {
        ThemeFragment fragment = new ThemeFragment();
        main = mainActivity;
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        type = getArguments().getString(ARG_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recycler, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerMainCard);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(main));
        mAdapter = new ThemeAdapter(s.getThemesByType(type), R.layout.tile);
        mRecyclerView.setAdapter(mAdapter);
       /*if (!Shared.isIstalled(getString(R.string.rro_layers_manager), main)) {
            if (type.equals(Shared.RROTAG)) {
                new MaterialDialog.Builder(main)
                        .title(R.string.rro_dialog_nolayersmanager_title)
                        .content(R.string.rro_dialog_nolayersmanager_body)
                        .positiveText(R.string.rro_dialog_nolayersmanager_positive)
                        .negativeText(R.string.rro_dialog_nolayersmanager_negative)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getString(R.string.rro_layers_manager)));
                                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                main.getApplicationContext().startActivity(browserIntent);
                            }
                        }).show();
            }

        }*/
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
