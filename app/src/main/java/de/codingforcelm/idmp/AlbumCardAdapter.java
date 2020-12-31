package de.codingforcelm.idmp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.codingforcelm.idmp.audio.AudioLoader;

public class AlbumCardAdapter extends RecyclerView.Adapter<AlbumCardAdapter.AlbumCardViewHolder> {
    private ArrayList<PhysicalAlbum> albumList;
    private ArrayList<PhysicalAlbum> albumListCopy;
    public static final String LOG_TAG = "CardsAdapter";
    private onLongItemClickListener longClickListener;

    public static class AlbumCardViewHolder extends RecyclerView.ViewHolder {
        public ImageView item_image;
        public TextView item_title;
        public TextView item_artist;
        public ListView item_albumSong;
        public RecyclerView subRecyclerView;

        public AlbumCardViewHolder(View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_title = itemView.findViewById(R.id.item_title);
            item_artist = itemView.findViewById(R.id.item_artist);
          //  item_albumSong = itemView.findViewById(R.id.item_album_sublist_title);
            subRecyclerView = itemView.findViewById(R.id.item_subRecycler);
        }

        private void bind(PhysicalAlbum album) {
            // Get the state
            boolean expanded = album.isExpanded();
            // Set the visibility based on state
            subRecyclerView.setVisibility(expanded ? View.VISIBLE : View.GONE);
            subRecyclerView.setHasFixedSize(true);

            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false);
            subRecyclerView.setLayoutManager(layoutManager);

            ArrayList<PhysicalSong> songList = new AudioLoader(itemView.getContext()).getSongsFromAlbum(album.getId());
            SongCardAdapter adapter = new SongCardAdapter(songList);
            subRecyclerView.setAdapter(adapter);

            // item_albumSong.setText(album.getTitle());
        }
        
    }

    public AlbumCardAdapter(ArrayList<PhysicalAlbum> albumList) {
        this.albumList = albumList;
        this.albumListCopy = new ArrayList<>();
        this.albumListCopy.addAll(albumList);
    }

    @Override
    public AlbumCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_layout, parent, false);
        AlbumCardViewHolder cvh = new AlbumCardViewHolder(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(AlbumCardViewHolder holder, int position) {
        PhysicalAlbum currentItem = albumList.get(position);
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
        holder.bind(currentItem);
        holder.itemView.setOnClickListener(v -> {
                // Get the current state of the item
        boolean expanded = currentItem.isExpanded();
        // Change the state
            currentItem.setExpanded(!expanded);
        // Notify the adapter that item has changed
        notifyItemChanged(position);
    });;
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void setOnLongItemClickListener(onLongItemClickListener onLongItemClickListener) {
        this.longClickListener = onLongItemClickListener;
    }

    public interface onLongItemClickListener {
        void ItemLongClicked(View v, int position);
    }

    public void filter(String text) {
        albumList.clear();
        if(text.isEmpty()){
            albumList.addAll(albumListCopy);
        } else{
            text = text.toLowerCase();
            for(PhysicalAlbum album: albumListCopy){
                if(album.getTitle().toLowerCase().contains(text)){
                    albumList.add(album);
                }
            }
        }
        notifyDataSetChanged();
    }

}
