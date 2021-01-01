package de.codingforcelm.idmp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import de.codingforcelm.idmp.audio.AudioLoader;
import de.codingforcelm.idmp.fragment.BigPlayerFragment;
import de.codingforcelm.idmp.fragment.HomeFragment;
import de.codingforcelm.idmp.fragment.StatisticsFragment;
import de.codingforcelm.idmp.fragment.tab.TabFragment;
import de.codingforcelm.idmp.fragment.TestFragment;
import de.codingforcelm.idmp.player.service.MusicService;

public class MainActivity extends AppCompatActivity {

    public static final String Broadcast_PLAY_NEW_AUDIO = "de.codingforcelm.idmp.PlayNewAudio";
    public static final String LOG_TAG = "MainActivity";

    public static final String CONTEXT_SONGLIST = "de.codingforcelm.idmp.player.service.SONGLIST";

    private List<PhysicalSong> songList;
    private boolean bound;

    private MusicService service;
    private Intent playIntent;
    private MediaBrowserCompat browser;
    private MediaControllerCompat.TransportControls transportControls;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Handler delayHandler;
    private boolean listview;
    private boolean playstatus;//this
    private MediaMetadataCompat mediaMetadata;
    private int duration;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", bound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        bound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        browser = new MediaBrowserCompat(
                this,
                new ComponentName(this, MusicService.class),
                connectionCallback,
                null
        );

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navDrawer = (NavigationView) findViewById(R.id.navView);
        drawerToggle = setupDrawerToggle();
        setupDrawerContent(navDrawer);
        // Setup toggle to display hamburger icon with animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        drawerLayout.addDrawerListener(drawerToggle);

        bound = false;

        songList = new AudioLoader(this).getSongs();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.mainFrame, new TabFragment(), TabFragment.class.getSimpleName());

        listview = true;
        playstatus = false;

        delayHandler = new Handler(getMainLooper());
        runOnUiThread(new SeekBarRunner());

        ft.commit();
        this.createNotificationChannel();

        //browser.connect();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!browser.isConnected()) {
            Log.e(LOG_TAG, "Connect browser");
            browser.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (MediaControllerCompat.getMediaController(this) != null) {
            MediaControllerCompat.getMediaController(this).unregisterCallback(controllerCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent stop = new Intent(this, MusicService.class);
        stopService(stop);
        browser.disconnect();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            MainActivity.this.service = binder.getService();
            MainActivity.this.service.setSongList(songList);
            bound = true;
            Toast.makeText(MainActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    private final MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            MediaSessionCompat.Token token = browser.getSessionToken();
            MediaControllerCompat controller = new MediaControllerCompat(MainActivity.this, token);
            MediaControllerCompat.setMediaController(MainActivity.this, controller);
            transportControls = controller.getTransportControls();

            // TODO utilize transport controls

            controller.sendCommand(MusicService.COMMAND_UPDATE_METADATA, null, new ResultReceiver(new Handler(getMainLooper())));

            controller.registerCallback(controllerCallback);
            Toast.makeText(MainActivity.this, "Browser connected", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Browser connected");
        }

        @Override
        public void onConnectionFailed() {
            Log.e("MainActivity", "Service crashed");
        }

        @Override
        public void onConnectionSuspended() {
            Log.e("MainActivity", "Connection refused");
        }
    };

    private MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            BigPlayerFragment bpf = (BigPlayerFragment) getSupportFragmentManager().findFragmentByTag(BigPlayerFragment.class.getSimpleName());
            if (bpf != null) {
                bpf.applyMetadata(metadata);
            }
            mediaMetadata = metadata;
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            BigPlayerFragment bpf = (BigPlayerFragment) getSupportFragmentManager().findFragmentByTag(BigPlayerFragment.class.getSimpleName());
            TabFragment tpf = (TabFragment) getSupportFragmentManager().findFragmentByTag(TabFragment.class.getSimpleName());

            int res = -1;

            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    Log.e(LOG_TAG, "Playbackstate changed to play");
                    res = R.drawable.ic_control_pause;
                    playstatus = true;
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    Log.e(LOG_TAG, "Playbackstate changed to pause");
                    res = R.drawable.ic_control_play;
                    playstatus = false;
                    break;
            }

            if (res != -1) {
                if (bpf != null) {
                    Log.e(LOG_TAG, "Set playback status for big player");
                    ImageView i = findViewById(R.id.bp_playPauseButton);
                    if (i != null) {
                        i.setImageResource(res);
                    }
                }
                if (tpf != null) {
                    Log.e(LOG_TAG, "Set playback status for list player");
                    ImageView i = findViewById(R.id.tp_playPauseButton);
                    if (i != null) {
                        i.setImageResource(res);
                    }
                }
            }
        }

    };


    public void songSelect(View view) {
        int pos = Integer.parseInt(view.getTag().toString());
        PhysicalSong song = songList.get(pos);
        String id = String.valueOf(song.getId());
        Bundle b = new Bundle();
        b.putString(MusicService.KEY_CONTEXT, CONTEXT_SONGLIST);
        b.putString(MusicService.KEY_CONTEXT_TYPE, MusicService.CONTEXT_TYPE_SONGLIST);
        transportControls.playFromMediaId(id, b);
        Log.e(LOG_TAG, "");
    }

    private void createNotificationChannel() {
        // Create a NotificationChannel for Systems running Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_desc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(MusicService.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        detachFragments(fragmentManager, fragmentTransaction);
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                if (fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName()) != null) {
                    fragmentTransaction.attach(fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName()));
                } else {
                    fragmentTransaction.add(R.id.mainFrame, new HomeFragment(), HomeFragment.class.getSimpleName());
                }
                break;
            case R.id.nav_tabPlayer:
                if (fragmentManager.findFragmentByTag(TabFragment.class.getSimpleName()) != null) {
                    fragmentTransaction.attach(fragmentManager.findFragmentByTag(TabFragment.class.getSimpleName()));
                } else {
                    fragmentTransaction.add(R.id.mainFrame, new TabFragment(), TabFragment.class.getSimpleName());
                }
                break;
            case R.id.nav_statistics:
                if (fragmentManager.findFragmentByTag(StatisticsFragment.class.getSimpleName()) != null) {
                    fragmentTransaction.attach(fragmentManager.findFragmentByTag(StatisticsFragment.class.getSimpleName()));
                } else {
                    fragmentTransaction.add(R.id.mainFrame, new StatisticsFragment(), StatisticsFragment.class.getSimpleName());
                }
                break;
            case R.id.nav_test:
                if (fragmentManager.findFragmentByTag(TestFragment.class.getSimpleName()) != null) {
                    fragmentTransaction.attach(fragmentManager.findFragmentByTag(TestFragment.class.getSimpleName()));
                } else {
                    fragmentTransaction.add(R.id.mainFrame, new TestFragment(), TestFragment.class.getSimpleName());
                }
                break;
            default:
                //
        }

        fragmentTransaction.commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());

        drawerLayout.closeDrawers();
    }

    /**
     * is used before adding or showing a fragment
     * hides all visible fragments but transaction must be commited AFTER this function call
     *
     * @param fragmentManager     fragmentManager to get all Fragments
     * @param fragmentTransaction fragmentTransaction needs to be commited after this function
     */
    public void detachFragments(FragmentManager fragmentManager, FragmentTransaction fragmentTransaction) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && !fragment.isDetached())
                    fragmentTransaction.detach(fragment);
            }
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
}

    @Override
    public void onBackPressed() {
        DrawerLayout layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (layout.isDrawerOpen(GravityCompat.START)) {
            layout.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().findFragmentByTag(BigPlayerFragment.class.getSimpleName()).isVisible()) {
            replaceFragments(TabFragment.class);
        } else {
            super.onBackPressed();
        }
    }


    public void replaceFragments(Class fragmentClass) {
        Fragment fragment = null;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        detachFragments(fragmentManager, fragmentTransaction);

        if (fragmentManager.findFragmentByTag(fragmentClass.getSimpleName()) != null) {
            fragmentTransaction.attach(fragmentManager.findFragmentByTag(fragmentClass.getSimpleName()));
        } else {
            fragmentTransaction.add(R.id.mainFrame, fragment, fragmentClass.getSimpleName());
        }
         
        fragmentTransaction.commit();

    }

    public MediaMetadataCompat getMetadata() {
        return mediaMetadata;
    }

    public boolean isPlaying() {
        return playstatus;
    }

    private class SeekBarRunner implements Runnable {

        @Override
        public void run() {
            if (playstatus) {
                ResultReceiver rr = new ResultReceiver(null) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        BigPlayerFragment bpf = (BigPlayerFragment) getSupportFragmentManager().findFragmentByTag(BigPlayerFragment.class.getSimpleName());
                        if (bpf != null) {
                            if (resultCode != 0 || !resultData.containsKey(MusicService.KEY_POSITION)) {
                                throw new IllegalStateException("result code or data invalied");
                            }
                            int pos = resultData.getInt(MusicService.KEY_POSITION);
                            bpf.setSeekBarTo(pos / 1000);
                            bpf.setCurrentTime(pos);
                        }

                    }
                };
                MediaControllerCompat.getMediaController(MainActivity.this).sendCommand(MusicService.COMMAND_GET_POSITION, null, rr);
            }
            delayHandler.postDelayed(this, 1000);
        }
    }
}