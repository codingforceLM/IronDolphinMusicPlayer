package de.codingforcelm.idmp.database.repository;

import android.app.Application;

import de.codingforcelm.idmp.database.IDMPRoomDatabase;
import de.codingforcelm.idmp.database.dao.PlayListDao;
import de.codingforcelm.idmp.database.dao.PlaylistEntryDao;
import de.codingforcelm.idmp.database.entity.Playlist;
import de.codingforcelm.idmp.database.entity.PlaylistEntry;

/**
 * Repository for the relation object {@link de.codingforcelm.idmp.database.entity.PlaylistEntry}
 */
public class PlaylistWithEntriesRepository {

    private static volatile PlaylistWithEntriesRepository INSTANCE;

    private final PlayListDao playListDao;
    private final PlaylistEntryDao playlistEntryDao;

    /**
     * Default constructor
     * @param application application
     */
    protected PlaylistWithEntriesRepository(Application application) {
        IDMPRoomDatabase db = IDMPRoomDatabase.getInstance(application);
        playListDao = db.playListDao();
        playlistEntryDao = db.playlistEntryDao();
    }

    /**
     * Returns PlaylistWithEntriesRepository instance
     * @param application application
     * @return PlaylistWithEntriesRepository instance
     */
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

    /**
     * Inserts a playlist with entries into database
     * @param playlist playlist
     * @param playlistEntries playlistEntries
     */
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
