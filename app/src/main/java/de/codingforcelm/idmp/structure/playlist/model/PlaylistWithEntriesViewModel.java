package de.codingforcelm.idmp.structure.playlist.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import de.codingforcelm.idmp.structure.playlist.Playlist;
import de.codingforcelm.idmp.structure.playlist.PlaylistEntry;
import de.codingforcelm.idmp.structure.playlist.PlaylistWithEntriesRepository;

public class PlaylistWithEntriesViewModel extends AndroidViewModel {

    private PlaylistWithEntriesRepository repository;

    public PlaylistWithEntriesViewModel(@NonNull Application application) {
        super(application);
        repository = PlaylistWithEntriesRepository.getInstance(application);
    }

    public void insert(Playlist playlist, PlaylistEntry[] playlistEntries) {
        repository.insert(playlist, playlistEntries);
    }
}
