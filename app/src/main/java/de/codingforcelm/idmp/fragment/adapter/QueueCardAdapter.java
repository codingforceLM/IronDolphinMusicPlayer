package de.codingforcelm.idmp.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import de.codingforcelm.idmp.PhysicalSong;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.audio.AudioLoader;
import de.codingforcelm.idmp.structure.queue.OnQueueChangedListener;

public class QueueCardAdapter extends RecyclerView.Adapter<QueueCardAdapter.QueueCardViewHolder> implements OnQueueChangedListener {

    private AudioLoader audioLoader;
    private List<PhysicalSong> songList;

    public static class QueueCardViewHolder extends RecyclerView.ViewHolder {

        public ImageView item_image;
        public TextView item_title;
        public TextView item_artist;
        public long currentSongID;

        public QueueCardViewHolder(@NonNull View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_title = itemView.findViewById(R.id.item_title);
            item_artist = itemView.findViewById(R.id.item_subtitle);
        }

        private void bind(PhysicalSong song) {
            if(song != null) {
                item_title.setText(song.getTitle());
                item_artist.setText(song.getArtist());
                currentSongID = song.getId();
            } else {
                item_title.setText("---");
                item_artist.setText("---");
                currentSongID = -1;
            }

        }
    }

    public QueueCardAdapter(List<PhysicalSong> songList, Context context) {
        this.songList = songList;
        audioLoader = new AudioLoader(context);
    }

    @NonNull
    @Override
    public QueueCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        QueueCardAdapter.QueueCardViewHolder cvh = new QueueCardAdapter.QueueCardViewHolder(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull QueueCardViewHolder holder, int position) {
        PhysicalSong current = songList.get(position);
        holder.bind(current);

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    @Override
    public void onQueueChanged(Queue<String> queue) {
        Iterator<String> it = queue.iterator();
        List<PhysicalSong> songs = new ArrayList<>();

        while(it.hasNext()) {
            String mediaId = it.next();
            PhysicalSong s = audioLoader.getSong(Long.parseLong(mediaId));
            if(s != null) {
                songs.add(s);
            }
        }

        songList = songs;

        notifyDataSetChanged();
    }

}
