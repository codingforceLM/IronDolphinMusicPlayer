package de.codingforcelm.idmp.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import de.codingforcelm.idmp.database.entity.PlaylistEntry;

@Dao
public interface PlaylistEntryDao {
    @Insert
    void insertAll(PlaylistEntry... entries);

    @Delete
    void delete(PlaylistEntry entry);

    @Query("DELETE FROM playlistentry")
    void deletePlaylistEntries();

    @Query("SELECT * FROM playlistentry")
    LiveData<List<PlaylistEntry>> getAll();

    @Query("SELECT * FROM playlistentry WHERE entryId = :id")
    LiveData<PlaylistEntry> getPlaylistEntry(long id);
}
