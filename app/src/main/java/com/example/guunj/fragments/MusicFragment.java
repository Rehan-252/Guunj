package com.example.guunj.fragments;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guunj.Models.Songs;
import com.example.guunj.Musicadapter;
import com.example.guunj.R;
import com.example.guunj.databinding.FragmentMusicBinding;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MusicFragment extends Fragment {
    FragmentMusicBinding binding;
    Musicadapter musicadapter;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMusicBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // Dummy song list for now
        List<Songs> songList = fechsongfromdevice(requireContext());
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

    private List<Songs> fechsongfromdevice(Context context) {
        List<Songs> songsList = new ArrayList<>();

        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";

        String[] projection = {MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.DURATION};

        Cursor cursor = context.getContentResolver().query(mediaUri,projection,selection,null,MediaStore.Audio.Media.TITLE + " ASC",null);

        if (cursor != null){
            while (cursor.moveToNext()){
                String titalC = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artistC = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String albumC = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                long durationMs = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                long durationC = Long.parseLong(String.format(Locale.getDefault(), "%d:%02d", TimeUnit.MILLISECONDS.toMinutes(durationMs),TimeUnit.MILLISECONDS.toSeconds(durationMs)));

                int thumbnailC = R.drawable.ic_music;
                songsList.add(new Songs(titalC,artistC,albumC,durationC,thumbnailC));
            }
            cursor.close();
        }
        return songsList;
    }
}