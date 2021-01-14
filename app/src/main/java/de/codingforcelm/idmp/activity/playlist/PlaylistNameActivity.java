package de.codingforcelm.idmp.activity.playlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.codingforcelm.idmp.R;

/**
 * This activity is used to get user input to set the name for a new created playlist
 */
public class PlaylistNameActivity extends AppCompatActivity {
    private static final String LOG_TAG = "PlaylistNameActivity";
    private EditText text;
    private Toolbar toolbar;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_name);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        text = findViewById(R.id.apn_edittext);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playlist_name_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(LOG_TAG, "--onContextItemSelected--" + item.getItemId());
        if (item.getItemId() == R.id.apn_action_accept) {
            String name = text.getText().toString();
            name = name.trim();
            if (name.equals("")) {
                Log.e(LOG_TAG, "no name input");
                return true;
            } else {
                Log.e(LOG_TAG, "creating playlist: "+name);
                Bundle b = new Bundle();
                b.putString(PlaylistCreateActivity.KEY_PLAYLIST_NAME, name);
                b.putString(PlaylistCreateActivity.KEY_MODE, PlaylistCreateActivity.MODE_CREATE);
                Intent intent = new Intent(this, PlaylistCreateActivity.class);
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }

        }

        return super.onOptionsItemSelected(item);
    }

}
