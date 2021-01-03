package de.codingforcelm.idmp.structure.playlist;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.database.IDMPRoomDatabase;
import de.codingforcelm.idmp.structure.playlist.dao.PlayListDao;

public class PlaylistRepository {

    private static volatile PlaylistRepository INSTANCE;

    private PlayListDao playListDao;
    private LiveData<List<PlaylistWithEntries>> playlists;

    protected PlaylistRepository(Application application) {
        IDMPRoomDatabase db = IDMPRoomDatabase.getInstance(application);
        playListDao = db.playListDao();
        playlists = playListDao.getAll();
    }

    public static PlaylistRepository getInstance(Application application) {
        if(INSTANCE == null) {
            synchronized (PlaylistRepository.class) {
                if(INSTANCE == null) {
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

    public LiveData<PlaylistWithEntries> getPlaylist(long id) {
        return playListDao.getPlaylist(id);
    }
}
