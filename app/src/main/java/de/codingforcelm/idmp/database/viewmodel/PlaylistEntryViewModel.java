package de.codingforcelm.idmp.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.codingforcelm.idmp.database.entity.PlaylistEntry;
import de.codingforcelm.idmp.database.repository.PlaylistEntryRepository;

public class PlaylistEntryViewModel extends AndroidViewModel {

    private final PlaylistEntryRepository repository;
    private final LiveData<List<PlaylistEntry>> entries;

    public PlaylistEntryViewModel(@NonNull Application application) {
        super(application);
        repository = PlaylistEntryRepository.getInstance(application);
        entries = repository.getEntries();
    }

    public LiveData<List<PlaylistEntry>> getEntries() {
        return entries;
    }

    public void insert(PlaylistEntry entry) {
        repository.insert(entry);
    }

    public void insertAll(PlaylistEntry... playlistEntries) {
        repository.insertAll(playlistEntries);
    }

    public void delete(PlaylistEntry entry) {
        repository.delete(entry);
    }
}
