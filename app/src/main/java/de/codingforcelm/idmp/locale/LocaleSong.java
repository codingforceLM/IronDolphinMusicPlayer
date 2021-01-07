package de.codingforcelm.idmp.locale;

public class LocaleSong {
    private long id;
    private String data;
    private String title;
    private String album;
    private String artist;

    public LocaleSong(long id, String data, String title, String album, String artist) {
        this.id = id;
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LocaleSong)) {
            return false;
        }
        LocaleSong that = (LocaleSong) o;
        return this.getId() == that.getId();
    }
}
