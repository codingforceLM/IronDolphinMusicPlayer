package de.codingforcelm.idmp.fragment.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.codingforcelm.idmp.activity.MainActivity;
import de.codingforcelm.idmp.fragment.tab.AlbumListFragment;
import de.codingforcelm.idmp.fragment.tab.PlaylistListFragment;
import de.codingforcelm.idmp.fragment.tab.SongListFragment;

public class TabFragmentAdapter extends FragmentStateAdapter {

    private final int numOfTabs;
    private final MainActivity activity;

    public TabFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, MainActivity activity) {
        super(fragmentManager, lifecycle);
        numOfTabs = 3;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                // Songs
                return new SongListFragment();
            case 1:
                // Albums
                return new AlbumListFragment();
            case 2:
                // Playlists
                return new PlaylistListFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return numOfTabs;
    }
}
