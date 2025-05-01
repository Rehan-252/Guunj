package com.example.guunj.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guunj.Models.Songs;
import com.example.guunj.Musicadapter;
import com.example.guunj.R;
import com.example.guunj.databinding.FragmentMusicBinding;

import java.util.ArrayList;
import java.util.List;

public class MusicFragment extends Fragment {
    FragmentMusicBinding binding;
    Musicadapter musicadapter;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMusicBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // Dummy song list for now
        List<Songs> songList = new ArrayList<>();
        songList.add(new Songs("Song 1", "Artist 1", "3:45", R.drawable.ic_music));
        songList.add(new Songs("Song 2", "Artist 2", "4:20", R.drawable.ic_music));
        songList.add(new Songs("Song 3", "Artist 3", "2:58", R.drawable.ic_music));

        // Set up RecyclerView
        binding.recyclerViewSongs.setHasFixedSize(true);
        binding.recyclerViewSongs.setItemViewCacheSize(10);
        binding.recyclerViewSongs.setLayoutManager(new LinearLayoutManager(getContext()));

        musicadapter = new Musicadapter(songList); // Constructor needs List<Songs>
        binding.recyclerViewSongs.setAdapter(musicadapter);

        // Set total songs count
        binding.totalSongsText.setText("Total Songs: " + songList.size());

        return view;
    }
}