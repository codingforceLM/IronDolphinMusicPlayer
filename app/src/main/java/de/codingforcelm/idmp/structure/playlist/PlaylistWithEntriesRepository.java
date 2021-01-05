package de.codingforcelm.idmp.structure.playlist;

import android.app.Application;

import de.codingforcelm.idmp.database.IDMPRoomDatabase;
import de.codingforcelm.idmp.structure.playlist.dao.PlayListDao;
import de.codingforcelm.idmp.structure.playlist.dao.PlaylistEntryDao;

public class PlaylistWithEntriesRepository {

    private static volatile PlaylistWithEntriesRepository INSTANCE;

    private PlayListDao playListDao;
    private PlaylistEntryDao playlistEntryDao;

    protected PlaylistWithEntriesRepository(Application application) {
        IDMPRoomDatabase db = IDMPRoomDatabase.getInstance(application);
        playListDao = db.playListDao();
        playlistEntryDao = db.playlistEntryDao();
    }

    public static PlaylistWithEntriesRepository getInstance(Application application) {
        if(INSTANCE == null) {
            synchronized (PlaylistRepository.class) {
                if(INSTANCE == null) {
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
