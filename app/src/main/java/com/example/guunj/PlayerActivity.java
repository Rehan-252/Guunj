package com.example.guunj;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.guunj.Models.Songs;
import com.example.guunj.databinding.ActivityPlayerBinding;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {

    private ActivityPlayerBinding binding;
    private MediaPlayer mediaPlayer;
    private ArrayList<Songs> songList;
    private int position = 0;
    private Handler handler = new Handler();
    private Runnable updateSeekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        songList = getIntent().getParcelableArrayListExtra("songList");
        position = getIntent().getIntExtra("position", 0);

        if (songList != null && !songList.isEmpty()) {
            playSongAt(position);
        } else {
            // handle empty or null list
            Toast.makeText(this, "No songs received", Toast.LENGTH_SHORT).show();
            finish();
            return;// Optional: close activity if no data
        }

        binding.playPauseBtn.setOnClickListener(v -> togglePlayPause());
        binding.nextBtn.setOnClickListener(v -> playNext());
        binding.prevBtn.setOnClickListener(v -> playPrevious());
        binding.backBtn.setOnClickListener(v -> finish());

        binding.songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void playSongAt(int position) {
        if (position < 0 || position >= songList.size()) return;

        Songs song = songList.get(position);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        new Thread(() -> {
            try {
                MediaPlayer tempPlayer = new MediaPlayer();
                tempPlayer.setDataSource(this, Uri.parse(String.valueOf(song.getSongUri())));
                tempPlayer.setOnPreparedListener(mp -> {
                    mediaPlayer = mp;
                    mediaPlayer.start();

                    runOnUiThread(() -> {
                        updateUI(song);
                        setupSeekBar();
                        updatePlayPauseIcon();
                    });
                });
                tempPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Error playing song", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();

    }

    private void updateUI(Songs song) {
        binding.songTitle.setText(song.getTitle());
        binding.songArtist.setText(song.getArtist());
        binding.totalDuration.setText(song.getDuration());

        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(this, song.getSongUri());
            byte[] art = mmr.getEmbeddedPicture();

            if (art != null) {
                Glide.with(this).asBitmap().load(art).placeholder(R.drawable.music).into(binding.songImage);
            } else {
                binding.songImage.setImageResource(R.drawable.music);
            }

            mmr.release();
        } catch (Exception e) {
            binding.songImage.setImageResource(R.drawable.music);
            e.printStackTrace();
        }
    }

    private void setupSeekBar() {
        if (mediaPlayer == null) return;

        binding.songSeekBar.setMax(mediaPlayer.getDuration());

        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        int currentPos = mediaPlayer.getCurrentPosition();
                        binding.songSeekBar.setProgress(currentPos);
                        binding.currentTime.setText(formatDuration(currentPos));
                        handler.postDelayed(this, 1000);
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace(); // Optional: log or handle error
                }
            }
        };
        handler.post(updateSeekbar);
    }

    private String formatDuration(int millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void updatePlayPauseIcon() {
        if (mediaPlayer.isPlaying()) {
            binding.playPauseBtn.setImageResource(R.drawable.play1); // your pause icon
        } else {
            binding.playPauseBtn.setImageResource(R.drawable.pause); // your play icon
        }
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) return;

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
        updatePlayPauseIcon();
    }

    private void playNext() {
        position = (position + 1) % songList.size();
        playSongAt(position);
    }

    private void playPrevious() {
        position = (position - 1 + songList.size()) % songList.size();
        playSongAt(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (handler != null && updateSeekbar != null) {
            handler.removeCallbacks(updateSeekbar);
        }

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
