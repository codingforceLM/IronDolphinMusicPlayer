package de.codingforcelm.idmp.fragment.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.codingforcelm.idmp.MainActivity;
import de.codingforcelm.idmp.MenuIdentifier;
import de.codingforcelm.idmp.PhysicalAlbum;
import de.codingforcelm.idmp.PhysicalSong;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.audio.AudioLoader;
import de.codingforcelm.idmp.fragment.tab.AlbumFragment;
import de.codingforcelm.idmp.fragment.tab.SongListFragment;
import de.codingforcelm.idmp.music.Album;
import de.codingforcelm.idmp.player.service.MusicService;

public class AlbumCardAdapter extends RecyclerView.Adapter<AlbumCardAdapter.AlbumCardViewHolder> {
    private ArrayList<PhysicalAlbum> albumList;
    private ArrayList<PhysicalAlbum> albumListCopy;
    private Context context;
    public static final String LOG_TAG = "AlbumCardAdapter";
    private onLongItemClickListener longClickListener;



    public static class AlbumCardViewHolder extends RecyclerView.ViewHolder{
        public ImageView item_image;
        public TextView item_title;
        public TextView item_artist;


        public AlbumCardViewHolder(View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_title = itemView.findViewById(R.id.item_title);
            item_artist = itemView.findViewById(R.id.item_subtitle);

        }

        private void bind(PhysicalAlbum album) {
            item_title.setText(album.getTitle());
            item_artist.setText(album.getArtist());
        }

    }

    public AlbumCardAdapter(ArrayList<PhysicalAlbum> albumList, Context context) {
        this.context = context;
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
        holder.bind(currentItem);

        holder.itemView.setTag(position);
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.ItemLongClicked(v, position, currentItem.getId());
            }
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            AudioLoader audioLoader = new AudioLoader(context);
            ((MainActivity)context).placeFragment(new AlbumFragment(audioLoader.getSongsFromAlbum(currentItem.getId())), R.id.mainFrame);
            ((MainActivity)context).setTitle(currentItem.getTitle());
            notifyItemChanged(position);

        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void setOnLongItemClickListener(onLongItemClickListener onLongItemClickListener) {
        this.longClickListener = onLongItemClickListener;
    }

    public interface onLongItemClickListener {
        void ItemLongClicked(View v, int position, long albumID);
    }

    public void filter(String text) {
        albumList.clear();
        if (text.isEmpty()) {
            albumList.addAll(albumListCopy);
        } else {
            text = text.toLowerCase();
            for (PhysicalAlbum album : albumListCopy) {
                if (album.getTitle().toLowerCase().contains(text)) {
                    albumList.add(album);
                }
            }
        }
        notifyDataSetChanged();
    }

}
