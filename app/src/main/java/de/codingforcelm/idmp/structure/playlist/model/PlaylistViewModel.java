package de.codingforcelm.idmp.structure.playlist.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.structure.playlist.Playlist;
import de.codingforcelm.idmp.structure.playlist.PlaylistEntry;
import de.codingforcelm.idmp.structure.playlist.PlaylistRepository;
import de.codingforcelm.idmp.structure.playlist.PlaylistWithEntries;

public class PlaylistViewModel extends AndroidViewModel {

    private PlaylistRepository repository;
    private LiveData<List<PlaylistWithEntries>> playlists;

    public PlaylistViewModel(@NonNull Application application) {
        super(application);
        repository = PlaylistRepository.getInstance(application);
        playlists = repository.getPlaylists();
    }

    public LiveData<List<PlaylistWithEntries>> getPlaylists() {
        return playlists;
    }

    public LiveData<PlaylistWithEntries> getPlaylist(String id) {
        return repository.getPlaylist(id);
    }

    public void insert(Playlist playlist) {
        repository.insert(playlist);
    }

}
