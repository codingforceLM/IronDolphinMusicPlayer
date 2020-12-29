package de.codingforcelm.idmp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import de.codingforcelm.idmp.MainActivity;
import de.codingforcelm.idmp.R;

public class TabPlayerFragment  extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_player, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ((MainActivity) getActivity()).detachFragments(fragmentManager, fragmentTransaction);

        if (fragmentManager.findFragmentByTag(TabFragment.class.getSimpleName()) != null) {
            fragmentTransaction.attach(fragmentManager.findFragmentByTag(TabFragment.class.getSimpleName()));

        } else {
            fragmentTransaction.add(R.id.tp_tab_frame, new TabFragment(), TabFragment.class.getSimpleName());
        }

        if (fragmentManager.findFragmentByTag(ControlsFragment.class.getSimpleName()) != null) {
            fragmentTransaction.attach(fragmentManager.findFragmentByTag(ControlsFragment.class.getSimpleName()));

        } else {
            fragmentTransaction.add(R.id.tp_controls_frame, new ControlsFragment(), ControlsFragment.class.getSimpleName());
        }

        fragmentTransaction.commit();
    }
}
