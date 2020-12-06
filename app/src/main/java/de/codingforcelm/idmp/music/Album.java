package de.codingforcelm.idmp.music;

import java.util.Date;
import java.util.List;

public class Album {

    private String name;
    private Date release;
    private long duration;
    private Artist artist;
    private List<Song> songs;

    public Album(String name, Date release, long duration, Artist artist, List<Song> songs) {
        this.name = name;
        this.release = release;
        this.duration = duration;
        this.artist = artist;
        this.songs = songs;
    }

    public Album(String name, Date release, Artist artist, List<Song> songs) {
        this.name = name;
        this.release = release;
        this.artist = artist;
        this.songs = songs;
        this.duration = calculateDuration(songs);
    }

    private long calculateDuration(List<Song> songlist) {
        long total = 0;
        for(Song song : songlist) {
            total += song.getDuration();
        }
        return total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getRelease() {
        return release;
    }

    public void setRelease(Date release) {
        this.release = release;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
