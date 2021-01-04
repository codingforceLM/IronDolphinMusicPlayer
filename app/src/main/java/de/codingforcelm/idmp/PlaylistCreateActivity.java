package de.codingforcelm.idmp;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.codingforcelm.idmp.audio.AudioLoader;
import de.codingforcelm.idmp.fragment.adapter.PlaylistCreateCardAdapter;
import de.codingforcelm.idmp.structure.playlist.Playlist;
import de.codingforcelm.idmp.structure.playlist.PlaylistEntry;
import de.codingforcelm.idmp.structure.playlist.PlaylistWithEntries;
import de.codingforcelm.idmp.structure.playlist.model.PlaylistEntryViewModel;
import de.codingforcelm.idmp.structure.playlist.model.PlaylistViewModel;
import de.codingforcelm.idmp.structure.playlist.model.PlaylistWithEntriesViewModel;

public class PlaylistCreateActivity extends AppCompatActivity {

    public static final String KEY_PLAYLIST_NAME = "de.codingforcelm.idmp.PLAYLIST_NAME";

    private PlaylistCreateCardAdapter adapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private AudioLoader audioLoader;
    private RecyclerView.LayoutManager layoutManager;
    private Toolbar toolbar;

    private String name;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_create);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchView =  findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.apc_recyclerview);
        searchView = findViewById(R.id.apc_searchview);
        layoutManager = new LinearLayoutManager(this);

        Bundle b = getIntent().getExtras();
        if(!b.containsKey(KEY_PLAYLIST_NAME)) {
            throw new IllegalStateException("Missing playlist name");
        }
        name = getIntent().getStringExtra(KEY_PLAYLIST_NAME);

        audioLoader = new AudioLoader(this);
        List<PhysicalSong> songs = audioLoader.getSongs();
        List<PlaylistSelection> selectionList = PlaylistSelection.createSelectionListFromList(songs);

        adapter = new PlaylistCreateCardAdapter(selectionList);

        recyclerView.setHasFixedSize(true);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playlist_create_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_accept) {
            savePlaylist();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void savePlaylist() {
        PlaylistViewModel playlistViewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);
        PlaylistEntryViewModel playlistEntryViewModel = new ViewModelProvider(this).get(PlaylistEntryViewModel.class);
        PlaylistWithEntriesViewModel viewModel = new ViewModelProvider(this).get(PlaylistWithEntriesViewModel.class);

        String playlistUuid = UUID.randomUUID().toString();
        List<PlaylistSelection> selected = adapter.getSelectedList();
        List<PlaylistEntry> entries = new ArrayList<>();

        Playlist playlist = new Playlist(playlistUuid, name);
        for(PlaylistSelection selection : selected) {
            PhysicalSong song = selection.getSong();
            PlaylistEntry entry = new PlaylistEntry(song.getId(), playlist.getListId());
            entries.add(entry);
        }

        PlaylistEntry[] entriesArr = entries.toArray(new PlaylistEntry[0]);

        //playlistViewModel.insert(playlist);
        //playlistEntryViewModel.insertAll();
        viewModel.insert(playlist, entriesArr);
    }


    public static class PlaylistSelection {

        private PhysicalSong song;
        private boolean isSelected;

        protected PlaylistSelection(PhysicalSong song) {
            this.song = song;
            this.setSelected(false);
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public PhysicalSong getSong() {
            return song;
        }

        public static List<PlaylistSelection> createSelectionListFromList(List<PhysicalSong> songlist) {
            List<PlaylistSelection> list = new ArrayList<>();
            for(PhysicalSong song : songlist) {
                list.add(new PlaylistSelection(song));
            }
            return list;
        }
    }

}