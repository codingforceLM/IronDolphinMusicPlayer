package de.codingforcelm.idmp.fragment;

import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import de.codingforcelm.idmp.MainActivity;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.fragment.tab.TabFragment;

public class ControlsFragment  extends NameAwareFragment {
    private ImageView playPauseButton;
    private ImageView nextButton;
    private ImageView prevButton;
    private ImageView image;

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
        nextButton = view.findViewById(R.id.tp_nextButton);
        nextButton.setOnClickListener(new ControlsFragment.NextOnClickListener());
        prevButton = view.findViewById(R.id.tp_prevButton);
        prevButton.setOnClickListener(new ControlsFragment.PrevOnClickListener());
        image = view.findViewById(R.id.tp_image);
        image.setOnClickListener(new ControlsFragment.ImageOnClickListener());

        if(((MainActivity)getActivity()).isPlaying()) {
            playPauseButton.setImageResource(R.drawable.ic_control_pause);
        } else {
            playPauseButton.setImageResource(R.drawable.ic_control_play);
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

    private class ImageOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ((MainActivity) getActivity()). placeFragment(BigPlayerFragment.class, R.id.mainFrame);
        }
    }
}
