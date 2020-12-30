package de.codingforcelm.idmp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardViewHolder> {
    private ArrayList<PhysicalSong> songList;

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public ImageView item_image;
        public TextView item_title;
        public TextView item_artist;

        public CardViewHolder(View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_title = itemView.findViewById(R.id.item_title);
            item_artist = itemView.findViewById(R.id.item_artist);
        }
    }

    public CardsAdapter(ArrayList<PhysicalSong> songList) {
        this.songList = songList;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        CardViewHolder cvh = new CardViewHolder(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        PhysicalSong currentItem = songList.get(position);
        holder.item_title.setText(currentItem.getTitle());
        holder.item_artist.setText(currentItem.getArtist());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }


}
