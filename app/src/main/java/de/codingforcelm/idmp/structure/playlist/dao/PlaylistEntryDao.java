package de.codingforcelm.idmp.structure.playlist.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.structure.playlist.Playlist;
import de.codingforcelm.idmp.structure.playlist.PlaylistEntry;
import de.codingforcelm.idmp.structure.playlist.PlaylistWithEntries;

@Dao
public interface PlaylistEntryDao {
    @Insert
    void insertAll(PlaylistEntry... playlistentries);

    @Delete
    void delete(PlaylistEntry playlist);

    @Query("DELETE FROM playlistentry")
    void deletePlaylistEntries();

    @Query("SELECT * FROM playlistentry")
    LiveData<List<PlaylistEntry>> getAll();

    @Query("SELECT * FROM playlistentry WHERE entryId = :id")
    LiveData<PlaylistEntry> getPlaylistEntry(long id);
}
