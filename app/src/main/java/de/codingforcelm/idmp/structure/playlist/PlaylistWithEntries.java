package de.codingforcelm.idmp.structure.playlist;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class PlaylistWithEntries {
    @Embedded
    private Playlist playlist;
    @Relation(
            parentColumn = "listId",
            entityColumn = "playlistId"
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
