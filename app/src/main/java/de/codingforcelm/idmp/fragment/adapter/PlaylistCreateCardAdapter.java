package de.codingforcelm.idmp.fragment.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.PhysicalSong;
import de.codingforcelm.idmp.PlaylistCreateActivity;
import de.codingforcelm.idmp.R;

public class PlaylistCreateCardAdapter extends RecyclerView.Adapter<PlaylistCreateCardAdapter.SelectionCardViewHolder> {

    private List<PlaylistCreateActivity.PlaylistSelection> selectionList;

    public static class SelectionCardViewHolder extends RecyclerView.ViewHolder {
        public ImageView item_image;
        public TextView item_title;
        public TextView item_artist;
        public long currentSongID;

        public SelectionCardViewHolder(View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_title = itemView.findViewById(R.id.item_title);
            item_artist = itemView.findViewById(R.id.item_subtitle);
        }

        private void bind(PlaylistCreateActivity.PlaylistSelection selection) {
            PhysicalSong song = selection.getSong();
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

    public PlaylistCreateCardAdapter(List<PlaylistCreateActivity.PlaylistSelection> selectionList) {
        this.selectionList = selectionList;
    }

    @Override
    public SelectionCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        SelectionCardViewHolder cvh = new SelectionCardViewHolder(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionCardViewHolder holder, int position) {
        PlaylistCreateActivity.PlaylistSelection selection = selectionList.get(position);
        holder.bind(selection);
        holder.itemView.setOnClickListener(v -> {
            selection.setSelected(!selection.isSelected());
            int color = -1;
            if(selection.isSelected()) {
                color = Color.GRAY;
            } else {
                color = Color.WHITE;
            }
            v.setBackgroundColor(color);
        });
        
        int color = -1;
        if(selection.isSelected()) {
            color = Color.GRAY;
        } else {
            color = Color.WHITE;
        }
        holder.itemView.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return selectionList.size();
    }

    public List<PlaylistCreateActivity.PlaylistSelection> getSelectedList() {
        List<PlaylistCreateActivity.PlaylistSelection> selectedList = new ArrayList<>();

        for(PlaylistCreateActivity.PlaylistSelection selection : selectedList) {
            if(selection.isSelected()) {
                selectedList.add(selection);
            }
        }

        return selectedList;
    }
}
