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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pillsoft.piller.Gallery;
import com.pillsoft.piller.MainActivity;
import com.pillsoft.piller.R;
import com.pillsoft.piller.Shared;
import com.squareup.picasso.Picasso;

public class ImageFragment extends Fragment {

    Shared s = Shared.getIt();
    private static Gallery main;
    private OnFragmentInteractionListener mListener;
    private static int res;

    public static ImageFragment newInstance(Gallery g, int r) {
        ImageFragment fragment = new ImageFragment();
        main = g;
        res = r;
        return fragment;
    }

    public ImageFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_image, container, false);

        ImageView image = (ImageView) v.findViewById(R.id.imageGallery);

        //Picasso.with(main).load(R.drawable.ttt).into(image);

        image.setImageBitmap(s.decodeSampledBitmapFromResource(getResources(), res, s.galleryImageWidth/3, s.galleryImageWidth/3));

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
