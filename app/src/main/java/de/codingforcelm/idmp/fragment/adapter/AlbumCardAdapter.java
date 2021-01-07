package de.codingforcelm.idmp.fragment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.activity.MainActivity;
import de.codingforcelm.idmp.locale.LocaleAlbum;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.loader.AudioLoader;
import de.codingforcelm.idmp.fragment.tab.AlbumFragment;
import de.codingforcelm.idmp.locale.LocaleSong;

public class AlbumCardAdapter extends RecyclerView.Adapter<AlbumCardAdapter.AlbumCardViewHolder> {
    public static final String LOG_TAG = "AlbumCardAdapter";
    private final ArrayList<LocaleAlbum> albumList;
    private final ArrayList<LocaleAlbum> albumListCopy;
    private final Context context;
    private onLongItemClickListener longClickListener;
    private AudioLoader audioLoader;


    public AlbumCardAdapter(ArrayList<LocaleAlbum> albumList, Context context) {
        this.context = context;
        this.albumList = albumList;
        this.albumListCopy = new ArrayList<>();
        this.albumListCopy.addAll(albumList);
        audioLoader = new AudioLoader(context);
    }

    @Override
    public AlbumCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_layout, parent, false);
        AlbumCardViewHolder cvh = new AlbumCardViewHolder(view);
        return cvh;
    }

    @Override
    public void onBindViewHolder(AlbumCardViewHolder holder, int position) {
        LocaleAlbum currentItem = albumList.get(position);
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
            ((MainActivity) context).placeFragment(new AlbumFragment(audioLoader.getSongsFromAlbum(currentItem.getId()),currentItem.getId()), R.id.mainFrame);
            ((MainActivity) context).setTitle(currentItem.getTitle());
            notifyItemChanged(position);

        });


        List<LocaleSong> songs = audioLoader.getSongsFromAlbum(currentItem.getId());
        if(songs.size() >= 1) {
            LocaleSong s = songs.get(0);
            Bitmap cover = audioLoader.getAlbumCoverForSong(s.getId());
            if(cover != null) {
                ImageView image = holder.itemView.findViewById(R.id.item_image);
                image.setImageBitmap(cover);
            }
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void setOnLongItemClickListener(onLongItemClickListener onLongItemClickListener) {
        this.longClickListener = onLongItemClickListener;
    }

    public void filter(String text) {
        albumList.clear();
        if (text.isEmpty()) {
            albumList.addAll(albumListCopy);
        } else {
            text = text.toLowerCase();
            for (LocaleAlbum album : albumListCopy) {
                if (album.getTitle().toLowerCase().contains(text)) {
                    albumList.add(album);
                }
            }
        }
        notifyDataSetChanged();
    }

    public interface onLongItemClickListener {
        void ItemLongClicked(View v, int position, long albumID);
    }

    public static class AlbumCardViewHolder extends RecyclerView.ViewHolder {
        public ImageView item_image;
        public TextView item_title;
        public TextView item_artist;


        public AlbumCardViewHolder(View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_title = itemView.findViewById(R.id.item_title);
            item_artist = itemView.findViewById(R.id.item_subtitle);

        }

        private void bind(LocaleAlbum album) {
            item_title.setText(album.getTitle());
            item_artist.setText(album.getArtist());
        }

    }

}
