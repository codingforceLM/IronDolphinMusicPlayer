package de.codingforcelm.idmp.loader;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

import de.codingforcelm.idmp.local.LocalAlbum;
import de.codingforcelm.idmp.local.LocalSong;

/**
 * Utility class to load data associated with local audio files
 */
public class AudioLoader {
    private final Context context;
    private String LOG_TAG = "AudioLoader";

    /**
     * Default constructor
     * @param context context
     */
    public AudioLoader(Context context) {
        this.context = context;
    }

    /**
     * Get all local songs from the MediaStore
     * @return  ArrayList of all songs
     */
    public ArrayList<LocalSong> getSongs() {
        Log.e(LOG_TAG, "--getSongs--");
        ArrayList<LocalSong> songs = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        Log.e(LOG_TAG, "Retrieve data for found files");
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                songs.add(new LocalSong(id, data, title, album, artist));
            }
        }
        cursor.close();

        return songs;
    }

    /**
     * Get a LocalSong from a given mediaId
     * @param songId song mediaId
     * @return song
     */
    public LocalSong getSong(long songId) {
        Log.e(LOG_TAG, "--getSong-- id: "+songId);
        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 and " + MediaStore.Audio.Media._ID + " = " + songId;
        Cursor cursor = contentResolver.query(uri, null, selection, null, null);

        LocalSong song = null;
        Log.e(LOG_TAG, "Retrieve data for found files");
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                song = new LocalSong(id, data, title, album, artist);
            }
        }
        cursor.close();

        return song;
    }

    /**
     * Get all local albums from the MediaStore
     * @return ArrayList of all albums
     */
    public ArrayList<LocalAlbum> getAlbums() {
        Log.e(LOG_TAG, "--getAlbums--");
        ArrayList<LocalAlbum> songs = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Audio.Albums.ALBUM + " ASC";
        Cursor cursor = contentResolver.query(uri, null, null, null, sortOrder);

        Log.e(LOG_TAG, "Retrieve data for found files");
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
                Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));

                songs.add(new LocalAlbum(id, album, artist));
            }
        }
        cursor.close();

        return songs;
    }

    /**
     * Get all song from a specific album
     * @param albumId album mediaId
     * @return ArrayList of all album-songs
     */
    public ArrayList<LocalSong> getSongsFromAlbum(long albumId) {
        Log.e(LOG_TAG, "--getSongsFromAlbum-- id: "+albumId);
        ArrayList<LocalSong> songs = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0" + " and " + MediaStore.Audio.Media.ALBUM_ID + " = " + albumId;
        String sortOrder = MediaStore.Audio.Media.TRACK + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        Log.e(LOG_TAG, "Retrieve data for found files");
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                songs.add(new LocalSong(id, data, title, album, artist));
            }
        }
        cursor.close();

        return songs;
    }

    /**
     * Get the album cover for a song
     * @param mediaId song mediaId
     * @return cover as bitmap
     */
    public Bitmap getAlbumCoverForSong(long mediaId) {
        Log.e(LOG_TAG, "--getAlbumCoverForSong-- id: "+mediaId);
        Uri trackUri = null;
        try {
            trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaId);
        } catch (NumberFormatException nfe) {
            Log.e(LOG_TAG, "Couldnt parse MediaId");
            throw new IllegalStateException("Couldnt parse MediaId");
        }

        Log.e(LOG_TAG, "Retrieve bytes of embedded art");
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        BitmapFactory.Options options = new BitmapFactory.Options();
        retriever.setDataSource(context, trackUri);
        byte[] bytes = retriever.getEmbeddedPicture();

        if(bytes == null) {
            Log.e(LOG_TAG, "no embedded art found");
            return null;
        }
        Log.e(LOG_TAG, "Return embedded art");
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }
}
