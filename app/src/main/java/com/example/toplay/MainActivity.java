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


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.ItemClickListener {

    RecyclerAdapter adapterum;
    RequestQueue mQueue;
    TextView text;

    //String rez;
    final ArrayList<File> songs = findSong(Environment.getExternalStorageDirectory());
    RecyclerView songListView;
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String appID = "355244";
        DeezerConnect deezerConnect = new DeezerConnect(this, appID);

        text = findViewById(R.id.test);
        songListView = findViewById(R.id.songListView);

        runtimePermission();
        mQueue = Volley.newRequestQueue(this);
        JsonParse();



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

    private void JsonParse(){
        String url = "https://api.deezer.com/track/3135556";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new
                Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("contributors");

                            for(int i = 0; i< jsonArray.length(); i++){
                                JSONObject contributors = jsonArray.getJSONObject(i);

                                int id = contributors.getInt("id");
                                String name = contributors.getString("name");

                                text.append(String.valueOf(id) +", " + name + "\n\n");

                                //rez = String.valueOf(id) + name;


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

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
            //items[i+1] = rez;
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
