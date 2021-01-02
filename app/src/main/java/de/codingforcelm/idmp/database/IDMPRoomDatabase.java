package de.codingforcelm.idmp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.codingforcelm.idmp.structure.playlist.Playlist;
import de.codingforcelm.idmp.structure.playlist.PlaylistEntry;
import de.codingforcelm.idmp.structure.playlist.dao.PlayListDao;

@Database(
        entities = {
                Playlist.class,
                PlaylistEntry.class
        },
        version = 1
)
public abstract class IDMPRoomDatabase extends RoomDatabase {

    public abstract PlayListDao playListDao();

    private static volatile IDMPRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static IDMPRoomDatabase getInstance(Context context) {
        if(INSTANCE == null) {
            synchronized (IDMPRoomDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), IDMPRoomDatabase.class, "idmp_database").build();
                }
            }
        }
    }
}
