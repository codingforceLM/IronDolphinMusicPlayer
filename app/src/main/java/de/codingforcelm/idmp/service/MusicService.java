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
import android.os.Binder;
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

import de.codingforcelm.idmp.activity.MainActivity;
import de.codingforcelm.idmp.locale.LocaleSong;
import de.codingforcelm.idmp.loader.AudioLoader;
import de.codingforcelm.idmp.database.entity.PlaylistEntry;
import de.codingforcelm.idmp.database.repository.PlaylistRepository;
import de.codingforcelm.idmp.queue.SongQueue;

public class MusicService extends MediaBrowserServiceCompat implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    public static final String CHANNEL_ID = "idmp_player_notification";
    public static final String COMMAND_GET_POSITION = "de.codingforcelm.idmp.player.service.GET_POSITION";
    public static final String COMMAND_SET_SHUFFLE = "de.codingforcelm.idmp.player.service.SET_SHUFFLE";
    public static final String COMMAND_SET_REPEAT = "de.codingforcelm.idmp.player.service.SET_REPEAT";
    public static final String COMMAND_UPDATE_METADATA = "de.codingforcelm.idmp.player.service.UPDATE_METADATA";
    public static final String COMMAND_LOAD_SONGLIST = "de.codingforcelm.idmp.player.service.LOAD_SONGLIST";
    public static final String COMMAND_LOAD_ALBUM = "de.codingforcelm.idmp.player.service.LOAD_ALBUM";
    public static final String COMMAND_RELOAD_PLAYLIST = "de.codingforcelm.idmp.player.service.RELOAD_PLAYLIST";
    public static final String COMMAND_ENQUEUE = "de.codingforcelm.idmp.player.service.ENQUEUE";
    public static final String KEY_ARTIST = "de.codingforcelm.idmp.player.service.ARTIST";
    public static final String KEY_ALBUM = "de.codingforcelm.idmp.player.service.ALBUM";
    public static final String KEY_TITLE = "de.codingforcelm.idmp.player.service.TITLE";
    public static final String KEY_DURATION = "de.codingforcelm.idmp.player.service.DURATION";
    public static final String KEY_POSITION = "de.codingforcelm.idmp.player.service.POSITION";
    public static final String KEY_SHUFFLE = "de.codingforcelm.idmp.player.service.SHUFFLE";
    public static final String KEY_REPEAT = "de.codingforcelm.idmp.player.service.REPEAT";
    public static final String KEY_CONTEXT = "de.codingforcelm.idmp.player.service.CONTEXT";
    public static final String KEY_CONTEXT_TYPE = "de.codingforcelm.idmp.player.service.CONTEXT_TYPE";
    public static final String KEY_ALBUM_ID = "de.codingforcelm.idmp.player.service.ALBUM_ID";
    public static final String KEY_PLAYLIST_ID = "de.codingforcelm.idmp.player.service.PLAYLIST_ID";
    public static final String KEY_MEDIA_ID = "de.codingforcelm.idmp.player.service.MEDIA_ID";
    public static final String CONTEXT_TYPE_SONGLIST = "de.codingforcelm.idmp.player.service.TYPE_SONGLIST";
    public static final String CONTEXT_TYPE_ALBUM = "de.codingforcelm.idmp.player.service.TYPE_ALBUM";
    public static final String CONTEXT_TYPE_PLAYLIST = "de.codingforcelm.idmp.player.service.TYPE_PLAYLIST";
    public static final String CONTEXT_PREFIX_ALBUM = "de.codingforcelm.idmp.player.service.PREFIX_ALBUM_";
    public static final String CONTEXT_PREFIX_PLAYLIST = "de.codingforcelm.idmp.player.service.PREFIX_PLAYLIST_";
    public static final String ACTION_MUSIC_PLAY = "de.codingforcelm.idmp.player.service.MUSIC_PLAY";
    public static final String ACTION_MUSIC_NEXT = "de.codingforcelm.idmp.player.service.MUSIC_NEXT";
    public static final String ACTION_MUSIC_PREV = "de.codingforcelm.idmp.player.service.MUSIC_PREV";
    private static final String MY_MEDIA_ROOT_ID = "My_Unique_Service";
    private static final int NOTIFICATION_ID = 666;
    private static final String LOG_TAG = "MusicService";
    private static final String ACTION_CANCEL_NOTIFICATION = "de.codingforcelm.idmp.player.service.CANCEL_NOTIFICATION";
    private MediaPlayer player;
    private List<LocaleSong> songList;
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

    @Override
    public void onCreate() {
        Log.e(LOG_TAG, "Create MusicService");
        super.onCreate();
        songPosition = 0;
        player = new MediaPlayer();
        queue = SongQueue.getInstance();
        initMusicPlayer();

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
        List<LocaleSong> l = al.getSongs();

        List<MediaBrowserCompat.MediaItem> items = new ArrayList<>();
        for (LocaleSong s : l) {
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

        mp.start();
        paused = false;

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
        player.setOnSeekCompleteListener(this);

        audioLoader = new AudioLoader(this);
        this.setSongList(audioLoader.getSongs());

        currMediaId = songList.get(songPosition).getId();

        player.reset();
        long curr = songList.get(songPosition).getId();
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, curr);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (IOException e) {
            Log.e(LOG_TAG, "error initializing player " + e.getLocalizedMessage());
        }
    }

    public void setSongList(List<LocaleSong> songList) {
        this.songList = songList;
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void pauseSong(boolean postNotification) {
        position = player.getCurrentPosition();
        player.stop();
        paused = true;
        if (postNotification) {
            notification = buildNotification(true);
            startForeground(NOTIFICATION_ID, notification);
        }
        stopForeground(false);
        updateSession();
    }

    public void resumeSong() {
        if (!player.isPlaying()) {
            player.prepareAsync();
        }
    }

    public void nextSong() {
        Log.e(LOG_TAG, "nextSong");
        int nextpos = -1;
        if (!queue.isEmpty()) {
            String next = queue.dequeue();
            Uri trackUri = null;
            try {
                trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.valueOf(next));
            } catch (NumberFormatException nfe) {
                Log.e(LOG_TAG, "Couldnt parse MediaId");
                throw new IllegalStateException("Couldnt parse MediaId");
            }
            currMediaId = Long.valueOf(next);
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
            this.playSong(nextpos, true);
        }
    }

    public void prevSong() {
        int nextpos = songPosition - 1;
        if (nextpos < 0) {
            nextpos = songList.size() - 1;
        }
        currMediaId = songList.get(nextpos).getId();
        this.playSong(nextpos, false);
    }

    public void playSong() {
        this.playSong(songPosition, false);
    }

    public void playSong(int pos, boolean fromStart) {
        this.setSong(pos);
        LocaleSong song = songList.get(songPosition);
        long curr = song.getId();
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, curr);

        Log.e(LOG_TAG, "Play song");
        this.playSong(trackUri, fromStart);
    }

    public void playSong(Uri trackUri, boolean fromStart) {
        player.reset();
        if (fromStart) {
            position = 0;
        }

        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (IOException e) {
            Log.e("MusicService", "Failed to set DataSource");
        }
        player.prepareAsync();
    }

    public void seekTo(long pos) {
        position = (int) pos;
        Log.e(LOG_TAG, "Seek to " + pos);
        player.seekTo(position);
    }

    public void setSong(int pos) {
        this.songPosition = pos;
    }

    public void toggleShuffle() {
        Log.e(LOG_TAG, "toggleShuffle");
        this.shuffle = !shuffle;
        Log.e(LOG_TAG, "shuffle mode now " + shuffle);
        updateSession();
    }

    public void toggleRepeat() {
        Log.e(LOG_TAG, "toggleRepeat");
        this.repeat = !repeat;
        Log.e(LOG_TAG, "repeat mode now " + repeat);
        updateSession();
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

        if (player.isPlaying()) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, player.getCurrentPosition(), 1.0f);
        } else {
            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, player.getCurrentPosition(), 1.0f);
        }

        LocaleSong song = audioLoader.getSong(currMediaId);

        MediaMetadataCompat.Builder dataBuilder = new MediaMetadataCompat.Builder();

        String artist = song.getArtist();
        String album = song.getAlbum();
        String title = song.getTitle();
        String duration = String.valueOf(player.getDuration());
        String shuffleStr = String.valueOf(shuffle);
        String repeatStr = String.valueOf(repeat);

        dataBuilder.putString(KEY_ARTIST, artist);
        dataBuilder.putString(KEY_ALBUM, album);
        dataBuilder.putString(KEY_TITLE, title);
        dataBuilder.putString(KEY_DURATION, duration);
        dataBuilder.putString(KEY_SHUFFLE, shuffleStr);
        dataBuilder.putString(KEY_REPEAT, repeatStr);

        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setMetadata(dataBuilder.build());
    }

    private boolean requestAudioFocus() {
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

        return res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void abondonAudioFocus() {
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
        LocaleSong dummy = new LocaleSong(mediaId, null, null, null, null);
        Log.e(LOG_TAG, "finding index");
        return songList.indexOf(dummy);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
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
                                    LocaleSong prev = songList.get(songPosition);
                                    List<LocaleSong> songs = new ArrayList<>();
                                    for (PlaylistEntry entry : playlistWithEntries.getEntries()) {
                                        LocaleSong s = audioLoader.getSong(entry.getMediaId());
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
            MusicService.this.pauseSong(true);
        }

        @Override
        public void onPlay() {
            Intent intent = new Intent(MusicService.this, MusicService.class);
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
            WaitForLoadObject load = new WaitForLoadObject();
            if (!context.equals(newContext)) {
                switch (contextType) {
                    case CONTEXT_TYPE_SONGLIST:
                        Log.e(LOG_TAG, "new context songlist");
                        songList = audioLoader.getSongs();
                        notifyLoadedAndPlay(mediaId, trackUri, false);
                        break;
                    case CONTEXT_TYPE_ALBUM:
                        Log.e(LOG_TAG, "new context album");
                        if (!extras.containsKey(KEY_ALBUM_ID)) {
                            throw new IllegalStateException("missing album id");
                        }
                        songList = audioLoader.getSongsFromAlbum(extras.getLong(KEY_ALBUM_ID));
                        notifyLoadedAndPlay(mediaId, trackUri, false);
                        break;
                    case CONTEXT_TYPE_PLAYLIST:
                        Log.e(LOG_TAG, "new context playlist");
                        load.setWaitForLoad(true);
                        if (!extras.containsKey(KEY_PLAYLIST_ID)) {
                            throw new IllegalStateException("missing album id");
                        }
                        String listId = extras.getString(KEY_PLAYLIST_ID);
                        PlaylistRepository repo = PlaylistRepository.getInstance(getApplication());
                        Uri finalTrackUri = trackUri;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                repo.getPlaylist(listId).observeForever(playlistWithEntries -> {
                                    if (context.startsWith(CONTEXT_PREFIX_PLAYLIST)) {
                                        Log.e(LOG_TAG, "Load playlist");
                                        List<LocaleSong> songs = new ArrayList<>();
                                        for (PlaylistEntry entry : playlistWithEntries.getEntries()) {
                                            LocaleSong s = audioLoader.getSong(entry.getMediaId());
                                            if (s != null) {
                                                songs.add(s);
                                            }
                                        }
                                        songList = songs;
                                        setSongPositionFromMediaId(Long.valueOf(mediaId));
                                        updateSession();
                                        notifyLoadedAndPlay(mediaId, finalTrackUri, player.isPlaying());
                                    }
                                });
                            }
                        });
                        break;
                }
                context = newContext;
            } else {
                notifyLoadedAndPlay(mediaId, trackUri, false);
            }
        }

        private void notifyLoadedAndPlay(String mediaId, Uri trackUri, boolean isPlaying) {
            setSongPositionFromMediaId(Long.valueOf(mediaId));
            if (!isPlaying) {
                MusicService.this.playSong(trackUri, true);
            }
        }

        private void notifyReload(LocaleSong prev) {
            long mediaId = prev.getId();
            setSongPositionFromMediaId(mediaId);
        }

        @Deprecated
        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            Log.e(LOG_TAG, "onPlayFromUri");
            MusicService.this.playSong(uri, true);
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
                    MusicService.this.pauseSong(false);
                    resumeAfterGain = true;
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
                mediaSession.getController().getTransportControls().pause();
            }
        }
    }

    private class WaitForLoadObject {
        private boolean waiting;

        public WaitForLoadObject() {
            waiting = false;
        }

        public boolean isWaiting() {
            return waiting;
        }

        public void setWaitForLoad(boolean waiting) {
            this.waiting = waiting;
        }
    }
}
