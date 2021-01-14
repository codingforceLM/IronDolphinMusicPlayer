package de.codingforcelm.idmp.activity.playlist;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.activity.MainActivity;
import de.codingforcelm.idmp.activity.MainActivitySingleton;
import de.codingforcelm.idmp.loader.AudioLoader;
import de.codingforcelm.idmp.fragment.adapter.PlaylistCreateCardAdapter;
import de.codingforcelm.idmp.locale.LocaleSong;
import de.codingforcelm.idmp.service.MusicService;
import de.codingforcelm.idmp.database.entity.Playlist;
import de.codingforcelm.idmp.database.entity.PlaylistEntry;
import de.codingforcelm.idmp.database.viewmodel.PlaylistEntryViewModel;
import de.codingforcelm.idmp.database.viewmodel.PlaylistWithEntriesViewModel;

/**
 * class
 */
public class PlaylistCreateActivity extends AppCompatActivity {
    public static final String LOG_TAG = "PlaylistCreateActivity";
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
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.apc_recyclerview);
        searchView = findViewById(R.id.apc_searchview);
        layoutManager = new LinearLayoutManager(this);

        Bundle b = getIntent().getExtras();
        if (!b.containsKey(KEY_MODE)) {
            throw new IllegalStateException("Missing mode");
        }
        mode = b.getString(KEY_MODE);
        switch (mode) {
            case MODE_ADD:
                if (!b.containsKey(KEY_PLAYLIST_UUID)) {
                    throw new IllegalStateException("Missing playlist name");
                }
                playlistUuid = b.getString(KEY_PLAYLIST_UUID);
                break;
            case MODE_CREATE:
                if (!b.containsKey(KEY_PLAYLIST_NAME)) {
                    throw new IllegalStateException("Missing playlist name");
                }
                name = getIntent().getStringExtra(KEY_PLAYLIST_NAME);
                break;
            default:
                throw new IllegalStateException("Unknown mode");
        }

        audioLoader = new AudioLoader(this);
        List<LocaleSong> songs = audioLoader.getSongs();
        List<PlaylistSelection> selectionList = PlaylistSelection.createSelectionListFromList(songs);

        adapter = new PlaylistCreateCardAdapter(selectionList, getApplicationContext());

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
            switch (mode) {
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
        Log.e(LOG_TAG, "--addSongsToPlaylist--");
        PlaylistEntryViewModel playlistEntryViewModel = new ViewModelProvider(this).get(PlaylistEntryViewModel.class);

        List<PlaylistSelection> selected = adapter.getSelectedList();
        List<PlaylistEntry> entries = new ArrayList<>();
        for (PlaylistSelection selection : selected) {
            LocaleSong song = selection.getSong();
            PlaylistEntry entry = new PlaylistEntry(song.getId(), playlistUuid);
            entries.add(entry);
        }
        PlaylistEntry[] entriesArr = entries.toArray(new PlaylistEntry[0]);
        playlistEntryViewModel.insertAll(entriesArr);

        MainActivity activity = MainActivitySingleton.getInstance().getMainActivity();
        if (activity == null) {
            throw new IllegalStateException("couldnt get MainActivity");
        }
        Bundle b = new Bundle();
        b.putString(MusicService.KEY_CONTEXT, MusicService.CONTEXT_PREFIX_PLAYLIST + playlistUuid);
        b.putString(MusicService.KEY_PLAYLIST_ID, playlistUuid);
        Log.e(LOG_TAG, "Songs added to Playlist");
    }

    private void savePlaylist() {
        Log.e(LOG_TAG, "--savePlaylist--");
        PlaylistWithEntriesViewModel viewModel = new ViewModelProvider(this).get(PlaylistWithEntriesViewModel.class);

        String uuid = UUID.randomUUID().toString();
        List<PlaylistSelection> selected = adapter.getSelectedList();
        List<PlaylistEntry> entries = new ArrayList<>();

        Playlist playlist = new Playlist(uuid, name);
        for (PlaylistSelection selection : selected) {
            LocaleSong song = selection.getSong();
            PlaylistEntry entry = new PlaylistEntry(song.getId(), playlist.getListId());
            entries.add(entry);
        }

        PlaylistEntry[] entriesArr = entries.toArray(new PlaylistEntry[0]);
        viewModel.insert(playlist, entriesArr);
        Log.e(LOG_TAG, "Playlist saved");
    }


    public static class PlaylistSelection {

        private final LocaleSong song;
        private boolean isSelected;

        protected PlaylistSelection(LocaleSong song) {
            this.song = song;
            this.setSelected(false);
        }

        public static List<PlaylistSelection> createSelectionListFromList(List<LocaleSong> songlist) {
            List<PlaylistSelection> list = new ArrayList<>();
            for (LocaleSong song : songlist) {
                list.add(new PlaylistSelection(song));
            }
            return list;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public LocaleSong getSong() {
            return song;
        }
    }

}
