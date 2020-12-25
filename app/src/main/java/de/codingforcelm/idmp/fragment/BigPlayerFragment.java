package de.codingforcelm.idmp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;

import de.codingforcelm.idmp.R;

public class BigPlayerFragment extends Fragment {
    private ImageView bp_repeatButton;
    private ImageView bp_prevButton;
    private ImageView bp_playPauseButton;
    private ImageView bp_nextButton;
    private ImageView bp_shuffleButton;
    private SeekBar bp_seekBar;


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
    }

    private class PlayPauseOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //TODO implement
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
            //TODO implement
        }
    }

    private class PrevOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //TODO implement
        }
    }

    private class ShuffleOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //TODO implement
        }
    }

    private class SeekBarOnClickListener {
            //TODO implement
        }
    }


