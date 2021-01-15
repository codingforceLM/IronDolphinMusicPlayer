package de.codingforcelm.idmp.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import de.codingforcelm.idmp.database.entity.PlaylistEntry;

/**
 * Data Access Object Interface for playlistentry entity
 */
@Dao
public interface PlaylistEntryDao {
    /**
     * Insert variable number of entries into database
     * @param entries entries to be inserted
     */
    @Insert
    void insertAll(PlaylistEntry... entries);

    /**
     * Delete a specific entry
     * @param entry entry to be deleted
     */
    @Delete
    void delete(PlaylistEntry entry);

    /**
     * Get all playlist entries
     * @return all playlist entries
     */
    @Query("SELECT * FROM playlistentry")
    LiveData<List<PlaylistEntry>> getAll();

}
