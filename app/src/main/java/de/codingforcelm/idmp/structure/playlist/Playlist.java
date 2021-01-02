package de.codingforcelm.idmp.structure.playlist;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

import de.codingforcelm.idmp.music.Song;

@Entity(tableName = "playlist")
public class Playlist {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int listId;
    private String name;

    public Playlist(int listId, String name) {
        this.listId = listId;
        this.name = name;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
