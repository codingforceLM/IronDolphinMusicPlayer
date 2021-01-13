package de.codingforcelm.idmp.fragment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.activity.playlist.PlaylistCreateActivity;
import de.codingforcelm.idmp.loader.AudioLoader;
import de.codingforcelm.idmp.locale.LocaleSong;

/**
 * CardAdapter class for create playlist activity
 */
public class PlaylistCreateCardAdapter extends RecyclerView.Adapter<PlaylistCreateCardAdapter.SelectionCardViewHolder> {
    public static final String LOG_TAG = "PlaylistCreateCAdapter";
    private final List<PlaylistCreateActivity.PlaylistSelection> selectionListCopy;
    private final List<PlaylistCreateActivity.PlaylistSelection> selectionList;
    private AudioLoader audioLoader;

    /**
     * Default constructor
     * @param selectionList PlaylistSelection list
     * @param context context
     */
    public PlaylistCreateCardAdapter(List<PlaylistCreateActivity.PlaylistSelection> selectionList, Context context) {
        this.selectionList = selectionList;
        this.selectionListCopy = new ArrayList<>();
        this.selectionListCopy.addAll(selectionList);
        audioLoader = new AudioLoader(context);
    }

    @Override
    public SelectionCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        SelectionCardViewHolder cvh = new SelectionCardViewHolder(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionCardViewHolder holder, int position) {
        Log.e(LOG_TAG, "-- onBindViewHolder --");
        PlaylistCreateActivity.PlaylistSelection selection = selectionList.get(position);
        holder.bind(selection);
        Log.e(LOG_TAG, "Set on click listener");
        holder.itemView.setOnClickListener(v -> {
            selection.setSelected(!selection.isSelected());
            int color = -1;
            if (selection.isSelected()) {
                color = Color.GRAY;
            } else {
                color = Color.WHITE;
            }
            v.setBackgroundColor(color);
        });

        int color = -1;
        if (selection.isSelected()) {
            color = Color.GRAY;
        } else {
            color = Color.WHITE;
        }
        holder.itemView.setBackgroundColor(color);

        Bitmap cover = audioLoader.getAlbumCoverForSong(selection.getSong().getId());
        if(cover != null) {
            ImageView img = holder.itemView.findViewById(R.id.item_image);
            img.setImageBitmap(cover);
        }
    }

    @Override
    public int getItemCount() {
        return selectionList.size();
    }

    public List<PlaylistCreateActivity.PlaylistSelection> getSelectedList() {
        List<PlaylistCreateActivity.PlaylistSelection> selectedList = new ArrayList<>();

        for (PlaylistCreateActivity.PlaylistSelection selection : selectionListCopy) {
            if (selection.isSelected()) {
                selectedList.add(selection);
            }
        }

        return selectedList;
    }

    /**
     * Method to filter the list inside RecyclerView.
     * Checks if the album title contains a given String
     * @param text text to filter
     */
    public void filter(String text) {
        selectionList.clear();
        if (text.isEmpty()) {
            selectionList.addAll(selectionListCopy);
        } else {
            text = text.toLowerCase();
            for (PlaylistCreateActivity.PlaylistSelection song : selectionListCopy) {
                if (song.getSong().getTitle().toLowerCase().contains(text)) {
                    selectionList.add(song);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for selection cards
     */
    public static class SelectionCardViewHolder extends RecyclerView.ViewHolder {
        public ImageView item_image;
        public TextView item_title;
        public TextView item_artist;
        public long currentSongID;

        /**
         * Default constructor
         * @param itemView view
         */
        public SelectionCardViewHolder(View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_title = itemView.findViewById(R.id.item_title);
            item_artist = itemView.findViewById(R.id.item_subtitle);
        }

        private void bind(PlaylistCreateActivity.PlaylistSelection selection) {
            LocaleSong song = selection.getSong();
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

