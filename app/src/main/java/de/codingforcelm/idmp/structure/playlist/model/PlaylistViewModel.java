package de.codingforcelm.idmp.structure.playlist.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.structure.playlist.PlaylistRepository;
import de.codingforcelm.idmp.structure.playlist.PlaylistWithEntries;

public class PlaylistViewModel extends AndroidViewModel {

    private PlaylistRepository repository;
    private LiveData<List<PlaylistWithEntries>> playlists;

    public PlaylistViewModel(@NonNull Application application) {
        super(application);
        repository = new PlaylistRepository(application);
        playlists = repository.getPlaylists();
    }

    public LiveData<List<PlaylistWithEntries>> getPlaylists() {
        return playlists;
    }

    public LiveData<PlaylistWithEntries> getPlaylist(long id) {
        return repository.getPlaylist(id);
    }
}
