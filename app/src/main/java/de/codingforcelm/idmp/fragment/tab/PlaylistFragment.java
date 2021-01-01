package de.codingforcelm.idmp.fragment.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.fragment.app.Fragment;

import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.fragment.ControlsFragment;


public class PlaylistFragment extends Fragment {

    public PlaylistFragment() {
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
        return inflater.inflate(R.layout.fragment_songlist, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (getChildFragmentManager().findFragmentByTag(ControlsFragment.class.getSimpleName()) != null) {
            getChildFragmentManager().beginTransaction().attach(getChildFragmentManager().findFragmentByTag(ControlsFragment.class.getSimpleName())).commit();

        } else {
            getChildFragmentManager().beginTransaction().add(R.id.tp_controls_frame, new ControlsFragment(), ControlsFragment.class.getSimpleName()).commit();
        }
    }
}