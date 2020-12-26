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
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
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

import java.util.ArrayList;
import java.util.List;

import de.codingforcelm.idmp.audio.AudioLoader;
import de.codingforcelm.idmp.fragment.ListPlayer;
import de.codingforcelm.idmp.fragment.BigPlayerFragment;
import de.codingforcelm.idmp.fragment.HomeFragment;
import de.codingforcelm.idmp.fragment.ListPlayerFragment;
import de.codingforcelm.idmp.fragment.StatisticsFragment;
import de.codingforcelm.idmp.player.service.MusicService;

public class MainActivity extends AppCompatActivity {

    public static final String Broadcast_PLAY_NEW_AUDIO = "de.codingforcelm.idmp.PlayNewAudio";
    public static final String LOG_TAG = "MainActivity";

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

        ft.replace(R.id.mainFrame, new ListPlayerFragment(), "LISTPLAYER");

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
        playIntent = new Intent(this, MusicService.class);
        Log.e(LOG_TAG,"Connect browser");
        browser.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(MediaControllerCompat.getMediaController(this) != null) {
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
            initializePlayer();
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

            // TODO display initial state

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
            MediaDescriptionCompat desc = metadata.getDescription();
            // TODO change Metadata once we have some shit to do so
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            ImageView i = findViewById(R.id.playPauseButton);
            switch(state.getState()) {
                case (int)PlaybackStateCompat.ACTION_PLAY:
                    i.setImageResource(android.R.drawable.ic_media_pause);
                    break;
                case (int)PlaybackStateCompat.ACTION_PAUSE:
                    i.setImageResource(android.R.drawable.ic_media_play);
                    break;
            }
        }

    };

    /**
     * gets called after the service is bound
     * passes the service to player fragments
     */
    private void initializePlayer() {
        ListPlayerFragment lp = (ListPlayerFragment)getSupportFragmentManager().findFragmentByTag("LISTPLAYER");
        lp.setService(service);
    }

    private void loadAudio() {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        songList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                songList.add(new PhysicalSong(id, data, title, album, artist));
            }
        }
        cursor.close();
    }


    public void songSelect(View view) {
        int pos = Integer.parseInt(view.getTag().toString());
        PhysicalSong song = songList.get(pos);
        String id = String.valueOf(song.getId());
        Bundle b = new Bundle();
        b.putInt("position", pos);
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
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch(menuItem.getItemId()) {
            case R.id.nav_home:
                fragment = getSupportFragmentManager().findFragmentByTag("HOME");
                if(fragment == null){
                    fragment = new HomeFragment();
                }
                break;
            case R.id.nav_listPlayer:
                //TODO check service after mediaplayer rework
                fragment = getSupportFragmentManager().findFragmentByTag("LISTPLAYER");
                if(fragment == null){
                    fragment = new ListPlayerFragment();
                }
                break;
            case R.id.nav_statistics:
                fragment = getSupportFragmentManager().findFragmentByTag("STATISTICS");
                if(fragment == null){
                    fragment = new StatisticsFragment();
                }
                break;
            case R.id.nav_test:
                fragment = getSupportFragmentManager().findFragmentByTag("STATIddddSTICS");
                if(fragment == null){
                    fragment = new BigPlayerFragment();
                }
                break;
            default:
                fragment = new HomeFragment();
        }


        fragmentManager.beginTransaction().replace(R.id.mainFrame, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());

        drawerLayout.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,  R.string.drawer_close);
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
        DrawerLayout layout = (DrawerLayout)findViewById(R.id.drawer_layout);
        if (layout.isDrawerOpen(GravityCompat.START)) {
            layout.closeDrawer(GravityCompat.START);
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
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainFrame, fragment)
                .commit();
    }
}