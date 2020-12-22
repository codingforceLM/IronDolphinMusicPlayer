package de.codingforcelm.idmp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import de.codingforcelm.idmp.PhysicalSong;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.player.service.MusicService;

public class ListPlayer extends Fragment {
    private ListView songView;
    private List<PhysicalSong> songList;
    private MusicService service;
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.

    public ListPlayer(List<PhysicalSong> songList) {
        this.songList=songList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.player_list, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.player_list_bot, new ControlsSmall(service), "CONTROLS_SMALL");
        ft.replace(R.id.player_list_top, new SongList(songList), "SONGLIST");
        ft.commit();
    }

    /**
     * TODO doc
     * @param service service
     */
    public void setService(MusicService service) {
        ControlsSmall frag = (ControlsSmall)getChildFragmentManager().findFragmentByTag("CONTROLS_SMALL");
        frag.setService(service);
    }
}