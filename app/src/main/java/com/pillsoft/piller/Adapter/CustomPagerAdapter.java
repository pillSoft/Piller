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
package com.pillsoft.piller.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

import com.pillsoft.piller.R;
import com.pillsoft.piller.Shared;

import java.util.List;

public class CustomPagerAdapter extends PagerAdapter {

    private List<WeakReference<ImageView>>  list = null;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    Shared s = Shared.getIt();

    public CustomPagerAdapter(Context context,List<WeakReference<ImageView>> list) {
        mContext = context;
        this.list = list;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.pagerImageView);

        Drawable drawable = list.get(position).get().getDrawable();

        imageView.setImageBitmap(Bitmap.createScaledBitmap(((BitmapDrawable) drawable).getBitmap(), s.galleryImageWidth, s.galleryImageHeight, false));

        container.addView(list.get(position).get());
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
