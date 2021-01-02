package de.codingforcelm.idmp.structure.playlist.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import de.codingforcelm.idmp.structure.playlist.Playlist;
import de.codingforcelm.idmp.structure.playlist.PlaylistWithEntries;

@Dao
public interface PlayListDao {
    @Insert
    void insertAll(Playlist... playlists);

    @Delete
    void delete(Playlist playlist);

    @Query("SELECT * FROM playlist")
    List<PlaylistWithEntries> getAll();

    @Query("SELECT * FROM playlist WHERE listId = :id")
    Playlist getPlaylist(long id);
}
