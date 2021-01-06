package de.codingforcelm.idmp.player;

import java.util.List;

import de.codingforcelm.idmp.music.Song;
import de.codingforcelm.idmp.structure.playlist.Playlist;

public class Player {

    private List<Playlist> playlist;
    private SongQueue queue;
    private List<Song> history;
    private List<Song> following;
    private Playlist context;
    private boolean shuffle;
    private RepeatMode rm;

    public void play() {

    }

    public void play(Song song) {

    }

    public void play(Song song, Playlist context) {

    }

    public void nextSong() {

    }

    public void previousSong() {

    }

    public void stop() {

    }

    public void toggleShuffle() {
        shuffle = !shuffle;
    }
    
    public void setRepeatMode(RepeatMode rm) {
        this.rm = rm;
    }

    public void skipTo(long timestamp) {
        
    }

}
