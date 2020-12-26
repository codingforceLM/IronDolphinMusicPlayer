package de.codingforcelm.idmp.audio;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.PhysicalSong;

public class AudioLoader {

    private List<PhysicalSong> songs;
    private Context context;

    public AudioLoader(Context context) {
        this.context = context;
    }

    public List<PhysicalSong> getSongs() {
        if(context == null) {
            return null;
        }
        if(songs != null) {
            return songs;
        }

        loadAudio();
        return songs;
    }

    private void loadAudio() {
        songs = new ArrayList<>();

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

                songs.add(new PhysicalSong(id, data, title, album, artist));
            }
        }
        cursor.close();
    }

    public void reloadAudio() {
        loadAudio();
    }
}
