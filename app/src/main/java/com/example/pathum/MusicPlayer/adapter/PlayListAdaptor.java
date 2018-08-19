package com.example.muvindu.recyclerdemo.adapter;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.example.muvindu.recyclerdemo.Interface.playList_interface;

import com.example.muvindu.recyclerdemo.R;
import com.example.muvindu.recyclerdemo.UI.PlayList_view;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class PlayListAdaptor extends RecyclerView.Adapter<PlayListAdaptor.ViewHolder> {
    private ArrayList<String> playLists = new ArrayList<String>();
    private LayoutInflater inflater;
    private Context contextView;


    public PlayListAdaptor(Cursor cursor, Context context) {
        String s = "";
        if (cursor != null) {
            cursor.moveToFirst();
            while (true) {
                s = cursor.getString(1);
                playLists.add(s);
                if (cursor.isLast()) {
                    break;
                }
                cursor.moveToNext();

            }
        }


        contextView = context;

        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.playlist_item, parent, false);

        return new PlayListAdaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = playLists.get(position);
        holder.playList_name.setText(name);
        Typeface font = Typeface.createFromAsset(contextView.getAssets(), "Fonts/Aaargh.ttf");
        holder.playList_name.setTypeface(font);
    }

    @Override
    public int getItemCount() {
        return playLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, playList_interface {

        public TextView playList_name;
        public View container;


        public ViewHolder(final View itemView) {
            super(itemView);
            playList_name = (TextView) itemView.findViewById(R.id.playList_name);
            //  albumArt = (ImageView) itemView.findViewById(R.id.album_image);
            container = itemView.findViewById(R.id.playListContainer);

            container.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.playListContainer) {
                playList_Click(getAdapterPosition(), v);
            }
        }


        @Override
        public void playList_Click(int position, View v) {
            contextView = v.getContext();

            Intent i = new Intent(contextView.getApplicationContext(), PlayList_view.class);
            i.putExtra("name", playLists.get(position));
            contextView.startActivity(i);


        }


    }


}
