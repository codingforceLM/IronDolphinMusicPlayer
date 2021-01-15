package de.codingforcelm.idmp.database.entity.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import de.codingforcelm.idmp.database.entity.Playlist;
import de.codingforcelm.idmp.database.entity.PlaylistEntry;

/**
 *
 */
public class PlaylistWithEntries {
    @Embedded
    private Playlist playlist;
    @Relation(
            parentColumn = "listId",
            entityColumn = "playlistId",
            entity = PlaylistEntry.class
    )
    private List<PlaylistEntry> entries;

    /**
     * Default constructor
     * @param playlist playlist
     * @param entries entryList
     */
    public PlaylistWithEntries(Playlist playlist, List<PlaylistEntry> entries) {
        this.playlist = playlist;
        this.entries = entries;
    }

    /**
     * Returns playlist
     * @return playlist
     */
    public Playlist getPlaylist() {
        return playlist;
    }

    /**
     * Sets playlist
     * @param playlist playlist
     */
    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    /**
     * Returns list of playlist entries
     * @return playlist entries
     */
    public List<PlaylistEntry> getEntries() {
        return entries;
    }

    /**
     * Sets playlist entries
     * @param entries list of playlist entries
     */
    public void setEntries(List<PlaylistEntry> entries) {
        this.entries = entries;
    }
}
