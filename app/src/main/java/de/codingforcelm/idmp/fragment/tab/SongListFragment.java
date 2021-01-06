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
import android.widget.ListView;
import android.widget.SearchView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.MainActivity;
import de.codingforcelm.idmp.MenuIdentifier;
import de.codingforcelm.idmp.fragment.NameAwareFragment;
import de.codingforcelm.idmp.fragment.adapter.SongCardAdapter;
import de.codingforcelm.idmp.PhysicalSong;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.audio.AudioLoader;
import de.codingforcelm.idmp.player.service.MusicService;
import de.codingforcelm.idmp.structure.playlist.Playlist;
import de.codingforcelm.idmp.structure.playlist.PlaylistEntry;
import de.codingforcelm.idmp.structure.playlist.PlaylistWithEntries;
import de.codingforcelm.idmp.structure.playlist.model.PlaylistEntryViewModel;
import de.codingforcelm.idmp.structure.playlist.model.PlaylistViewModel;

public class SongListFragment extends NameAwareFragment {
    private static final String LOG_TAG = "SongListFragment";
    private ListView songView;
    private ArrayList<PhysicalSong> songList;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private SongCardAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private int currItemPos;
    private long currSongId;
    private PlaylistViewModel playlistViewModel;
    private List<PlaylistWithEntries> currPlaylistWithEntries;

    public SongListFragment(ArrayList<PhysicalSong> songList) {
        setFragmentname(this.getClass().getSimpleName());
        this.songList=songList;
    }

    public SongListFragment() {
        //needed default constructor
        setFragmentname(this.getClass().getSimpleName());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songlist, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        songList = new AudioLoader(this.getContext()).getSongs();
        searchView =  view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        registerForContextMenu(recyclerView);
        layoutManager = new LinearLayoutManager(view.getContext());
        adapter = new SongCardAdapter(songList, this.getContext(), MusicService.CONTEXT_TYPE_SONGLIST, MainActivity.CONTEXT_SONGLIST);
        adapter.setOnLongItemClickListener((v, position, songId) -> {
            currSongId = songId;
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
        contextMenu.add(MenuIdentifier.GROUP_SONGLIST, MenuIdentifier.ADD_TO_QUEUE, 0, R.string.add_to_queue);
        SubMenu subMenu = contextMenu.addSubMenu(MenuIdentifier.GROUP_SONGLIST, MenuIdentifier.ADD_TO_PLAYLIST, 1, R.string.add_to_playlist);
        playlistViewModel.getPlaylists().observe(getViewLifecycleOwner(), playlistWithEntries -> {
            currPlaylistWithEntries=playlistWithEntries;
            for(int i=0; i < playlistWithEntries.size(); i++){
                subMenu.add(MenuIdentifier.GROUP_SONGLIST, i+MenuIdentifier.OFFSET_PLAYLISTID, i, playlistWithEntries.get(i).getPlaylist().getName());
            }
            super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getGroupId() != MenuIdentifier.GROUP_SONGLIST){
            return false;
        }
        Log.e(LOG_TAG, "--onContextItemSelected--"+item.getItemId());

        if(item.getItemId() >= MenuIdentifier.OFFSET_PLAYLISTID){
            PlaylistEntryViewModel playlistEntryViewModel = new ViewModelProvider(this).get(PlaylistEntryViewModel.class);
            Playlist playlist = currPlaylistWithEntries.get(item.getItemId()-MenuIdentifier.OFFSET_PLAYLISTID).getPlaylist();
            playlistEntryViewModel.insertAll(new PlaylistEntry(currSongId,playlist.getListId()));
            Log.e(LOG_TAG, "added song with mediaID: "+currSongId+", to playlist: "+playlist.getName());
            return true;
        }

        switch (item.getItemId()) {
            case MenuIdentifier.ADD_TO_PLAYLIST:
                Log.e(LOG_TAG, "waiting for submenu");
            case MenuIdentifier.ADD_TO_QUEUE:
                Bundle b = new Bundle();
                b.putString(MusicService.KEY_MEDIA_ID, String.valueOf(currSongId));
                MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
                controller.sendCommand(MusicService.COMMAND_ENQUEUE, b, null);
                Log.e(LOG_TAG, "added mediaID: "+currSongId+ "to Queue");
                break;
            default:
                Log.e(LOG_TAG, "unexpected menu item clicked"+item.toString());
                break;
        }
        return true;
    }

}

