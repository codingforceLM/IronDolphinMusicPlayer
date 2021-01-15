package de.codingforcelm.idmp.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.codingforcelm.idmp.database.dao.PlayListDao;
import de.codingforcelm.idmp.database.dao.PlaylistEntryDao;
import de.codingforcelm.idmp.database.entity.Playlist;
import de.codingforcelm.idmp.database.entity.PlaylistEntry;

@Database(
        entities = {
                Playlist.class,
                PlaylistEntry.class,
        },
        version = 1
)
public abstract class IDMPRoomDatabase extends RoomDatabase {

    private static final String LOG_TAG = "IDMPRoomDatabase";
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final RoomDatabase.Callback databaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            Log.e(LOG_TAG, "onCreate");
            super.onCreate(db);

            // --- The following is for test purposes only ---
            // Comment this out in production as it will delete the whole database on startup
            // You may need to adjust the values depending on you accessible local files
            databaseWriteExecutor.execute(() -> {
                Log.e(LOG_TAG, "Add initial testing data");
                /*
                PlayListDao pld = INSTANCE.playListDao();
                PlaylistEntryDao ped = INSTANCE.playlistEntryDao();

                pld.deletePlaylist();
                ped.deletePlaylistEntries();

                Playlist p1 = new Playlist(1, "Cages List No1");
                Playlist p2 = new Playlist(2, "Spaceys List No2");

                PlaylistEntry p1e1 = new PlaylistEntry(1, 125, 1);
                PlaylistEntry p1e2 = new PlaylistEntry(2, 126, 1);
                PlaylistEntry p1e3 = new PlaylistEntry(3, 128, 1);
                PlaylistEntry p1e4 = new PlaylistEntry(4, 129, 1);
                PlaylistEntry p1e5 = new PlaylistEntry(5, 130, 1);

                PlaylistEntry p2e1 = new PlaylistEntry(6, 144, 2);
                PlaylistEntry p2e2 = new PlaylistEntry(7, 145, 2);
                PlaylistEntry p2e3 = new PlaylistEntry(8, 146, 2);
                PlaylistEntry p2e4 = new PlaylistEntry(9, 147, 2);
                PlaylistEntry p2e5 = new PlaylistEntry(10, 150, 2);

                pld.insertAll(p1, p2);
                ped.insertAll(p1e1, p1e2, p1e3, p1e4, p1e5, p2e1, p2e2, p2e3, p2e4, p2e5);
                */
            });
        }

    };
    private static volatile IDMPRoomDatabase INSTANCE;

    public static IDMPRoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (IDMPRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), IDMPRoomDatabase.class, "idmp_database")
                            .addCallback(databaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract PlayListDao playListDao();

    public abstract PlaylistEntryDao playlistEntryDao();

}
