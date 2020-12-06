package de.codingforcelm.idmp.structure;

import java.util.LinkedList;

import de.codingforcelm.idmp.music.Song;

public class SongQueue {

    private LinkedList<Song> songs;

    private SongQueue() {
        songs = new LinkedList<>();
    }

    public Song nextSong() {
        return songs.remove();
    }

    public LinkedList<Song> getRepresentation() {
        return songs;
    }

    public void addSong(Song song) {
        songs.add(song);
    }
}
