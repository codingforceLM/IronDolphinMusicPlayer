package de.codingforcelm.idmp.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "playlistentry")
public class PlaylistEntry {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int entryId;
    private long mediaId;
    private String playlistId;

    public PlaylistEntry(long mediaId, String playlistId) {
        this.entryId = 0; // 0 tells framework to autogenerate id
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

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }
}
