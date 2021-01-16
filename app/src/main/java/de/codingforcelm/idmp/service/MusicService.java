package de.codingforcelm.idmp.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import de.codingforcelm.idmp.activity.MainActivity;
import de.codingforcelm.idmp.database.entity.PlaylistEntry;
import de.codingforcelm.idmp.database.repository.PlaylistRepository;
import de.codingforcelm.idmp.loader.AudioLoader;
import de.codingforcelm.idmp.local.LocalSong;
import de.codingforcelm.idmp.queue.SongQueue;

/**
 * A child class of MediaBrowserServiceCompat responsible for audio playback
 */
public class MusicService extends MediaBrowserServiceCompat implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    /**
     * ID of the notification channel used by the service
     */
    public static final String CHANNEL_ID = "idmp_player_notification";

    /**
     * Command to get the current position of the playback. To be used with {@link android.support.v4.media.session.MediaSessionCompat.Callback#onCommand(String, Bundle, ResultReceiver) onCommand} method
     */
    public static final String COMMAND_GET_POSITION = "de.codingforcelm.idmp.player.service.GET_POSITION";

    /**
     * Command to set shuffle state. To be used with {@link android.support.v4.media.session.MediaSessionCompat.Callback#onCommand(String, Bundle, ResultReceiver) onCommand} method
     */
    public static final String COMMAND_SET_SHUFFLE = "de.codingforcelm.idmp.player.service.SET_SHUFFLE";

    /**
     * Command to set repeat state. To be used with {@link android.support.v4.media.session.MediaSessionCompat.Callback#onCommand(String, Bundle, ResultReceiver) onCommand} method
     */
    public static final String COMMAND_SET_REPEAT = "de.codingforcelm.idmp.player.service.SET_REPEAT";

    /**
     * Command to explicitly update the metadata. To be used with {@link android.support.v4.media.session.MediaSessionCompat.Callback#onCommand(String, Bundle, ResultReceiver) onCommand} method
     */
    public static final String COMMAND_UPDATE_METADATA = "de.codingforcelm.idmp.player.service.UPDATE_METADATA";

    /**
     * Command to explicitly load the songlist. To be used with {@link android.support.v4.media.session.MediaSessionCompat.Callback#onCommand(String, Bundle, ResultReceiver) onCommand} method
     */
    public static final String COMMAND_LOAD_SONGLIST = "de.codingforcelm.idmp.player.service.LOAD_SONGLIST";

    /**
     * Command to explicitly load a specific album. To be used with {@link android.support.v4.media.session.MediaSessionCompat.Callback#onCommand(String, Bundle, ResultReceiver) onCommand} method
     */
    public static final String COMMAND_LOAD_ALBUM = "de.codingforcelm.idmp.player.service.LOAD_ALBUM";

    /**
     * Command to explicitly reload a playlist. To be used with {@link android.support.v4.media.session.MediaSessionCompat.Callback#onCommand(String, Bundle, ResultReceiver) onCommand} method
     */
    public static final String COMMAND_RELOAD_PLAYLIST = "de.codingforcelm.idmp.player.service.RELOAD_PLAYLIST";

    /**
     * Command to put a song into the queue. To be used with {@link android.support.v4.media.session.MediaSessionCompat.Callback#onCommand(String, Bundle, ResultReceiver) onCommand} method
     */
    public static final String COMMAND_ENQUEUE = "de.codingforcelm.idmp.player.service.ENQUEUE";

    /**
     * Key for {@link android.os.Bundle} to identify an artist.
     */
    public static final String KEY_ARTIST = "de.codingforcelm.idmp.player.service.ARTIST";

    /**
     * Key for {@link android.os.Bundle} to identify an album
     */
    public static final String KEY_ALBUM = "de.codingforcelm.idmp.player.service.ALBUM";
    /**
     * Key for {@link android.os.Bundle} to identify a song title
     */
    public static final String KEY_TITLE = "de.codingforcelm.idmp.player.service.TITLE";

    /**
     * Key for {@link android.os.Bundle} to identify a song duration
     */
    public static final String KEY_DURATION = "de.codingforcelm.idmp.player.service.DURATION";

    /**
     * Key for {@link android.os.Bundle} to identify a playback position
     */
    public static final String KEY_POSITION = "de.codingforcelm.idmp.player.service.POSITION";

    /**
     * Key for {@link android.os.Bundle} to identify a shuffle state
     */
    public static final String KEY_SHUFFLE = "de.codingforcelm.idmp.player.service.SHUFFLE";

    /**
     * Key for {@link android.os.Bundle} to identify a repeat state
     */
    public static final String KEY_REPEAT = "de.codingforcelm.idmp.player.service.REPEAT";

    /**
     * Key for {@link android.os.Bundle} to identify a playback context
     */
    public static final String KEY_CONTEXT = "de.codingforcelm.idmp.player.service.CONTEXT";

    /**
     * Key for {@link android.os.Bundle} to identify a playback context type
     */
    public static final String KEY_CONTEXT_TYPE = "de.codingforcelm.idmp.player.service.CONTEXT_TYPE";

    /**
     * Key for {@link android.os.Bundle} to identify an album id
     */
    public static final String KEY_ALBUM_ID = "de.codingforcelm.idmp.player.service.ALBUM_ID";

    /**
     * Key for {@link android.os.Bundle} to identify a playlist id
     */
    public static final String KEY_PLAYLIST_ID = "de.codingforcelm.idmp.player.service.PLAYLIST_ID";

    /**
     * Key for {@link android.os.Bundle} to identify a media id
     */
    public static final String KEY_MEDIA_ID = "de.codingforcelm.idmp.player.service.MEDIA_ID";

    /**
     * Context type to be used for playback from the songlist
     */
    public static final String CONTEXT_TYPE_SONGLIST = "de.codingforcelm.idmp.player.service.TYPE_SONGLIST";

    /**
     * Context type to be used for playback from an album
     */
    public static final String CONTEXT_TYPE_ALBUM = "de.codingforcelm.idmp.player.service.TYPE_ALBUM";

    /**
     * Context type to be used for playback from a playlist
     */
    public static final String CONTEXT_TYPE_PLAYLIST = "de.codingforcelm.idmp.player.service.TYPE_PLAYLIST";

    /**
     * Context prefix to be used in conjunction with an album id to create a unique context for an album
     */
    public static final String CONTEXT_PREFIX_ALBUM = "de.codingforcelm.idmp.player.service.PREFIX_ALBUM_";

    /**
     * Context prefix to be used in conjunction with a playlist id to creae a unique context for a playlist
     */
    public static final String CONTEXT_PREFIX_PLAYLIST = "de.codingforcelm.idmp.player.service.PREFIX_PLAYLIST_";

    /**
     * Custom action to identify play action
     */
    public static final String ACTION_MUSIC_PLAY = "de.codingforcelm.idmp.player.service.MUSIC_PLAY";

    /**
     * Custom action to identify next song action
     */
    public static final String ACTION_MUSIC_NEXT = "de.codingforcelm.idmp.player.service.MUSIC_NEXT";

    /**
     * Custom action to identify previous song action
     */
    public static final String ACTION_MUSIC_PREV = "de.codingforcelm.idmp.player.service.MUSIC_PREV";

    private static final String MY_MEDIA_ROOT_ID = "My_Unique_Service";
    private static final int NOTIFICATION_ID = 666;
    private static final String LOG_TAG = "MusicService";
    private static final String ACTION_CANCEL_NOTIFICATION = "de.codingforcelm.idmp.player.service.CANCEL_NOTIFICATION";
    private MediaPlayer player;
    private List<LocalSong> songList;
    private int songPosition;
    private int position;
    private boolean paused;
    private boolean shuffle;
    private boolean repeat;
    private Notification notification;
    private String context;
    private MediaSessionCompat mediaSession;
    private AudioManager audioManager;
    private AudioFocusChangeListener afcl;
    private AudioLoader audioLoader;
    private IntentFilter intentFilter;
    private BecomingNoisyReceiver noisyReceiver;
    private SongQueue queue;
    private long currMediaId;
    private boolean resumeAfterGain;
    private AtomicBoolean playNewSong;

    @Override
    public void onCreate() {
        Log.e(LOG_TAG, "Create MusicService");
        super.onCreate();
        songPosition = 0;
        player = new MediaPlayer();
        queue = SongQueue.getInstance();
        initMusicPlayer();

        playNewSong = new AtomicBoolean(false);
        resumeAfterGain = false;
        paused = true;
        context = MainActivity.CONTEXT_SONGLIST;

        mediaSession = new MediaSessionCompat(this, LOG_TAG);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        afcl = new AudioFocusChangeListener();
        intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        noisyReceiver = new BecomingNoisyReceiver();
        registerReceiver(noisyReceiver, intentFilter);

        updateSession();
        mediaSession.setCallback(new MusicCallbackHandler());
        setSessionToken(mediaSession.getSessionToken());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (action == null) {
            return START_NOT_STICKY;
        }

        switch (action) {
            case MusicService.ACTION_MUSIC_PLAY:
                if (this.isPlaying()) {
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
    public void onDestroy() {
        unregisterReceiver(noisyReceiver);
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
        List<LocalSong> l = al.getSongs();

        List<MediaBrowserCompat.MediaItem> items = new ArrayList<>();
        for (LocalSong s : l) {
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
        if (!paused) {
            this.pauseSong(true);
            if (repeat) {
                Log.e(LOG_TAG, "repeat song");
                this.playSong(songPosition, true);
            } else {
                Log.e(LOG_TAG, "dont repeat song");
                this.nextSong();
            }
        } else {
            Log.e(LOG_TAG, "paused");
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e(LOG_TAG, "onPrepared");

        if (!requestAudioFocus()) {
            Log.e(LOG_TAG, "Audio Focus not granted - return");
            return;
        }

        mp.seekTo(position);
        mp.start();
        paused = false;

        Log.e(LOG_TAG, "update session");
        updateSession();

        Log.e(LOG_TAG, "build new notification");
        notification = buildNotification(false);
        Log.e(LOG_TAG, "update notification");
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.e(LOG_TAG, "onSeekComplete");

        Log.e(LOG_TAG, "start playback");
        mp.start();
        paused = false;

        Log.e(LOG_TAG, "update session");
        updateSession();

        Log.e(LOG_TAG, "build new notification");
        notification = buildNotification(false);
        Log.e(LOG_TAG, "update notification");
        startForeground(NOTIFICATION_ID, notification);
    }

    /**
     * Builds notification
     * @param play playstatus
     * @return notification
     */
    private Notification buildNotification(boolean play) {
        Log.e(LOG_TAG, "buildNotification");
        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetdata = controller.getMetadata();
        MediaDescriptionCompat desc = mediaMetdata.getDescription();

        Log.e(LOG_TAG, "create Intent for notification cancel");
        Intent cancelIntent = new Intent(this, MusicService.class);
        cancelIntent.setAction(MusicService.ACTION_CANCEL_NOTIFICATION);
        PendingIntent cancelPendingIntent = PendingIntent.getService(this, 0, cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Log.e(LOG_TAG, "build the notification");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(android.R.drawable.ic_media_play);
        builder.setContentTitle(mediaMetdata.getString(KEY_TITLE));
        builder.setContentText(mediaMetdata.getString(KEY_ARTIST));
        builder.setSubText(mediaMetdata.getString(KEY_ALBUM));
        builder.setOngoing(false);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(controller.getSessionActivity());
        builder.setDeleteIntent(cancelPendingIntent);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setShowWhen(false);
        builder.setOnlyAlertOnce(true);

        Log.e(LOG_TAG, "add intents for media buttons");
        builder.addAction(new NotificationCompat.Action(
                android.R.drawable.ic_media_previous,
                "Previous",
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
        ));

        if (!play) {
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

        Log.e(LOG_TAG, "set MediaStyle style");
        builder.setStyle(
                new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSession.getSessionToken())
        );

        Log.e(LOG_TAG, "actually build notification");
        notification = builder.build();
        return notification;
    }

    /**
     * Initializes player
     */
    public void initMusicPlayer() {
        Log.e(LOG_TAG, "initMusicPlayer");
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);
        player.setOnSeekCompleteListener(this);

        Log.e(LOG_TAG, "Get initial song list");
        audioLoader = new AudioLoader(this);
        this.setSongList(audioLoader.getSongs());

        currMediaId = songList.get(songPosition).getId();

        Log.e(LOG_TAG, "prepare player for first playback");
        player.reset();
        long curr = songList.get(songPosition).getId();
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, curr);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (IOException e) {
            Log.e(LOG_TAG, "error initializing player " + e.getLocalizedMessage());
        }
    }

    /**
     * Sets the song list
     * @param songList songList
     */
    public void setSongList(List<LocalSong> songList) {
        this.songList = songList;
    }

    /**
     * Returns true if player is playing
     * @return isPlaying
     */
    public boolean isPlaying() {
        return player.isPlaying();
    }

    /**
     * Pauses the song
     * @param postNotification postNotification
     */
    public void pauseSong(boolean postNotification) {
        Log.e(LOG_TAG, "pauseSong");
        position = player.getCurrentPosition();
        Log.e(LOG_TAG, "stop playback");
        player.stop();
        paused = true;

        Log.e(LOG_TAG, "update notification and session");
        if (postNotification) {
            notification = buildNotification(true);
            startForeground(NOTIFICATION_ID, notification);
        }
        stopForeground(false);
        updateSession();
    }

    /**
     * Resumes the song
     */
    public void resumeSong() {
        Log.e(LOG_TAG, "resumeSong");
        if (!player.isPlaying()) {
            Log.e(LOG_TAG, "restart playback");
            player.prepareAsync();
        }
    }

    /**
     * Plays next song in list
     */
    public void nextSong() {
        Log.e(LOG_TAG, "nextSong");
        int nextpos = -1;
        if (!queue.isEmpty()) {
            Log.e(LOG_TAG, "select song from queue");
            String next = queue.dequeue();
            Uri trackUri = null;
            try {
                trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.valueOf(next));
            } catch (NumberFormatException nfe) {
                Log.e(LOG_TAG, "Couldnt parse MediaId");
                throw new IllegalStateException("Couldnt parse MediaId");
            }
            currMediaId = Long.valueOf(next);
            Log.e(LOG_TAG, "play queued song");
            playSong(trackUri, true);
            return;
        }
        if (shuffle) {
            Log.e(LOG_TAG, "shuffle next song");
            nextpos = new Random().nextInt(songList.size());
        } else {
            Log.e(LOG_TAG, "dont shuffle next song");
            nextpos = ++songPosition;
            if (nextpos >= songList.size()) {
                Log.e(LOG_TAG, "next song index 0");
                nextpos = 0;
            }
        }
        if (nextpos >= 0 && nextpos < songList.size()) {
            currMediaId = songList.get(nextpos).getId();
            Log.e(LOG_TAG, "play next song");
            this.playSong(nextpos, true);
        }
    }

    /**
     * Plays previous song in list
     */
    public void prevSong() {
        Log.e(LOG_TAG, "prevSong");
        int nextpos = songPosition - 1;
        if (nextpos < 0) {
            nextpos = songList.size() - 1;
        }
        currMediaId = songList.get(nextpos).getId();
        this.playSong(nextpos, false);
    }

    /**
     * Plays a song
     */
    public void playSong() {
        Log.e(LOG_TAG,"play song from last saved position");
        this.playSong(songPosition, false);
    }

    /**
     * Play the song siting on the given position
     * @param pos index where song is located in arraylist
     * @param fromStart if playback shall start from the beginning of the spng
     */
    public void playSong(int pos, boolean fromStart) {
        Log.e(LOG_TAG, "play song from position "+pos);
        this.setSong(pos);
        LocalSong song = songList.get(songPosition);
        long curr = song.getId();
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, curr);

        Log.e(LOG_TAG, "Play song");
        this.playSong(trackUri, fromStart);
    }

    public void playSong(Uri trackUri, boolean fromStart) {
        Log.e(LOG_TAG, "playSong");
        player.reset();
        if (fromStart) {
            position = 0;
        }

        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (IOException e) {
            Log.e("MusicService", "Failed to set DataSource");
        }
        Log.e(LOG_TAG, "prepare player for playback");
        player.prepareAsync();
    }

    /**
     * Seeks current song to position
     * @param pos position
     */
    public void seekTo(long pos) {
        position = (int) pos;
        Log.e(LOG_TAG, "Seek to " + pos);
        player.seekTo(position);
    }

    /**
     * Set position of the current song in the songlist
     * @param pos index of song in list
     */
    public void setSong(int pos) {
        this.songPosition = pos;
    }

    /**
     * Toggles the shuffle mode
     */
    public void toggleShuffle() {
        Log.e(LOG_TAG, "toggleShuffle");
        this.shuffle = !shuffle;
        Log.e(LOG_TAG, "shuffle mode now " + shuffle);
        updateSession();
    }

    /**
     * Toggles the repeat mode
     */
    public void toggleRepeat() {
        Log.e(LOG_TAG, "toggleRepeat");
        this.repeat = !repeat;
        Log.e(LOG_TAG, "repeat mode now " + repeat);
        updateSession();
    }

    private void updateSession() {
        Log.e(LOG_TAG, "updateSession");
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();

        stateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_STOP
        );

        Log.e(LOG_TAG, "set playback state");
        if (player.isPlaying()) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, player.getCurrentPosition(), 1.0f);
        } else {
            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, player.getCurrentPosition(), 1.0f);
        }

        LocalSong song = audioLoader.getSong(currMediaId);

        MediaMetadataCompat.Builder dataBuilder = new MediaMetadataCompat.Builder();

        Log.e(LOG_TAG, "get current metadata");
        String artist = song.getArtist();
        String album = song.getAlbum();
        String title = song.getTitle();
        String duration = String.valueOf(player.getDuration());
        String shuffleStr = String.valueOf(shuffle);
        String repeatStr = String.valueOf(repeat);
        String mid = String.valueOf(currMediaId);

        Log.e(LOG_TAG, "put metadata in container");
        dataBuilder.putString(KEY_ARTIST, artist);
        dataBuilder.putString(KEY_ALBUM, album);
        dataBuilder.putString(KEY_TITLE, title);
        dataBuilder.putString(KEY_DURATION, duration);
        dataBuilder.putString(KEY_SHUFFLE, shuffleStr);
        dataBuilder.putString(KEY_REPEAT, repeatStr);
        dataBuilder.putString(KEY_MEDIA_ID, mid);

        Log.e(LOG_TAG, "update playback state and metadata");
        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setMetadata(dataBuilder.build());
    }

    private boolean requestAudioFocus() {
        Log.e(LOG_TAG, "requestAudioFocus");
        int res = -1;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            AudioFocusRequest.Builder builder = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN);
            builder.setFocusGain(AudioManager.AUDIOFOCUS_GAIN);
            builder.setWillPauseWhenDucked(false);
            builder.setAcceptsDelayedFocusGain(false);
            builder.setOnAudioFocusChangeListener(afcl);

            res = audioManager.requestAudioFocus(builder.build());

        } else {
            res = audioManager.requestAudioFocus(afcl, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }

        Log.e(LOG_TAG, "return whether audio focus is granted");
        return res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void abondonAudioFocus() {
        Log.e(LOG_TAG, "release audio focus");
        if (!(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)) {
            audioManager.abandonAudioFocus(afcl);
        }
    }

    private void setSongPositionFromMediaId(long mediaId) {
        Log.e(LOG_TAG, "setSongPositionFromMediaId");
        Log.e(LOG_TAG, "get index for mediaId");
        int index = getIndexForMediaId(mediaId);
        if (index > -1) {
            Log.e(LOG_TAG, "update songPosition");
            songPosition = index;
            currMediaId = mediaId;
        } else {
            Log.e(LOG_TAG, "song wasnt found in given songList");
            throw new IllegalStateException("song not loaded");
        }
    }

    private int getIndexForMediaId(long mediaId) {
        Log.e(LOG_TAG, "getIndexForMediaId");
        Log.e(LOG_TAG, "building dummy");
        LocalSong dummy = new LocalSong(mediaId, null, null, null, null);
        Log.e(LOG_TAG, "finding index");
        return songList.indexOf(dummy);
    }

    private class MusicCallbackHandler extends MediaSessionCompat.Callback {

        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            switch (command) {
                case COMMAND_GET_POSITION:
                    Log.e(LOG_TAG, "received COMMAND_GET_POSITION");
                    Bundle b = new Bundle();
                    b.putInt(KEY_POSITION, player.getCurrentPosition());
                    cb.send(0, b);
                    break;
                case COMMAND_SET_REPEAT:
                    Log.e(LOG_TAG, "received COMMAND_SET_REPEAT");
                    toggleRepeat();
                    break;
                case COMMAND_SET_SHUFFLE:
                    Log.e(LOG_TAG, "received COMMAND_SET_SHUFFLE");
                    toggleShuffle();
                    break;
                case COMMAND_UPDATE_METADATA:
                    Log.e(LOG_TAG, "received COMMAND_SET_SHUFFLE");
                    updateSession();
                    break;
                case COMMAND_LOAD_SONGLIST:
                    Log.e(LOG_TAG, "received COMMAND_LOAD_SONGLIST");
                    if (!context.equals(MainActivity.CONTEXT_SONGLIST)) {
                        songList = new AudioLoader(getApplicationContext()).getSongs();
                    }
                    break;
                case COMMAND_LOAD_ALBUM:
                    Log.e(LOG_TAG, "received COMMAND_LOAD_ALBUM");
                    if (!extras.containsKey(KEY_ALBUM_ID)) {
                        throw new IllegalStateException("missing album id");
                    }
                    long id = extras.getLong(KEY_ALBUM_ID);
                    if (!context.equals(String.valueOf(id))) {
                        songList = new AudioLoader(getApplicationContext()).getSongsFromAlbum(id);
                    }
                    break;
                case COMMAND_RELOAD_PLAYLIST:
                    Log.e(LOG_TAG, "received COMMAND_RELOAD_ALBUM");
                    if (!extras.containsKey(KEY_CONTEXT)) {
                        throw new IllegalStateException("missing context");
                    }
                    String reloadContext = extras.getString(KEY_CONTEXT);
                    Log.e(LOG_TAG, "check if new context is different from the current");
                    if (context.equals(reloadContext)) {
                        if (!extras.containsKey(KEY_PLAYLIST_ID)) {
                            throw new IllegalStateException("missing playlist id");
                        }
                        PlaylistRepository repo = PlaylistRepository.getInstance(getApplication());
                        String listId = extras.getString(KEY_PLAYLIST_ID);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                repo.getPlaylist(listId).observeForever(playlistWithEntries -> {
                                    Log.e(LOG_TAG, "reload playlist");
                                    LocalSong prev = songList.get(songPosition);
                                    List<LocalSong> songs = new ArrayList<>();
                                    for (PlaylistEntry entry : playlistWithEntries.getEntries()) {
                                        LocalSong s = audioLoader.getSong(entry.getMediaId());
                                        if (s != null) {
                                            songs.add(s);
                                        }
                                    }
                                    songList = songs;
                                    notifyReload(prev);
                                });
                            }
                        });
                    }
                    break;
                case COMMAND_ENQUEUE:
                    Log.e(LOG_TAG, "received COMMAND_ENQUEUE");
                    if (!extras.containsKey(KEY_MEDIA_ID)) {
                        throw new IllegalStateException("missing mediaid");
                    }
                    String mediaId = extras.getString(KEY_MEDIA_ID);
                    queue.enqueue(mediaId);
                    break;
            }
        }

        @Override
        public void onPause() {
            Log.e(LOG_TAG, "pause playback");
            MusicService.this.pauseSong(true);
        }

        @Override
        public void onPlay() {
            Log.e(LOG_TAG, "play playback");
            Intent intent = new Intent(MusicService.this, MusicService.class);
            Log.e(LOG_TAG, "make sure the service is started");
            startService(intent);
            MusicService.this.playSong();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            if (!extras.containsKey(KEY_CONTEXT)) {
                throw new IllegalStateException("missing context");
            }
            if (!extras.containsKey(KEY_CONTEXT_TYPE)) {
                throw new IllegalStateException("missing context type");
            }

            Uri trackUri = null;
            try {
                trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(mediaId));
            } catch (NumberFormatException nfe) {
                Log.e(LOG_TAG, "Couldnt parse MediaId");
                throw new IllegalStateException("Couldnt parse MediaId");
            }

            String newContext = extras.getString(KEY_CONTEXT);
            String contextType = extras.getString(KEY_CONTEXT_TYPE);
            Log.e(LOG_TAG, "check if new context is different from the current");
            if (!context.equals(newContext)) {
                switch (contextType) {
                    case CONTEXT_TYPE_SONGLIST:
                        Log.e(LOG_TAG, "new context songlist");
                        songList = audioLoader.getSongs();
                        playNewSong.set(true);
                        notifyLoadedAndPlay(mediaId, trackUri, playNewSong.getAndSet(false));
                        break;
                    case CONTEXT_TYPE_ALBUM:
                        Log.e(LOG_TAG, "new context album");
                        if (!extras.containsKey(KEY_ALBUM_ID)) {
                            throw new IllegalStateException("missing album id");
                        }
                        songList = audioLoader.getSongsFromAlbum(extras.getLong(KEY_ALBUM_ID));
                        playNewSong.set(true);
                        notifyLoadedAndPlay(mediaId, trackUri, playNewSong.getAndSet(false));
                        break;
                    case CONTEXT_TYPE_PLAYLIST:
                        Log.e(LOG_TAG, "new context playlist");
                        if (!extras.containsKey(KEY_PLAYLIST_ID)) {
                            throw new IllegalStateException("missing album id");
                        }
                        String listId = extras.getString(KEY_PLAYLIST_ID);
                        PlaylistRepository repo = PlaylistRepository.getInstance(getApplication());
                        Uri finalTrackUri = trackUri;

                        Log.e(LOG_TAG, "set play status to true so playlist load start playback after loading");
                        playNewSong.set(true);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                repo.getPlaylist(listId).observeForever(playlistWithEntries -> {
                                    if (context.startsWith(CONTEXT_PREFIX_PLAYLIST)) {
                                        Log.e(LOG_TAG, "Load playlist");
                                        List<LocalSong> songs = new ArrayList<>();
                                        for (PlaylistEntry entry : playlistWithEntries.getEntries()) {
                                            LocalSong s = audioLoader.getSong(entry.getMediaId());
                                            if (s != null) {
                                                songs.add(s);
                                            }
                                        }
                                        songList = songs;
                                        setSongPositionFromMediaId(Long.valueOf(mediaId));
                                        updateSession();
                                        notifyLoadedAndPlay(mediaId, finalTrackUri, playNewSong.getAndSet(false));
                                    }
                                });
                            }
                        });
                        break;
                }
                context = newContext;
            } else {
                notifyLoadedAndPlay(mediaId, trackUri, true);
            }
        }

        private void notifyLoadedAndPlay(String mediaId, Uri trackUri, boolean isPlaying) {
            Log.e(LOG_TAG, "update songPosition");
            setSongPositionFromMediaId(Long.valueOf(mediaId));
            if (isPlaying) {
                MusicService.this.playSong(trackUri, true);
            }
        }

        private void notifyReload(LocalSong prev) {
            long mediaId = prev.getId();
            Log.e(LOG_TAG, "update songPosition");
            setSongPositionFromMediaId(mediaId);
        }

        @Deprecated
        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            Log.e(LOG_TAG, "onPlayFromUri - this function is incompatible with the current service architecture");
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
            abondonAudioFocus();
            unregisterReceiver(noisyReceiver);
            stopSelf();
        }
    }

    private class AudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.e(LOG_TAG, "onAudioFocusChange");
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    Log.e(LOG_TAG, "Audio Focus loss transient");
                    MusicService.this.pauseSong(true);
                    resumeAfterGain = player.isPlaying();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    Log.e(LOG_TAG, "Audio Focus loss transient can duck");
                    player.setVolume(0.5f, 0.5f);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    Log.e(LOG_TAG, "Audio Focus loss");
                    mediaSession.getController().getTransportControls().stop();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    Log.e(LOG_TAG, "Audio Focus gain");
                    player.setVolume(1.0f, 1.0f);
                    if (resumeAfterGain) {
                        MusicService.this.resumeSong();
                    }
                    resumeAfterGain = false;
            }
        }
    }

    private class BecomingNoisyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                Log.e(LOG_TAG, "device became noisy - stop playback");
                mediaSession.getController().getTransportControls().pause();
            }
        }
    }

}
