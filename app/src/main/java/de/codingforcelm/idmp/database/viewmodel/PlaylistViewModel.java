package de.codingforcelm.idmp.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.codingforcelm.idmp.database.entity.Playlist;
import de.codingforcelm.idmp.database.entity.relation.PlaylistWithEntries;
import de.codingforcelm.idmp.database.repository.PlaylistRepository;

/**
 * ViewModel encapsulating {@link de.codingforcelm.idmp.database.repository.PlaylistRepository}
 */
public class PlaylistViewModel extends AndroidViewModel {

    private final PlaylistRepository repository;
    private final LiveData<List<PlaylistWithEntries>> playlists;

    /**
     * Default constructor
     * @param application application
     */
    public PlaylistViewModel(@NonNull Application application) {
        super(application);
        repository = PlaylistRepository.getInstance(application);
        playlists = repository.getPlaylists();
    }

    /**
     * Returns LiveData list of PlaylistWithEntries
     * @return all playlists
     */
    public LiveData<List<PlaylistWithEntries>> getPlaylists() {
        return playlists;
    }

    /**
     * Returns LiveData for a specific playlist
     * @param id playlistId
     * @return playlist
     */
    public LiveData<PlaylistWithEntries> getPlaylist(String id) {
        return repository.getPlaylist(id);
    }

    /**
     * Deletes a specific playlist in database
     * @param playlist playlist
     */
    public void deletePlaylist(Playlist playlist) {
        repository.deletePlaylist(playlist);
    }

    /**
     * Inserts a playlist into database
     * @param playlist playlist
     */
    public void insert(Playlist playlist) {
        repository.insert(playlist);
    }


}

