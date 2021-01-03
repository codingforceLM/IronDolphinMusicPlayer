package de.codingforcelm.idmp.structure.playlist;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "playlistentry")
public class PlaylistEntry {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int entryId;
    private long mediaId;
    private int playlistId;

    public PlaylistEntry(int entryId, long mediaId, int playlistId) {
        this.entryId = entryId;
        this.mediaId = mediaId;
        this.playlistId = playlistId;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public long getMediaId() {
        return mediaId;
    }

    public void setMediaId(long mediaId) {
        this.mediaId = mediaId;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }
}
