package de.codingforcelm.idmp.fragment.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.fragment.adapter.TabFragmentAdapter;

public class TabFragment extends Fragment {

    private String[] tabTexts = new String[]{"Songs", "Albums", "Playlists"};

    private TabLayout layout;
    private TabItem songsTab;
    private TabItem playlistTab;
    private TabItem albumTab;
    private ViewPager2 pager;
    private TabLayoutMediator mediator;

    public TabFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tabs, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layout = view.findViewById(R.id.tabLayout);
        songsTab = view.findViewById(R.id.songTab);
        playlistTab = view.findViewById(R.id.playlistTab);
        albumTab = view.findViewById(R.id.albumTab);
        pager = view.findViewById(R.id.tabFragmentPager);

        TabFragmentAdapter adapter = new TabFragmentAdapter(getChildFragmentManager(), this.getLifecycle());
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
        for(int i=0; i<tabTexts.length; i++) {
            layout.getTabAt(i).setText(tabTexts[i]);
        }
    }
}
