package de.codingforcelm.idmp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.activity.MainActivity;
import de.codingforcelm.idmp.fragment.adapter.TabFragmentAdapter;
/**
 * Fragment to display a tab layout, which embeds Fragments
 */
public class TabFragment extends NameAwareFragment {
    private static final String LOG_TAG = "TabFragment";
    private final String[] tabTexts = new String[]{"Songs", "Albums", "Playlists"};
    private TabLayout layout;
    private ViewPager2 pager;
    private TabLayoutMediator mediator;

    /**
     * Default constructor, which sets the NameAwareFragment name
     */
    public TabFragment() {
        setFragmentname(this.getClass().getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tabs, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.e(LOG_TAG, "--onViewCreated--");
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
                Log.e(LOG_TAG, "--onTabSelected--");
                MainActivity activity = (MainActivity) getActivity();
                switch (tab.getPosition()) {
                    case 0:
                        // Songs
                        Log.e(LOG_TAG, "set current tab: Songs");
                        activity.setCurrentTab(MainActivity.TAB_SONGS);
                        activity.invalidateOptionsMenu();
                        break;
                    case 1:
                        // Albums
                        Log.e(LOG_TAG, "set current tab: Albums");
                        activity.setCurrentTab(MainActivity.TAB_ALBUMS);
                        activity.invalidateOptionsMenu();
                        break;
                    case 2:
                        // Playlists
                        Log.e(LOG_TAG, "set current tab: Playlists");
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

    private void initializeTabTexts() {
        for (int i = 0; i < tabTexts.length; i++) {
            layout.getTabAt(i).setText(tabTexts[i]);
        }
    }

}
