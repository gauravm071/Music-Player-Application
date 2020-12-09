package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity {
    static int position;
    TextView songname, artistname;
    ImageView next, prev, play_pause;
    static SeekBar seekBar;
    Song mySong;
    Boolean firstTime=true;
    static int currentPosition = 0;
     String state = "pause";
    static MediaPlayer mp = null;
    Boolean prev_next = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        // Initializing the data;
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);
        play_pause = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekBar);
        songname = findViewById(R.id.tvsongName);
        artistname = findViewById(R.id.tvSingerName);
        // getting the song which user wants to play
        Intent intent = getIntent();
        position = Integer.parseInt(intent.getStringExtra("position"));
        String curSong = intent.getStringExtra("user");
        Gson gson = new Gson();
        mySong = gson.fromJson(curSong, Song.class);
        songname.setText(mySong.getName());
        artistname.setText(mySong.getArtist());
        if(currentPosition!=position && mp!=null) {
//            seekBar.setProgress(0);
//            mp.seekTo(0);
//            mp.release();
            try {

                startPlaying(mySong);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(mp!=null && currentPosition==position ){
            play_pause.setImageResource(R.drawable.pause);
            firstTime=false;
        }
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state.equals("pause")) {
                    try {
                        startPlaying(mySong);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (state.equals("play")) {
                    if (mp != null && mp.isPlaying()) stopPlaying();
                }
            }

        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    nextSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    prevSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mp != null && fromUser) {
                    mp.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Handler mHandler = new Handler();
        //Make sure you update Seekbar on UI thread
        MusicActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mp != null) {
                        int mCurrentPosition = mp.getCurrentPosition() / 1000;
                        if (!prev_next) seekBar.setProgress(mCurrentPosition);
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                seekBar.setProgress(0);
                                play_pause.setImageResource(R.drawable.play);
                                state = "pause";
                                mp.seekTo(0);
                                mp = null;
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.postDelayed(this, 1000);

            }
        });
    }


    public void startPlaying(Song song) throws IOException {
        songname.setText(song.getName());
        artistname.setText(song.getArtist());
        play_pause.setImageResource(R.drawable.pause);
        state = "play";
        if(firstTime && mp!=null && mp.isPlaying()){
            mp.reset();
            mp=null;
            mp= new MediaPlayer();
            mp.setDataSource(song.getUrl());
            mp.prepare();
            mp.start();
            play_pause.setImageResource(R.drawable.pause);
            state="pause";
            firstTime=false;
            currentPosition=position;
        }
        else if(mp==null){
            mp= new MediaPlayer();
            mp.setDataSource(song.getUrl());
            mp.prepare();
            mp.start();
            currentPosition=position;
            state="pause";
            play_pause.setImageResource(R.drawable.pause);
        }
        else{
            if(mp!=null && !mp.isPlaying()){
                mp.start();
            }
            else{
                stopPlaying();
            }
        }
    }

    public void stopPlaying() {
        play_pause.setImageResource(R.drawable.play);
        state = "pause";
        mp.pause();
    }

    public void nextSong() throws IOException {
        if (position + 1 < SongData.getInstance().getListOfSongs().size()) {
            mySong = SongData.getInstance().getListOfSongs().get(position + 1);
            songname.setText(mySong.getName());
            artistname.setText(mySong.getArtist());
            position++;
            songname.setText(mySong.getName());
            artistname.setText(mySong.getArtist());
            mp.reset();
            mp.setDataSource(mySong.getUrl());
            mp.prepare();
            mp.start();
            state = "play";
            play_pause.setImageResource(R.drawable.pause);
        }
    }

    public void prevSong() throws IOException {
        if (position - 1 >= 0) {
            mySong = SongData.getInstance().getListOfSongs().get(position - 1);
            songname.setText(mySong.getName());
            artistname.setText(mySong.getArtist());
            position--;
            songname.setText(mySong.getName());
            artistname.setText(mySong.getArtist());
            mp.reset();
            mp.setDataSource(mySong.getUrl());
            mp.prepare();
            mp.start();
            state = "play";
            play_pause.setImageResource(R.drawable.pause);
        }
    }

    public void startThread() {
        Handler mHandler = new Handler();
        //Make sure you update Seekbar on UI thread
        MusicActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mp != null) {
                        int mCurrentPosition = mp.getCurrentPosition() / 1000;
                        if (!prev_next) seekBar.setProgress(mCurrentPosition);
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                seekBar.setProgress(0);
                                play_pause.setImageResource(R.drawable.play);
                                state = "pause";
                                mp.seekTo(0);
                                mp = null;
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.postDelayed(this, 1000);

            }
        });
    }


}
