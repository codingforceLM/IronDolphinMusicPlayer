package de.codingforcelm.idmp.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.codingforcelm.idmp.database.IDMPRoomDatabase;
import de.codingforcelm.idmp.database.dao.PlaylistEntryDao;
import de.codingforcelm.idmp.database.entity.PlaylistEntry;

public class PlaylistEntryRepository {

    private static volatile PlaylistEntryRepository INSTANCE;

    private final PlaylistEntryDao playlistEntryDao;
    private final LiveData<List<PlaylistEntry>> entries;

    protected PlaylistEntryRepository(Application application) {
        IDMPRoomDatabase db = IDMPRoomDatabase.getInstance(application);
        playlistEntryDao = db.playlistEntryDao();
        entries = playlistEntryDao.getAll();
    }

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

    public LiveData<List<PlaylistEntry>> getEntries() {
        return entries;
    }

    public void insert(PlaylistEntry entry) {
        IDMPRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playlistEntryDao.insertAll(entry);
            }
        });
    }

    public void insertAll(PlaylistEntry... playlistEntries) {
        IDMPRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playlistEntryDao.insertAll(playlistEntries);
            }
        });
    }

    public void delete(PlaylistEntry entry) {
        IDMPRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playlistEntryDao.delete(entry);
            }
        });
    }
}
