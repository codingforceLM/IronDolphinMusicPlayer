package de.codingforcelm.idmp.fragment.adapter;

import android.app.Application;
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
import de.codingforcelm.idmp.database.entity.PlaylistEntry;
import de.codingforcelm.idmp.database.entity.relation.PlaylistWithEntries;
import de.codingforcelm.idmp.database.repository.PlaylistRepository;
import de.codingforcelm.idmp.fragment.tab.PlaylistFragment;
import de.codingforcelm.idmp.loader.AudioLoader;
import de.codingforcelm.idmp.local.LocalSong;
/**
 * CardAdapter for playlists list
 */
public class PlaylistCardAdapter extends RecyclerView.Adapter<PlaylistCardAdapter.PlaylistCardViewHolder> {
    private static final String LOG_TAG = "PlaylistCardAdapter";
    private final Application application;
    private final Context context;
    private final PlaylistRepository repository;
    private List<PlaylistWithEntries> playlistList;
    private List<PlaylistWithEntries> playlistListCopy;
    private onLongItemClickListener longClickListener;
    private AudioLoader audioLoader;

    /**
     * Default constructor
     * @param application application
     * @param context context
     */
    public PlaylistCardAdapter(Application application, Context context) {
        this.context = context;
        this.application = application;
        this.playlistList = new ArrayList<>();
        this.playlistListCopy = new ArrayList<>();
        this.playlistListCopy.addAll(playlistList);
        this.repository = PlaylistRepository.getInstance(application);
        audioLoader = new AudioLoader(context);
    }

    @Override
    public PlaylistCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_layout, parent, false);
        PlaylistCardViewHolder cvh = new PlaylistCardViewHolder(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(PlaylistCardViewHolder holder, int position) {
        Log.e(LOG_TAG, "-- onBindViewHolder --");
        PlaylistWithEntries currentItem = playlistList.get(position);
        holder.bind(currentItem);
        holder.itemView.setTag(position);

        holder.itemView.setTag(position);
        Log.e(LOG_TAG, "Set on long click listener");
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.ItemLongClicked(v, position, currentItem.getPlaylist().getListId());
            }
            return true;
        });

        Log.e(LOG_TAG, "Set on click listener");
        holder.itemView.setOnClickListener(v -> {
            int itemPos = (int) v.getTag();
            if (context instanceof MainActivity) {
                PlaylistFragment fragment = new PlaylistFragment((int) v.getTag(), playlistList.get(itemPos).getPlaylist().getListId());
                ((MainActivity) context).placeFragment(fragment, R.id.mainFrame);
                ((MainActivity) context).setTitle(playlistList.get(itemPos).getPlaylist().getName());
            }
        });

        if(position == 0) {
            List<PlaylistEntry> entries = currentItem.getEntries();
            if(entries.size() >= 1) {
                Log.e(LOG_TAG, "Set playlist cover as the cover of the first entry");
                PlaylistEntry entry = entries.get(0);
                LocalSong s = audioLoader.getSong(entry.getMediaId());
                Bitmap cover = audioLoader.getAlbumCoverForSong(s.getId());
                ImageView image = holder.itemView.findViewById(R.id.item_image);
                if(cover != null) {
                    image.setImageBitmap(cover);
                } else {
                    image.setImageResource(R.drawable.ic_item_default_image);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    /**
     * Method to set the long click listener for a playlist card
     * @param onLongItemClickListener interface onLongItemClickListener
     */
    public void setOnLongItemClickListener(onLongItemClickListener onLongItemClickListener) {
        this.longClickListener = onLongItemClickListener;
    }

    /**
     * Explicitly set the data of the adapter
     * @param data data
     */
    public void setData(List<PlaylistWithEntries> data) {
        if (playlistList != null) {
            playlistList.clear();
            playlistList.addAll(data);
        } else {
            playlistList = data;
        }
        playlistListCopy = new ArrayList<>();
        playlistListCopy.addAll(playlistList);

        notifyDataSetChanged();
    }
    /**
     * Method to filter the list inside RecyclerView.
     * Checks if the album title contains a given String
     * @param text text to filter
     */
    public void filter(String text) {
        playlistList.clear();
        if (text.isEmpty()) {
            playlistList.addAll(playlistListCopy);
        } else {
            text = text.toLowerCase();
            for (PlaylistWithEntries playlist : playlistListCopy) {
                if (playlist.getPlaylist().getName().toLowerCase().contains(text)) {
                    playlistList.add(playlist);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Interface to get information from long click
     */
    public interface onLongItemClickListener {
        void ItemLongClicked(View v, int position, String playlistID);
    }

    /**
     * ViewHolder class for playlist cards
     */
    public static class PlaylistCardViewHolder extends RecyclerView.ViewHolder {
        public ImageView item_image;
        public TextView item_title;
        public TextView item_artist;

        /**
         * Default constructor
         * @param itemView view
         */
        public PlaylistCardViewHolder(View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_title = itemView.findViewById(R.id.item_title);
            item_artist = itemView.findViewById(R.id.item_subtitle);
        }

        private void bind(PlaylistWithEntries playlist) {
            item_title.setText(playlist.getPlaylist().getName());
            item_artist.setText(playlist.getEntries().size() + " Songs");
        }

    }

}
