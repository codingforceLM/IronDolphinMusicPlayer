package de.codingforcelm.idmp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.List;

import de.codingforcelm.idmp.audio.AudioLoader;

public class AlbumCardAdapter extends BaseAdapter {

    private List<PhysicalAlbum> albums;
    private LayoutInflater songInf;

    public AlbumCardAdapter(Context c) {
        this.albums = new AudioLoader(c).getSongsFromAlbum();
        this.songInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardView lay = (CardView) songInf.inflate(R.layout.item_layout, parent, false);
        TextView title = (TextView) lay.findViewById(R.id.title);
        TextView artist = (TextView) lay.findViewById(R.id.artist);

        PhysicalAlbum song = albums.get(position);
        title.setText(song.getTitle());
        artist.setText(song.getArtist());
        lay.setTag(position);

        return lay;
    }
}
