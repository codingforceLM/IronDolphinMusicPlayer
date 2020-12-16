package de.codingforcelm.idmp.player.service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.List;

import de.codingforcelm.idmp.PhysicalSong;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{

    private MediaPlayer player;
    private List<PhysicalSong> songList;
    private int songPosition;
    private final IBinder binder = new MusicBinder();
    private int position;

    @Override
    public void onCreate() {
        super.onCreate();
        songPosition = 0;
        player = new MediaPlayer();
        initMusicPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public void initMusicPlayer() {
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);
    }

    public void setSongList(List<PhysicalSong> songList) {
        this.songList = songList;
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void pauseSong() {
        position = player.getCurrentPosition();
        player.stop();
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong() {
        player.reset();
        position = 0;
        PhysicalSong song = songList.get(songPosition);
        long curr = song.getId();
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, curr);

        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (IOException e) {
            Log.e("MusicService", "Failed to set DataSource");
        }
        player.prepareAsync();
    }

    public void setSong(int pos) {
        this.songPosition = pos;
    }
}
