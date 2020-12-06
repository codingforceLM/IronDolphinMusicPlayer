package de.codingforcelm.idmp.structure;

import android.net.Uri;

import java.util.List;

import de.codingforcelm.idmp.music.Song;

public class Playlist {

    private String name;
    private List<Song> songs;
    private Uri image;

    public Playlist(String name, List<Song> songs, Uri image) {
        this.name = name;
        this.songs = songs;
        this.image = image;
    }

    public long getOverallDuration() {
        long total = 0;
        for(Song song : songs) {
            total += song.getDuration();
        }
        return total;
    }

    public int size() {
        return songs.size();
    }

    public Song getSong(int index) {
        return songs.get(index);
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public void addListOfSongs(List<Song> songs) {
        this.songs.addAll(songs);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
