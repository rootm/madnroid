package com.example.muvindu.recyclerdemo.adapter;


import android.content.ContentUris;
import android.content.Context;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.muvindu.recyclerdemo.Audio.music;

import com.example.muvindu.recyclerdemo.Fragments.BottomSheet_fragments;
import com.example.muvindu.recyclerdemo.Interface.recInterface;
import com.example.muvindu.recyclerdemo.Model.Song;
import com.example.muvindu.recyclerdemo.R;


import java.util.List;


import static com.example.muvindu.recyclerdemo.MusicService.setSonglist1;
import static com.example.muvindu.recyclerdemo.Services.MediaService.getListType;
import static com.example.muvindu.recyclerdemo.Services.MediaService.setSonglist;

import static com.example.muvindu.recyclerdemo.UI.player_main.btmPlayer;


/**
 * Created by Muvindu on 12/6/2016.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    public static List<Song> songList;
    private LayoutInflater inflater;
    private music Music = new music();
    private int currentID = 0;
    private Context contextView;
    private FragmentActivity fragment;
    private static long id;

    public static Uri getUri() {
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        Uri uri = ContentUris.withAppendedId(sArtworkUri, id);
        return uri;
    }
// private Boolean serviceBound=false;


    public SongAdapter(List<Song> list, Context context, FragmentActivity frag) {
        this.songList = list;
        setSonglist1(list, "all");
        contextView = context;
        this.fragment = frag;
        this.inflater = LayoutInflater.from(context);

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.song_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.songName.setText(song.getSongName());
        Typeface font = Typeface.createFromAsset(contextView.getAssets(), "Fonts/Aaargh.ttf");
        holder.songName.setTypeface(font);
        //holder.songName.setSelected(true);

        // final Uri sArtworkUri = Uri
        //       .parse("content://media/external/audio/albumart");

        //  Uri uri = ContentUris.withAppendedId(sArtworkUri,song.getAlbumId());
        //  Glide.with(holder.albumArt.getContext()).load(uri).centerCrop().into(holder.albumArt);
        // if (song.getAlbum()!=null){  holder.albumArt.setImageBitmap(song.getAlbum());}
        //Uri uri = ContentUris.withAppendedId(sArtworkUri,5);
        //

    }


    @Override
    public int getItemCount() {
        return songList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, recInterface {

        public TextView songName;
        public ImageView albumArt;
        public View container;
        public ImageView fav;
        public ImageButton option;


        public ViewHolder(final View itemView) {
            super(itemView);
            songName = (TextView) itemView.findViewById(R.id.songName);
            //  albumArt = (ImageView) itemView.findViewById(R.id.album_image);
            container = itemView.findViewById(R.id.songContainer);
            fav = (ImageView) itemView.findViewById(R.id.favButton);
            option = (ImageButton) itemView.findViewById(R.id.optionButton);
            container.setOnClickListener(this);
            fav.setOnClickListener(this);
            option.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.songContainer) {
                songClick(getAdapterPosition(), v);
            } else if (v.getId() == R.id.favButton) {
                favClick(getAdapterPosition(), v);

            } else if (v.getId() == R.id.optionButton) {
                optionClick(getAdapterPosition(), v);

            }
        }


        @Override
        public void songClick(int position, View v) {
            contextView = v.getContext();


            if (getListType() != "all") {
                setSonglist(songList, "all");
            }
            Music.playAudio(songList.get(position).getData(), position, contextView);


            Toast.makeText(v.getContext(), songList.get(position).getSongName(), Toast.LENGTH_SHORT).show();
            if (btmPlayer.getVisibility() == View.GONE) {
                btmPlayer.setVisibility(View.VISIBLE);
            }

        }


        @Override
        public void favClick(int position, View v) {
            Drawable notfav = ContextCompat.getDrawable(v.getContext(), android.R.drawable.btn_star_big_off);
            Drawable fav = ContextCompat.getDrawable(v.getContext(), android.R.drawable.btn_star_big_on);
            if (songList.get(position).getFavarite()) {
                //Toast.makeText(v.getContext(),"hi",Toast.LENGTH_SHORT).show();
                //  Glide.with(this).load(notfav).into(star);
                // star.setImageDrawable(notfav);
                songList.get(position).setFavarite(false);
            } else {
                //  star.setImageDrawable(fav);
                //  songList.get(position).setFavarite(true);
                //    Glide.with(star.getContext()).load(fav).into(star);
            }
        }

        @Override
        public void optionClick(int position, View v) {


            //BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(fragment);
            // View sheetView = fragment.getLayoutInflater().inflate(R.layout.song_options, null);
            //  mBottomSheetDialog.setContentView(sheetView);
            //  mBottomSheetDialog.show();

            Bundle bundle = new Bundle();
            bundle.putString("id", String.valueOf(songList.get(position).getId()));

            BottomSheet_fragments bottomSheet = new BottomSheet_fragments();
            bottomSheet.setArguments(bundle);
            bottomSheet.show(fragment.getSupportFragmentManager(), "Dialog");


        }
    }


}
