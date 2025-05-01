package com.example.guunj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.guunj.Models.Songs;
import com.example.guunj.databinding.ItemMusicBinding;

import java.util.List;

public class Musicadapter extends RecyclerView.Adapter<Musicadapter.MusicViewHolder> {

    private final List<Songs> songList;
    private final Context context;

    public Musicadapter(Context context, List<Songs> songList) {
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemMusicBinding binding = ItemMusicBinding.inflate(inflater, parent, false);
        return new MusicViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        Songs song = songList.get(position);
        holder.binding.songTitle.setText(song.getTitle());
        holder.binding.songArtist.setText(song.getArtist());
        holder.binding.songDuration.setText(song.getDuration());

        Glide.with(context)
                .load(song.getArtist())
                .placeholder(R.drawable.music)
                .into(holder.binding.songThumbnail);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    static class MusicViewHolder extends RecyclerView.ViewHolder {
        ItemMusicBinding binding;

        public MusicViewHolder(ItemMusicBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
