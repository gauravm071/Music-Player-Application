package com.example.musicplayer;

import java.util.ArrayList;

public class SongData {
    private static SongData songData = null;
    private static ArrayList<Song> listOfSongs= new ArrayList<>();
    private SongData(){

    }
    static SongData getInstance(){
        if(songData==null){
            songData= new SongData();
        }
        return songData;
    }

    public ArrayList<Song> getListOfSongs() {
        return listOfSongs;
    }

    public void setListOfSongs(ArrayList<Song> listOfSongs) {
        this.listOfSongs = listOfSongs;
    }
}
