package phramusca.com.jamuzkids;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by raph on 10/06/17.
 */
public class MusicLibraryDb extends SQLiteOpenHelper {

    private static final int DB_VERSION = 3;

    static final String TABLE_TRACKS = "tracks";
    static final String COL_ID_REMOTE = "idFileRemote";
    static final String COL_ID_SERVER = "idFileServer";
    static final String COL_TITLE = "title";
    static final String COL_ALBUM = "album";
    static final String COL_RATING = "rating";
    static final String COL_ARTIST = "artist";
    static final String COL_PATH = "path";
    static final String COL_GENRE = "genre";
    static final String COL_ADDED_DATE = "addedDate";
    static final String COL_LAST_PLAYED = "lastPlayed";
    static final String COL_PLAY_COUNTER = "playCounter";
    static final String COL_STATUS = "status";
    static final String COL_SIZE = "size";

    private static final String CREATE_BDD = "CREATE TABLE " + TABLE_TRACKS + " ("
            + COL_ID_REMOTE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ID_SERVER + " INTEGER, "
            + COL_ARTIST + " TEXT NOT NULL, "
            + COL_TITLE + " TEXT NOT NULL, "
            + COL_ALBUM + " TEXT NOT NULL, "
            + COL_GENRE + " TEXT, "
            + COL_RATING + " INTEGER NOT NULL, "
            + COL_ADDED_DATE + " TEXT NOT NULL, "
            + COL_PLAY_COUNTER + " INTEGER NOT NULL, "
            + COL_LAST_PLAYED + " TEXT NOT NULL, "
            + COL_STATUS + " TEXT NOT NULL, "
            + COL_SIZE + " LONG NOT NULL, "
            + COL_PATH + " TEXT NOT NULL); ";

    //By default store in user internal folder
    //public MusicLibraryDb(Context context) {
    //    super(context, DB_NAME, null, DB_VERSION);
    //}

    MusicLibraryDb(final Context context) {
        super(context, ActivityMain.musicLibraryDbFile.getAbsolutePath(), null, DB_VERSION);
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
                "\tPRIMARY KEY (idFile, 'idTag'),\n" +
                "\tFOREIGN KEY(idFile) REFERENCES "+TABLE_TRACKS+"("+ COL_ID_REMOTE +"),\n" +
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
        db.execSQL("DROP TABLE tag");
        db.execSQL("DROP TABLE tagfile");
        db.execSQL("DROP TABLE genre");
        onCreate(db);
    }
}
