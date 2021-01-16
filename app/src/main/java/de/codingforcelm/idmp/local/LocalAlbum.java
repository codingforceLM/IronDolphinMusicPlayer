package de.codingforcelm.idmp.local;

/**
 * Representation object for a local stored album
 */
public class LocalAlbum {

    private long id;
    private String title;
    private String artist;

    /**
     * Default constructor
     * @param id id
     * @param title title
     * @param artist artist
     */
    public LocalAlbum(long id, String title, String artist) {
        this.setId(id);
        this.setTitle(title);
        this.setArtist(artist);
    }

    /**
     * Returns album id
     * @return album id
     */
    public long getId() {
        return id;
    }

    /**
     * Sets album id
     * @param id album id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns album title
     * @return album title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets album title
     * @param title album title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns album artist
     * @return album artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Sets album artist
     * @param artist album title
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LocalSong)) {
            return false;
        }
        LocalAlbum that = (LocalAlbum) o;
        return this.getId() == that.getId();
    }
}
