package com.example.muvindu.recyclerdemo.UI;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.muvindu.recyclerdemo.MusicService;
import com.example.muvindu.recyclerdemo.R;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import static com.example.muvindu.recyclerdemo.MusicService.mediaPlayer;
import static com.example.muvindu.recyclerdemo.Services.serviceConnection.player;
import static com.example.muvindu.recyclerdemo.Utils.utils.getscreenHeigth;


public class nowPlaying extends AppCompatActivity{

    private SeekBar seekBar;
    private Handler handler=new Handler();
    public static Toolbar mActionBarToolbar;
    static  ImageView albumImage;

    private TextView album;
    private AdView mAdView;


    private int currentState;

    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController;
    private MediaMetadataCompat mediaMetadata;
    private PlaybackStateCompat playbackState;
    private ImageButton playBtn;


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
                mediaController = new MediaControllerCompat(nowPlaying.this, mediaBrowser.getSessionToken());
                mediaController.registerCallback(mMediaControllerCompatCallback);
                setSupportMediaController(mediaController);
                mediaMetadata = mediaController.getMetadata();
                //  getSupportMediaController().getTransportControls().playFromMediaId(String.valueOf(R.raw.warner_tautz_off_broadway), null);

                try {
                    setPlaybackState(mediaController.getPlaybackState());
                }catch (NullPointerException e){
                    currentState=-1;
                }


                setImage(0);

                Log.e("myapp nowplay","connected");
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
           // setPlayerData(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE),metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI));
            setImage(0);
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
                playBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.pause_btn));
                break;
            }
            case PlaybackStateCompat.STATE_PAUSED: {
                currentState = 0;
                playBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.play_btn));
                break;
            }
            case PlaybackStateCompat.STATE_NONE:{
                currentState = 3;

            }
        }

    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);


        Typeface font = Typeface.createFromAsset(getAssets(), "Fonts/Aaargh.ttf");
        Typeface font1 = Typeface.createFromAsset(getAssets(), "Fonts/ABeeZee-Regular.otf");



        playBtn =(ImageButton) findViewById(R.id.nowPlayin_playbtn);
        seekBar=(SeekBar)findViewById(R.id.playing_seekbar) ;

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                 //   player.mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        handler.postDelayed(thread,100);

        mActionBarToolbar = (Toolbar) findViewById(R.id.playerToolBar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("My title");

        Toast.makeText(this,String.valueOf(getscreenHeigth(this)) , Toast.LENGTH_LONG).show();
         albumImage =(ImageView) findViewById(R.id.albumImage);
        final float height=(getscreenHeigth(this));

      /*  if(getscreenHeigth(this)<=500){
            albumImage.getLayoutParams().height= (int)(height/2.45);
            albumImage.getLayoutParams().width= (int)(height/2.45);
            albumImage.requestLayout();
        }

        else if (getscreenHeigth(this)<=540){
            albumImage.getLayoutParams().height= (int)(height/1.5);
            albumImage.getLayoutParams().width= (int)(height/1.5);
            albumImage.requestLayout();


        } else if (getscreenHeigth(this)<=940){
            albumImage.getLayoutParams().height= (int)(height/1.35);
            albumImage.getLayoutParams().width= (int)(height/1.35);
            albumImage.requestLayout();


        }else{
            albumImage.getLayoutParams().height= (int)(height/1.15);
            albumImage.getLayoutParams().width= (int)(height/1.15);
            albumImage.requestLayout();
        }*/


        album=(TextView) findViewById(R.id.albumName);
        TextView startTime =(TextView) findViewById(R.id.startTime);
        TextView endTime =(TextView) findViewById(R.id.endTime);
        startTime.setTypeface(font1);
        endTime.setTypeface(font1);
       album.setTypeface(font);


        mAdView = (AdView) findViewById(R.id.adView);


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
              /*  if (height<=500){
                    albumImage.getLayoutParams().height= (int)(height/2.7);
                    albumImage.getLayoutParams().width= (int)(height/2.7);
                    albumImage.requestLayout();
                }
                else if (height<=540){
                    albumImage.getLayoutParams().height= (int)(height/1.75);
                    albumImage.getLayoutParams().width= (int)(height/1.75);
                    albumImage.requestLayout();}
             else if (height<=940){
                albumImage.getLayoutParams().height= (int)(height/1.45);
                albumImage.getLayoutParams().width= (int)(height/1.45);
                albumImage.requestLayout();


            }*/
                if (mAdView.getVisibility() == View.GONE) {
                    mAdView.setVisibility(View.VISIBLE);
                }
            }
        });


        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("9C5C4DD7BFBD799D2DEE86B698AE7F1E")
                .build();
        mAdView.loadAd(adRequest);


         //  mediaBrowser =new MediaBrowserCompat(this,new ComponentName(this, MediaService.class),connectionCallback,null );

            Log.e("MyApp xx","here");


        mediaBrowser = new MediaBrowserCompat(this, new ComponentName(getApplicationContext(), MusicService.class),
                mMediaBrowserCompatConnectionCallback,null);
         //   Log.e("MyApp xx",  mMediaBrowserCompat.getRoot());


        mediaBrowser.connect();


        handler.postDelayed(thread,300);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
     if (mediaController.getMediaController()!=null) {
         mediaController.unregisterCallback(mMediaControllerCompatCallback);

     }
        mediaBrowser.disconnect();
    }






    public void nowPlaying_playPause(View view) {

/*        if (player!=null) {
            if (player.mediaPlayer.isPlaying()) {
                play.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.pause_btn));
                playBtn.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.pause_btn));
                player.pause();
            }else{
                play.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.play_btn));
                playBtn.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.play_btn));
                player.resume();
            }
        }
        */

     //   try {
            setPlaybackState(mediaController.getPlaybackState());
      //  }catch (NullPointerException e){
     //       currentState=-1;
      //  }

        if (currentState==1){
            playBtn.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.play_btn));
            mediaController.getTransportControls().pause();
        }else if (currentState==0){
            playBtn.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.pause));
            mediaController.getTransportControls().play();

        }


    }

    Thread thread=new Thread(new Runnable() {
        @Override
        public void run() {


       if (mediaBrowser.isConnected() & mediaPlayer!=null) {
                if (currentState==1 ) {
                    long pos=mediaPlayer.getCurrentPosition();
                    //   Log.e("MyApp pos",String.valueOf(pos));
                    //   if ( String.valueOf(pos)!=null){

                    // Toast.makeText(getApplicationContext(),pos,Toast.LENFGTH_SHORT).show();

                    //  Log.e("MyApp pos",String.valueOf(mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)));
                    seekBar.setMax(mediaPlayer.getDuration());
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    // }

                }
            }

            handler.postDelayed(thread,500);






        }
    });


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    private  void setImage(int state){

        if (state==0){Glide.with(this).load(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)).centerCrop().fitCenter().into(albumImage);}
        else if (state==1){
            Glide.with(this).load(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)).centerCrop().fitCenter().error( R.drawable.albumartxxx).animate(R.anim.right2mid).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right2mid);

                    albumImage.startAnimation(animSlide);
                    return false;
                }
            }).into(albumImage);
        }
        else if((state==2)){
            Glide.with(this).load(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)).centerCrop().fitCenter().error( R.drawable.albumartxxx).animate(R.anim.left2mid).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left2mid);

                    albumImage.startAnimation(animSlide);
                    return false;
                }
            }).into(albumImage);

        }


    }

    public void previous(View view) {

            if (mediaBrowser.isConnected()) {
                if (currentState == 1 || currentState == 0) {



                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mid2right);

                    albumImage.startAnimation(animSlide);

                    animSlide.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mediaController.getTransportControls().skipToPrevious();
                            setImage(2);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }
    }

    public void next(View view) {
        if (mediaBrowser.isConnected()) {
            if (currentState == 1 || currentState == 0) {
          Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mid2left);

            albumImage.startAnimation(animSlide);

           animSlide.setAnimationListener(new Animation.AnimationListener() {
            @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mediaController.getTransportControls().skipToNext();
                    setImage(1);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            }
        }
    }




    public void newx(View view) {


        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = this.getLayoutInflater().inflate(R.layout.song_options, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
    }
}


