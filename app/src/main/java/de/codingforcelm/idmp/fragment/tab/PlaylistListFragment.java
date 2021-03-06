package de.codingforcelm.idmp.fragment.tab;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.activity.MainActivity;
import de.codingforcelm.idmp.activity.MenuIdentifier;
import de.codingforcelm.idmp.database.entity.Playlist;
import de.codingforcelm.idmp.database.viewmodel.PlaylistViewModel;
import de.codingforcelm.idmp.fragment.NameAwareFragment;
import de.codingforcelm.idmp.fragment.adapter.PlaylistCardAdapter;

/**
 * Fragment to display a list of all playlists
 */
public class PlaylistListFragment extends NameAwareFragment {
    private static final String LOG_TAG = "PlaylistListFragment";
    private RecyclerView recyclerView;
    private SearchView searchView;
    private PlaylistCardAdapter adapter;
    private PlaylistViewModel playlistViewModel;
    private RecyclerView.LayoutManager layoutManager;
    private int currItemPos;
    private String playlistID;

    /**
     * Default constructor, which sets the NameAwareFragment name
     */
    public PlaylistListFragment() {
        setFragmentname(this.getClass().getSimpleName());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songlist, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.e(LOG_TAG, "--onViewCreated--");
        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        registerForContextMenu(recyclerView);

        layoutManager = new LinearLayoutManager(view.getContext());

        Log.e(LOG_TAG, "tell MainActivity which fragment is currently visible");
        ((MainActivity) getContext()).setCurrentFragment(MainActivity.FRAGMENT_TABS);
        ((MainActivity) getContext()).invalidateOptionsMenu();

        adapter = new PlaylistCardAdapter(getActivity().getApplication(), getActivity());
        playlistViewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);
        playlistViewModel.getPlaylists().observe(getViewLifecycleOwner(), playlistWithEntries -> {
            adapter.setData(playlistWithEntries);
        });
        adapter.setOnLongItemClickListener((v, position, listID) -> {
            currItemPos = position;
            playlistID = listID;
            v.showContextMenu();

        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
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
        contextMenu.add(MenuIdentifier.GROUP_PLAYLISTLIST, MenuIdentifier.DELETE_PLAYLIST, 1, R.string.delete_playlist);
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() != MenuIdentifier.GROUP_PLAYLISTLIST) {
            return false;
        }
        Log.e(LOG_TAG, "--onContextItemSelected--");

        switch (item.getItemId()) {

            case MenuIdentifier.DELETE_PLAYLIST:
                Log.e(LOG_TAG, "menu item: " + item.toString() + " selected");
                playlistViewModel.getPlaylist(playlistID).observe(getViewLifecycleOwner(), playlistWithEntries -> {
                    if (playlistWithEntries != null) {
                        Playlist playlist = playlistWithEntries.getPlaylist();
                        AlertDialog alertDialog = new AlertDialog.Builder(this.getContext()).create();
                        alertDialog.setTitle("Warning deleting Playlist");
                        alertDialog.setMessage("Are you sure you want to delete\n\"" + playlist.getName() + "\"\nThis action can not be undone!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "YES",
                                (dialog, which) -> {
                                    playlistViewModel.deletePlaylist(playlist);
                                    Fragment fragment = getChildFragmentManager().findFragmentByTag(PlaylistFragment.class.getSimpleName() + "_" + currItemPos);
                                    if (fragment != null) {
                                        getChildFragmentManager().beginTransaction().remove(fragment).commit();
                                    }
                                    dialog.dismiss();
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                                (dialog, which) -> dialog.dismiss());
                        alertDialog.show();
                    }
                });
                break;
            default:
                Log.e(LOG_TAG, "unexpected menu item clicked" + item.toString());
                break;
        }
        return true;
    }

}

