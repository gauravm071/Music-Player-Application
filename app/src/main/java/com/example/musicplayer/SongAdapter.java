package com.example.musicplayer;

import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    ArrayList<Song> listOfSongs = new ArrayList<>();
    private RecyclerViewClickInterface recyclerViewClickInterface;

    public SongAdapter(ArrayList<Song> listOfSongs, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.listOfSongs = listOfSongs;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }


    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.song_item, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, int position) {
        Song mysong = listOfSongs.get(position);
        holder.name.setText(mysong.getName());
        if (mysong.getBitmap() != null) {
            holder.imageView.setImageBitmap(mysong.getBitmap());
        } else {
            holder.imageView.setImageResource(R.drawable.music_icon);
        }
    }

    @Override
    public int getItemCount() {
        return listOfSongs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, artistName;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickInterface.onItemClick(getAdapterPosition());
                }

            });
            name = itemView.findViewById(R.id.tvName);
            imageView = itemView.findViewById(R.id.ivid);
        }
    }

}
