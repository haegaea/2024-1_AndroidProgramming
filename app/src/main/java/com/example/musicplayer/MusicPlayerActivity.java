package com.example.musicplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MusicPlayerActivity extends AppCompatActivity {

    private TextView musicTitle;
    private SeekBar seekBar;
    private TextView duration;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread updateSeekBarThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> onBackPressed());

        musicTitle = findViewById(R.id.music_title);
        seekBar = findViewById(R.id.seekBar);
        duration = findViewById(R.id.duration);

        String title = getIntent().getStringExtra("title");
        String artist = getIntent().getStringExtra("artist");
        String durationText = getIntent().getStringExtra("duration");
        int resId = getIntent().getIntExtra("resId", -1);
        String dataPath = getIntent().getStringExtra("dataPath");

        musicTitle.setText(title);

        if (resId != -1) {
            mediaPlayer = MediaPlayer.create(this, resId);
        } else if (dataPath != null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(dataPath);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mediaPlayer != null) {
            mediaPlayer.setOnPreparedListener(mp -> {
                seekBar.setMax(mp.getDuration());
                duration.setText(formatTime(mp.getDuration()));
                startUpdatingSeekBar();
                mp.start();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MusicPlayerActivity", "MediaPlayer error: what=" + what + ", extra=" + extra);
                return true;
            });
        } else {
            Log.e("MusicPlayerActivity", "Invalid resource or data path");
            finish(); // 리소스 ID가 잘못된 경우 액티비티를 종료합니다.
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void startUpdatingSeekBar() {
        updateSeekBarThread = new Thread(() -> {
            while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                try {
                    Thread.sleep(1000);
                    runOnUiThread(() -> {
                        if (mediaPlayer != null) {
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            duration.setText(formatTime(mediaPlayer.getCurrentPosition()));
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        updateSeekBarThread.start();
    }

    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            if (updateSeekBarThread != null && updateSeekBarThread.isAlive()) {
                updateSeekBarThread.interrupt();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        super.onBackPressed();
    }
}
