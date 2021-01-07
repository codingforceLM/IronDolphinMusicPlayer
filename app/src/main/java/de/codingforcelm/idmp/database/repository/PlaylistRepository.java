package de.codingforcelm.idmp.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.codingforcelm.idmp.database.IDMPRoomDatabase;
import de.codingforcelm.idmp.database.dao.PlayListDao;
import de.codingforcelm.idmp.database.entity.Playlist;
import de.codingforcelm.idmp.database.entity.relation.PlaylistWithEntries;

public class PlaylistRepository {

    private static volatile PlaylistRepository INSTANCE;

    private final PlayListDao playListDao;
    private final LiveData<List<PlaylistWithEntries>> playlists;

    protected PlaylistRepository(Application application) {
        IDMPRoomDatabase db = IDMPRoomDatabase.getInstance(application);
        playListDao = db.playListDao();
        playlists = playListDao.getAll();
    }

    public static PlaylistRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (PlaylistRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PlaylistRepository(application);
                }
            }
        }
        return INSTANCE;
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

    public LiveData<PlaylistWithEntries> getPlaylist(String id) {
        return playListDao.getPlaylist(id);
    }

    public void deletePlaylist(Playlist playlist) {
        IDMPRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playListDao.delete(playlist);
            }
        });
    }
}
