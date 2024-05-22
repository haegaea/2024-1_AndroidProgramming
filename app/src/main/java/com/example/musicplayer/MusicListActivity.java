package com.example.musicplayer;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class MusicListActivity extends AppCompatActivity {

    private ListView musicListView;
    private List<MusicItem> musicItems;
    private static final int REQUEST_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicListView = findViewById(R.id.music_list_view);
        musicItems = new ArrayList<>();

        // 권한 요청
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);

        // 정적으로 MP3 파일 정보 추가
        addStaticMusicItems();

        // 동적으로 MP3 파일 정보 추가
        addDynamicMusicItems();

        MusicListAdapter adapter = new MusicListAdapter(this, musicItems);
        musicListView.setAdapter(adapter);

        musicListView.setOnItemClickListener((parent, view, position, id) -> {
            MusicItem selectedMusicItem = musicItems.get(position);
            Intent intent = new Intent(MusicListActivity.this, MusicPlayerActivity.class);
            intent.putExtra("title", selectedMusicItem.getTitle());
            intent.putExtra("artist", selectedMusicItem.getArtist());
            intent.putExtra("duration", selectedMusicItem.getDuration());
            intent.putExtra("resId", selectedMusicItem.getResId());
            startActivity(intent);
        });
    }

    private void addStaticMusicItems() {
        Resources resources = getResources();
        String packageName = getPackageName();

        int[] resIds = {
                resources.getIdentifier("aespa_supernova", "raw", packageName),
                resources.getIdentifier("ahnyeeun_theredknot", "raw", packageName),
                resources.getIdentifier("taeteon_fourseasons", "raw", packageName)
        };

        if (resIds[0] != 0) {
            musicItems.add(new MusicItem("Supernova", "Aespa", resIds[0], this));
        }
        if (resIds[1] != 0) {
            musicItems.add(new MusicItem("홍연", "안예은", resIds[1], this));
        }
        if (resIds[2] != 0) {
            musicItems.add(new MusicItem("사계", "태연", resIds[2], this));
        }
    }

    private void addDynamicMusicItems() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cursor = contentResolver.query(uri, projection, selection, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                int durationInMillis = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                int minutes = (durationInMillis / 1000) / 60;
                int seconds = (durationInMillis / 1000) % 60;
                String duration = String.format("%d:%02d", minutes, seconds);

                musicItems.add(new MusicItem(title, artist, duration, data));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}
