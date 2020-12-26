package de.codingforcelm.idmp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import de.codingforcelm.idmp.PhysicalSong;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.player.service.MusicService;

public class ListPlayerFragment extends Fragment {
    private List<PhysicalSong> songList;
    private ImageView playPauseButton;
    private ImageView nextButton;
    private ImageView prevButton;
    private MusicService service;

    public ListPlayerFragment(List<PhysicalSong> songList) {
        this.songList=songList;
    }
    public ListPlayerFragment(){
        //needed default constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player_list, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.player_list_top, new SongListFragment(songList), "SONGLIST");
        ft.commit();

        playPauseButton = view.findViewById(R.id.lp_playPauseButton);
        playPauseButton.setOnClickListener(new ListPlayerFragment.PlayPauseOnClickListener());
        nextButton = view.findViewById(R.id.lp_nextButton);
        nextButton.setOnClickListener(new ListPlayerFragment.NextOnClickListener());
        prevButton = view.findViewById(R.id.lp_prevButton);
        prevButton.setOnClickListener(new ListPlayerFragment.PrevOnClickListener());
    }


    /**
     * TODO doc
     * @param service service
     */
    public void setService(MusicService service) {
        this.service=service;
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