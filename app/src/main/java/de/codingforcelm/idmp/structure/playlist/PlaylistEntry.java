package de.codingforcelm.idmp.structure.playlist;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PlaylistEntry {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int entryId;
    private long mediaId;
    private int playlistId;
}
