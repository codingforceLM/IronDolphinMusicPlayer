package de.codingforcelm.idmp.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.codingforcelm.idmp.MainActivity;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.fragment.tab.PlaylistFragment;
import de.codingforcelm.idmp.structure.Playlist;

public class PlaylistCardAdapter extends RecyclerView.Adapter<PlaylistCardAdapter.PlaylistCardViewHolder> {
    private ArrayList<Playlist> playlistList;
    private ArrayList<Playlist> playlistListCopy;
    private Context context;
    public static final String LOG_TAG = "PlaylistCardAdapter";
    private onLongItemClickListener longClickListener;

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

        private void bind(Playlist playlist) {
            item_title.setText(playlist.getName());
            item_artist.setText(playlist.size()+" Songs");
        }

    }

    public PlaylistCardAdapter(ArrayList<Playlist> playlistList, Context context) {
        this.playlistList = playlistList;
        this.context = context;
        this.playlistListCopy = new ArrayList<>();
        this.playlistListCopy.addAll(playlistList);
    }

    @Override
    public PlaylistCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_layout, parent, false);
        PlaylistCardViewHolder cvh = new PlaylistCardViewHolder(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(PlaylistCardViewHolder holder, int position) {
        Playlist currentItem = playlistList.get(position);
        holder.bind(currentItem);

        holder.itemView.setTag(position);
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.ItemLongClicked(v, position);
            }
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity)context).replaceFragments(PlaylistFragment.class);
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

    public interface onLongItemClickListener {
        void ItemLongClicked(View v, int position);
    }

    public void filter(String text) {
        playlistList.clear();
        if(text.isEmpty()){
            playlistList.addAll(playlistListCopy);
        } else{
            text = text.toLowerCase();
            for(Playlist playlist: playlistListCopy){
                if(playlist.getName().toLowerCase().contains(text)){
                    playlistList.add(playlist);
                }
            }
        }
        notifyDataSetChanged();
    }

}
