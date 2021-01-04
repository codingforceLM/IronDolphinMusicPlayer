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
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.codingforcelm.idmp.MainActivity;
import de.codingforcelm.idmp.MenuIdentifier;
import de.codingforcelm.idmp.fragment.NameAwareFragment;
import de.codingforcelm.idmp.fragment.adapter.SongCardAdapter;
import de.codingforcelm.idmp.PhysicalSong;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.audio.AudioLoader;
import de.codingforcelm.idmp.player.service.MusicService;
import de.codingforcelm.idmp.structure.playlist.Playlist;

public class SongListFragment extends NameAwareFragment {
    private static final String LOG_TAG = "SongListFragment";
    private static final String MENU_1_ITEM = "chagneThis";
    private ListView songView;
    private ArrayList<PhysicalSong> songList;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private SongCardAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private int currItemPos;
    private static final int MENU2 = 2;

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

    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
   //TODO
    /*contextMenu.add(MENU2, 0, 0, "menu1");
        contextMenu.add(MENU2, 1, 1, "menu1");
        SubMenu menu3 = contextMenu.addSubMenu(MENU2, 2, 2, "menu1");
        menu3.add(MENU2, 3, 0, "menu1");
        menu3.add(MENU2, 4, 1, "menu1");*/
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.e(LOG_TAG, "--onContextItemSelected--PlaylistList "+item.toString());
        if(item.getGroupId() != MenuIdentifier.MENU_SONGLIST){
            return false;
        }

        switch (item.getItemId()) {
            case 0:

                break;
            default:
                Log.e(LOG_TAG, "unexpected menu item clicked"+item.toString());
                break;
        }
        return true;
    }

}

