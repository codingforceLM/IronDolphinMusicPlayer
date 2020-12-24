package de.codingforcelm.idmp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import de.codingforcelm.idmp.MainActivity;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.player.service.MusicService;

public class ControlsSmallFragment extends Fragment {
    private ImageView playPauseButton;
    private ImageView nextButton;
    private ImageView prevButton;
    private MusicService service;
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.

    public ControlsSmallFragment(MusicService service){
        this.service=service;
    }
    public ControlsSmallFragment(){
    }

    public void setService(MusicService service){
        this.service=service;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment

        return inflater.inflate(R.layout.controls_small, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        playPauseButton = view.findViewById(R.id.playPauseButton);
        playPauseButton.setOnClickListener(new PlayPauseOnClickListener());
        nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new NextOnClickListener());
        prevButton = view.findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new PrevOnClickListener());
    }


    private class NextOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
                if(service != null){
                    service.nextSong();
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                }
        }
    }

    private class PlayPauseOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (service != null) {
                if (service.isPlaying()) {
                    service.pauseSong(true);
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    service.resumeSong();
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        }
    }

    private class PrevOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(service != null){
                service.prevSong();
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            }
        }
    }

}