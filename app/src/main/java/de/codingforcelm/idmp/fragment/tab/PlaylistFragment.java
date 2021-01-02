package de.codingforcelm.idmp.fragment.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.fragment.ControlsFragment;
import de.codingforcelm.idmp.fragment.adapter.PlaylistCardAdapter;
import de.codingforcelm.idmp.structure.playlist.Playlist;
import de.codingforcelm.idmp.structure.playlist.model.PlaylistViewModel;


public class PlaylistFragment extends Fragment {

    private PlaylistCardAdapter adapter;
    private PlaylistViewModel playlistViewModel;

    public PlaylistFragment() {
        //needed default constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PlaylistCardAdapter(getActivity().getApplication());
        playlistViewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);
        playlistViewModel.getPlaylists().observe(this, playlistWithEntries -> {
            adapter.setData(playlistWithEntries);
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