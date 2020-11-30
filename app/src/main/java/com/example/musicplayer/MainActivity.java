package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickInterface,Serializable {
    public int REQUEST_CODE=1;
    ArrayList<Song> listOfSongs= new ArrayList<>();
    RecyclerView songlist;
    LinearLayout songitem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songlist= findViewById(R.id.songlist);
        songitem= findViewById(R.id.songitem);
        checkPermission();
        
    }
    public void checkPermission(){
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
        else{
            getAllAudioFromDevice(MainActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CODE){
            if(permissions.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this, "Permission Granted",Toast.LENGTH_LONG).show();
                getAllAudioFromDevice(MainActivity.this);
            }
            else {
                Toast.makeText(MainActivity.this, "Permission Denied",Toast.LENGTH_LONG).show();
            }
        }
    }
   public void getAllAudioFromDevice(final Context context){
        final ArrayList<Song> temp= new ArrayList<>();
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection= {MediaStore.Audio.AudioColumns.DATA,MediaStore.Audio.AudioColumns.ALBUM,MediaStore.Audio.ArtistColumns.ARTIST};
        Cursor cursor= context.getContentResolver().query(uri,projection,null,null,null);
        if(cursor!=null){
            while(cursor.moveToNext()){
                Song song=new Song();
                String path= cursor.getString(0);
                String album= cursor.getString(1);
                String artist= cursor.getString(2);
                String name= path.substring(path.lastIndexOf("/")+1);
                String songURL= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                song.setName(name);
                song.setAlbum(album);
                song.setArtist(artist);
                song.setPath(path);
                song.setUrl(songURL);
                listOfSongs.add(song);
            }
            cursor.close();
        }
//       System.out.println("size is :"+listOfSongs.size());
       SongData.getInstance().setListOfSongs(listOfSongs);
       songlist.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));
       songlist.setAdapter(new SongAdapter(listOfSongs,this::onItemClick));
   }

   public void startMusic(View v){

   }


    @Override
    public void onItemClick(int position) {

        System.out.println("entered here");
        Intent intent= new Intent(MainActivity.this,MusicActivity.class);
        Song curSong= listOfSongs.get(position);
        Gson gson=new Gson();
        String myuser= gson.toJson(curSong);
        System.out.println("pos: "+position);
        intent.putExtra("size",listOfSongs.size());
        intent.putExtra("position",String.valueOf(position));
        intent.putExtra("user",myuser);
        startActivity(intent);
    }
}