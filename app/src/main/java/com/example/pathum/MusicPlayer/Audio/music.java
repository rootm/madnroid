package com.example.pathum.MusicPlayer.Audio;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;

import android.os.Handler;

import com.example.pathum.MusicPlayer.Model.Song;
import com.example.pathum.MusicPlayer.MusicService;
import com.example.pathum.MusicPlayer.Services.MediaService;
import com.example.pathum.MusicPlayer.Services.serviceConnection;
import com.example.pathum.MusicPlayer.Services.serviceStat;


import java.util.List;

import static com.example.pathum.MusicPlayer.Services.serviceConnection.player;
import static com.example.pathum.MusicPlayer.Services.serviceConnection.serviceBound;


/**
 * Created by Muvindu on 1/1/2017.
 */

public class music extends Activity {
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.muvindu.recyclerdemo.PlayNewAudio";
    serviceConnection connection = new serviceConnection();
    serviceStat state = new serviceStat();
    private Handler handler = new Handler();

    public void playAudio(String data, int position, Context contextView) {
        //Check is service is active


        Intent playerIntent = new Intent(contextView, MusicService.class);
        boolean running = state.isMyServiceRunning(contextView.getApplicationContext());

        if (!running) {

            playerIntent.putExtra("filePath", data);
            playerIntent.putExtra("id", position);
            playerIntent.setAction("PLAYER_START");
            contextView.getApplicationContext().startService(playerIntent);
            // contextView.getApplicationContext().bindService(playerIntent, connection.newServiceConnection, Context.BIND_AUTO_CREATE);

            // progressBar.setProgress(0);
            // handler.postDelayed(thread,100);
        } else {

            //Intent playerIntent = new Intent(contextView, MediaService.class);

            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            broadcastIntent.putExtra("filePath", data);
            broadcastIntent.putExtra("id", position);
            contextView.getApplicationContext().sendBroadcast(broadcastIntent);
            //Service is active
            //Send media with BroadcastReceiver


        }
    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            if (player != null) {
                //   if (player.mediaPlayer.isPlaying()) {
                // seekBar.setMax(player.mediaPlayer.getDuration());
                // seekBar.setProgress(player.mediaPlayer.getCurrentPosition());
                //     progressBar.setMax(player.mediaPlayer.getDuration());
                //      progressBar.setProgress(player.mediaPlayer.getCurrentPosition());
                // seekBar.setProgress(player.mediaPlayer.getCurrentPosition());
                //  }

            }
            // handler.postDelayed(thread,500);
        }
    });

   /* Runnable progress = new Runnable() {
        @Override
        public void run() {

        }
    };*/


}
