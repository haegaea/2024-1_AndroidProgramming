package com.example.musicplayer;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicItem {
    private String title;
    private String artist;
    private String duration;
    private int resId;
    private String dataPath;

    // 정적 파일을 위한 생성자
    public MusicItem(String title, String artist, int resId, Context context) {
        this.title = title;
        this.artist = artist;
        this.resId = resId;
        this.dataPath = null;
        this.duration = getDurationFromResource(resId, context);
    }

    // 동적 파일을 위한 생성자
    public MusicItem(String title, String artist, String duration, String dataPath) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.dataPath = dataPath;
        this.resId = -1;
    }

    private String getDurationFromResource(int resId, Context context) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, resId);
        int durationInMillis = mediaPlayer.getDuration();
        mediaPlayer.release();

        int minutes = (durationInMillis / 1000) / 60;
        int seconds = (durationInMillis / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }

    public int getResId() {
        return resId;
    }

    public String getDataPath() {
        return dataPath;
    }
}
