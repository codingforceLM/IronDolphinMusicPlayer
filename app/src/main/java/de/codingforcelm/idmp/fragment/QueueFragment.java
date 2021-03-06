package de.codingforcelm.idmp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.activity.MainActivity;
import de.codingforcelm.idmp.fragment.adapter.QueueCardAdapter;
import de.codingforcelm.idmp.loader.AudioLoader;
import de.codingforcelm.idmp.local.LocalSong;
import de.codingforcelm.idmp.queue.SongQueue;

/**
 * Fragment to display a queue of songs
 */
public class QueueFragment extends NameAwareFragment {
    private static final String LOG_TAG = "QueueFragment";
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private AudioLoader audioLoader;
    private QueueCardAdapter adapter;

    /**
     * Default constructor, which sets the NameAwareFragment name
     */
    public QueueFragment() {
        setFragmentname(this.getClass().getSimpleName());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_queue, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.e(LOG_TAG, "--onViewCreated--");
        if (getChildFragmentManager().findFragmentByTag(ControlsFragment.class.getSimpleName()) != null) {
            getChildFragmentManager().beginTransaction().attach(getChildFragmentManager().findFragmentByTag(ControlsFragment.class.getSimpleName())).commit();

        } else {
            getChildFragmentManager().beginTransaction().add(
                    R.id.pl_controls_frame,
                    new ControlsFragment(),
                    ControlsFragment.class.getSimpleName()
            ).commit();
        }

        Log.e(LOG_TAG, "tell MainActivity which fragment is currently visible");
        ((MainActivity) getContext()).setCurrentFragment(MainActivity.FRAGMENT_QUEUE);
        ((MainActivity) getContext()).invalidateOptionsMenu();

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView = view.findViewById(R.id.recyclerView);
        audioLoader = new AudioLoader(getContext());

        SongQueue queue = SongQueue.getInstance();
        List<LocalSong> songs = new ArrayList<>();
        List<String> ids = queue.getListRepresentation();

        for (String id : ids) {
            LocalSong s = audioLoader.getSong(Long.parseLong(id));
            if (s != null) {
                songs.add(s);
            }
        }

        adapter = new QueueCardAdapter(songs, getContext());
        queue.registerOnQueueChangedListener(adapter);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }
}