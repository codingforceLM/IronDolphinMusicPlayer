package de.codingforcelm.idmp.fragment.tab;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.codingforcelm.idmp.MenuIdentifier;
import de.codingforcelm.idmp.fragment.NameAwareFragment;
import de.codingforcelm.idmp.fragment.adapter.AlbumCardAdapter;
import de.codingforcelm.idmp.PhysicalAlbum;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.audio.AudioLoader;
import de.codingforcelm.idmp.structure.playlist.model.PlaylistViewModel;

public class AlbumListFragment extends NameAwareFragment {
    private static final String LOG_TAG = "AlbumListFragment";
    private static final int ADD_TO_PLAYLIST = 0;
    private ArrayList<PhysicalAlbum> albumList;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private AlbumCardAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private int currItemPos;
    private PlaylistViewModel playlistViewModel;

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
        searchView =  view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        registerForContextMenu(recyclerView);

        layoutManager = new LinearLayoutManager(view.getContext());

        adapter = new AlbumCardAdapter(albumList);

        adapter.setOnLongItemClickListener((v, position) -> {
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
        SubMenu subMenu = contextMenu.addSubMenu(MenuIdentifier.MENU_ALBUMLIST, ADD_TO_PLAYLIST, 0, "Add to Playlist");
        playlistViewModel.getPlaylists().observe(getViewLifecycleOwner(), playlistWithEntries -> {
            for(int i=0; i < playlistWithEntries.size(); i++){
                subMenu.add(MenuIdentifier.MENU_ALBUMLIST, ADD_TO_PLAYLIST, i, playlistWithEntries.get(i).getPlaylist().getName());
            }
            super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getGroupId() != MenuIdentifier.MENU_SONGLIST){
            return false;
        }
        Log.e(LOG_TAG, "--onContextItemSelected--");
        switch (item.getItemId()) {
            case ADD_TO_PLAYLIST:
                //TODO implement
                break;
            default:
                Log.e(LOG_TAG, "unexpected menu item clicked"+item.toString());
                break;
        }
        return true;
    }

}

