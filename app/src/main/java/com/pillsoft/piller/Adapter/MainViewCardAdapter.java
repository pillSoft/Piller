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
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pillsoft.piller.ChangelogsActivity;
import com.pillsoft.piller.MainActivity;
import com.pillsoft.piller.MainViewCard;
import com.pillsoft.piller.R;

import java.util.ArrayList;
import java.util.List;

public class MainViewCardAdapter extends RecyclerView.Adapter<MainViewCardAdapter.ViewHolder> {

    public String action;
    private List<MainViewCard> items;
    private List<String> names = new ArrayList<>();
    private int card_view;

    public MainViewCardAdapter(List<MainViewCard> items, int card_view) {
        this.items = items;
        for (MainViewCard t : items) {
            names.add(t.getTitle());
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
        Log.d("Adapter", "Creating view");
        action = items.get(i).getActionButton();
        String color = items.get(i).getBackgroundColor();
        viewHolder.name.setText(items.get(i).getTitle().toString());
        viewHolder.motto.setText(items.get(i).getBody().toString());
        viewHolder.button.setTag(items.get(i).getActionButton());
        viewHolder.card.setTag(action);
        switch (action) {
            case "report":
                viewHolder.card.setClickable(false);
                viewHolder.button.setText(MainActivity.context.getString(R.string.report_card_mainactivity));
                viewHolder.button.setVisibility(View.VISIBLE);
                break;
            case "change":
                viewHolder.card.setClickable(false);
                viewHolder.button.setText(MainActivity.context.getString(R.string.more_changelog_card_mainactivity));
                viewHolder.button.setVisibility(View.VISIBLE);
                break;
            default:
                viewHolder.button.setText("Piller");
                viewHolder.button.setVisibility(View.GONE);
                break;
        }

        String backgroundImage = items.get(i).getBackgroundImage();
        if (backgroundImage != null) {
            int drawableId = MainActivity.context.getResources().getIdentifier(backgroundImage, "drawable", MainActivity.context.getPackageName());
            viewHolder.card.setBackgroundResource(drawableId);
        } else {
            viewHolder.card.setBackgroundColor(Color.parseColor(items.get(i).getBackgroundColor()));
        }

    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView motto;
        public CardView card;
        public Button button;


        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tile_name);
            motto = (TextView) itemView.findViewById(R.id.tile_motto);
            card = (CardView) itemView.findViewById(R.id.card);
            button = (Button) itemView.findViewById(R.id.button);

            card.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String tag = v.getTag().toString();
                    if ((tag != "") || (!tag.equals("report")) || (!tag.equals("change"))) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + tag));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MainActivity.context.startActivity(intent);
                    }
                }
            });

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = null;
                    String tag = v.getTag().toString();
                    switch (tag) {
                        case "report":
                            Log.d("MAIN", "Send EMail");
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + MainActivity.context.getString(R.string.developer_email)));
                            intent.putExtra(Intent.EXTRA_SUBJECT, MainActivity.context.getString(R.string.no_changelogs_email) + MainActivity.context.getString(R.string.app_name));
                            break;
                        case "change":
                            intent = new Intent(MainActivity.context, ChangelogsActivity.class);
                            break;


                    }
                    Log.d("Adapter", "Clicked");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.context.startActivity(intent);
                }
            });


        }

    }


}
