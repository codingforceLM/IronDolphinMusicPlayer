package de.codingforcelm.idmp.fragment;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.CardsAdapter;
import de.codingforcelm.idmp.MainActivity;
import de.codingforcelm.idmp.PhysicalSong;
import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.audio.AudioLoader;

public class SongListFragment extends Fragment {
    private ListView songView;
    private List<PhysicalSong> songList;

    public SongListFragment(List<PhysicalSong> songList) {
        this.songList=songList;
    }
    public SongListFragment() {
        //needed default constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songlist, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        songView = (ListView)view.findViewById(R.id.songlist);
        songList = new AudioLoader(this.getContext()).getSongs();
        CardsAdapter adapter = new CardsAdapter(this.getContext(), songList);
        songView.setAdapter(adapter);
    }


}