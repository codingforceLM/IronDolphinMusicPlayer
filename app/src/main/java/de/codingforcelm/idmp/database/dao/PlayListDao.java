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

@Dao
public interface PlayListDao {
    @Insert
    void insertAll(Playlist... playlists);

    @Delete
    void delete(Playlist playlist);

    @Query("DELETE FROM playlist")
    void deletePlaylist();

    @Transaction
    @Query("SELECT * FROM playlist")
    LiveData<List<PlaylistWithEntries>> getAll();

    @Transaction
    @Query("SELECT * FROM playlist WHERE listId = :id")
    LiveData<PlaylistWithEntries> getPlaylist(String id);
}
