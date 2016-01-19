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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pillsoft.piller.Adapter.SettingsAdapter;
import com.pillsoft.piller.MainActivity;
import com.pillsoft.piller.R;
import com.pillsoft.piller.Shared;

public class SettingsFragment extends Fragment {

    private static MainActivity main;
    private OnFragmentInteractionListener mListener;

    public static SettingsFragment newInstance(MainActivity mainActivity) {
        SettingsFragment fragment = new SettingsFragment();
        main = mainActivity;
        return fragment;
    }

    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        //Piller Card
        CardView pillCard = (CardView) v.findViewById(R.id.card);
        TextView pillNameCard = (TextView) v.findViewById(R.id.name_card);
        TextView pillBodyCard = (TextView) v.findViewById(R.id.body_card);
        Button pillPlusButton = (Button) v.findViewById(R.id.plus_button);
        Button pillGitButton = (Button) v.findViewById(R.id.github_button);
        // Devs card
        CardView devCard = (CardView) v.findViewById(R.id.card2);
        TextView devBodyText = (TextView) devCard.findViewById(R.id.body_card);
        TextView devTitleText = (TextView) devCard.findViewById(R.id.name_card);


        //Change color of piller Card
        pillCard.setBackgroundColor(ContextCompat.getColor(MainActivity.context, R.color.primary));
        pillBodyCard.setTextColor(Color.WHITE);
        pillNameCard.setTextColor(Color.WHITE);
        pillPlusButton.setTextColor(Color.WHITE);
        pillGitButton.setTextColor(Color.WHITE);
        // End change color for Piller Card

        pillCard.setTag("pillsoft");
        pillBodyCard.setText(Html.fromHtml(getString(R.string.body_card_piller)));
        devBodyText.setText(Html.fromHtml(getString(R.string.body_card_dev)));
        devTitleText.setText(Html.fromHtml(getString(R.string.name_card_dev)));
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
