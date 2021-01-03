package de.codingforcelm.idmp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
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
    private static final int STORAGE_PERMISSION_CODE = 1;

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

    public void checkStoragePermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "You have already granted this permission!",Toast.LENGTH_SHORT).show();
            createBrowser();
        } else {
            requestStoragePermission();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkStoragePermission();

        setContentView(R.layout.activity_main);

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


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.mainFrame, new HomeFragment(), HomeFragment.class.getSimpleName());

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
            songList = new AudioLoader(MainActivity.this).getSongs();
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


    public void songSelect(long songId, String playContext, String playContextType) {
        Bundle b = new Bundle();

        if(playContextType.equals(MusicService.CONTEXT_TYPE_ALBUM)) {
            //Context for album must be the album id
            b.putLong(MusicService.KEY_ALBUM_ID, Long.parseLong(playContext));
            b.putString(MusicService.KEY_CONTEXT, MusicService.CONTEXT_PREFIX_ALBUM + playContext);
        } else if(playContextType.equals(MusicService.CONTEXT_TYPE_PLAYLIST)) {
            // TODO Add String with mediaId s
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
            case R.id.nav_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_tabPlayer:
                fragmentClass = TabFragment.class;
                break;
            case R.id.nav_statistics:
                fragmentClass = StatisticsFragment.class;
                break;
            case R.id.nav_test:
                fragmentClass = TestFragment.class;
                break;
            default:
                Log.e(LOG_TAG, "unknown navigation selected");
        }
        Log.e(LOG_TAG, fragmentClass.getSimpleName()+" selected");
        placeFragment(fragmentClass, R.id.mainFrame);

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());

        drawerLayout.closeDrawers();

    }

    /** places a Fragment on a given layout id
     * @param fragmentClass FragmentClass to display
     * @param frameId layoutFrame to place Fragment on
     */
    public void placeFragment(Class fragmentClass, int frameId){
        Log.e(LOG_TAG, "--placeFragment--");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // detach fragments
        String simpleName = fragmentClass.getSimpleName();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment f : fragments) {
                if (f != null && !f.isDetached())
                    fragmentTransaction.detach(f);
                    Log.e(LOG_TAG, f.getClass().getSimpleName()+" detatched");
            }
        }

        // add/attach fragments
        if (fragmentManager.findFragmentByTag(simpleName) != null) {
            fragmentTransaction.attach(fragmentManager.findFragmentByTag(simpleName));
            Log.e(LOG_TAG, simpleName+" attached");
        } else {
            Fragment fragment = null;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Exception creating Fragment instance\n"+e.getMessage());
            }
            fragmentTransaction.add(frameId, fragment, simpleName);
            Log.e(LOG_TAG, simpleName+" added");
        }
        fragmentTransaction.commit();
    }

    public void placeFragment(Fragment fragment, int frameId){
        Log.e(LOG_TAG, "--placeFragment--");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // detach fragments
        String simpleName = fragment.getClass().getSimpleName();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment f : fragments) {
                if (f != null && !f.isDetached())
                    fragmentTransaction.detach(f);
                Log.e(LOG_TAG, f.getClass().getSimpleName()+" detatched");
            }
        }

        // add/attach fragments
        if (fragmentManager.findFragmentByTag(simpleName) != null) {
            fragmentTransaction.attach(fragmentManager.findFragmentByTag(simpleName));
            Log.e(LOG_TAG, simpleName+" attached");
        } else {
            fragmentTransaction.add(frameId, fragment, simpleName);
            Log.e(LOG_TAG, simpleName+" added");
        }
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
        DrawerLayout layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (layout.isDrawerOpen(GravityCompat.START)) {
            layout.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().findFragmentByTag(BigPlayerFragment.class.getSimpleName()).isVisible()) {
            placeFragment(TabFragment.class, R.id.mainFrame);
        } else {
            super.onBackPressed();
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
                                new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
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
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                createBrowser();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createBrowser(){
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