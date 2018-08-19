package com.example.muvindu.recyclerdemo.UI;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.muvindu.recyclerdemo.Model.Song;
import com.example.muvindu.recyclerdemo.R;
import com.example.muvindu.recyclerdemo.adapter.AlbumSong_Adapter;
import com.example.muvindu.recyclerdemo.adapter.PlayListAdaptor;
import com.example.muvindu.recyclerdemo.adapter.PlayList_songsAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.example.muvindu.recyclerdemo.Utils.PlayList.listSongs;
import static com.example.muvindu.recyclerdemo.Utils.PlayList.queryPlaylists;
import static com.example.muvindu.recyclerdemo.Utils.utils.getscreenSize;

public class PlayList_view extends AppCompatActivity {
    private RecyclerView recView;
    private PlayList_songsAdapter adapter;
    private String playList_name;
    private List<Song> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_view);
        playList_name = getIntent().getStringExtra("name");

        recView = (RecyclerView) findViewById(R.id.ListSongs_view);
        recView.setLayoutManager(new LinearLayoutManager(recView.getContext()));
        this.songList = SongList();
        if (!this.songList.isEmpty()) {

            adapter = new PlayList_songsAdapter(this.songList, recView.getContext(), playList_name);
            recView.setAdapter(adapter);
        }
    }


    public ArrayList<Song> SongList() {


        Cursor songCursor = listSongs(playList_name, this.getContentResolver());
        ArrayList<Song> songs = new ArrayList<>();
        if (songCursor != null && songCursor.moveToFirst()) {
            int songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            long art;//=//songCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID);
            int dataID = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);


            do {
                // for (int i=0;i<5;i++){
                String data = songCursor.getString(dataID);
                art = Long.valueOf(songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID)));
                long currentId = songCursor.getLong(songId);
                String currentTitle = songCursor.getString(songTitle);
                Bitmap bm = null;
                String artist = songCursor.getString(songCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String album = songCursor.getString(songCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                int duration = songCursor.getInt(songCursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

                Song song = new Song();
                // Drawable d = ContextCompat.getDrawable(this,R.drawable.albumart);
                //     Bitmap bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.albumart);
                //     bm=getAlbumart(art);
                //      if (bm!=null){ song.setAlbum(bm);}
                //      else{song.setAlbum(bImage);}

                song.setData(data);
                song.setAlbumId(art);
                song.setSongName(currentTitle);
                song.setId(currentId);
                song.setAlbum(album);
                song.setArtist(artist);
                song.setDuration(duration);
                ;
                //  if (bm!=null){song.setAlbum(bm);}
                songs.add(song);

            } while (songCursor.moveToNext());
            // ;
            // }
        }


        return songs;
    }


}