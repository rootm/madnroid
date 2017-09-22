package com.example.muvindu.recyclerdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.example.muvindu.recyclerdemo.Audio.music;
import com.example.muvindu.recyclerdemo.Model.Song;

import com.example.muvindu.recyclerdemo.Services.PlaybackStatus;

import java.io.IOException;
import java.util.List;



/**
 * Created by Muvindu on 7/3/2017.
 */

public class MusicService extends MediaBrowserServiceCompat  implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,  MediaPlayer.OnSeekCompleteListener, AudioManager.OnAudioFocusChangeListener {

    //================================================================================
    // region Variable Declarations

    private static List<Song> songlist;
    public static String listType1="none";
  //  private final IBinder iBinder = new MediaService.LocalBinder();
    public static MediaPlayer mediaPlayer;
    private String file;
    private int resumePosition = 0;
    private AudioManager audioManager;
    private int currentID = -1;


    private boolean shuffle=false;
    private boolean repeatOne =false;
    private boolean repeateAll=false;

    private static final String ACTION_PLAY = "com.example.muvindu.recyclerdemo.ACTION_PLAY";
    private static final String ACTION_PAUSE = "com.example.muvindu.recyclerdemo.ACTION_PAUSE";
    private static final String ACTION_PREVIOUS = "com.example.muvindu.recyclerdemo.ACTION_PREVIOUS";
    private static final String ACTION_NEXT = "com.example.muvindu.recyclerdemo.ACTION_NEXT";
    private static final String ACTION_STOP = "com.example.muvindu.recyclerdemo.ACTION_STOP";

    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;

    private MediaControllerCompat.TransportControls transportControls;

    //AudioPlayer notification ID
    private static final int NOTIFICATION_ID = 0116;

    private BroadcastReceiver playNew = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                file = intent.getExtras().getString("filePath");
                currentID = intent.getExtras().getInt("id");
            } catch (NullPointerException e) {
                stopSelf();
            }

            if (file != null && file != "") {
                stop();
                // mediaPlayer.reset();
                initializePlayer();
            }
        }
    };
    private void register_playNewAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(music.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNew, filter);
    }

    private MediaSessionCompat.Callback callback=new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();
            play();
            buildNotification(PlaybackStatus.PLAYING);
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.e("MYAPP- here iiiiii","pause");
            pause();
            buildNotification(PlaybackStatus.PAUSED);
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            next();

        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            previous();

        }

        @Override
        public void onStop() {
            super.onStop();
            stop();
            stopForeground(removeNotification());
            Intent service = new Intent(getApplicationContext(), MusicService.class);
            setMediaPlaybackState(PlaybackStateCompat.STATE_NONE);
            stopService(service);
           // removeNotification();


        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            mediaPlayer.seekTo((int)pos);
        }
    };

    //endregion
    // ===============================================================================


    public static void setSonglist1(List<Song> list,String type) {
        songlist = list;
        listType1=type;
    }



    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if(TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
           }

           return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        register_playNewAudio();
        mediaSession=new MediaSessionCompat(this,"MediaService");
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setCallback(callback);

        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS|MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);



        setSessionToken(mediaSession.getSessionToken());
        Log.e("MYAPP", mediaSession.getSessionToken().toString());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!intent.getAction().toString().equalsIgnoreCase("PLAYER_START")){

            handleIncomingActions(intent);
            return super.onStartCommand(intent, flags, startId);
        }


        try {
            file = intent.getExtras().getString("filePath");
            currentID = intent.getExtras().getInt("id");
        } catch (NullPointerException e) {
            stopSelf();
        }

        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf();
        }




        if (file != null && file != "") {

            initializePlayer();
        }else{stopSelf();}


        MediaButtonReceiver.handleIntent(mediaSession,intent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            stop();
            mediaPlayer.release();
        }

        unregisterReceiver(playNew);
        removeAudioFocus();
        removeNotification();
        mediaSession.release();
    }




    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stop();
       // stopSelf();
        // mediaPlayer.release();
        next();
    }



    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        play();
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }

    private void initializePlayer() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);


            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaPlayer.setDataSource(file);
            mediaPlayer.prepareAsync();
            updateMetaData();

        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
            Log.e("MYAPP", "exception1", e);
        }

    }

    // ===============================================================================
    //region Playing Functions


    private void setMediaPlaybackState(int state) {
        PlaybackStateCompat.Builder playbackstateBuilder = new PlaybackStateCompat.Builder();
        if( state == PlaybackStateCompat.STATE_PLAYING ) {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PAUSE);
        } else {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY);
        }
        playbackstateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
        mediaSession.setPlaybackState(playbackstateBuilder.build());
    }


    private void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            // seekBar.setProgress(0);
            updateMetaData();
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            buildNotification(PlaybackStatus.PLAYING);


        }

    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);

        }
    }

    private void stop() {
        if (mediaPlayer != null) {
            resumePosition = 0;
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED);
                this.stopSelf();
            }
            setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED);
            this.stopSelf();
        }

    }


    public void resume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            play();
        }

    }

    public void next(){

        try {
            stopSelf();
            stop();
            Song song;
            mediaPlayer.reset();
            if (currentID<(songlist.size()-1)){
                ++currentID;
            }else if (currentID==(songlist.size()-1)){
                currentID=0;
            }
            song = songlist.get(currentID);
            file = song.getData();


            // mediaPlayer.reset();
            initializePlayer();

            Log.e("MYAPP", "currentID " +currentID) ;
        } catch (Exception e) {
            Log.e("MYAPP", "exception", e);
        }

    }
    public void previous(){
        stopSelf();
        stop();
        Song song;
        mediaPlayer.reset();
        if (currentID==0){
            currentID=(songlist.size()-1);
        }else{
            --currentID;
        }
        song = songlist.get(currentID);
        file = song.getData();


        // mediaPlayer.reset();
        initializePlayer();


    }
    //endregion
    // ===============================================================================



    //================================================================================
    // region Audio Focus Handle


    @Override
    public void onAudioFocusChange(int i) {
        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback

                if (mediaSession.getController().getPlaybackState().getState()==PlaybackStateCompat.STATE_PAUSED) {
                    resume();
                    mediaPlayer.setVolume(1.0f, 1.0f);
                   // initializePlayer();
                }
              //  else if(!mediaPlayer.isPlaying()) ;

                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) stop();
                mediaPlayer.release();
                mediaPlayer = null;
                stopSelf();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }


    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocus(this);
    }

    //endregion
    // ===============================================================================

    public long position(){
        return mediaPlayer!=null?mediaPlayer.getCurrentPosition():0;

    }

    private void updateMetaData() {
        final Song song=songlist.get(currentID);


        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        Uri uri = ContentUris.withAppendedId(sArtworkUri,song.getAlbumId());




        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, uri.toString() )
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getSongName())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,song.getDuration() )
                .build());

    }


    private void buildNotification(PlaybackStatus playbackStatus) {
        final Song song=songlist.get(currentID);

        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();


     // Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.albumartxxx); //replace with your own image

        // Create a new Notification
        RemoteViews notificationLayout =new RemoteViews(getPackageName(),R.layout.big_notification);
        notificationLayout.setTextViewText(R.id.notification_song,mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        notificationLayout.setTextViewText(R.id.notification_artist,mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        notificationLayout.setOnClickPendingIntent(R.id.notification_play, playbackAction(1));
        notificationLayout.setOnClickPendingIntent(R.id.notification_next, playbackAction(2));
        notificationLayout.setOnClickPendingIntent(R.id.notification_prev, playbackAction(3));
        notificationLayout.setOnClickPendingIntent(R.id.notification_close, playbackAction(4));

        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationLayout.setInt(R.id.notification_play,"setBackgroundResource",R.drawable.pause_btn);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationLayout.setInt(R.id.notification_play,"setBackgroundResource",R.drawable.play_btn);
        }

        Log.e("MYAPP",mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE) );
        NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(this)
                ;
        notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
      //  notificationBuilder.setLargeIcon(largeIcon);
        notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_headset);
        notificationBuilder.setCustomContentView(notificationLayout);

        final Notification notification =notificationBuilder.build();

        NotificationTarget notificationTarget=new NotificationTarget(this,notificationLayout,R.id.notification_albumArt,notification,NOTIFICATION_ID);

        Glide.with(getApplicationContext()).load(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)).asBitmap().error( R.drawable.albumartxxx).centerCrop().into(notificationTarget);

        startForeground(NOTIFICATION_ID,notification);
       ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);


    }

    private boolean removeNotification() {
        try {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID);
            return  true;
        }catch ( Exception e   ){
            Log.e("MYAPP- Notification",e.getMessage());
        }

          return  false;

    }

    private PendingIntent playbackAction(int actionNumber) {

        Intent playbackAction = new Intent(this, MusicService.class);

        if (actionNumber==1){
            if (mediaPlayer.isPlaying()){playbackAction.setAction(ACTION_PAUSE);}
            else{playbackAction.setAction(ACTION_PLAY);}

            Log.e("MYAPP- playbackAction",playbackAction.getAction() );
            return PendingIntent.getService(this, actionNumber, playbackAction, 0);
        }
        else if(actionNumber==2){
            playbackAction.setAction(ACTION_NEXT);
            return PendingIntent.getService(this, actionNumber, playbackAction, 0);
        }
        else if(actionNumber==3){
            playbackAction.setAction(ACTION_PREVIOUS);
            return PendingIntent.getService(this, actionNumber, playbackAction, 0);
        }else if(actionNumber==4){
            playbackAction.setAction(ACTION_STOP);
            return PendingIntent.getService(this, actionNumber, playbackAction, 0);

        }

        return null;
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        Log.w("myApp XXXX", actionString);
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }


}
