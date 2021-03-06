package com.example.pathum.MusicPlayer.Fragments;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.example.pathum.MusicPlayer.DataLoader.Album_loader;
import com.example.pathum.MusicPlayer.Model.Album;
import com.example.pathum.MusicPlayer.R;
import com.example.pathum.MusicPlayer.Utils.SpacesItemDecoration;
import com.example.pathum.MusicPlayer.Utils.utils;
import com.example.pathum.MusicPlayer.adapter.AlbumAdapter;
import com.example.pathum.MusicPlayer.adapter.SongAdapter;

import java.util.ArrayList;

import static com.example.pathum.MusicPlayer.Utils.utils.getscreenSize;


/**
 * A simple {@link Fragment} subclass.
 */
public class album_fragment extends Fragment {

    private RecyclerView recView;
    private AlbumAdapter adapter;
    private Album_loader albumLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        recView = (RecyclerView) inflater.inflate(R.layout.album_fragment, container, false);

        Toast.makeText(recView.getContext(), String.valueOf(getscreenSize(recView.getContext())), Toast.LENGTH_LONG).show();


        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));


        albumLoader = new Album_loader(recView.getContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //   Toast.makeText(getActivity(), "Permission checking", Toast.LENGTH_SHORT).show();
            checkPermission();
        } else {

            adapter = new AlbumAdapter(albumLoader.AlbumList(), recView.getContext());
            recView.setAdapter(adapter);
        }


        return recView;


        //    return inflater.inflate(R.layout.fragment_album_fragment, container, false);
    }

    public ArrayList<Album> AlbumList() {

        ContentResolver contentResolver = recView.getContext().getContentResolver();
        Uri AlbumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor AlbumCursor = contentResolver.query(AlbumUri, new String[]{"_id", "album", "artist", "artist_id", "numsongs", "minyear"}, null, null, null);
        ArrayList<Album> Albums = new ArrayList<>();
        if (AlbumCursor != null && AlbumCursor.moveToFirst()) {
            do {
                Albums.add(new Album(AlbumCursor.getLong(0), AlbumCursor.getString(1), AlbumCursor.getString(2), AlbumCursor.getLong(3), AlbumCursor.getInt(4), AlbumCursor.getInt(5)));

            } while (AlbumCursor.moveToNext());
            if (AlbumCursor != null) {
                AlbumCursor.close();
            }
        }


        return Albums;
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),

                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {//Can add more as per requirement

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);

        } else {
            adapter = new AlbumAdapter(albumLoader.AlbumList(), recView.getContext());
            recView.setAdapter(adapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //   Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
                    adapter = new AlbumAdapter(albumLoader.AlbumList(), recView.getContext());
                    recView.setAdapter(adapter);

                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                    //checkPermission();
                }
                return;
            }
        }
    }
}
