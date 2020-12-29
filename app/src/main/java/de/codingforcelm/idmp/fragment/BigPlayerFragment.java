package de.codingforcelm.idmp.fragment;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import de.codingforcelm.idmp.MainActivity;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.player.service.MusicService;

public class BigPlayerFragment extends Fragment {

    private final String LOG_TAG = BigPlayerFragment.class.getSimpleName();

    private ImageView bp_repeatButton;
    private ImageView bp_prevButton;
    private ImageView bp_playPauseButton;
    private ImageView bp_nextButton;
    private ImageView bp_shuffleButton;
    private SeekBar bp_seekBar;
    private ImageView bp_image;
    private TextView bp_title;
    private TextView bp_artistAlbum;
    private SeekBar bp_seekbar;

    public BigPlayerFragment() {
        //needed default constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_big_player, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

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
        bp_artistAlbum = view.findViewById(R.id.bp_albumArtist);
        bp_seekBar = view.findViewById(R.id.bp_seekBar);

        bp_seekBar.setOnSeekBarChangeListener(new SeekBarOnClickListener());

        if(((MainActivity)getActivity()).isPlaying()) {
            bp_playPauseButton.setImageResource(R.drawable.ic_control_pause);
        } else {
            bp_playPauseButton.setImageResource(R.drawable.ic_control_play);
        }
    }

    public void applyMetadata(MediaMetadataCompat metadata) {
        if(!metadata.containsKey(MusicService.KEY_ARTIST)) {
            throw new IllegalStateException("Missing artist");
        }
        if(!metadata.containsKey(MusicService.KEY_ALBUM)) {
            throw new IllegalStateException("Missing album");
        }
        if(!metadata.containsKey(MusicService.KEY_TITLE)) {
            throw new IllegalStateException("Missing title");
        }
        if(!metadata.containsKey(MusicService.KEY_DURATION)) {
            throw new IllegalStateException("Missing duration");
        }

        String title = metadata.getString(MusicService.KEY_TITLE);
        String artistAlbum = metadata.getString(MusicService.KEY_ARTIST) + " - " + metadata.getString(MusicService.KEY_ALBUM);
        int duration = Integer.parseInt(metadata.getString(MusicService.KEY_DURATION));

        bp_title.setText(title);
        bp_artistAlbum.setText(artistAlbum);
        bp_seekBar.setMax(duration/1000);
    }

    public void setPlaybackState(boolean play) {
        if(play) {
            bp_playPauseButton.setImageResource(R.drawable.ic_control_pause);
        } else {
            bp_playPauseButton.setImageResource(R.drawable.ic_control_play);
        }
    }

    public void setSeekBarTo(int pos) {
        bp_seekBar.setProgress(pos);
    }

    private class PlayPauseOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
            if(controller != null) {
                MediaControllerCompat.TransportControls tc = controller.getTransportControls();
                if(tc != null) {
                    if(((MainActivity)getActivity()).isPlaying()) {
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
            //TODO implement
        }
    }

    private class NextOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
            if(controller != null) {
                MediaControllerCompat.TransportControls tc = controller.getTransportControls();
                if(tc != null) {
                    tc.skipToNext();
                }
            }
        }
    }

    private class PrevOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
            if(controller != null) {
                MediaControllerCompat.TransportControls tc = controller.getTransportControls();
                if(tc != null) {
                    tc.skipToPrevious();
                }
            }
        }
    }

    private class ShuffleOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //TODO implement
        }
    }

    private class ImageOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
               ((MainActivity) getActivity()).replaceFragments(TabPlayerFragment.class);
        }
    }

    private class SeekBarOnClickListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                Log.e(LOG_TAG, "SeekBar changed to "+seekBar.getProgress());
                MediaControllerCompat.getMediaController(getActivity()).getTransportControls().seekTo(seekBar.getProgress()*1000);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
        //TODO implement
        }
    }



