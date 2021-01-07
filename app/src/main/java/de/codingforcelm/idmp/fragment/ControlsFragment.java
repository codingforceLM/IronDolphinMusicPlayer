package de.codingforcelm.idmp.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.codingforcelm.idmp.activity.MainActivity;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.loader.AudioLoader;
import de.codingforcelm.idmp.service.MusicService;

public class ControlsFragment extends NameAwareFragment {
    private ImageView playPauseButton;
    private ImageView image;
    private TextView songTitle;
    private TextView songArtist;

    public ControlsFragment() {
        setFragmentname(this.getClass().getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_controls, parent, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        playPauseButton = view.findViewById(R.id.tp_playPauseButton);
        playPauseButton.setOnClickListener(new ControlsFragment.PlayPauseOnClickListener());
        image = view.findViewById(R.id.tp_image);
        image.setOnClickListener(new ControlsFragment.ImageOnClickListener());
        songTitle = view.findViewById(R.id.tp_songTitle);
        songTitle.setSelected(true);
        songArtist = view.findViewById(R.id.tp_songArtist);
        songArtist.setSelected(true);

        if (((MainActivity) getActivity()).isPlaying()) {
            playPauseButton.setImageResource(R.drawable.ic_control_pause);
        } else {
            playPauseButton.setImageResource(R.drawable.ic_control_play);
        }
        applyMetadata(((MainActivity) getActivity()).getMetadata());
    }

    public void applyMetadata(MediaMetadataCompat metadata) {
        if(metadata != null){
            String title = metadata.getString(MusicService.KEY_TITLE);
            String artist = metadata.getString(MusicService.KEY_ARTIST);
            String mediaId = metadata.getString(MusicService.KEY_MEDIA_ID);
            songTitle.setText(title);
            songArtist.setText(artist);
            AudioLoader al = new AudioLoader(getContext());
            Bitmap cover = al.getAlbumCoverForSong(Long.valueOf(mediaId));
            if(cover != null) {
                image.setImageBitmap(cover);
            }
        }
    }


    private class PlayPauseOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
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


    private class ImageOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ((MainActivity) getActivity()).placeFragment(BigPlayerFragment.class, R.id.mainFrame);
        }
    }
}
