package de.codingforcelm.idmp.structure.playlist.dao;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.codingforcelm.idmp.database.IDMPRoomDatabase;
import de.codingforcelm.idmp.structure.playlist.Playlist;
import de.codingforcelm.idmp.structure.playlist.PlaylistEntry;
import de.codingforcelm.idmp.structure.playlist.PlaylistRepository;
import de.codingforcelm.idmp.structure.playlist.PlaylistWithEntries;

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
