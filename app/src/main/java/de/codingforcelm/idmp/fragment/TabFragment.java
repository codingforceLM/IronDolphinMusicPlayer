package de.codingforcelm.idmp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import de.codingforcelm.idmp.activity.MainActivity;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.fragment.adapter.TabFragmentAdapter;

public class TabFragment extends NameAwareFragment {

    private final String[] tabTexts = new String[]{"Songs", "Albums", "Playlists"};

    private TabLayout layout;
    private TabItem songsTab;
    private TabItem playlistTab;
    private TabItem albumTab;
    private ViewPager2 pager;
    private TabLayoutMediator mediator;

    public TabFragment() {
        setFragmentname(this.getClass().getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tabs, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((MainActivity) getContext()).setTitle(R.string.idmp);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentManager.findFragmentByTag(ControlsFragment.class.getSimpleName()) != null) {
            fragmentTransaction.attach(fragmentManager.findFragmentByTag(ControlsFragment.class.getSimpleName()));
        } else {
            fragmentTransaction.add(R.id.tp_controls_frame, new ControlsFragment(), ControlsFragment.class.getSimpleName());
        }
        fragmentTransaction.commit();

        ((MainActivity) getContext()).setCurrentFragment(MainActivity.FRAGMENT_TABS);
        ((MainActivity) getContext()).invalidateOptionsMenu();

        layout = view.findViewById(R.id.tabLayout);
        songsTab = view.findViewById(R.id.songTab);
        playlistTab = view.findViewById(R.id.playlistTab);
        albumTab = view.findViewById(R.id.albumTab);
        pager = view.findViewById(R.id.tabFragmentPager);

        TabFragmentAdapter adapter = new TabFragmentAdapter(getChildFragmentManager(), this.getLifecycle(), (MainActivity) getActivity());
        pager.setAdapter(adapter);

        TabLayoutMediator.TabConfigurationStrategy strategy = new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                // stub
            }
        };
        mediator = new TabLayoutMediator(layout, pager, strategy);

        layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                MainActivity activity = (MainActivity) getActivity();
                switch (tab.getPosition()) {
                    case 0:
                        // Songs
                        activity.setCurrentTab(MainActivity.TAB_SONGS);
                        activity.invalidateOptionsMenu();
                        break;
                    case 1:
                        // Albums
                        activity.setCurrentTab(MainActivity.TAB_ALBUMS);
                        activity.invalidateOptionsMenu();
                        break;
                    case 2:
                        // Playlists
                        activity.setCurrentTab(MainActivity.TAB_PAYLISTS);
                        activity.invalidateOptionsMenu();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mediator.attach();
        this.initializeTabTexts();
    }

    public void initializeTabTexts() {
        for (int i = 0; i < tabTexts.length; i++) {
            layout.getTabAt(i).setText(tabTexts[i]);
        }
    }

}
