package com.example.muvindu.recyclerdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.muvindu.recyclerdemo.Audio.music;
import com.example.muvindu.recyclerdemo.Interface.playList_interface;
import com.example.muvindu.recyclerdemo.Interface.playList_songList;
import com.example.muvindu.recyclerdemo.Model.Song;
import com.example.muvindu.recyclerdemo.R;
import com.example.muvindu.recyclerdemo.UI.nowPlaying;

import java.util.ArrayList;
import java.util.List;

import static com.example.muvindu.recyclerdemo.MusicService.setSonglist1;
import static com.example.muvindu.recyclerdemo.Services.MediaService.getListType;
import static com.example.muvindu.recyclerdemo.Services.MediaService.setSonglist;

public class PlayList_songsAdapter extends RecyclerView.Adapter<PlayList_songsAdapter.ViewHolder> {
    public static List<Song> PlayList_songList;
    private LayoutInflater inflater;
    private music Music = new music();
    private static String listName;
    private Context contextView;


    public PlayList_songsAdapter(List<Song> list, Context context, String listName) {
        this.PlayList_songList = list;
        setSonglist1(list, "all");
        contextView = context;
        this.listName = listName;
        this.listName = listName;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.playlist_songs_item, parent, false);

        return new PlayList_songsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song song = PlayList_songList.get(position);
        holder.playList_SongName.setText(song.getSongName());
        Typeface font = Typeface.createFromAsset(contextView.getAssets(), "Fonts/Aaargh.ttf");
        holder.playList_SongName.setTypeface(font);
    }

    @Override
    public int getItemCount() {

        return PlayList_songList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, playList_songList {

        public TextView playList_SongName;
        public View container;


        public ViewHolder(final View itemView) {
            super(itemView);
            playList_SongName = (TextView) itemView.findViewById(R.id.listSong_name);
            //  albumArt = (ImageView) itemView.findViewById(R.id.album_image);
            container = itemView.findViewById(R.id.playListSongContainer);

            container.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.playListSongContainer) {
                playList_songClick(getAdapterPosition(), v);
            }
        }


        @Override
        public void playList_songClick(int position, View view) {
            if (getListType() != listName) {
                setSonglist(PlayList_songList, listName);
            }
            Music.playAudio(PlayList_songList.get(position).getData(), position, contextView);

            Intent intent = new Intent(contextView, nowPlaying.class);

            contextView.startActivity(intent);
        }
    }


}
