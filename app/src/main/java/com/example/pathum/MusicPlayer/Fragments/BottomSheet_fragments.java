package com.example.muvindu.recyclerdemo.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.muvindu.recyclerdemo.R;

import java.util.ArrayList;

import static com.example.muvindu.recyclerdemo.Utils.PlayList.addToPlaylist;
import static com.example.muvindu.recyclerdemo.Utils.PlayList.queryPlaylists;

public class BottomSheet_fragments extends BottomSheetDialogFragment {

    private Long songID;
    private String[] playLists;

    public BottomSheet_fragments() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View sheet = inflater.inflate(R.layout.song_options, container, false);
        songID = Long.parseLong(getArguments().getString("id"));

        ImageButton addTo = (ImageButton) sheet.findViewById(R.id.addTo_button);

        addTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(getActivity().getBaseContext(),"to add button",Toast.LENGTH_SHORT);
                Log.e("Bottom", "Bottom sheet clieck");

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
// Add the buttons

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog dialog = builder.create();


            }
        });

        return sheet;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //=null;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Cursor cursor = queryPlaylists(getActivity().getContentResolver());
        String s = "";
        if (cursor != null) {
            playLists = new String[cursor.getCount()];
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                s = cursor.getString(1);
                playLists[i] = (s);
                cursor.moveToNext();

            }
        }

        builder.setTitle("Select Play List to Add")
                .setItems(playLists, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item

                        addToPlaylist(getActivity().getContentResolver(), getActivity().getApplicationContext(), playLists[which], songID);
                    }
                });
        return builder.create();
    }
}


