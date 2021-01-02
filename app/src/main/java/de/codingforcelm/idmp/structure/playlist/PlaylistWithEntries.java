package de.codingforcelm.idmp.structure.playlist;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class PlaylistWithEntries {
    @Embedded
    public Playlist playlist;
    @Relation(
            parentColumn = "listId",
            entityColumn = "playlistId"
    )
    public List<PlaylistEntry> entries;
}
