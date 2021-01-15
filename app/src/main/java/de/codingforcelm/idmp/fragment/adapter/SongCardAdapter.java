package de.codingforcelm.idmp.fragment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.activity.MainActivity;
import de.codingforcelm.idmp.loader.AudioLoader;
import de.codingforcelm.idmp.locale.LocaleSong;

/**
 * CardAdapter class for songs
 */
public class SongCardAdapter extends RecyclerView.Adapter<SongCardAdapter.SongCardViewHolder> {
    private static final String LOG_TAG = "SongCardAdapter";
    private final Context context;
    private final String playContext;
    private final String playContextType;
    private List<LocaleSong> songList;
    private List<LocaleSong> songListCopy;
    private onLongItemClickListener longClickListener;
    private AudioLoader audioLoader;

    /**
     * Default constructor
     * @param songList list of songs to display
     * @param context context
     * @param playContextType type of playContext
     * @param playContext playContext which songs are played from
     */
    public SongCardAdapter(ArrayList<LocaleSong> songList, Context context, String playContextType, String playContext) {
        this.songList = songList;
        this.context = context;
        this.songListCopy = new ArrayList<>();
        this.songListCopy.addAll(songList);
        this.playContextType = playContextType;
        this.playContext = playContext;
        audioLoader = new AudioLoader(context);
    }

    @Override
    public SongCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        SongCardViewHolder cvh = new SongCardViewHolder(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(SongCardViewHolder holder, int position) {
        Log.e(LOG_TAG, "-- onBindViewHolder --");
        LocaleSong currentItem = songList.get(position);
        holder.bind(currentItem);

        holder.itemView.setTag(position);
        Log.e(LOG_TAG, "Set on long click listener");
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.ItemLongClicked(v, position, currentItem.getId());
            }
            return true;
        });

        Log.e(LOG_TAG, "Set on click listener");
        holder.itemView.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).songSelect(currentItem.getId(), playContext, playContextType);
            }
        });

        holder.itemView.setTag(position);
        Bitmap cover = audioLoader.getAlbumCoverForSong(currentItem.getId());
        if(cover != null) {
            ImageView img = holder.itemView.findViewById(R.id.item_image);
            img.setImageBitmap(cover);
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    /**
     * Method to set the long click listener for a song card
     * @param onLongItemClickListener interface onLongItemClickListener
     */
    public void setOnLongItemClickListener(onLongItemClickListener onLongItemClickListener) {
        this.longClickListener = onLongItemClickListener;
    }

    public void setData(List<LocaleSong> data) {
        if (songList != null) {
            songList.clear();
            songList.addAll(data);
        } else {
            songList = data;
        }
        songListCopy = new ArrayList<>();
        songListCopy.addAll(songList);

        notifyDataSetChanged();
    }
    /**
     * Method to filter the list inside RecyclerView.
     * Checks if the album title contains a given String
     * @param text text to filter
     */
    public void filter(String text) {
        songList.clear();
        if (text.isEmpty()) {
            songList.addAll(songListCopy);
        } else {
            text = text.toLowerCase();
            for (LocaleSong song : songListCopy) {
                if (song.getTitle().toLowerCase().contains(text)) {
                    songList.add(song);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Interface to get information from long click
     */
    public interface onLongItemClickListener {
        void ItemLongClicked(View v, int position, long songId);
    }

    /**
     * ViewHolder class for song cards
     */
    public static class SongCardViewHolder extends RecyclerView.ViewHolder {
        public ImageView item_image;
        public TextView item_title;
        public TextView item_artist;
        public long currentSongID;

        /**
         * Default constructor
         * @param itemView view
         */
        public SongCardViewHolder(View itemView) {
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
