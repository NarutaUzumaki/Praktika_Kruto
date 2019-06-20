package com.example.toplay;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.design.widget.Snackbar;


import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.ItemClickListener {

    RecyclerAdapter adapterum;


    final ArrayList<File> songs = findSong(Environment.getExternalStorageDirectory());
    RecyclerView songListView;
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songListView = findViewById(R.id.songListView);

        runtimePermission();

        RecyclerView recyclerView = findViewById(R.id.songListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterum = new RecyclerAdapter( Arrays.asList(items), this);
        adapterum.setClickListener(this);
        recyclerView.setAdapter(adapterum);
    }

    public void runtimePermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        display();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    AsyncTask.execute(new Runnable(){
        URL DeezerEndPoint = new URL("https://api.deezer.com/version/service/id/method/?parameters");
    });

    public ArrayList<File> findSong(File file) {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();
        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                arrayList.addAll(findSong(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                    arrayList.add(singleFile);
                }
            }
        }

        return arrayList;
    }

    void display() {
        //final ArrayList<File> songs = findSong(Environment.getExternalStorageDirectory());
        items = new String[songs.size()];

        for (int i = 0; i < songs.size(); i++) {
            items[i] = (i + 1) + "." + songs.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");
            //(i+1) служит номером композиции в списке, можно удалить, если проблемы
        }

        //myAdapter
        //final RecyclerAdapter adapter = new RecyclerAdapter(items, android.R.layout.simple_list_item_1);
        songListView.setAdapter(adapterum);



//        songListView.setAdapter(new RecyclerAdapter(Arrays.asList(items), new RecyclerAdapter.ItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                String songName = adapterum.getItem(position).replace("mp3", "");
//
//                startActivity(new Intent(getApplicationContext(), PlayerActivity.class).putExtra("songs", songs)
//                        .putExtra("songname", songName)
//                        .putExtra("pos", position));
//            }

//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position) {
//                String songName = adapterum.getItem(position).replace("mp3", "");
//
//                startActivity(new Intent(getApplicationContext(), PlayerActivity.class).putExtra("songs", songs)
//                        .putExtra("songname", songName)
//                        .putExtra("pos", position));
//            }
//        });

//        @Override
//        public void onItemClick(View view,int position){
//            String songName = adapterum.getItem(position).replace("mp3", "");
//
//            startActivity(new Intent(getApplicationContext(), PlayerActivity.class).putExtra("songs", songs)
//                    .putExtra("songname", songName)
//                    .putExtra("pos", position));
//        }
//    }));

}

    @Override
    public void onItemClick(View view, int position) {
        String songName = adapterum.getItem(position).toString().replace("mp3", "");

        startActivity(new Intent(getApplicationContext(), PlayerActivity.class).putExtra("songs", songs)
                .putExtra("songname", songName)
                .putExtra("pos", position));
    }
}
