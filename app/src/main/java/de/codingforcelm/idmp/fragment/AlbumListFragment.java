package de.codingforcelm.idmp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.codingforcelm.idmp.fragment.adapter.AlbumCardAdapter;
import de.codingforcelm.idmp.PhysicalAlbum;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.audio.AudioLoader;

public class AlbumListFragment extends Fragment {
    private static final String LOG_TAG = "AlbumListFragment";
    private ListView songView;
    private ArrayList<PhysicalAlbum> albumList;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private RecyclerView subRecyclerView;
    private AlbumCardAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private int currItemPos;

  public AlbumListFragment() {
        //needed default constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songlist, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (getChildFragmentManager().findFragmentByTag(ControlsFragment.class.getSimpleName()) != null) {
            getChildFragmentManager().beginTransaction().attach(getChildFragmentManager().findFragmentByTag(ControlsFragment.class.getSimpleName())).commit();

        } else {
            getChildFragmentManager().beginTransaction().add(R.id.tp_controls_frame, new ControlsFragment(), ControlsFragment.class.getSimpleName()).commit();
        }
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
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Context Menu");
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Log.e(LOG_TAG, "clicked context item: "+item.toString());
        switch (item.getItemId()) {
            case R.id.item_menu_1:
                Log.e(LOG_TAG, "clicked context item: "+item.toString());
                break;
            case R.id.item_menu_2:
                Log.e(LOG_TAG, "clicked context item: "+item.toString());
                break;
            case R.id.item_menu_3:
                Log.e(LOG_TAG, "clicked context item: "+item.toString());
                break;
            case R.id.item_menu_3_1:
                Log.e(LOG_TAG, "clicked context item: "+item.toString());
                break;
            case R.id.item_menu_3_2:
                Log.e(LOG_TAG, "clicked context item: "+item.toString());
                break;
            default:
                Log.e(LOG_TAG, "clicked context item: "+item.toString());
                break;
        }
        return true;
    }

}

