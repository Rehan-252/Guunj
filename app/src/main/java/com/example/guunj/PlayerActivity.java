package com.example.guunj;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.guunj.databinding.ActivityPlayerBinding;

import java.io.IOException;

public class PlayerActivity extends AppCompatActivity {

    ActivityPlayerBinding binding;
    MediaPlayer mediaPlayer;
    private static final String TAG = "PlayerActivity";
    boolean isPlay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable full screen (edge-to-edge)
        EdgeToEdge.enable(this);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve song details from the launching Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String artist = intent.getStringExtra("artist");
        String duration = intent.getStringExtra("duration");
        String uriString = intent.getStringExtra("uri");

        // Set title, artist, and duration; provide fallbacks if null
        binding.songTitle.setText((title != null) ? title : "Unknown Title");
        binding.songArtist.setText((artist != null) ? artist : "Unknown Artist");
        binding.totalDuration.setText((duration != null) ? duration : "00:00");

        // Parse the song URI
        Uri songUri = null;
        try {
            if (uriString != null) {
                songUri = Uri.parse(uriString);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing song URI", e);
        }

        // Load album art using MediaMetadataRetriever
        if (songUri != null) {
            android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
            try {
                mmr.setDataSource(this, songUri);
                byte[] art = mmr.getEmbeddedPicture();

                if (art != null) {
                    Glide.with(this)
                            .asBitmap()
                            .load(art)
                            .placeholder(R.drawable.music)
                            .into(binding.songImage);
                } else {
                    binding.songImage.setImageResource(R.drawable.music); // fallback if no album art
                }
            } catch (Exception e) {
                e.printStackTrace();
                binding.songImage.setImageResource(R.drawable.music); // fallback if error
            } finally {
                try {
                    mmr.release();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            binding.songImage.setImageResource(R.drawable.music);
        }


        // Attempt to initialize and start MediaPlayer if we have a valid song URI
        if (songUri != null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(this, songUri);
                mediaPlayer.prepare();
                mediaPlayer.start();
                isPlay = true;
                binding.playPauseBtn.setImageResource(R.drawable.play1);
                // Optionally, update the play/pause button icon here if your layout supports it
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to play the selected song", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "MediaPlayer error: " + e.getMessage());
            }
        } else {
            Toast.makeText(this, "Invalid song URI", Toast.LENGTH_SHORT).show();
        }

        binding.playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null){
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        binding.playPauseBtn.setImageResource(R.drawable.pause);
                        isPlay = false;
                    }
                    else {
                        mediaPlayer.start();
                        binding.playPauseBtn.setImageResource(R.drawable.play1);
                        isPlay = true;
                    }
                }
            }
        });

        // Handle window insets for devices with notches or gesture navigation
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
                intent.putExtra("fragment", "music");
                intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up the MediaPlayer to avoid memory leaks
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
