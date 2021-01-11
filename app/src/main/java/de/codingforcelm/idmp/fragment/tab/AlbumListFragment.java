package de.codingforcelm.idmp.fragment.tab;

import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.activity.MenuIdentifier;
import de.codingforcelm.idmp.locale.LocaleAlbum;
import de.codingforcelm.idmp.locale.LocaleSong;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.loader.AudioLoader;
import de.codingforcelm.idmp.fragment.NameAwareFragment;
import de.codingforcelm.idmp.fragment.adapter.AlbumCardAdapter;
import de.codingforcelm.idmp.service.MusicService;
import de.codingforcelm.idmp.database.entity.PlaylistEntry;
import de.codingforcelm.idmp.database.entity.relation.PlaylistWithEntries;
import de.codingforcelm.idmp.database.viewmodel.PlaylistEntryViewModel;
import de.codingforcelm.idmp.database.viewmodel.PlaylistViewModel;

public class AlbumListFragment extends NameAwareFragment {
    private static final String LOG_TAG = "AlbumListFragment";
    private static final int ADD_TO_PLAYLIST = 0;
    private ArrayList<LocaleAlbum> albumList;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private AlbumCardAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private int currItemPos;
    private PlaylistViewModel playlistViewModel;
    private List<PlaylistWithEntries> currPlaylistWithEntries;
    private long currAlbumID;

    public AlbumListFragment() {
        //needed default constructor
        setFragmentname(this.getClass().getSimpleName());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songlist, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        albumList = new AudioLoader(this.getContext()).getAlbums();
        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        registerForContextMenu(recyclerView);

        layoutManager = new LinearLayoutManager(view.getContext());

        adapter = new AlbumCardAdapter(albumList, getActivity());

        adapter.setOnLongItemClickListener((v, position, album) -> {
            currAlbumID = album;
            currItemPos = position;
            v.showContextMenu();
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
        playlistViewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.clear();
        contextMenu.add(MenuIdentifier.GROUP_ALBUMLIST, MenuIdentifier.ADD_TO_QUEUE, 0, R.string.add_to_queue);
        SubMenu subMenu = contextMenu.addSubMenu(MenuIdentifier.GROUP_ALBUMLIST, MenuIdentifier.ADD_TO_PLAYLIST, 1, R.string.add_to_playlist);
        playlistViewModel.getPlaylists().observe(getViewLifecycleOwner(), playlistWithEntries -> {
            currPlaylistWithEntries = playlistWithEntries;
            for (int i = 0; i < playlistWithEntries.size(); i++) {
                subMenu.add(MenuIdentifier.GROUP_ALBUMLIST, i + MenuIdentifier.OFFSET_PLAYLISTID, i, playlistWithEntries.get(i).getPlaylist().getName());
            }
            super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() != MenuIdentifier.GROUP_ALBUMLIST) {
            return false;
        }
        Log.e(LOG_TAG, "--onContextItemSelected--" + item.getItemId());

        if (item.getItemId() >= MenuIdentifier.OFFSET_PLAYLISTID) {
            String playlistID = currPlaylistWithEntries.get(item.getItemId() - MenuIdentifier.OFFSET_PLAYLISTID).getPlaylist().getListId();
            PlaylistEntryViewModel playlistEntryViewModel = new ViewModelProvider(this).get(PlaylistEntryViewModel.class);
            AudioLoader audioLoader = new AudioLoader(this.getContext());
            ArrayList<LocaleSong> list = audioLoader.getSongsFromAlbum(currAlbumID);
            ArrayList<PlaylistEntry> new_entries = new ArrayList<>();
            for (LocaleSong song : list) {
                new_entries.add(new PlaylistEntry(song.getId(), playlistID));
            }
            playlistEntryViewModel.insertAll(new_entries.toArray(new PlaylistEntry[0]));
            Log.e(LOG_TAG, "added album with albumID: " + currAlbumID + ", to playlistID: " + playlistID);
            return true;
        }

        switch (item.getItemId()) {
            case MenuIdentifier.ADD_TO_PLAYLIST:
                Log.e(LOG_TAG, "waiting for submenu");
                break;
            case MenuIdentifier.ADD_TO_QUEUE:
                AudioLoader audioLoader = new AudioLoader(this.getContext());
                ArrayList<LocaleSong> list = audioLoader.getSongsFromAlbum(currAlbumID);
                MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
                for (LocaleSong song : list) {
                    Bundle b = new Bundle();
                    String currSongId = String.valueOf(song.getId());
                    b.putString(MusicService.KEY_MEDIA_ID, currSongId);
                    controller.sendCommand(MusicService.COMMAND_ENQUEUE, b, null);
                    Log.e(LOG_TAG, "added mediaID: " + currSongId + "to Queue");
                }
                Log.e(LOG_TAG, "added albumID: " + currAlbumID + " to Queue");
                break;
            default:
                Log.e(LOG_TAG, "unexpected menu item clicked" + item.toString());
                break;
        }
        return true;
    }
}

