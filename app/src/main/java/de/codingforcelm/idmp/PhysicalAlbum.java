package de.codingforcelm.idmp;

public class PhysicalAlbum {

    private long id;
    private String title;
    private String artist;

    public PhysicalAlbum(long id, String title, String artist) {
        this.setId(id);
        this.setTitle(title);
        this.setArtist(artist);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(!(o instanceof PhysicalSong)) {
            return false;
        }
        PhysicalAlbum that = (PhysicalAlbum) o;
        return this.getId() == that.getId();
    }
}
