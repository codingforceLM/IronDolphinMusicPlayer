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
    public static final String KEY_PLAYLIST_UUID = "de.codingforcelm.idmp.PLAYLIST_UUID";
    public static final String KEY_MODE = "de.codingforcelm.idmp.MODE";

    public static final String MODE_CREATE = "de.codingforcelm.idmp.MODE_CREATE";
    public static final String MODE_ADD = "de.codingforcelm.idmp.MODE_ADD";

    private PlaylistCreateCardAdapter adapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private AudioLoader audioLoader;
    private RecyclerView.LayoutManager layoutManager;
    private Toolbar toolbar;

    private String mode;
    private String name;
    private String playlistUuid;

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

        recyclerView = findViewById(R.id.apc_recyclerview);
        searchView = findViewById(R.id.apc_searchview);
        layoutManager = new LinearLayoutManager(this);

        Bundle b = getIntent().getExtras();
        if(!b.containsKey(KEY_MODE)) {
            throw new IllegalStateException("Missing mode");
        }
        mode = b.getString(KEY_MODE);
        switch(mode) {
            case MODE_ADD:
                if(!b.containsKey(KEY_PLAYLIST_UUID)) {
                    throw new IllegalStateException("Missing playlist name");
                }
                playlistUuid = b.getString(KEY_PLAYLIST_UUID);
                break;
            case MODE_CREATE:
                if(!b.containsKey(KEY_PLAYLIST_NAME)) {
                    throw new IllegalStateException("Missing playlist name");
                }
                name = getIntent().getStringExtra(KEY_PLAYLIST_NAME);
                break;
            default:
                throw new IllegalStateException("Unknown mode");
        }

        audioLoader = new AudioLoader(this);
        List<PhysicalSong> songs = audioLoader.getSongs();
        List<PlaylistSelection> selectionList = PlaylistSelection.createSelectionListFromList(songs);

        adapter = new PlaylistCreateCardAdapter(selectionList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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
            switch(mode) {
                case MODE_ADD:
                    addSongsToPlaylist();
                    break;
                case MODE_CREATE:
                    savePlaylist();
                    break;
            }
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addSongsToPlaylist() {
        PlaylistEntryViewModel playlistEntryViewModel = new ViewModelProvider(this).get(PlaylistEntryViewModel.class);

        List<PlaylistSelection> selected = adapter.getSelectedList();
        List<PlaylistEntry> entries = new ArrayList<>();
        for(PlaylistSelection selection : selected) {
            PhysicalSong song = selection.getSong();
            PlaylistEntry entry = new PlaylistEntry(song.getId(), playlistUuid);
            entries.add(entry);
        }
        PlaylistEntry[] entriesArr = entries.toArray(new PlaylistEntry[0]);
        playlistEntryViewModel.insertAll(entriesArr);
    }

    private void savePlaylist() {
        PlaylistWithEntriesViewModel viewModel = new ViewModelProvider(this).get(PlaylistWithEntriesViewModel.class);

        String uuid = UUID.randomUUID().toString();
        List<PlaylistSelection> selected = adapter.getSelectedList();
        List<PlaylistEntry> entries = new ArrayList<>();

        Playlist playlist = new Playlist(uuid, name);
        for(PlaylistSelection selection : selected) {
            PhysicalSong song = selection.getSong();
            PlaylistEntry entry = new PlaylistEntry(song.getId(), playlist.getListId());
            entries.add(entry);
        }

        PlaylistEntry[] entriesArr = entries.toArray(new PlaylistEntry[0]);
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
