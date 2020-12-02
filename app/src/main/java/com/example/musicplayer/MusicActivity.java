package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity {
    TextView songName, artistName;
    ArrayList<Song> listOfSongs;
    static MediaPlayer mp;
    int position, currentSongPosition, mpPrevPosition;
    ImageView play_pause, next, prev;
    String state = "play";
    Boolean isplay = false, seekBarReachedToEnd = false, nextOrPrevActive = false;
    Boolean firstTime = true;
    SeekBar seekBar;
    Song nextSong = null, prevSong = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);


        initializeData();

        Intent intent = getIntent();
        position = Integer.parseInt(intent.getStringExtra("position"));
        String curSong = intent.getStringExtra("user");
        Gson gson = new Gson();
        Song mySong = gson.fromJson(curSong, Song.class);
        songName.setText(mySong.getName());
        System.out.println("url: " + mySong.getUrl());
        artistName.setText(mySong.getArtist());
        if (MusicActivity.mp != null) {
            MusicActivity.mp.release();
            MusicActivity.mp = null;
            startPlaying(mySong);
        }
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state.equals("play")) {
                    startPlaying(mySong);
                } else {
                    stopPlaying();
                }
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextSong();
            }
        });


        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevSong();
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getMax() == seekBar.getProgress()) {
                    seekBarReachedToEnd = true;
                    mp.release();
                    seekBar.setProgress(0);
                    play_pause.setImageResource(R.drawable.play);
                    state = "play";
                    return;
                }
                if (mp != null && fromUser) {
                    mp.seekTo(progress * 1000);
                }
                else{
//
                    Log.v("Media Player","Holds Null");
                }
            }
        });

        Handler mHandler = new Handler();

        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (mp != null) {
                        int mCurrentPosition = mp.getCurrentPosition() / 1000;
                        if (!isplay || currentSongPosition == position)
                            seekBar.setProgress(mCurrentPosition);
                        else seekBar.setProgress(0);
                    } else {
                        Log.v("Song", "Finished");
                    }
                    mHandler.postDelayed(this, 1000);
                } catch (Exception e) {
                    seekBar.setProgress(0);
                    play_pause.setImageResource(R.drawable.play);
                    mp = null;
                    state = "play";
                    e.printStackTrace();
                }
            }
        });
    }

    private void initializeData() {
        songName = findViewById(R.id.tvsongName);
        artistName = findViewById(R.id.tvSingerName);
        play_pause = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekBar);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);
    }

    private void startPlaying(Song playPauseButtonSong) {
        if (!isplay && prevSong != playPauseButtonSong && nextOrPrevActive == false) {
            nextOrPrevActive = true;
            songName.setText(playPauseButtonSong.getName());
            artistName.setText(playPauseButtonSong.getArtist());
            prepareSong(playPauseButtonSong);
            prevSong = playPauseButtonSong;
        } else if (isplay && currentSongPosition != position) {
            isplay = false;
            Song nextSong = getNextPrevSong();
            songName.setText(nextSong.getName());
            artistName.setText(nextSong.getArtist());
            prepareSong(nextSong);
            prevSong = nextSong;

        } else if (isplay && currentSongPosition == position) {
            if (state.equals("play")) {
                state = "pause";
                try {
                    if (mp != null && !mp.isPlaying()) {
                        System.out.println("mp!=null");
                        mp.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                play_pause.setImageResource(R.drawable.pause);
            }
        } else if (seekBarReachedToEnd) {
            seekBarReachedToEnd = false;
            prepareSong(prevSong);
            prevSong = prevSong;

        }

        try {
            if (mp != null && !mp.isPlaying()) {
                mp.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        state = "pause";
        play_pause.setImageResource(R.drawable.pause);
        Handler mHandler = new Handler();
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (mp != null) {
                        int mCurrentPosition = mp.getCurrentPosition() / 1000;
                        if (!isplay || currentSongPosition == position)
                            seekBar.setProgress(mCurrentPosition);
                        else seekBar.setProgress(0);
                    } else {
                        Log.v("Medial Player", "Media player Holds Null");
                    }
                    mHandler.postDelayed(this, 1000);
                } catch (Exception e) {
                    seekBar.setProgress(0);
                    play_pause.setImageResource(R.drawable.play);
                    mp = null;
                    state = "play";
                    e.printStackTrace();
                }
            }
        });
    }

    private void stopPlaying() {
        if (isplay && currentSongPosition != position) {
            state = "pause";
            play_pause.setImageResource(R.drawable.pause);
            if (mp != null && mp.isPlaying()) mp.pause();
            startPlaying(getNextPrevSong());
        } else if (currentSongPosition == position) {
            if (state.equals("pause")) {
                state = "play";
                try {
                    if (mp != null && mp.isPlaying()) {
                        System.out.println("mp!=null");
                        mp.pause();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                play_pause.setImageResource(R.drawable.play);
            }
        } else {
            try {
                if (mp != null && !mp.isPlaying()) {
                    System.out.println("mp!=null");
                    mp.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            state = "play";
            play_pause.setImageResource(R.drawable.play);
        }

    }

    private void nextSong() {
        if (position < SongData.getInstance().getListOfSongs().size() - 1) {
            play_pause.setImageResource(R.drawable.play);
            Song song = SongData.getInstance().getListOfSongs().get(position + 1);
            position = position + 1;
            if (currentSongPosition == position) {
                if (state.equals("pause")) {
                    play_pause.setImageResource(R.drawable.pause);
                }
            } else seekBar.setProgress(0);

            songName.setText(song.getName());
            artistName.setText(song.getArtist());

            setNextPrevSong(song);
            nextOrPrevActive = true;
            isplay = true;
        } else {
            Log.v("listOfSongs: ", "No Song exist in this position");
        }
    }


    private void prevSong() {
        if (position > 0) {
            play_pause.setImageResource(R.drawable.play);
            Song song = SongData.getInstance().getListOfSongs().get(position - 1);
            songName.setText(song.getName());
            artistName.setText(song.getArtist());
            position -= 1;
            isplay = true;
            if (currentSongPosition == position) {
                System.out.println("prev wala");
                if (state.equals("pause")) {
                    play_pause.setImageResource(R.drawable.pause);
                }
            } else seekBar.setProgress(0);
            nextOrPrevActive = true;
            setNextPrevSong(song);
        } else {
            Log.v("listOfSongs: ", "No Song exist in this position");
        }
    }

    private void setNextPrevSong(Song song) {
        this.nextSong = song;
    }

    private Song getNextPrevSong() {
        return this.nextSong;
    }

    void prepareSong(Song mySong) {
        if (mySong.getUrl() != null) {
            firstTime = false;
            mp = null;
            seekBar.setProgress(0);
            if (mp == null) {
                mp = new MediaPlayer();
                try {
                    mp.setDataSource(mySong.getUrl());
                    currentSongPosition = position;
                    mp.prepare();
                    seekBar.setMax(mp.getDuration() / 1000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
