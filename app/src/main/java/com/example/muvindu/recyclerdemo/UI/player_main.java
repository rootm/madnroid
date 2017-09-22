package com.example.muvindu.recyclerdemo.UI;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.muvindu.recyclerdemo.DataLoader.Album_loader;
import com.example.muvindu.recyclerdemo.Fragments.album_fragment;
import com.example.muvindu.recyclerdemo.Fragments.songList_fragment;

import com.example.muvindu.recyclerdemo.MusicService;
import com.example.muvindu.recyclerdemo.R;
import com.example.muvindu.recyclerdemo.Services.serviceConnection;
import com.example.muvindu.recyclerdemo.Services.serviceStat;
import com.example.muvindu.recyclerdemo.adapter.pageAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.crash.FirebaseCrash;

import static com.example.muvindu.recyclerdemo.MusicService.mediaPlayer;
import static com.example.muvindu.recyclerdemo.Services.serviceConnection.player;
import static com.example.muvindu.recyclerdemo.Utils.utils.getscreenSize;


public class player_main extends AppCompatActivity {
    private Album_loader album_loader = new Album_loader(this);
    private RecyclerView recview;
    private  ProgressBar progressBar;
    private  ImageButton play  ;
    private  ImageView small_albumArt;
    private  TextView topic ;
   public static LinearLayout btmPlayer;
    private Handler handler=new Handler();
    private View adLayout;
   // public

    private AdView mAdView;


    private int currentState;

    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController;
    private MediaMetadataCompat mediaMetadata;
    private PlaybackStateCompat playbackState;


    private MediaBrowserCompat.ConnectionCallback mMediaBrowserCompatConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnectionFailed() {
            super.onConnectionFailed();
            Log.e("myapp con","con error");
        }

        @Override
        public void onConnected() {
            super.onConnected();
            try {
                mediaController = new MediaControllerCompat(player_main.this, mediaBrowser.getSessionToken());
                mediaController.registerCallback(mMediaControllerCompatCallback);
                setSupportMediaController(mediaController);
                mediaMetadata = mediaController.getMetadata();
                //  getSupportMediaController().getTransportControls().playFromMediaId(String.valueOf(R.raw.warner_tautz_off_broadway), null);

                try {
                    setPlaybackState(mediaController.getPlaybackState());
                }catch (NullPointerException e){
                    currentState=-1;
                }


                if (currentState==1||currentState==0){
                    playerSetVisibility(true);

                }else{
                    playerSetVisibility(false);

                }

                Log.e("myapp con","connected");
            } catch( RemoteException e ) {

            }
        }
    };

    private MediaControllerCompat.Callback mMediaControllerCompatCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            mediaMetadata=metadata;
       //     String x=String.valueOf(metadata.size());
          //  Log.e("my",x );
            setPlayerData(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE),metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI));
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);

            if( state == null ) {
                return;
            }
            playbackState=state;
            setPlaybackState(state);


        }
    };


    private void setPlaybackState(PlaybackStateCompat state){
        switch( state.getState() ) {
            case PlaybackStateCompat.STATE_PLAYING: {
                currentState = 1;
                play.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.pause_btn));
                break;
            }
            case PlaybackStateCompat.STATE_PAUSED: {
                currentState = 0;
                play.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.play_btn));
                break;
            }
            case PlaybackStateCompat.STATE_NONE:{
                currentState = 3;
                playerSetVisibility(false);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_main);

        adLayout=findViewById(R.id.ad);
        btmPlayer=(LinearLayout) findViewById(R.id.bottomPlayer);
        play =(ImageButton) findViewById(R.id.playButton);
        small_albumArt=(ImageView) findViewById(R.id.albumArt);
        topic=(TextView) findViewById(R.id.songName_small);
        topic.setSelected(true);
        Typeface font = Typeface.createFromAsset(getAssets(), "Fonts/Aaargh.ttf");
        topic.setTypeface(font);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        ViewPager viewPager = (ViewPager)  findViewById(R.id.mainViewPager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.mainTabLayout);

        tabs.setTabTextColors( ContextCompat.getColor(this, android.R.color.white),ContextCompat.getColor(this, android.R.color.holo_red_dark));
        tabs.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTaNew));
        tabs.setSelectedTabIndicatorColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        tabs.setSmoothScrollingEnabled(true);
        tabs.setupWithViewPager(viewPager);
        serviceStat state = new serviceStat();


        changeTabsFont(tabs);


        mAdView = (AdView) findViewById(R.id.adView1);

        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Toast.makeText(mAdView.getContext().getApplicationContext(),String.valueOf(errorCode)+ " erroooor",Toast.LENGTH_LONG);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();



                if (mAdView.getVisibility() == View.GONE) {
                    adLayout.setVisibility(View.VISIBLE);
                    mAdView.setVisibility(View.VISIBLE);
                }
            }
        });


        AdRequest adRequest = new AdRequest.Builder()

                .build();
        mAdView.loadAd(adRequest);


      mediaBrowser = new MediaBrowserCompat(getApplicationContext() , new ComponentName(getApplicationContext(), MusicService.class),
                mMediaBrowserCompatConnectionCallback,null);
        Log.e("MyApp xx","here1");

      mediaBrowser.connect();


        handler.postDelayed(thread,300);

    }




    private void setupViewPager(ViewPager viewPager) {
        pageAdapter adapter = new pageAdapter(getSupportFragmentManager());
        adapter.addFragment(new songList_fragment(), "Songs");
        adapter.addFragment(new album_fragment(), "Album");
        adapter.addFragment(new songList_fragment(), "Artist");
        adapter.addFragment(new songList_fragment(), "Songs");
        adapter.addFragment(new album_fragment(), "Album");
        adapter.addFragment(new songList_fragment(), "Artist");


        viewPager.setAdapter(adapter);
    }

    private void changeTabsFont(TabLayout Tablayout) {
        float size;
        size=getscreenSize(this.getApplicationContext());
        Typeface font = Typeface.createFromAsset(getAssets(), "Fonts/Aaargh.ttf");
        ViewGroup vg = (ViewGroup) Tablayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(font);


                }
            }
        }
    }


    public void playPause(View view) {

          //int state=mediaController.getPlaybackState().getState();

        if (currentState==1){
            play.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.play_btn));
            mediaController.getTransportControls().pause();
        }else if(currentState==0){
            play.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.pause_btn));
            mediaController.getTransportControls().play();

        }

    }

    public void previousSong(View view) {

        if (mediaBrowser.isConnected()  ) {
            if(currentState==1 ||  currentState==0){
            mediaController.getTransportControls().skipToPrevious();
            }
        }

    }

    public void nextSong(View view) {
        if (mediaBrowser.isConnected()  ) {
            if(currentState==1 ||  currentState==0){
                mediaController.getTransportControls().skipToNext();
            }
        }
    }

    public void playing_activity(View view) {



        if (currentState==1|currentState==0){
            Intent intent = new Intent(this, nowPlaying.class);

            startActivity(intent);
        }else{


        }

    }

    private void playerSetVisibility(boolean state){

        if (state && btmPlayer.getVisibility()==View.GONE){
            setPlayerData(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE),mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI));
            btmPlayer.setVisibility(View.VISIBLE);
        }else if (!state  && btmPlayer.getVisibility()==View.VISIBLE){
            setPlayerData("","");
            btmPlayer.setVisibility(View.GONE);
        }
    }

    private void setPlayerData(String songName,String uri){
        Glide.with(getApplicationContext()).load(uri).asBitmap().error( R.drawable.albumartxxx).centerCrop().into(small_albumArt);
        topic.setText(songName);

    }


    Thread thread=new Thread(new Runnable() {
        @Override
        public void run() {
            if (mediaBrowser.isConnected() & mediaPlayer!=null) {
                if (currentState==1 & playbackState!=null) {
                    long pos=mediaPlayer.getCurrentPosition();
                 //   Log.e("MyApp pos",String.valueOf(pos));
                 //   if ( String.valueOf(pos)!=null){

                        // Toast.makeText(getApplicationContext(),pos,Toast.LENFGTH_SHORT).show();

                      //  Log.e("MyApp pos",String.valueOf(mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)));
                        progressBar.setMax(Integer.parseInt(String.valueOf(mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION))));

                        progressBar.setProgress(mediaPlayer.getCurrentPosition());
                    }

                    //     progressBar.setMax(player.mediaPlayer.getDuration());
                    //      progressBar.setProgress(player.mediaPlayer.getCurrentPosition());
                    // seekBar.setProgress(player.mediaPlayer.getCurrentPosition());
               // }

            }
           handler.postDelayed(thread,500);
        }
    });


}
