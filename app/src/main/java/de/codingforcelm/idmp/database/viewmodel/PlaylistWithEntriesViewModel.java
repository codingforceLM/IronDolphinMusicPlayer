package de.codingforcelm.idmp.database.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import de.codingforcelm.idmp.database.entity.Playlist;
import de.codingforcelm.idmp.database.entity.PlaylistEntry;
import de.codingforcelm.idmp.database.repository.PlaylistWithEntriesRepository;

public class PlaylistWithEntriesViewModel extends AndroidViewModel {

    private final PlaylistWithEntriesRepository repository;

    public PlaylistWithEntriesViewModel(@NonNull Application application) {
        super(application);
        repository = PlaylistWithEntriesRepository.getInstance(application);
    }

    public void insert(Playlist playlist, PlaylistEntry[] playlistEntries) {
        repository.insert(playlist, playlistEntries);
    }
}
