package com.example.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MusicListAdapter extends BaseAdapter {

    private Context context;
    private List<MusicItem> musicItems;

    public MusicListAdapter(Context context, List<MusicItem> musicItems) {
        this.context = context;
        this.musicItems = musicItems;
    }

    @Override
    public int getCount() {
        return musicItems.size();
    }

    @Override
    public Object getItem(int position) {
        return musicItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_music, parent, false);
        }

        MusicItem musicItem = musicItems.get(position);

        TextView titleView = convertView.findViewById(R.id.music_title);
        TextView artistView = convertView.findViewById(R.id.music_artist);
        TextView durationView = convertView.findViewById(R.id.music_duration);

        titleView.setText(musicItem.getTitle());
        artistView.setText(musicItem.getArtist());
        durationView.setText(musicItem.getDuration());

        return convertView;
    }
}
