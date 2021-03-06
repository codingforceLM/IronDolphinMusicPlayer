package de.codingforcelm.idmp.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.activity.MainActivity;
import de.codingforcelm.idmp.loader.AudioLoader;
import de.codingforcelm.idmp.service.MusicService;

/**
 * Fragment to display a big player
 */
public class BigPlayerFragment extends NameAwareFragment {

    private static final String LOG_TAG = "BigPlayerFragment";
    private ImageView bp_repeatButton;
    private ImageView bp_prevButton;
    private ImageView bp_playPauseButton;
    private ImageView bp_nextButton;
    private ImageView bp_shuffleButton;
    private SeekBar bp_seekBar;
    private ImageView bp_image;
    private TextView bp_title;
    private TextView bp_artist;
    private TextView bp_album;
    private TextView bp_currentTime;
    private TextView bp_duration;
    private int duration;

    /**
     * Default constructor, which sets the NameAwareFragment name
     */
    public BigPlayerFragment() {
        setFragmentname(this.getClass().getSimpleName());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_big_player, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.e(LOG_TAG, "--onViewCreated--");
        bp_repeatButton = view.findViewById(R.id.bp_repeatButton);
        bp_repeatButton.setOnClickListener(new BigPlayerFragment.RepeatOnClickListener());
        bp_prevButton = view.findViewById(R.id.bp_prevButton);
        bp_prevButton.setOnClickListener(new BigPlayerFragment.PrevOnClickListener());
        bp_playPauseButton = view.findViewById(R.id.bp_playPauseButton);
        bp_playPauseButton.setOnClickListener(new BigPlayerFragment.PlayPauseOnClickListener());
        bp_nextButton = view.findViewById(R.id.bp_nextButton);
        bp_nextButton.setOnClickListener(new BigPlayerFragment.NextOnClickListener());
        bp_shuffleButton = view.findViewById(R.id.bp_shuffleButton);
        bp_shuffleButton.setOnClickListener(new BigPlayerFragment.ShuffleOnClickListener());
        bp_seekBar = view.findViewById(R.id.bp_seekBar);
        bp_seekBar.setOnClickListener(new BigPlayerFragment.PrevOnClickListener());
        bp_image = view.findViewById(R.id.bp_image);
        bp_image.setOnClickListener(new BigPlayerFragment.ImageOnClickListener());
        bp_title = view.findViewById(R.id.bp_track);
        bp_title.setSelected(true);
        bp_artist = view.findViewById(R.id.bp_artist);
        bp_artist.setSelected(true);
        bp_album = view.findViewById(R.id.bp_album);
        bp_album.setSelected(true);
        bp_seekBar = view.findViewById(R.id.bp_seekBar);
        bp_seekBar.setOnSeekBarChangeListener(new SeekBarOnClickListener());
        bp_currentTime = view.findViewById(R.id.bp_currentTime);
        bp_duration = view.findViewById(R.id.bp_duration);

        Log.e(LOG_TAG, "get initial playstatus from mainactivity");
        if (((MainActivity) getActivity()).isPlaying()) {
            bp_playPauseButton.setImageResource(R.drawable.ic_control_pause);
        } else {
            bp_playPauseButton.setImageResource(R.drawable.ic_control_play);
        }

        Log.e(LOG_TAG, "tell MainActivity which fragment is currently visible");
        ((MainActivity) getContext()).setCurrentFragment(MainActivity.FRAGMENT_TABS);
        ((MainActivity) getContext()).invalidateOptionsMenu();
    }

    /**
     * Function to apply metadata and update controls
     * @param metadata metadata
     */
    public void applyMetadata(MediaMetadataCompat metadata) {
        Log.e(LOG_TAG, "--applyMetadata--");
        if (!metadata.containsKey(MusicService.KEY_ARTIST)) {
            throw new IllegalStateException("Missing artist");
        }
        if (!metadata.containsKey(MusicService.KEY_ALBUM)) {
            throw new IllegalStateException("Missing album");
        }
        if (!metadata.containsKey(MusicService.KEY_TITLE)) {
            throw new IllegalStateException("Missing title");
        }
        if (!metadata.containsKey(MusicService.KEY_DURATION)) {
            throw new IllegalStateException("Missing duration");
        }
        if (!metadata.containsKey(MusicService.KEY_SHUFFLE)) {
            throw new IllegalStateException("Missing shuffle");
        }
        if (!metadata.containsKey(MusicService.KEY_REPEAT)) {
            throw new IllegalStateException("Missing repeat");
        }
        if (!metadata.containsKey(MusicService.KEY_MEDIA_ID)) {
            throw new IllegalStateException("Missing mediaId");
        }

        String title = metadata.getString(MusicService.KEY_TITLE);
        String artist = metadata.getString(MusicService.KEY_ARTIST);
        String album = metadata.getString(MusicService.KEY_ALBUM);
        boolean shuffle = Boolean.parseBoolean(metadata.getString(MusicService.KEY_SHUFFLE));
        boolean repeat = Boolean.parseBoolean(metadata.getString(MusicService.KEY_REPEAT));
        duration = Integer.parseInt(metadata.getString(MusicService.KEY_DURATION));
        String mediaId = metadata.getString(MusicService.KEY_MEDIA_ID);

        int seconds = (int) ((duration / 1000) % 60);
        int minutes = (int) ((duration / 1000) / 60);
        bp_title.setText(title);
        bp_artist.setText(artist);
        bp_album.setText(album);
        bp_seekBar.setMax(duration / 1000);
        if (seconds < 10) {
            bp_duration.setText(minutes + ":0" + seconds);
        } else {
            bp_duration.setText(minutes + ":" + seconds);
        }


        if (shuffle) {
            bp_shuffleButton.setImageResource(R.drawable.ic_control_shuffle_active);
        } else {
            bp_shuffleButton.setImageResource(R.drawable.ic_control_shuffle);
        }
        if (repeat) {
            bp_repeatButton.setImageResource(R.drawable.ic_control_repeat_active);
        } else {
            bp_repeatButton.setImageResource(R.drawable.ic_control_repeat);
        }

        AudioLoader al = new AudioLoader(getContext());
        Bitmap cover = al.getAlbumCoverForSong(Long.valueOf(mediaId));
        if(cover != null) {
            bp_image.setImageBitmap(cover);
        } else {
            bp_image.setImageResource(R.drawable.ic_item_default_image);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        MediaMetadataCompat c = ((MainActivity) getActivity()).getMetadata();
        applyMetadata(c);
    }

    /**
     * Sets seekbar to a given position
     * @param pos position
     */
    public void setSeekBarTo(int pos) {
        bp_seekBar.setProgress(pos);
    }

    /**
     * Sets current time textfield
     * @param time time
     */
    public void setCurrentTime(int time) {
        int seconds = (int) ((time / 1000) % 60);
        int minutes = (int) ((time / 1000) / 60);
        if (seconds < 10) {
            bp_currentTime.setText(minutes + ":0" + seconds);
        } else {
            bp_currentTime.setText(minutes + ":" + seconds);
        }

    }

    private class PlayPauseOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.e(LOG_TAG, "onClick");
            MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
            if (controller != null) {
                MediaControllerCompat.TransportControls tc = controller.getTransportControls();
                if (tc != null) {
                    if (((MainActivity) getActivity()).isPlaying()) {
                        tc.pause();
                    } else {
                        tc.play();
                    }
                }
            }
        }
    }

    private class RepeatOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.e(LOG_TAG, "onClick");
            MediaControllerCompat.getMediaController(getActivity()).sendCommand(MusicService.COMMAND_SET_REPEAT, null, new ResultReceiver(new Handler(Looper.getMainLooper())));
        }
    }

    private class NextOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.e(LOG_TAG, "onClick");
            MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
            if (controller != null) {
                MediaControllerCompat.TransportControls tc = controller.getTransportControls();
                if (tc != null) {
                    tc.skipToNext();
                }
            }
        }
    }

    private class PrevOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.e(LOG_TAG, "onClick");
            MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
            if (controller != null) {
                MediaControllerCompat.TransportControls tc = controller.getTransportControls();
                if (tc != null) {
                    tc.skipToPrevious();
                }
            }
        }
    }

    private class ShuffleOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.e(LOG_TAG, "onClick");
            MediaControllerCompat.getMediaController(getActivity()).sendCommand(MusicService.COMMAND_SET_SHUFFLE, null, new ResultReceiver(new Handler(Looper.getMainLooper())));
        }
    }

    private class ImageOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ((MainActivity) getActivity()).onBackPressed();
        }
    }

    private class SeekBarOnClickListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                Log.e(LOG_TAG, "SeekBar changed to " + seekBar.getProgress());
                MediaControllerCompat.getMediaController(getActivity()).getTransportControls().seekTo(seekBar.getProgress() * 1000);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

    }
}



