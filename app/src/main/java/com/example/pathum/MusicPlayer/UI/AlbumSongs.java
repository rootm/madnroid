package com.example.pathum.MusicPlayer.UI;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.pathum.MusicPlayer.DataLoader.Album_loader;
import com.example.pathum.MusicPlayer.Model.Song;
import com.example.pathum.MusicPlayer.R;
import com.example.pathum.MusicPlayer.adapter.AlbumSong_Adapter;
import com.example.pathum.MusicPlayer.adapter.SongAdapter;

import java.util.ArrayList;


import static com.example.pathum.MusicPlayer.Utils.utils.getscreenSize;

public class AlbumSongs extends AppCompatActivity {
    private RecyclerView recView;
    private AlbumSong_Adapter adapter;
    private Album_loader albumLoader;
    private ImageView albumImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_album_songs);
        setImage(intent.getLongExtra("id", -1));
        albumImage = (ImageView) findViewById(R.id.album_image_small);
        albumImage.getLayoutParams().height = (int) (getscreenSize(this) / 1.5);
        albumImage.getLayoutParams().width = (int) (getscreenSize(this) / 1.5);
        albumImage.requestLayout();

        recView = (RecyclerView) findViewById(R.id.AlbumSongs_view);
        recView.setLayoutManager(new LinearLayoutManager(recView.getContext()));
        adapter = new AlbumSong_Adapter(AlbumongList(intent.getLongExtra("id", -1)), recView.getContext());
        recView.setAdapter(adapter);


    }


    public ArrayList<Song> AlbumongList(long id) {

        ContentResolver contentResolver = recView.getContext().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, MediaStore.Audio.Media.IS_MUSIC + " !=0 and album_id =" + id, null, null);
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
                //  if (bm!=null){song.setAlbum(bm);}
                songs.add(song);

            } while (songCursor.moveToNext());
            // ;
            // }
        }


        return songs;
    }


    public void setImage(long id) {
        ImageView imageView = (ImageView) findViewById(R.id.album_image_small);
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        Uri uri = ContentUris.withAppendedId(sArtworkUri, id);
        Glide.with(this).load(uri).centerCrop().into(imageView);


    }

}
