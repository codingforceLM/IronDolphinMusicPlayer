package de.codingforcelm.idmp.locale;

/**
 * Representation object for a local stored song
 */
public class LocaleSong {
    private long id;
    private String data;
    private String title;
    private String album;
    private String artist;

    /**
     * Default constructor
     * @param id id
     * @param data data
     * @param title title
     * @param album album
     * @param artist artist
     */
    public LocaleSong(long id, String data, String title, String album, String artist) {
        this.id = id;
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
    }

    /**
     * Returns song id
     * @return song id
     */
    public long getId() {
        return id;
    }
    /**
     * Sets song id
     * @param id song id
     */
    public void setId(long id) {
        this.id = id;
    }
    /**
     * Returns song data
     * @return song data
     */
    public String getData() {
        return data;
    }
    /**
     * Sets song data
     * @param data song data
     */
    public void setData(String data) {
        this.data = data;
    }
    /**
     * Returns song title
     * @return song title
     */
    public String getTitle() {
        return title;
    }
    /**
     * Sets song title
     * @param title song title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * Returns album from song
     * @return album from song
     */
    public String getAlbum() {
        return album;
    }
    /**
     * Sets album for song
     * @param album song title
     */
    public void setAlbum(String album) {
        this.album = album;
    }
    /**
     * Returns song artist
     * @return song artist
     */
    public String getArtist() {
        return artist;
    }
    /**
     * Sets song artist
     * @param artist song artist
     */
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
