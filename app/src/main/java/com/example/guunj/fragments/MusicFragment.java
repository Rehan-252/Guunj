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

        musicadapter = new Musicadapter(getContext(), songList);
        binding.recyclerViewSongs.setAdapter(musicadapter);

        // Set total songs count
        binding.totalSongsText.setText("Total Songs: " + songList.size());

        return view;
    }

    private List<Songs> fechsongfromdevice(Context context) {
        List<Songs> songsList = new ArrayList<>();

        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = context.getContentResolver().query(
                mediaUri,
                projection,
                selection,
                null,
                MediaStore.Audio.Media.TITLE + " ASC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                long durationMs = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

                // Format duration as mm:ss
                String durationFormatted = String.format(
                        Locale.getDefault(),
                        "%d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(durationMs),
                        TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
                );

                // Build content URI for album art
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                // Add to list
                songsList.add(new Songs(title, artist, durationFormatted, contentUri));

            } while (cursor.moveToNext());

            cursor.close();
        }

        return songsList;
    }

}