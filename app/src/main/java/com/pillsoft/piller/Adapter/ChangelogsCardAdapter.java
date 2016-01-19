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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pillsoft.piller.Changelog;
import com.pillsoft.piller.ChangelogsActivity;
import com.pillsoft.piller.R;

import java.util.ArrayList;
import java.util.List;

public class ChangelogsCardAdapter extends RecyclerView.Adapter<ChangelogsCardAdapter.ViewHolder> {

    private List<Changelog> items;
    private List<String> names = new ArrayList<>();
    private int card_view;

    public ChangelogsCardAdapter(List<Changelog> items, int card_view) {
        this.items = items;
        for (Changelog t : items) {
            names.add(t.getDate());
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
        String date = items.get(i).getDate();
        String version = items.get(i).getVersionName();
        String description = items.get(i).getDescription();
        viewHolder.title.setText(ChangelogsActivity.context.getString(R.string.version_card_changelogs) + " " + version);
        viewHolder.date.setText(ChangelogsActivity.context.getString(R.string.date_card_changelogs) + " " + date);
        viewHolder.description.setText(description.replaceAll("\\\\n", "\n"));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public TextView date;


        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.changelog_title);
            description = (TextView) itemView.findViewById(R.id.description);
            date = (TextView) itemView.findViewById(R.id.date);
        }

    }


}
