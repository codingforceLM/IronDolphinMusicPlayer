package de.codingforcelm.idmp.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Playlist entity class to represent a persisted playlist
 */
@Entity(tableName = "playlist")
public class Playlist {

    @PrimaryKey()
    @NonNull
    private String listId;
    private String name;

    /**
     * Default constructor
     * @param listId playlistId
     * @param name name
     */
    public Playlist(String listId, String name) {
        this.listId = listId;
        this.name = name;
    }

    /**
     * Returns playlistId
     * @return playlistId
     */
    public String getListId() {
        return listId;
    }

    /**
     * Sets playlistId
     * @param listId playlistId
     */
    public void setListId(String listId) {
        this.listId = listId;
    }

    /**
     * Returns name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }
}
