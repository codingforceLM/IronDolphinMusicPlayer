package de.codingforcelm.idmp.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.codingforcelm.idmp.database.IDMPRoomDatabase;
import de.codingforcelm.idmp.database.dao.PlaylistEntryDao;
import de.codingforcelm.idmp.database.entity.PlaylistEntry;

/**
 * Repository for the relation object {@link de.codingforcelm.idmp.database.entity.relation.PlaylistWithEntries}
 */
public class PlaylistEntryRepository {

    private static volatile PlaylistEntryRepository INSTANCE;

    private final PlaylistEntryDao playlistEntryDao;
    private final LiveData<List<PlaylistEntry>> entries;

    /**
     * Default constructor
     * @param application application
     */
    protected PlaylistEntryRepository(Application application) {
        IDMPRoomDatabase db = IDMPRoomDatabase.getInstance(application);
        playlistEntryDao = db.playlistEntryDao();
        entries = playlistEntryDao.getAll();
    }

    /**
     * Returns PlaylistEntryRepository instance
     * @param application application
     * @return PlaylistEntryRepository instance
     */
    public static PlaylistEntryRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (PlaylistRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PlaylistEntryRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Returns LiveData list of PlaylistEntry
     * @return entries
     */
    public LiveData<List<PlaylistEntry>> getEntries() {
        return entries;
    }

    /**
     * Inserts PlaylistEntry into database
     * @param entry entry
     */
    public void insert(PlaylistEntry entry) {
        IDMPRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playlistEntryDao.insertAll(entry);
            }
        });
    }

    /**
     * Inserts variable number of PlaylistEntries into database
     * @param playlistEntries playlistEntries
     */
    public void insertAll(PlaylistEntry... playlistEntries) {
        IDMPRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playlistEntryDao.insertAll(playlistEntries);
            }
        });
    }

    /**
     * Deletes a PlaylistEntry in database
     * @param entry entry
     */
    public void delete(PlaylistEntry entry) {
        IDMPRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playlistEntryDao.delete(entry);
            }
        });
    }
}
