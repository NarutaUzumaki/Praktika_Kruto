package com.example.toplay;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Trace;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.deezer.sdk.model.Track;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.player.PlayerWrapper;
import com.deezer.sdk.player.TrackPlayer;
import com.deezer.sdk.player.event.PlayerState;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    static MediaPlayer projectMP;
    int position;
    ArrayList<Track> getSong;
    Thread updateSeekBar;
    String sName;

    PlayerWrapper mPlayer;
    static TrackPlayer trackPL;
//    String appID = "355244";
//    DeezerConnect deezerConnect = new DeezerConnect(MainActivity, appID);
    Context context;

    Button btn_next, btn_previous, btn_pause;
    TextView songTextLabel;
    SeekBar songSeekbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btn_next = (Button) findViewById(R.id.next);
        btn_previous = (Button) findViewById(R.id.previous);
        btn_pause = (Button) findViewById(R.id.pause);

        songTextLabel = (TextView) findViewById(R.id.songName);
        songSeekbar = (SeekBar) findViewById(R.id.seekBar);

//        getSupportActionBar().setTitle("Now playing");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        updateSeekBar = new Thread(){
            @Override
            public void run(){
                int totalDuration = projectMP.getDuration();
                int currentPosition = 0;

                while (currentPosition < totalDuration){
                    try {
                        sleep(500);
                        currentPosition = projectMP.getCurrentPosition();
                        songSeekbar.setProgress(currentPosition);
                    }catch (InterruptedException ex){
                        ex.printStackTrace();
                    }catch (IllegalStateException ex){

                    }
                }
            }
        };
        if(projectMP != null){
            projectMP.stop();
            projectMP.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        getSong = (ArrayList) bundle.getParcelableArrayList("songs");

        sName = getSong.get(position).getTitle().toString();


        String songName = i.getStringExtra("songname");
        songTextLabel.setText(songName.replace("mp3", ""));
        songTextLabel.setSelected(true);

        position = bundle.getInt("pos", 0);

        Uri u = Uri.parse(getSong.get(position).toString());

        projectMP =  MediaPlayer.create(getApplicationContext(), u);
        mPlayer.play();
        songSeekbar.setMax(projectMP.getDuration());

        updateSeekBar.start();

        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                projectMP.seekTo(seekBar.getProgress());
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songSeekbar.setMax(projectMP.getDuration());

                if (projectMP.isPlaying()){
                    btn_pause.setBackgroundResource(R.drawable.icon_play);
                    projectMP.pause();
                }else{
                    btn_pause.setBackgroundResource(R.drawable.icon_pause);
                    projectMP.start();
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectMP.stop();
                projectMP.release();

                position = ((position + 1) % getSong.size());

                Uri u = Uri.parse(getSong.get(position).toString());
                projectMP = MediaPlayer.create(getApplicationContext(), u);

                sName = getSong.get(position).getTitle().toString();
                songTextLabel.setText(sName);

                projectMP.start();
            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectMP.stop();
                projectMP.release();

                position = ((position - 1)<0) ? (getSong.size()-1):(position-1);

                Uri u = Uri.parse(getSong.get(position).toString());
                projectMP = MediaPlayer.create(getApplicationContext(), u);

                sName = getSong.get(position).getTitle().toString();
                songTextLabel.setText(sName);

                projectMP.start();
            }
        });


        @Override
        protected void onDestroy() {
            doDestroyPlayer();
            super.onDestroy();
        }

        /**
         * Will destroy player. Subclasses can override this hook.
         */
        protected void doDestroyPlayer() {

            if (mPlayer == null) {
                // No player, ignore
                return;
            }

            if (mPlayer.getPlayerState() == PlayerState.RELEASED) {
                // already released, ignore
                return;
            }

            // first, stop the player if it is not
            if (mPlayer.getPlayerState() != PlayerState.STOPPED) {
                mPlayer.stop();
            }

            // then release it
            mPlayer.release();
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//        if (item.getItemId() == android.R.id.home){
//            onBackPressed();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
