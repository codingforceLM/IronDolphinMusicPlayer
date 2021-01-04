package de.codingforcelm.idmp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PlaylistNameActivity extends AppCompatActivity {

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
        if (item.getItemId() == R.id.apn_action_accept) {
            String name = text.getText().toString();
            name = name.trim();
            if(name == null || name.equals("")) {
                // TODO do something?
            } else {
                Bundle b = new Bundle();
                b.putString(PlaylistCreateActivity.KEY_PLAYLIST_NAME, name);
                Intent intent = new Intent(this, PlaylistCreateActivity.class);
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }

        }

        return super.onOptionsItemSelected(item);
    }

}
