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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.activity.MainActivity;
import de.codingforcelm.idmp.activity.MenuIdentifier;
import de.codingforcelm.idmp.database.entity.Playlist;
import de.codingforcelm.idmp.database.entity.PlaylistEntry;
import de.codingforcelm.idmp.database.entity.relation.PlaylistWithEntries;
import de.codingforcelm.idmp.database.viewmodel.PlaylistEntryViewModel;
import de.codingforcelm.idmp.database.viewmodel.PlaylistViewModel;
import de.codingforcelm.idmp.fragment.ControlsFragment;
import de.codingforcelm.idmp.fragment.NameAwareFragment;
import de.codingforcelm.idmp.fragment.OnManualDetachListener;
import de.codingforcelm.idmp.fragment.adapter.SongCardAdapter;
import de.codingforcelm.idmp.loader.AudioLoader;
import de.codingforcelm.idmp.locale.LocaleSong;
import de.codingforcelm.idmp.service.MusicService;

/**
 * Fragment to display a Playlist
 */
public class PlaylistFragment extends NameAwareFragment implements OnManualDetachListener {
    private static final String LOG_TAG = "PlaylistFragment";
    private final int position;
    private SongCardAdapter adapter;
    private PlaylistViewModel playlistViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SearchView searchView;
    private String listId;
    private AudioLoader loader;
    private long currSongId;
    private int currItemPos;
    private List<PlaylistWithEntries> currPlaylistWithEntries;


    /**
     * Default constructor, which sets the NameAwareFragment name
     * @param position position
     * @param listId playlistId
     */
    public PlaylistFragment(int position, String listId) {
        this.position = position;
        this.listId = listId;

        String name = this.getClass().getSimpleName();
        if (position >= 0) {
            name = name + "_" + position;
        }
        setFragmentname(name);
    }

    /**
     * Needed default constructor for FragmentManager
     */
    public PlaylistFragment() {
        position = -1;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.e(LOG_TAG, "--onViewCreated--");
        if (getChildFragmentManager().findFragmentByTag(ControlsFragment.class.getSimpleName()) != null) {
            getChildFragmentManager().beginTransaction().attach(getChildFragmentManager().findFragmentByTag(ControlsFragment.class.getSimpleName())).commit();

        } else {
            getChildFragmentManager().beginTransaction().add(
                    R.id.pl_controls_frame,
                    new ControlsFragment(),
                    ControlsFragment.class.getSimpleName()
            ).commit();
        }

        ((MainActivity) getContext()).setCurrentFragment(MainActivity.FRAGMENT_TABS);
        ((MainActivity) getContext()).invalidateOptionsMenu();
        ((MainActivity) getActivity()).setPlaylistUuid(listId);
        ((MainActivity) getActivity()).setInPlaylist(true);
        ((MainActivity) getActivity()).invalidateOptionsMenu();

        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        registerForContextMenu(recyclerView);

        layoutManager = new LinearLayoutManager(view.getContext());

        loader = new AudioLoader(getContext());
        adapter = new SongCardAdapter(new ArrayList<>(), getContext(), MusicService.CONTEXT_TYPE_PLAYLIST, listId);
        adapter.setOnLongItemClickListener((v, position, songId) -> {
            currSongId = songId;
            currItemPos = position;
            v.showContextMenu();
        });
        playlistViewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);
        playlistViewModel.getPlaylists().observe(getViewLifecycleOwner(), playlistWithEntries -> {
            if (position < 0) {
                throw new IllegalStateException("missing position in PlaylistFragment");
            }
            List<LocaleSong> data = new ArrayList<>();
            for (PlaylistEntry entry : playlistWithEntries.get(position).getEntries()) {
                LocaleSong s = loader.getSong(entry.getMediaId());
                data.add(s);
            }
            adapter.setData(data);
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
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        Log.e(LOG_TAG, "--onCreateContextMenu--");
        contextMenu.add(MenuIdentifier.GROUP_PLAYLIST, MenuIdentifier.ADD_TO_QUEUE, 0, R.string.add_to_queue);
        contextMenu.add(MenuIdentifier.GROUP_PLAYLIST, MenuIdentifier.REMOVE_FROM_PLAYLIST, 2, R.string.remove_from_playlist);
        SubMenu subMenu = contextMenu.addSubMenu(MenuIdentifier.GROUP_PLAYLIST, MenuIdentifier.ADD_TO_PLAYLIST, 1, R.string.add_to_playlist);
        playlistViewModel.getPlaylists().observe(getViewLifecycleOwner(), playlistWithEntries -> {
            currPlaylistWithEntries = playlistWithEntries;
            for (int i = 0; i < playlistWithEntries.size(); i++) {
                subMenu.add(MenuIdentifier.GROUP_PLAYLIST, i + MenuIdentifier.OFFSET_PLAYLISTID, i, playlistWithEntries.get(i).getPlaylist().getName());
            }
            super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() != MenuIdentifier.GROUP_PLAYLIST) {
            return false;
        }
        Log.e(LOG_TAG, "--onContextItemSelected--" + item.getItemId());

        if (item.getItemId() >= MenuIdentifier.OFFSET_PLAYLISTID) {
            PlaylistEntryViewModel playlistEntryViewModel = new ViewModelProvider(this).get(PlaylistEntryViewModel.class);
            Playlist playlist = currPlaylistWithEntries.get(item.getItemId() - MenuIdentifier.OFFSET_PLAYLISTID).getPlaylist();
            playlistEntryViewModel.insertAll(new PlaylistEntry(currSongId, playlist.getListId()));
            Log.e(LOG_TAG, "added song with mediaID: " + currSongId + ", to playlist: " + playlist.getName());
            return true;
        }

        switch (item.getItemId()) {
            case MenuIdentifier.REMOVE_FROM_PLAYLIST:
                PlaylistEntryViewModel playlistEntryViewModel = new ViewModelProvider(this).get(PlaylistEntryViewModel.class);
                LiveData<PlaylistWithEntries> playlist = playlistViewModel.getPlaylist(listId);
                AtomicBoolean removed = new AtomicBoolean(false);
                playlist.observe(getViewLifecycleOwner(), entries -> {
                    if (!removed.get()) {
                        List<PlaylistEntry> entry = entries.getEntries();
                        playlistEntryViewModel.delete(entry.get(currItemPos));
                    }
                    removed.set(true);
                    Log.e(LOG_TAG, "removed selected playlist");
                });
                break;
            case MenuIdentifier.ADD_TO_PLAYLIST:
                Log.e(LOG_TAG, "waiting for submenu");
            case MenuIdentifier.ADD_TO_QUEUE:
                Bundle b = new Bundle();
                b.putString(MusicService.KEY_MEDIA_ID, String.valueOf(currSongId));
                MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
                controller.sendCommand(MusicService.COMMAND_ENQUEUE, b, null);
                Log.e(LOG_TAG, "added mediaID: " + currSongId + "to Queue");
                break;
            default:
                Log.e(LOG_TAG, "unexpected menu item clicked" + item.toString());
                break;
        }
        return true;
    }


    @Override
    public void onManualDetach() {
        Log.e(LOG_TAG, "onManualDetach");
        ((MainActivity) getActivity()).setPlaylistUuid(null);
        ((MainActivity) getActivity()).setInPlaylist(false);
        ((MainActivity) getActivity()).invalidateOptionsMenu();
    }
}