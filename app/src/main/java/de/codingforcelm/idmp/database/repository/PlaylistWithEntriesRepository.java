package de.codingforcelm.idmp.database.repository;

import android.app.Application;

import de.codingforcelm.idmp.database.IDMPRoomDatabase;
import de.codingforcelm.idmp.database.dao.PlayListDao;
import de.codingforcelm.idmp.database.dao.PlaylistEntryDao;
import de.codingforcelm.idmp.database.entity.Playlist;
import de.codingforcelm.idmp.database.entity.PlaylistEntry;

public class PlaylistWithEntriesRepository {

    private static volatile PlaylistWithEntriesRepository INSTANCE;

    private final PlayListDao playListDao;
    private final PlaylistEntryDao playlistEntryDao;

    protected PlaylistWithEntriesRepository(Application application) {
        IDMPRoomDatabase db = IDMPRoomDatabase.getInstance(application);
        playListDao = db.playListDao();
        playlistEntryDao = db.playlistEntryDao();
    }

    public static PlaylistWithEntriesRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (PlaylistRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PlaylistWithEntriesRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    public void insert(Playlist playlist, PlaylistEntry[] playlistEntries) {
        IDMPRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playListDao.insertAll(playlist);
                playlistEntryDao.insertAll(playlistEntries);
            }
        });
    }

}
