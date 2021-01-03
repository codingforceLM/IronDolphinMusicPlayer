package de.codingforcelm.idmp;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.audio.AudioLoader;
import de.codingforcelm.idmp.fragment.adapter.PlaylistCreateCardAdapter;

public class PlaylistCreateActivity extends AppCompatActivity {

    private PlaylistCreateCardAdapter adapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private AudioLoader audioLoader;
    private RecyclerView.LayoutManager layoutManager;
    private Toolbar toolbar;

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
            savePlaylist();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void savePlaylist() {
        // TODO implement database storage
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
