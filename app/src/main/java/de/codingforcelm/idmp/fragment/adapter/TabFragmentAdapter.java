package de.codingforcelm.idmp.fragment.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.codingforcelm.idmp.activity.MainActivity;
import de.codingforcelm.idmp.fragment.tab.AlbumListFragment;
import de.codingforcelm.idmp.fragment.tab.PlaylistListFragment;
import de.codingforcelm.idmp.fragment.tab.SongListFragment;

/**
 * Adapter class to retrieve fragments for tabs
 */
public class TabFragmentAdapter extends FragmentStateAdapter {
    private static final String LOG_TAG = "TabFragmentAdapter";
    private final int numOfTabs;
    private final MainActivity activity;

    /**
     * Default constructor
     * @param fragmentManager fragmentManager
     * @param lifecycle lifecycle
     * @param activity activity
     */
    public TabFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, MainActivity activity) {
        super(fragmentManager, lifecycle);
        numOfTabs = 3;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.e(LOG_TAG, "-- createFragment --");
        switch (position) {
            case 0:
                // Songs
                Log.e(LOG_TAG, "position:"+position+", creating "+SongListFragment.class.getSimpleName());
                return new SongListFragment();
            case 1:
                // Albums
                Log.e(LOG_TAG, "position:"+position+", creating "+AlbumListFragment.class.getSimpleName());
                return new AlbumListFragment();
            case 2:
                // Playlists
                Log.e(LOG_TAG, "position:"+position+", creating "+PlaylistListFragment.class.getSimpleName());
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
