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

}
