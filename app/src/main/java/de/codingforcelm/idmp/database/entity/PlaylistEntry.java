package de.codingforcelm.idmp.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 *
 */
@Entity(tableName = "playlistentry")
public class PlaylistEntry {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int entryId;
    private long mediaId;
    private String playlistId;

    /**
     * Default constructor
     * @param mediaId mediaId
     * @param playlistId playlistId
     */
    public PlaylistEntry(long mediaId, String playlistId) {
        this.entryId = 0; // 0 tells framework to autogenerate id
        this.mediaId = mediaId;
        this.playlistId = playlistId;
    }

    /**
     * Returns entryId
     * @return entryId
     */
    public int getEntryId() {
        return entryId;
    }

    /**
     * Sets entryId
     * @param entryId entryId
     */
    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    /**
     * Returns mediaId
     * @return mediaId
     */
    public long getMediaId() {
        return mediaId;
    }

    /**
     * Sets mediaId
     * @param mediaId mediaId
     */
    public void setMediaId(long mediaId) {
        this.mediaId = mediaId;
    }

    /**
     * Returns playlistId
     * @return playlistId
     */
    public String getPlaylistId() {
        return playlistId;
    }

    /**
     * Sets playlistId
     * @param playlistId playlistId
     */
    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }
}
