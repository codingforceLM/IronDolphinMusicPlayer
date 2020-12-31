package de.codingforcelm.idmp.fragment.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.codingforcelm.idmp.fragment.AlbumListFragment;
import de.codingforcelm.idmp.fragment.ListPlayerFragment;
import de.codingforcelm.idmp.fragment.PlaylistListFragment;
import de.codingforcelm.idmp.fragment.SongListFragment;
import de.codingforcelm.idmp.fragment.TestFragment;

public class TabFragmentAdapter extends FragmentStateAdapter {

    private int numOfTabs;

    public TabFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        numOfTabs = 3;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
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
