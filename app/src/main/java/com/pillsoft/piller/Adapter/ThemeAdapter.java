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

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pillsoft.piller.MainActivity;
import com.pillsoft.piller.R;
import com.pillsoft.piller.Theme;
import com.pillsoft.piller.ThemeActivity;


import java.util.ArrayList;
import java.util.List;


public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ViewHolder> {

    private List<Theme> items;
    private List<String> names = new ArrayList<>();
    private int card_view;


    public ThemeAdapter(List<Theme> items, int card_view) {
        this.items = items;
        for (Theme t : items){
            names.add(t.getTheme_name());
        }
        this.card_view = card_view;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(card_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        viewHolder.name.setText(items.get(i).getTheme_name().toString());
        viewHolder.packageName = items.get(i).getTheme_package().toString();
        viewHolder.motto.setText(items.get(i).getTheme_motto().toString());
        viewHolder.card.setTag(items.get(i).getTheme_name().toString());
        viewHolder.card.setBackgroundColor(Color.parseColor(items.get(i).getTheme_color()));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView motto;
        public CardView card;
        public String packageName;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tile_name);
            motto = (TextView) itemView.findViewById(R.id.tile_motto);
            card = (CardView) itemView.findViewById(R.id.tile);
            packageName = "";
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.context, ThemeActivity.class);
                    intent.putExtra("PackageName",packageName);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.context.startActivity(intent);
                }
            });
        }

    }

}

