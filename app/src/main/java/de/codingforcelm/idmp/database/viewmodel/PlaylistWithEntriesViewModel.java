package de.codingforcelm.idmp.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import de.codingforcelm.idmp.database.entity.Playlist;
import de.codingforcelm.idmp.database.entity.PlaylistEntry;
import de.codingforcelm.idmp.database.repository.PlaylistWithEntriesRepository;

/**
 *
 */
public class PlaylistWithEntriesViewModel extends AndroidViewModel {

    private final PlaylistWithEntriesRepository repository;

    /**
     * Default constructor
     * @param application application
     */
    public PlaylistWithEntriesViewModel(@NonNull Application application) {
        super(application);
        repository = PlaylistWithEntriesRepository.getInstance(application);
    }

    /**
     * Inserts a playlist with entries into database
     * @param playlist playlist
     * @param playlistEntries playlistEntries
     */
    public void insert(Playlist playlist, PlaylistEntry[] playlistEntries) {
        repository.insert(playlist, playlistEntries);
    }
}
