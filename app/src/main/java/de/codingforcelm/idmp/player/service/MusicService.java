package de.codingforcelm.idmp.player.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.icu.text.Transliterator;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import java.io.IOException;
import java.io.PipedWriter;
import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.MainActivity;
import de.codingforcelm.idmp.PhysicalSong;
import de.codingforcelm.idmp.audio.AudioLoader;

public class MusicService extends MediaBrowserServiceCompat implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{

    private static final String MY_MEDIA_ROOT_ID = "My_Unique_Service";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";
    public static final String CHANNEL_ID = "idmp_player_notification";
    private static final int NOTIFICATION_ID = 666;
    private static final String LOG_TAG = "MusicService";

    public static final String ACTION_MUSIC_PLAY = "de.codingforcelm.idmp.player.service.MUSIC_PLAY";
    public static final String ACTION_MUSIC_NEXT = "de.codingforcelm.idmp.player.service.MUSIC_NEXT";
    public static final String ACTION_MUSIC_PREV = "de.codingforcelm.idmp.player.service.MUSIC_PREV";
    private static final String ACTION_CANCEL_NOTIFICATION = "de.codingforcelm.idmp.player.service.CANCEL_NOTIFICATION";

    private MediaPlayer player;
    private List<PhysicalSong> songList;
    private int songPosition;
    private final IBinder binder = new MusicBinder();
    private int position;
    private Notification notification;

    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;

    @Override
    public void onCreate() {
        Log.e(LOG_TAG, "Create MusicService");
        super.onCreate();
        songPosition = 0;
        player = new MediaPlayer();
        initMusicPlayer();

        mediaSession = new MediaSessionCompat(this, LOG_TAG);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        updateSession();
        mediaSession.setCallback(new MusicCallbackHandler());
        setSessionToken(mediaSession.getSessionToken());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if(action == null) {
            return START_NOT_STICKY;
        }

        switch (action) {
            case MusicService.ACTION_MUSIC_PLAY:
                if(this.isPlaying()) {
                    this.pauseSong(true);
                } else {
                    this.resumeSong();
                }
                break;
            case MusicService.ACTION_MUSIC_NEXT:
                this.nextSong();
                break;
            case MusicService.ACTION_MUSIC_PREV:
                this.prevSong();
                break;
            case MusicService.ACTION_CANCEL_NOTIFICATION:
                this.pauseSong(false);

        }

        MediaButtonReceiver.handleIntent(mediaSession, intent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()  {
        stopForeground(true);
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        AudioLoader al = new AudioLoader(this);
        List<PhysicalSong> l = al.getSongs();

        List<MediaBrowserCompat.MediaItem> items = new ArrayList<>();
        for(PhysicalSong s : l) {
            items.add(
                    new MediaBrowserCompat.MediaItem(
                            new MediaDescriptionCompat.Builder()
                                    .setMediaId(String.valueOf(s.getId()))
                                    .setTitle(s.getTitle())
                                    .setDescription(s.getArtist())
                                    .build(),
                            0
                    )
            );
        }

        result.sendResult(items);
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
        Log.e(LOG_TAG, "onPrepared");
        if(position != 0) {
            mp.seekTo(position);
        }
        mp.start();

        Log.e(LOG_TAG, "update session");
        updateSession();

        Log.e(LOG_TAG, "build new notification");
        notification = buildNotification(false);
        Log.e(LOG_TAG, "update notification");
        startForeground(NOTIFICATION_ID, notification);
    }

    private Notification buildNotification(boolean play) {
        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetdata = controller.getMetadata();
        MediaDescriptionCompat desc = mediaMetdata.getDescription();

        Intent cancelIntent = new Intent(this, MusicService.class);
        cancelIntent.setAction(MusicService.ACTION_CANCEL_NOTIFICATION);
        PendingIntent cancelPendingIntent = PendingIntent.getService(this, 0, cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(android.R.drawable.ic_media_play);
        builder.setContentTitle(mediaMetdata.getString("title"));
        builder.setContentText(mediaMetdata.getString("artist"));
        builder.setSubText(mediaMetdata.getString("album"));
        builder.setOngoing(false);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(controller.getSessionActivity());
        builder.setDeleteIntent(cancelPendingIntent);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setShowWhen(false);
        builder.setOnlyAlertOnce(true);

        builder.addAction(new NotificationCompat.Action(
                android.R.drawable.ic_media_previous,
                "Previous",
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
        ));

        if(!play) {
            builder.addAction(new NotificationCompat.Action(
                    android.R.drawable.ic_media_pause,
                    "Pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PAUSE)
            ));
        } else {
            builder.addAction(new NotificationCompat.Action(
                    android.R.drawable.ic_media_play,
                    "Play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY)
            ));
        }

        builder.addAction(new NotificationCompat.Action(
                android.R.drawable.ic_media_next,
                "Previous",
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
        ));

        builder.setStyle(
                new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSession.getSessionToken())
        );

        notification = builder.build();
        return notification;
    }

    public void initMusicPlayer() {
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);

        AudioLoader al = new AudioLoader(this);
        this.setSongList(al.getSongs());
    }

    public void setSongList(List<PhysicalSong> songList) {
        this.songList = songList;
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void pauseSong(boolean postNotification) {
        position = player.getCurrentPosition();
        player.stop();
        if(postNotification) {
            notification = buildNotification(true);
            startForeground(NOTIFICATION_ID, notification);
        }
        stopForeground(false);
        updateSession();
    }

    public void resumeSong() {
        player.prepareAsync();
    }

    public void nextSong() {
        int nextpos = songPosition + 1;
        if(nextpos >= songList.size()) {
            nextpos = 0;
        }
        this.playSong(nextpos);
    }

    public void prevSong() {
        int nextpos = songPosition - 1;
        if(nextpos < 0) {
            nextpos = songList.size() - 1;
        }
        this.playSong(nextpos);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong(int pos) {
        this.setSong(pos);
        PhysicalSong song = songList.get(songPosition);
        long curr = song.getId();
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, curr);

        Log.e(LOG_TAG, "Play song");
        this.playSong(trackUri);
    }

    public void playSong(Uri trackUri) {
        player.reset();
        position = 0;

        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (IOException e) {
            Log.e("MusicService", "Failed to set DataSource");
        }
        player.prepareAsync();
    }

    public void seekTo(long pos) {
        position = (int)pos;
        player.prepareAsync();
    }

    public void setSong(int pos) {
        this.songPosition = pos;
    }

    private void updateSession() {
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();

        stateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_STOP
        );

        if(player.isPlaying()) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, player.getCurrentPosition(), 1.0f);
        } else {
            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, player.getCurrentPosition(), 1.0f);
        }

        PhysicalSong song = songList.get(songPosition);

        MediaMetadataCompat.Builder dataBuilder = new MediaMetadataCompat.Builder();
        dataBuilder.putString("artist", song.getArtist());
        dataBuilder.putString("album", song.getAlbum());
        dataBuilder.putString("title", song.getTitle());

        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setMetadata(dataBuilder.build());
    }

    private class MusicCallbackHandler extends MediaSessionCompat.Callback {
        @Override
        public void onPause() {
            MusicService.this.pauseSong(true);
        }

        @Override
        public void onPlay() {
            Intent intent = new Intent(MusicService.this, MusicService.class);
            startService(intent);
            MusicService.this.resumeSong();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            Uri trackUri = null;
            try {
                trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(mediaId));
            } catch(NumberFormatException nfe) {
                Log.e(LOG_TAG, "Couldnt parse MediaId");
            }

            if(extras.containsKey("position")) {
                songPosition = extras.getInt("position");
            } else {
                throw new IllegalStateException("Missing songlist position");
            }

            MusicService.this.playSong(trackUri);
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            Log.e(LOG_TAG, "onPlayFromUri");
            MusicService.this.playSong(uri);
        }

        @Override
        public void onSeekTo(long pos) {
            Log.e(LOG_TAG, "onSeekTo");
            MusicService.this.seekTo(pos);
        }

        @Override
        public void onSkipToNext() {
            Log.e(LOG_TAG, "onSkipToNext");
            MusicService.this.nextSong();
        }

        @Override
        public void onSkipToPrevious() {
            Log.e(LOG_TAG, "OnSkipToPrevious");
            MusicService.this.prevSong();
        }

        @Override
        public void onStop() {
            MusicService.this.pauseSong(true);
            Log.e(LOG_TAG, "onStop");
            stopSelf();
        }
    }
}
