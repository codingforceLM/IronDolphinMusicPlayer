package de.codingforcelm.idmp.fragment;

import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import de.codingforcelm.idmp.MainActivity;
import de.codingforcelm.idmp.PhysicalSong;
import de.codingforcelm.idmp.R;

@Deprecated
public class ListPlayerFragment extends Fragment {
    private ImageView playPauseButton;
    private ImageView nextButton;
    private ImageView prevButton;
    private ImageView image;
    private List<PhysicalSong> songList;

    public ListPlayerFragment(){
        //needed default constructor
    }
    public ListPlayerFragment(List<PhysicalSong> songList){
        this.songList = songList;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player_list, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ((MainActivity)getActivity()).hideVisibleFragments(fragmentManager,fragmentTransaction);

        if(fragmentManager.findFragmentByTag(SongListFragment.class.getSimpleName()) != null) {
            fragmentTransaction.show(fragmentManager.findFragmentByTag(SongListFragment.class.getSimpleName()));

        } else {
            fragmentTransaction.add(R.id.player_list_top, new SongListFragment(songList), SongListFragment.class.getSimpleName());
        }
        fragmentTransaction.commit();

        playPauseButton = view.findViewById(R.id.tp_playPauseButton);
        playPauseButton.setOnClickListener(new ListPlayerFragment.PlayPauseOnClickListener());
        nextButton = view.findViewById(R.id.tp_nextButton);
        nextButton.setOnClickListener(new ListPlayerFragment.NextOnClickListener());
        prevButton = view.findViewById(R.id.tp_prevButton);
        prevButton.setOnClickListener(new ListPlayerFragment.PrevOnClickListener());
        image = view.findViewById(R.id.tp_image);
        image.setOnClickListener(new ListPlayerFragment.ImageOnClickListener());

        if(((MainActivity)getActivity()).isPlaying()) {
            playPauseButton.setImageResource(R.drawable.ic_control_pause);
        } else {
            playPauseButton.setImageResource(R.drawable.ic_control_play);
        }
    }

    public void setPlaybackState(boolean play) {
        if(play) {
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
                ((MainActivity) getActivity()).replaceFragments(BigPlayerFragment.class);
        }
    }


}