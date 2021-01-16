package de.codingforcelm.idmp.fragment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.loader.AudioLoader;
import de.codingforcelm.idmp.locale.LocaleSong;
import de.codingforcelm.idmp.queue.OnQueueChangedListener;

/**
 * CardAdapter for queue list
 */
public class QueueCardAdapter extends RecyclerView.Adapter<QueueCardAdapter.QueueCardViewHolder> implements OnQueueChangedListener {
    private static final String LOG_TAG = "QueueCardAdapter";
    private final AudioLoader audioLoader;
    private List<LocaleSong> songList;

    /**
     * Default constructor
     * @param songList list of LocaleSongs
     * @param context context
     */
    public QueueCardAdapter(List<LocaleSong> songList, Context context) {
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
        Log.e(LOG_TAG, "-- onBindViewHolder --");
        LocaleSong current = songList.get(position);
        holder.bind(current);

        holder.itemView.setTag(position);
        Bitmap cover = audioLoader.getAlbumCoverForSong(current.getId());
        ImageView img = holder.itemView.findViewById(R.id.item_image);
        if(cover != null) {
            Log.e(LOG_TAG, "Set album cover for the item");
            img.setImageBitmap(cover);
        } else {
            img.setImageResource(R.drawable.ic_item_default_image);
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    @Override
    public void onQueueChanged(Queue<String> queue) {
        Iterator<String> it = queue.iterator();
        List<LocaleSong> songs = new ArrayList<>();

        while (it.hasNext()) {
            String mediaId = it.next();
            LocaleSong s = audioLoader.getSong(Long.parseLong(mediaId));
            if (s != null) {
                songs.add(s);
            }
        }

        songList = songs;

        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for queue cards
     */
    public static class QueueCardViewHolder extends RecyclerView.ViewHolder {

        public ImageView item_image;
        public TextView item_title;
        public TextView item_artist;
        public long currentSongID;

        /**
         * Default constructor
         * @param itemView view
         */
        public QueueCardViewHolder(@NonNull View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_title = itemView.findViewById(R.id.item_title);
            item_artist = itemView.findViewById(R.id.item_subtitle);
        }

        private void bind(LocaleSong song) {
            if (song != null) {
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

}
