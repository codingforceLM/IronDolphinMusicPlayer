package de.codingforcelm.idmp.audio;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

import de.codingforcelm.idmp.LocaleAlbum;
import de.codingforcelm.idmp.LocaleSong;

public class AudioLoader {

    private final Context context;

    public AudioLoader(Context context) {
        this.context = context;
    }

    public ArrayList<LocaleSong> getSongs() {
        ArrayList<LocaleSong> songs = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                songs.add(new LocaleSong(id, data, title, album, artist));
            }
        }
        cursor.close();

        return songs;
    }

    public LocaleSong getSong(long songId) {
        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 and " + MediaStore.Audio.Media._ID + " = " + songId;
        Cursor cursor = contentResolver.query(uri, null, selection, null, null);

        LocaleSong song = null;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                song = new LocaleSong(id, data, title, album, artist);
            }
        }
        cursor.close();

        return song;
    }

    public ArrayList<LocaleAlbum> getAlbums() {
        ArrayList<LocaleAlbum> songs = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        //String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder = MediaStore.Audio.Albums.ALBUM + " ASC";
        Cursor cursor = contentResolver.query(uri, null, null, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
                Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));

                songs.add(new LocaleAlbum(id, album, artist, false));
            }
        }
        cursor.close();

        return songs;
    }

    public ArrayList<LocaleSong> getSongsFromAlbum(long albumId) {
        ArrayList<LocaleSong> songs = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0" + " and " + MediaStore.Audio.Media.ALBUM_ID + " = " + albumId;
        String sortOrder = MediaStore.Audio.Media.TRACK + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                songs.add(new LocaleSong(id, data, title, album, artist));
            }
        }
        cursor.close();

        return songs;
    }
}
