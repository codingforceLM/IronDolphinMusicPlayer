package de.codingforcelm.idmp.database.entity.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import de.codingforcelm.idmp.database.entity.Playlist;
import de.codingforcelm.idmp.database.entity.PlaylistEntry;

public class PlaylistWithEntries {
    @Embedded
    private Playlist playlist;
    @Relation(
            parentColumn = "listId",
            entityColumn = "playlistId",
            entity = PlaylistEntry.class
    )
    private List<PlaylistEntry> entries;

    public PlaylistWithEntries(Playlist playlist, List<PlaylistEntry> entries) {
        this.playlist = playlist;
        this.entries = entries;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public List<PlaylistEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<PlaylistEntry> entries) {
        this.entries = entries;
    }
}
