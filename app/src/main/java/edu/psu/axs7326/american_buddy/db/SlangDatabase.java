package edu.psu.axs7326.american_buddy.db;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

// Note version should be changed whenever database changes to ensure
// that db is recreated.  You can add a migration to say how the database
// should be changed from version to version.
// Note: If you are changing the schema of your database while debugging,
// you will get an error.  Simply uninstall the app on your phone to
// ensure that the database will be deleted, and then recreated with the
// new schema.
@Database(entities = {Slang.class}, version = 1, exportSchema = false)
public abstract class SlangDatabase extends RoomDatabase {
    public interface SlangListener {
        void onSlangReturned(Slang slang);
    }

    public abstract SlangDAO slangDAO();

    private static SlangDatabase INSTANCE;

    public static SlangDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SlangDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SlangDatabase.class, "slang_database")
                            .addCallback(createSlangDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Note this call back will be run
    private static RoomDatabase.Callback createSlangDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            createSlangTable();
        }
    };

    private static void createSlangTable() {
        for (int i = 0; i < DefaultContent.TITLE.length; i++) {
            insert(new Slang(0, DefaultContent.TITLE[i], DefaultContent.SLANG[i], DefaultContent.ENGLISH_MEANING[i], false));
        }
    }

    public static void getSlang(int id, SlangListener listener) {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                listener.onSlangReturned((Slang) msg.obj);
            }
        };

        (new Thread(() -> {
                Message msg = handler.obtainMessage();
                msg.obj = INSTANCE.slangDAO().getById(id);
                handler.sendMessage(msg);
            })).start();
    }

    public static void insert(Slang slang) {
        (new Thread(()-> INSTANCE.slangDAO().insert(slang))).start();
    }

    public static void delete(int slangId) {
        (new Thread(() -> INSTANCE.slangDAO().delete(slangId))).start();
    }


    public static void update(Slang slang) {
        (new Thread(() -> INSTANCE.slangDAO().update(slang))).start();
    }
}