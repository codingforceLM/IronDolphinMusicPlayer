package de.codingforcelm.idmp.structure.playlist.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.codingforcelm.idmp.structure.playlist.PlaylistEntry;
import de.codingforcelm.idmp.structure.playlist.PlaylistEntryRepository;
import de.codingforcelm.idmp.structure.playlist.PlaylistRepository;
import de.codingforcelm.idmp.structure.playlist.PlaylistWithEntries;

public class PlaylistEntryViewModel extends AndroidViewModel {

    private PlaylistEntryRepository repository;
    private LiveData<List<PlaylistEntry>> entries;

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
