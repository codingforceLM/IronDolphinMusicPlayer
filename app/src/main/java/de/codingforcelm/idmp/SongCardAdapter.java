package de.codingforcelm.idmp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SongCardAdapter extends RecyclerView.Adapter<SongCardAdapter.SongCardViewHolder> {
    private ArrayList<PhysicalSong> songList;
    public static final String LOG_TAG = "CardsAdapter";
    private onLongItemClickListener longClickListener;

    public static class SongCardViewHolder extends RecyclerView.ViewHolder {
        public ImageView item_image;
        public TextView item_title;
        public TextView item_artist;

        public SongCardViewHolder(View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_title = itemView.findViewById(R.id.item_title);
            item_artist = itemView.findViewById(R.id.item_artist);
        }
        
    }

    public SongCardAdapter(ArrayList<PhysicalSong> songList) {
        this.songList = songList;
    }

    @Override
    public SongCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        SongCardViewHolder cvh = new SongCardViewHolder(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(SongCardViewHolder holder, int position) {
        PhysicalSong currentItem = songList.get(position);
        holder.item_title.setText(currentItem.getTitle());
        holder.item_artist.setText(currentItem.getArtist());
        holder.itemView.setTag(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longClickListener != null) {
                    longClickListener.ItemLongClicked(v, position);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void setOnLongItemClickListener(onLongItemClickListener onLongItemClickListener) {
        this.longClickListener = onLongItemClickListener;
    }

    public interface onLongItemClickListener {
        void ItemLongClicked(View v, int position);
    }

}
