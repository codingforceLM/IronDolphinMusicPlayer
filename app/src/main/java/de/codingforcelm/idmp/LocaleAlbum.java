package de.codingforcelm.idmp;

public class LocaleAlbum {

    private long id;
    private String title;
    private String artist;
    private boolean expanded;

    public LocaleAlbum(long id, String title, String artist, boolean expanded) {
        this.setId(id);
        this.setTitle(title);
        this.setArtist(artist);
        this.setExpanded(expanded);
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

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LocaleSong)) {
            return false;
        }
        LocaleAlbum that = (LocaleAlbum) o;
        return this.getId() == that.getId();
    }
}
