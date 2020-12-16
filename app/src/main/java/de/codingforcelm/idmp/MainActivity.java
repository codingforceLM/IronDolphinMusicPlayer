package de.codingforcelm.idmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.player.service.MusicService;

public class MainActivity extends AppCompatActivity {

    public static final String Broadcast_PLAY_NEW_AUDIO = "de.codingforcelm.idmp.PlayNewAudio";

    private List<PhysicalSong> songList;
    private ListView songView;
    private boolean bound;

    private MusicService service;
    private Intent playIntent;

    // TODO add controls to layout
    private ImageView playPauseButton;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", bound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        bound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songView = (ListView)findViewById(R.id.songlist);
        bound = false;

        playPauseButton = findViewById(R.id.playPauseButton);
        playPauseButton.setOnClickListener(new PlayPauseOnClickListener());
        loadAudio();
        CardsAdapter adapter = new CardsAdapter(this, songList);
        songView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        playIntent = new Intent(this, MusicService.class);
        bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(playIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            MainActivity.this.service = binder.getService();
            MainActivity.this.service.setSongList(songList);
            bound = true;
            Toast.makeText(MainActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };


    private void loadAudio() {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        songList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                songList.add(new PhysicalSong(id, data, title, album, artist));
            }
        }
        cursor.close();
    }

    public void songSelect(View view) {
        int pos = Integer.parseInt(view.getTag().toString());
        service.playSong(pos);
        playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
    }

    private class PlayPauseOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(bound){
                if(service.isPlaying()){
                    service.pauseSong();
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                }else {
                    service.resumeSong();
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        }
    }
}