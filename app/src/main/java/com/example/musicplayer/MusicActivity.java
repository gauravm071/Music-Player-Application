package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MusicActivity extends AppCompatActivity {
    TextView songName, artistName;
    ArrayList<Song> listOfSongs;
    static MediaPlayer mp;
    int position;
    ImageView play_pause,next,prev;
    String state= "play";
    SeekBar seekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        if(MusicActivity.mp!=null){
            MusicActivity.mp.release();
            MusicActivity.mp=null;
        }
        songName = findViewById(R.id.tvsongName);
        artistName = findViewById(R.id.tvSingerName);
        play_pause = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekBar);
        next= findViewById(R.id.next);
        prev=findViewById(R.id.prev);
        Intent intent = getIntent();
         position=Integer.parseInt(intent.getStringExtra("position"));
        String curSong = intent.getStringExtra("user");
        Gson gson = new Gson();
        Song mySong = gson.fromJson(curSong, Song.class);
        songName.setText(mySong.getName());
        System.out.println("url: " + mySong.getUrl());
        artistName.setText(mySong.getArtist());
        prepareSong(mySong);

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(state.equals("play")){
                    state="pause";
                    play_pause.setImageResource(R.drawable.pause);
                    if(mp!=null){
                        if(mp.isPlaying()){
                            mp.reset();
                            mp.start();
                        }
                        else{
                            mp.start();
                        }
                    }
                    else{
                        mp.start();
                    }
                }else{
                    state="play";
                    play_pause.setImageResource(R.drawable.play);
                    mp.pause();
                }
            }
        });


        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                System.out.println("chla");
                seekBar.setProgress(0);
                mp.reset();
                if(state.equals("pause")){
                    play_pause.setImageResource(R.drawable.play);
                }
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position<SongData.getInstance().getListOfSongs().size()-1) {
                    Song nextSong = SongData.getInstance().getListOfSongs().get(position + 1);
                    position=position+1;
                    prepareSong(nextSong);
                }
                else{
                    Log.v("listOfSongs: ","No Song exist in this position");
                }
            }
        });


        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position>0){
                    Song song= SongData.getInstance().getListOfSongs().get(position-1);
                    if(mp!=null){
                        mp.release();
                        mp=null;
                    }
                    position-=1;
                    prepareSong(song);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });


    }

    void startThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mp != null && mp.isPlaying()) {
                    try {

                            seekBar.setProgress(mp.getCurrentPosition());
                            Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    void prepareSong(Song mySong){
        if (mySong.getUrl() != null) {
            if(mp!=null && mp.isPlaying()){
                mp.reset();
            }
            state="play";
            songName.setText(mySong.getName());
            artistName.setText(mySong.getArtist());
            mp=null;
            seekBar.setProgress(0);
            play_pause.setImageResource(R.drawable.play);
            if (mp == null) {
                mp = new MediaPlayer();
                try {
                    mp.setDataSource(mySong.getUrl());
                    mp.prepare();
                    seekBar.setMax(mp.getDuration());
                    startThread();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}