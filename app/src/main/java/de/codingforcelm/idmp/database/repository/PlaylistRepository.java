package de.codingforcelm.idmp.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.codingforcelm.idmp.database.IDMPRoomDatabase;
import de.codingforcelm.idmp.database.dao.PlayListDao;
import de.codingforcelm.idmp.database.entity.Playlist;
import de.codingforcelm.idmp.database.entity.relation.PlaylistWithEntries;

/**
 *
 */
public class PlaylistRepository {

    private static volatile PlaylistRepository INSTANCE;

    private final PlayListDao playListDao;
    private final LiveData<List<PlaylistWithEntries>> playlists;

    /**
     * Default constructor
     * @param application application
     */
    protected PlaylistRepository(Application application) {
        IDMPRoomDatabase db = IDMPRoomDatabase.getInstance(application);
        playListDao = db.playListDao();
        playlists = playListDao.getAll();
    }

    /**
     * Returns PlaylistRepository instance
     * @param application application
     * @return PlaylistRepository instance
     */
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

    /**
     * Returns LiveData list of PlaylistWithEntries
     * @return playlists
     */
    public LiveData<List<PlaylistWithEntries>> getPlaylists() {
        return playlists;
    }

    /**
     * Inserts playlist into database
     * @param playlist playlist
     */
    public void insert(Playlist playlist) {
        IDMPRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playListDao.insertAll(playlist);
            }
        });
    }

    /**
     * Returns LiveData of PlaylistWithEntries
     * @param id playlistId
     * @return
     */
    public LiveData<PlaylistWithEntries> getPlaylist(String id) {
        return playListDao.getPlaylist(id);
    }

    /**
     * Deletes a specific playlist in database
     * @param playlist playlist
     */
    public void deletePlaylist(Playlist playlist) {
        IDMPRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                playListDao.delete(playlist);
            }
        });
    }
}
