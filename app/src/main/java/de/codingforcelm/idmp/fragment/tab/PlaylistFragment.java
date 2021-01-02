package de.codingforcelm.idmp.fragment.tab;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.PhysicalSong;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.audio.AudioLoader;
import de.codingforcelm.idmp.fragment.ControlsFragment;
import de.codingforcelm.idmp.fragment.adapter.PlaylistCardAdapter;
import de.codingforcelm.idmp.fragment.adapter.SongCardAdapter;
import de.codingforcelm.idmp.player.service.MusicService;
import de.codingforcelm.idmp.structure.playlist.Playlist;
import de.codingforcelm.idmp.structure.playlist.PlaylistEntry;
import de.codingforcelm.idmp.structure.playlist.PlaylistWithEntries;
import de.codingforcelm.idmp.structure.playlist.model.PlaylistViewModel;


public class PlaylistFragment extends Fragment {

    private SongCardAdapter adapter;
    private PlaylistViewModel playlistViewModel;
    private int position;
    private int listId;
    private AudioLoader loader;

    public PlaylistFragment(int position, int listId) {
        this.position = position;
        this.listId = listId;
    }

    public PlaylistFragment() {
        //needed default constructor
        position = -1;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = new AudioLoader(getContext());
        adapter = new SongCardAdapter(new ArrayList<>(), getContext(), MusicService.CONTEXT_TYPE_PLAYLIST, String.valueOf(listId));
        playlistViewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);
        playlistViewModel.getPlaylists().observe(this, playlistWithEntries -> {
            if(position < 0) {
                throw new IllegalStateException("missing position in PlaylistFragment");
            }
            List<PhysicalSong> data = new ArrayList<>();
            for(PlaylistEntry entry : playlistWithEntries.get(position).getEntries()) {
                PhysicalSong s = loader.getSong(entry.getMediaId());
                data.add(s);
            }
            adapter.setData(data);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (getChildFragmentManager().findFragmentByTag(ControlsFragment.class.getSimpleName()) != null) {
            getChildFragmentManager().beginTransaction().attach(getChildFragmentManager().findFragmentByTag(ControlsFragment.class.getSimpleName())).commit();

        } else {
            getChildFragmentManager().beginTransaction().add(R.id.pl_controls_frame, new ControlsFragment(), ControlsFragment.class.getSimpleName()).commit();
        }
    }
}