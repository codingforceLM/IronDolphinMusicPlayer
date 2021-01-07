package de.codingforcelm.idmp.fragment.adapter;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.MainActivity;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.fragment.tab.PlaylistFragment;
import de.codingforcelm.idmp.database.repository.PlaylistRepository;
import de.codingforcelm.idmp.database.entity.relation.PlaylistWithEntries;

public class PlaylistCardAdapter extends RecyclerView.Adapter<PlaylistCardAdapter.PlaylistCardViewHolder> {
    public static final String LOG_TAG = "PlaylistCardAdapter";
    private final Application application;
    private final Context context;
    private final PlaylistRepository repository;
    private List<PlaylistWithEntries> playlistList;
    private List<PlaylistWithEntries> playlistListCopy;
    private onLongItemClickListener longClickListener;

    public PlaylistCardAdapter(Application application, Context context) {
        this.context = context;
        this.application = application;
        this.playlistList = new ArrayList<>();
        this.playlistListCopy = new ArrayList<>();
        this.playlistListCopy.addAll(playlistList);
        this.repository = PlaylistRepository.getInstance(application);
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
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.ItemLongClicked(v, position, currentItem.getPlaylist().getListId());
            }
            return true;
        });

        Log.e(LOG_TAG, "Set the click listener");
        holder.itemView.setOnClickListener(v -> {
            int itemPos = (int) v.getTag();
            if (context instanceof MainActivity) {
                PlaylistFragment fragment = new PlaylistFragment((int) v.getTag(), playlistList.get(itemPos).getPlaylist().getListId());
                ((MainActivity) context).placeFragment(fragment, R.id.mainFrame);
                ((MainActivity) context).setTitle(playlistList.get(itemPos).getPlaylist().getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    public void setOnLongItemClickListener(onLongItemClickListener onLongItemClickListener) {
        this.longClickListener = onLongItemClickListener;
    }

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

    public interface onLongItemClickListener {
        void ItemLongClicked(View v, int position, String playlistID);
    }

    public static class PlaylistCardViewHolder extends RecyclerView.ViewHolder {
        public ImageView item_image;
        public TextView item_title;
        public TextView item_artist;

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
