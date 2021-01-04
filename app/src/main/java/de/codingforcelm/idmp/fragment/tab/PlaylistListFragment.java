package de.codingforcelm.idmp.fragment.tab;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.codingforcelm.idmp.MenuIdentifier;
import de.codingforcelm.idmp.PhysicalAlbum;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.audio.AudioLoader;
import de.codingforcelm.idmp.fragment.NameAwareFragment;
import de.codingforcelm.idmp.fragment.adapter.PlaylistCardAdapter;
import de.codingforcelm.idmp.structure.playlist.Playlist;
import de.codingforcelm.idmp.structure.playlist.model.PlaylistViewModel;

public class PlaylistListFragment extends NameAwareFragment {
    private static final String LOG_TAG = "PlaylistListFragment";
    private static final int DELETE_PLAYLIST = 0;
    private ArrayList<PhysicalAlbum> albumList;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private PlaylistCardAdapter adapter;
    private PlaylistViewModel playlistViewModel;
    private RecyclerView.LayoutManager layoutManager;
    private int currItemPos;
    private int playlistID;


    public PlaylistListFragment() {
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

        adapter = new PlaylistCardAdapter(getActivity().getApplication(), getActivity());
        playlistViewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);
        playlistViewModel.getPlaylists().observe(getViewLifecycleOwner(), playlistWithEntries -> {
            adapter.setData(playlistWithEntries);
        });
        adapter.setOnLongItemClickListener((v, position,listID) -> {
            currItemPos=position;
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
        contextMenu.add(MenuIdentifier.MENU_PLAYLISTLIST, DELETE_PLAYLIST, 0, "Delete Playlist");
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getGroupId() != MenuIdentifier.MENU_PLAYLISTLIST){
            return false;
        }
        Log.e(LOG_TAG, "--onContextItemSelected--");

        switch (item.getItemId()) {
            case DELETE_PLAYLIST:
                Log.e(LOG_TAG, "menu item: "+item.toString()+" selected");
                 playlistViewModel.getPlaylist(playlistID).observe(getViewLifecycleOwner(), playlistWithEntries -> {
                     if(playlistWithEntries != null){
                         Playlist playlist = playlistWithEntries.getPlaylist();
                         AlertDialog alertDialog = new AlertDialog.Builder(this.getContext()).create();
                         alertDialog.setTitle("Warning deleting Playlist");
                         alertDialog.setMessage("Are you sure you want to delete\n\""+playlist.getName()+"\"\nThis action can not be undone!");
                         alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "YES",
                                 (dialog, which) -> {
                                     playlistViewModel.deletePlaylist(playlist);
                                     Fragment fragment = getChildFragmentManager().findFragmentByTag(PlaylistFragment.class.getSimpleName()+"_"+currItemPos);
                                     if(fragment != null){
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
                Log.e(LOG_TAG, "unexpected menu item clicked"+item.toString());
                break;
        }
        return true;
    }

}

