package phramusca.com.jamuzkids;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by raph on 10/06/17.
 */
public class MusicLibraryDb extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;

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
        db.execSQL("CREATE TABLE 'tag' (\n" +
                "'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "'value' TEXT NOT NULL,\n" +
                "CONSTRAINT name_unique UNIQUE ('value')\n" +
                ")");
        db.execSQL("CREATE TABLE 'tagfile' (\n" +
                "    'idFile' INTEGER NOT NULL,\n" +
                "    'idTag' INTEGER NOT NULL,\n" +
                "\tPRIMARY KEY ('idFile', 'idTag'),\n" +
                "\tFOREIGN KEY(idFile) REFERENCES "+TABLE_TRACKS+"("+COL_ID+"),\n" +
                "\tFOREIGN KEY(idTag) REFERENCES tag(id) ON DELETE CASCADE\n" +
                ");");
        db.execSQL("CREATE TABLE \"genre\" (\n" +
                "    \"id\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "    \"value\" TEXT NOT NULL\n" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO: How to warn user he needs to sync before upgrading version ?
        // in case database change and this is called.
        db.execSQL("DROP TABLE " + TABLE_TRACKS + ";");
        db.execSQL("DROP TABLE tags");
        db.execSQL("DROP TABLE tagfile");
        db.execSQL("DROP TABLE genre");
        onCreate(db);
    }
}
