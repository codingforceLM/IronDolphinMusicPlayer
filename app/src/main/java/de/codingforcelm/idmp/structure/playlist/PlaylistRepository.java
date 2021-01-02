package de.codingforcelm.idmp.structure.playlist;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.database.IDMPRoomDatabase;
import de.codingforcelm.idmp.structure.playlist.dao.PlayListDao;

public class PlaylistRepository {

    private PlayListDao playListDao;
    private LiveData<List<PlaylistWithEntries>> playlists;

    public PlaylistRepository(Application application) {
        IDMPRoomDatabase db = IDMPRoomDatabase.getInstance(application);
        playListDao = db.playListDao();
        playlists = playListDao.getAll();
    }

    public LiveData<List<PlaylistWithEntries>> getPlaylists() {
        return playlists;
    }

    public void insert(Playlist playlist) {
        IDMPRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playListDao.insertAll(playlist);
            }
        });
    }

    public LiveData<PlaylistWithEntries> getPlaylist(long id) {
        return playListDao.getPlaylist(id);
    }
}
