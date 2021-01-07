package de.codingforcelm.idmp.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.codingforcelm.idmp.database.entity.Playlist;
import de.codingforcelm.idmp.database.repository.PlaylistRepository;
import de.codingforcelm.idmp.database.entity.relation.PlaylistWithEntries;

public class PlaylistViewModel extends AndroidViewModel {

    private final PlaylistRepository repository;
    private final LiveData<List<PlaylistWithEntries>> playlists;

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

    public void deletePlaylist(Playlist playlist) {
        repository.deletePlaylist(playlist);
    }

    public void insert(Playlist playlist) {
        repository.insert(playlist);
    }


}

