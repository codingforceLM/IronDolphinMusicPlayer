package de.codingforcelm.idmp.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.codingforcelm.idmp.database.entity.PlaylistEntry;
import de.codingforcelm.idmp.database.repository.PlaylistEntryRepository;

/**
 *
 */
public class PlaylistEntryViewModel extends AndroidViewModel {

    private final PlaylistEntryRepository repository;
    private final LiveData<List<PlaylistEntry>> entries;

    /**
     * Default constructor
     * @param application application
     */
    public PlaylistEntryViewModel(@NonNull Application application) {
        super(application);
        repository = PlaylistEntryRepository.getInstance(application);
        entries = repository.getEntries();
    }

    /**
     * Returns LiveData list of PlaylistEntry
     * @return entries
     */
    public LiveData<List<PlaylistEntry>> getEntries() {
        return entries;
    }

    /**
     * Inserts PlaylistEntry into database
     * @param entry playlistEntry
     */
    public void insert(PlaylistEntry entry) {
        repository.insert(entry);
    }

    /**
     * Inserts variable number of entries into database
     * @param playlistEntries playlistEntry
     */
    public void insertAll(PlaylistEntry... playlistEntries) {
        repository.insertAll(playlistEntries);
    }

    /**
     * Deletes PlaylistEntry in database
     * @param entry playlistEntry
     */
    public void delete(PlaylistEntry entry) {
        repository.delete(entry);
    }
}
