package com.example.pathum.MusicPlayer.Fragments;

import android.Manifest;

import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.pathum.MusicPlayer.R;
import com.example.pathum.MusicPlayer.adapter.PlayListAdaptor;


import static com.example.pathum.MusicPlayer.Utils.PlayList.createPlaylist;
import static com.example.pathum.MusicPlayer.Utils.PlayList.queryPlaylists;
import static com.example.pathum.MusicPlayer.Utils.utils.getscreenSize;

public class playList_fragment extends Fragment {

    private RecyclerView recView;
    private PlayListAdaptor adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        recView = (RecyclerView) inflater.inflate(R.layout.playlist_fragment, container, false);

        Toast.makeText(recView.getContext(), String.valueOf(getscreenSize(recView.getContext())), Toast.LENGTH_LONG).show();
        recView.setLayoutManager(new LinearLayoutManager(recView.getContext()));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //   Toast.makeText(getActivity(), "Permission checking", Toast.LENGTH_SHORT).show();
            checkPermission();
        } else {

            adapter = new PlayListAdaptor(queryPlaylists(recView.getContext().getContentResolver()), recView.getContext());
            recView.setAdapter(adapter);
        }


        return recView;


        //    return inflater.inflate(R.layout.fragment_album_fragment, container, false);
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),

                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {//Can add more as per requirement

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);

        } else {
            //   createPlaylist(getActivity().getContentResolver(),"playLIst1");
            // createPlaylist(getActivity().getContentResolver(),"playLIst2");

            adapter = new PlayListAdaptor(queryPlaylists(recView.getContext().getContentResolver()), recView.getContext());
            recView.setAdapter(adapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // createPlaylist(getActivity().getContentResolver(),"playLIst1");
                    // createPlaylist(getActivity().getContentResolver(),"playLIst2");


                    adapter = new PlayListAdaptor(queryPlaylists(recView.getContext().getContentResolver()), recView.getContext());
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