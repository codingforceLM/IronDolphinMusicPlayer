package de.codingforcelm.idmp.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import de.codingforcelm.idmp.R;
import de.codingforcelm.idmp.activity.playlist.PlaylistCreateActivity;
import de.codingforcelm.idmp.activity.playlist.PlaylistNameActivity;
import de.codingforcelm.idmp.fragment.BigPlayerFragment;
import de.codingforcelm.idmp.fragment.ControlsFragment;
import de.codingforcelm.idmp.fragment.NameAwareFragment;
import de.codingforcelm.idmp.fragment.OnManualDetachListener;
import de.codingforcelm.idmp.fragment.QueueFragment;
import de.codingforcelm.idmp.fragment.TabFragment;
import de.codingforcelm.idmp.service.MusicService;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "MainActivity";

    public static final String TAB_SONGS = "de.codingforcelm.idmp.TAB_SONGS";
    public static final String TAB_ALBUMS = "de.codingforcelm.idmp.TAB_ALBUMS";
    public static final String TAB_PAYLISTS = "de.codingforcelm.idmp.TAB_PLAYLISTS";

    public static final String FRAGMENT_QUEUE = "de.codingforcelm.idmp.FRAGMENT_QUEUE";
    public static final String FRAGMENT_TABS = "de.codingforcelm.idmp.FRAGMENT_TABS";
    public static final String FRAGMENT_BIG_PLAYER = "de.codingforcelm.idmp.FRAGMENT_BIG_PLAYER";

    public static final String CONTEXT_SONGLIST = "de.codingforcelm.idmp.player.service.SONGLIST";
    private static final int STORAGE_PERMISSION_CODE = 1;
    private boolean bound;


    private MediaBrowserCompat browser;
    private MediaControllerCompat.TransportControls transportControls;
    private final MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            MediaSessionCompat.Token token = browser.getSessionToken();
            MediaControllerCompat controller = new MediaControllerCompat(MainActivity.this, token);
            MediaControllerCompat.setMediaController(MainActivity.this, controller);
            transportControls = controller.getTransportControls();


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
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Handler delayHandler;
    private boolean playstatus;
    private MediaMetadataCompat mediaMetadata;
    private String currentTab;
    private String currentFragment;
    private boolean inPlaylist;
    private String playlistUuid;

    private final MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            BigPlayerFragment bpf = (BigPlayerFragment) fragmentManager.findFragmentByTag(BigPlayerFragment.class.getSimpleName());
            ControlsFragment ctrl = null;

            if (bpf != null) {
                bpf.applyMetadata(metadata);
            }

            List<Fragment> fragments = fragmentManager.getFragments();
            for (Fragment f : fragments) {
                if (f != null && !f.isDetached()) {
                    ctrl = (ControlsFragment) f.getChildFragmentManager().findFragmentByTag(ControlsFragment.class.getSimpleName());
                    if (ctrl != null) {
                        ctrl.applyMetadata(metadata);
                    }
                }
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

    public void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();
            createBrowser();
        } else {
            requestStoragePermission();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkStoragePermission();

        MainActivitySingleton.getInstance().setMainActivity(this);

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentTab = TAB_SONGS;

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navDrawer = (NavigationView) findViewById(R.id.navView);
        drawerToggle = setupDrawerToggle();
        setupDrawerContent(navDrawer);
        // Setup toggle to display hamburger icon with animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        drawerLayout.addDrawerListener(drawerToggle);

        bound = false;


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.mainFrame, new TabFragment(), TabFragment.class.getSimpleName());

        playstatus = false;

        delayHandler = new Handler(getMainLooper());
        runOnUiThread(new SeekBarRunner());

        ft.commit();
        this.createNotificationChannel();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        int m = -1;
        switch (currentTab) {
            case TAB_SONGS:
                // empty stud
                break;
            case TAB_ALBUMS:
                // empty stud
                break;
            case TAB_PAYLISTS:
                if(currentFragment.equals(FRAGMENT_TABS)) {
                    m = R.menu.playlist_add_menu;
                }
                break;
        }
        if (m >= 0) {
            inflater.inflate(m, menu);
        } else {
            menu.clear();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.ma_action_add) {
            if (!inPlaylist) {
                Intent intent = new Intent(this, PlaylistNameActivity.class);
                startActivity(intent);
            } else {
                if (playlistUuid == null) {
                    throw new IllegalStateException("missing uuid");
                }
                Intent intent = new Intent(this, PlaylistCreateActivity.class);
                Bundle b = new Bundle();
                b.putString(PlaylistCreateActivity.KEY_MODE, PlaylistCreateActivity.MODE_ADD);
                b.putString(PlaylistCreateActivity.KEY_PLAYLIST_UUID, playlistUuid);
                intent.putExtras(b);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        MediaControllerCompat con = MediaControllerCompat.getMediaController(this);
        if (browser != null && browser.isConnected() && con != null) {
            con.registerCallback(controllerCallback);
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

    public void songSelect(long songId, String playContext, String playContextType) {
        Bundle b = new Bundle();

        if (playContextType.equals(MusicService.CONTEXT_TYPE_ALBUM)) {
            //Context for album must be the album id
            b.putLong(MusicService.KEY_ALBUM_ID, Long.parseLong(playContext));
            b.putString(MusicService.KEY_CONTEXT, MusicService.CONTEXT_PREFIX_ALBUM + playContext);
        } else if (playContextType.equals(MusicService.CONTEXT_TYPE_PLAYLIST)) {
            b.putString(MusicService.KEY_PLAYLIST_ID, playContext);
            b.putString(MusicService.KEY_CONTEXT, MusicService.CONTEXT_PREFIX_PLAYLIST + playContext);
        } else {
            b.putString(MusicService.KEY_CONTEXT, playContext);
        }

        b.putString(MusicService.KEY_CONTEXT_TYPE, playContextType);

        transportControls.playFromMediaId(String.valueOf(songId), b);
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
        Log.e(LOG_TAG, "--selectDrawerItem--");

        Class fragmentClass = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_tabPlayer:
                fragmentClass = TabFragment.class;
                setTitle(R.string.idmp);
                break;
            case R.id.nav_queue:
                fragmentClass = QueueFragment.class;
                setTitle(R.string.queue);
                break;
            default:
                Log.e(LOG_TAG, "unknown navigation selected");
        }
        Log.e(LOG_TAG, fragmentClass.getSimpleName() + " selected");
        placeFragment(fragmentClass, R.id.mainFrame);

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);


        drawerLayout.closeDrawers();

    }

    /**
     * places a Fragment on a given layout id
     *
     * @param fragmentClass FragmentClass to display
     * @param frameId       layoutFrame to place Fragment on
     */
    public void placeFragment(Class fragmentClass, int frameId) {
        Log.e(LOG_TAG, "--placeFragment--");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // detach fragments
        String simpleName = fragmentClass.getSimpleName();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment f : fragments) {
                if (f != null && !f.isDetached()) {
                    fragmentTransaction.detach(f);
                    Log.e(LOG_TAG, f.getClass().getSimpleName() + " detatched");
                    if (f instanceof OnManualDetachListener) {
                        Log.e(LOG_TAG, "Calling onManualDetach");
                        ((OnManualDetachListener) f).onManualDetach();
                    }
                    Log.e(LOG_TAG, f.getClass().getSimpleName() + " detatched");
                }

            }
        }

        // add/attach fragments
        if (fragmentManager.findFragmentByTag(simpleName) != null) {
            fragmentTransaction.attach(fragmentManager.findFragmentByTag(simpleName));
            Log.e(LOG_TAG, simpleName + " attached");
        } else {
            Fragment fragment = null;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Exception creating Fragment instance\n" + e.getMessage());
            }
            fragmentTransaction.add(frameId, fragment, simpleName);
            Log.e(LOG_TAG, simpleName + " added");
        }
        fragmentTransaction.addToBackStack(simpleName);
        fragmentTransaction.commit();
    }

    public void placeFragment(Fragment fragment, int frameId) {
        Log.e(LOG_TAG, "--placeFragment--");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // detach fragments
        String simpleName = fragment.getClass().getSimpleName();
        if (fragment instanceof NameAwareFragment) {
            simpleName = ((NameAwareFragment) fragment).getFragmentname();
        }
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment f : fragments) {
                if (f != null && !f.isDetached())
                    fragmentTransaction.detach(f);
                Log.e(LOG_TAG, f.getClass().getSimpleName() + " detatched");
            }
        }

        // add/attach fragments
        if (fragmentManager.findFragmentByTag(simpleName) != null) {
            Fragment f = fragmentManager.findFragmentByTag(simpleName);
            fragmentTransaction.attach(f);
            Log.e(LOG_TAG, simpleName + " attached");
        } else {
            fragmentTransaction.add(frameId, fragment, simpleName);
            Log.e(LOG_TAG, simpleName + " added");
        }
        fragmentTransaction.addToBackStack(simpleName);
        fragmentTransaction.commit();
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
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private void requestStoragePermission() {
        new AlertDialog.Builder(this)
                .setTitle("Permission needed")
                .setMessage(R.string.permission_info)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                createBrowser();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createBrowser() {
        browser = new MediaBrowserCompat(
                this,
                new ComponentName(this, MusicService.class),
                connectionCallback,
                null
        );
        if (!browser.isConnected()) {
            Log.e(LOG_TAG, "Connect browser");
            browser.connect();
        }
    }

    public void setCurrentTab(String tab) {
        this.currentTab = tab;
    }

    public void setCurrentFragment(String fragment) {
        this.currentFragment = fragment;
    }

    public void setInPlaylist(boolean inPlaylist) {
        this.inPlaylist = inPlaylist;
    }

    public void setPlaylistUuid(String uuid) {
        this.playlistUuid = uuid;
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