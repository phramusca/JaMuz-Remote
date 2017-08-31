package phramusca.com.jamuzremote;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;

import java.io.File;

/**
 * Created by raph on 10/06/17.
 */
public class MusicLibraryDb extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    public static final String TABLE_TRACKS = "tracks";
    public static final String COL_ID = "ID";
    public static final String COL_TITLE = "title";
    public static final String COL_ALBUM = "album";
    public static final String COL_RATING = "rating";
    public static final String COL_ARTIST = "artist";
    public static final String COL_COVER_HASH = "coverHash";
    public static final String COL_PATH = "path";
    public static final String COL_GENRE = "genre";

    public static final String COL_ADDED_DATE = "addedDate";
    public static final String COL_LAST_PLAYED = "lastPlayed";
    public static final String COL_PLAY_COUNTER = "playCounter";

    private static final String CREATE_BDD = "CREATE TABLE " + TABLE_TRACKS + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TITLE + " TEXT NOT NULL, "
            + COL_RATING + " INTEGER NOT NULL, "
            + COL_ARTIST + " TEXT NOT NULL, "
            + COL_COVER_HASH + " TEXT NOT NULL, "
            + COL_PATH + " TEXT NOT NULL, "
            + COL_GENRE + " TEXT, "
            + COL_ADDED_DATE + " TEXT NOT NULL, "
            + COL_PLAY_COUNTER + " INTEGER NOT NULL, "
            + COL_LAST_PLAYED + " TEXT NOT NULL, "
            + COL_ALBUM + " TEXT NOT NULL);";

    //By default store in user internal folder
    //public MusicLibraryDb(Context context) {
    //    super(context, DB_NAME, null, DB_VERSION);
    //}

    public MusicLibraryDb(final Context context) {
        super(context, MainActivity.musicLibraryDbFile.getAbsolutePath(), null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BDD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_TRACKS + ";");
        onCreate(db);
    }
}
