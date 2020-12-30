package de.codingforcelm.idmp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.codingforcelm.idmp.fragment.ListPlayerFragment;
import de.codingforcelm.idmp.fragment.SongListFragment;
import de.codingforcelm.idmp.fragment.TestFragment;

public class TabFragmentAdapter extends FragmentStateAdapter {

    private int numOfTabs;
    private Context context;

    public TabFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, @NonNull Context context) {
        super(fragmentManager, lifecycle);
        this.numOfTabs = 3;
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                // Songs
                return new SongListFragment(new CardsAdapter(context));
            case 1:
                // Albums
                return new SongListFragment(new AlbumCardAdapter(context));
            case 2:
                // Playlists
                return new TestFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return numOfTabs;
    }
}
