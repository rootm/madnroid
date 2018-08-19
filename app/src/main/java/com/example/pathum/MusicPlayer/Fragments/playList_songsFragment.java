package com.example.muvindu.recyclerdemo.Fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.muvindu.recyclerdemo.R;
import com.example.muvindu.recyclerdemo.adapter.PlayListAdaptor;

import static com.example.muvindu.recyclerdemo.Utils.PlayList.queryPlaylists;
import static com.example.muvindu.recyclerdemo.Utils.utils.getscreenSize;

public class playList_songsFragment extends Fragment {
    private RecyclerView recView;
    private PlayListAdaptor adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        recView = (RecyclerView) inflater.inflate(R.layout.playlist_fragment, container, false);

        Toast.makeText(recView.getContext(), String.valueOf(getscreenSize(recView.getContext())), Toast.LENGTH_LONG).show();
        recView.setLayoutManager(new LinearLayoutManager(recView.getContext()));


        adapter = new PlayListAdaptor(queryPlaylists(recView.getContext().getContentResolver()), recView.getContext());
        recView.setAdapter(adapter);


        return recView;


        //    return inflater.inflate(R.layout.fragment_album_fragment, container, false);
    }

}
