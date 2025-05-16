package com.example.guunj;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.guunj.Models.Songs;
import com.example.guunj.databinding.ItemMusicBinding;

import java.io.IOException;
import java.util.List;

public class Musicadapter extends RecyclerView.Adapter<Musicadapter.MusicViewHolder> {

    private final List<Songs> songList;
    private final Context context;
    private final OnSongClickListener listener;

    public Musicadapter(Context context, List<Songs> songList, OnSongClickListener listener) {
        this.context = context;
        this.songList = songList;
        this.listener = listener;
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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(song);
            }
        });

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(context, Uri.parse(String.valueOf(song.getAlbumUri())));

            byte[] art = mmr.getEmbeddedPicture();
            if (art != null) {
                Glide.with(context)
                        .asBitmap()
                        .load(art)
                        .placeholder(R.drawable.music)
                        .into(holder.binding.songThumbnail);
            } else {
                holder.binding.songThumbnail.setImageResource(R.drawable.music);
            }
        } catch (Exception e) {
            holder.binding.songThumbnail.setImageResource(R.drawable.music);
        } finally {
            try {
                mmr.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
    public interface OnSongClickListener {
        void onSongClick(Songs song);
    }
    static class MusicViewHolder extends RecyclerView.ViewHolder {
        ItemMusicBinding binding;

        public MusicViewHolder(ItemMusicBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
