package com.example.toplay;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Trace;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.deezer.sdk.model.Album;
import com.deezer.sdk.model.Track;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.network.request.event.JsonRequestListener;
import com.deezer.sdk.network.request.event.RequestListener;
import com.deezer.sdk.player.AlbumPlayer;
import com.deezer.sdk.player.Player;
import com.deezer.sdk.player.PlayerWrapper;
import com.deezer.sdk.player.TrackPlayer;
import com.deezer.sdk.player.event.OnBufferErrorListener;
import com.deezer.sdk.player.event.OnBufferProgressListener;
import com.deezer.sdk.player.event.OnBufferStateChangeListener;
import com.deezer.sdk.player.event.OnPlayerErrorListener;
import com.deezer.sdk.player.event.OnPlayerProgressListener;
import com.deezer.sdk.player.event.OnPlayerStateChangeListener;
import com.deezer.sdk.player.event.PlayerState;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity implements Serializable {

    private static final String TAG = "Player";
    int position;
    List<Track> getSong = new ArrayList<Track>();
    Thread updateSeekBar;
    String sName;

    TrackPlayer trackPL = null;


    Button btn_next, btn_previous, btn_pause;
    TextView songTextLabel;
    SeekBar songSeekbar;
    public DeezerConnect deezerConnect;
    TrackPlayer newPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        btn_next = (Button) findViewById(R.id.next);
        btn_previous = (Button) findViewById(R.id.previous);
        btn_pause = (Button) findViewById(R.id.pause);

        songTextLabel = (TextView) findViewById(R.id.songName);
        songSeekbar = (SeekBar) findViewById(R.id.seekBar);

        String appID = "355244";
        deezerConnect = new DeezerConnect(this, appID);

        try {
            trackPL = new TrackPlayer(getApplication(), deezerConnect, new WifiAndMobileNetworkStateChecker());
        } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
            tooManyPlayersExceptions.printStackTrace();
        } catch (DeezerError deezerError) {
            deezerError.printStackTrace();
        }


        updateSeekBar = new Thread(){
            @Override
            public void run(){
                long totalDuration = getSong.get(position).getDuration();
                long currentPosition = 0;

                while (currentPosition < totalDuration){
                    try {
                        sleep(500);
                        currentPosition = getSong.get(position).getTrackPosition();
                        songSeekbar.setProgress((int) currentPosition);
                    }catch (InterruptedException ex){
                        ex.printStackTrace();
                    }catch (IllegalStateException ex){

                    }
                }
            }
        };
        if(newPlayer != null){
            newPlayer.stop();
            newPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        position = bundle.getInt("pos", 0);


        getSong = getIntent().getParcelableArrayListExtra("tracks");

        sName = getSong.get(position).getTitle().toString();


        String songName = getSong.get(position).getTitle().toString();
        songTextLabel.setText(songName);
        songTextLabel.setSelected(true);

//        position = bundle.getInt("pos", 0);

        try {
            newPlayer = new TrackPlayer(getApplication(), deezerConnect, new WifiAndMobileNetworkStateChecker());
            newPlayer.playTrack(getSong.get(position).getId());


        songSeekbar.setMax((getSong.get(position).getDuration()));

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
                newPlayer.seek(seekBar.getProgress());
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songSeekbar.setMax(getSong.get(position).getDuration());
                Log.d(TAG, "PLAYER STATUS: " + newPlayer.getPlayerState());
                if (newPlayer.getPlayerState() == PlayerState.PLAYING){
                    btn_pause.setBackgroundResource(R.drawable.icon_play);
                    newPlayer.pause();
                }else{
                    btn_pause.setBackgroundResource(R.drawable.icon_pause);
                    newPlayer.play();
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPlayer.stop();
                newPlayer.release();

                position = ((position + 1) % getSong.size());


                sName = getSong.get(position).getTitle().toString();
                songTextLabel.setText(sName);
                try {
                    newPlayer = new TrackPlayer(getApplication(),deezerConnect, new WifiAndMobileNetworkStateChecker());
                } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
                    tooManyPlayersExceptions.printStackTrace();
                } catch (DeezerError deezerError) {
                    deezerError.printStackTrace();
                }
                newPlayer.playTrack(getSong.get(position).getId());
            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPlayer.stop();
                newPlayer.release();

                position = ((position - 1)<0) ? (getSong.size()-1):(position-1);


                sName = getSong.get(position).getTitle().toString();
                songTextLabel.setText(sName);

                try {
                    newPlayer = new TrackPlayer(getApplication(),deezerConnect, new WifiAndMobileNetworkStateChecker());
                } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
                    tooManyPlayersExceptions.printStackTrace();
                } catch (DeezerError deezerError) {
                    deezerError.printStackTrace();
                }
                newPlayer.playTrack(getSong.get(position).getId());

            }
        });
        } catch (TooManyPlayersExceptions tooManyPlayersExceptions) {
            tooManyPlayersExceptions.printStackTrace();
        } catch (DeezerError deezerError) {
            deezerError.printStackTrace();
        }




}
    RequestListener listenerRequest = new JsonRequestListener() {

        public void onResult(Object result, Object requestId) {
            List<Album> albums = (List<Album>) result;
            // do something with the albums

        }

        public void onUnparsedResult(String requestResponse, Object requestId) {}

        public void onException(Exception e, Object requestId) {}
    };

}

