package de.codingforcelm.idmp.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.MainActivity;
import de.codingforcelm.idmp.PhysicalSong;
import de.codingforcelm.idmp.R;

public class SongCardAdapter extends RecyclerView.Adapter<SongCardAdapter.SongCardViewHolder> {
    private List<PhysicalSong> songList;
    private List<PhysicalSong> songListCopy;
    private Context context;
    public static final String LOG_TAG = "CardsAdapter";
    private onLongItemClickListener longClickListener;

    public static class SongCardViewHolder extends RecyclerView.ViewHolder {
        public ImageView item_image;
        public TextView item_title;
        public TextView item_artist;
        public long currentSongID;

        public SongCardViewHolder(View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_title = itemView.findViewById(R.id.item_title);
            item_artist = itemView.findViewById(R.id.item_subtitle);
        }

        private void bind(PhysicalSong song) {
            item_title.setText(song.getTitle());
            item_artist.setText(song.getArtist());
            currentSongID = song.getId();
        }
        
    }

    public SongCardAdapter(ArrayList<PhysicalSong> songList,Context context) {
        this.songList = songList;
        this.context = context;
        this.songListCopy = new ArrayList<>();
        this.songListCopy.addAll(songList);
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
                ((MainActivity)context).songSelect(currentItem.getId());
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

    public void filter(String text) {
        songList.clear();
        if(text.isEmpty()){
            songList.addAll(songListCopy);
        } else{
            text = text.toLowerCase();
            for(PhysicalSong song: songListCopy){
                if(song.getTitle().toLowerCase().contains(text)){
                    songList.add(song);
                }
            }
        }
        notifyDataSetChanged();
    }

}
