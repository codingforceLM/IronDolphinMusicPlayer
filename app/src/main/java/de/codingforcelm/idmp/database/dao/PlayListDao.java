package de.codingforcelm.idmp.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import de.codingforcelm.idmp.database.entity.Playlist;
import de.codingforcelm.idmp.database.entity.relation.PlaylistWithEntries;

/**
 * Data Access Object Interface for playlist entity
 */
@Dao
public interface PlayListDao {
    /**
     * Insert variable number of playlists into database
     * @param playlists playlists to be inserted
     */
    @Insert
    void insertAll(Playlist... playlists);

    /**
     * Delete a specific playlist
     * @param playlist playlist to be deleted
     */
    @Delete
    void delete(Playlist playlist);

    /**
     * Get all persisted playlists
     * @return Every playlist as a relation object
     */
    @Transaction
    @Query("SELECT * FROM playlist")
    LiveData<List<PlaylistWithEntries>> getAll();

    /**
     * Get a specifc playlist identified by its id
     * @param id uuid of the playlist
     * @return playlist as a relation object
     */
    @Transaction
    @Query("SELECT * FROM playlist WHERE listId = :id")
    LiveData<PlaylistWithEntries> getPlaylist(String id);
}
