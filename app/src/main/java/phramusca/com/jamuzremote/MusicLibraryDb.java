package phramusca.com.jamuzremote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by raph on 10/06/17.
 */
public class MusicLibraryDb extends SQLiteOpenHelper {

    private static final int DB_VERSION = 6;

    static final String TABLE_TRACKS = "tracks";

    //TODO: Use this syntax and make the same for other tables
    //static final String COL_ID_REMOTE = TABLE_TRACKS+".idFileRemote";
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
    static final String COL_LENGTH = "length";
    static final String COL_ID_PATH = "idPath";
    static final String COL_ALBUM_ARTIST = "albumArtist";
    static final String COL_YEAR = "year";
    static final String COL_TRACK_NO = "trackNo";
    static final String COL_TRACK_TOTAL = "trackTotal";
    static final String COL_DISC_NO = "discNo";
    static final String COL_DISC_TOTAL = "discTotal";
    static final String COL_BITRATE = "bitrate";
    static final String COL_FORMAT = "format";
    static final String COL_BPM = "bpm";
    static final String COL_CHECKED_FLAG = "checkedFlag";
    static final String COL_COPYRIGHT = "copyright";
    static final String COL_COVER_HASH = "coverhash";
    static final String COL_MODIF_DATE = "modifDate";
    static final String COL_COMMENT = "comment";
    static final String COL_PATH_MODIF_DATE = "pathModifDate";
    static final String COL_PATH_MB_ID = "pathMbId";
    static final String COL_TRACK_GAIN = "trackGain";
    static final String COL_ALBUM_GAIN = "albumGain";

    private static final String CREATE_BDD = "CREATE TABLE " + TABLE_TRACKS + " ("
            + COL_ID_REMOTE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ID_SERVER + " INTEGER UNIQUE NOT NULL, "
            + COL_ID_PATH + " INTEGER NOT NULL, "
            + COL_ALBUM_ARTIST + " TEXT NOT NULL, "
            + COL_YEAR + " TEXT NOT NULL, "
            + COL_TRACK_NO + " INTEGER NOT NULL, "
            + COL_TRACK_TOTAL + " INTEGER NOT NULL, "
            + COL_DISC_NO + " INTEGER NOT NULL, "
            + COL_DISC_TOTAL + " INTEGER NOT NULL, "
            + COL_BITRATE + " TEXT NOT NULL, "
            + COL_FORMAT + " TEXT NOT NULL, "
            + COL_BPM + " TEXT NOT NULL, "
            + COL_MODIF_DATE + " TEXT NOT NULL, "
            + COL_CHECKED_FLAG + " TEXT NOT NULL, "
            + COL_COPYRIGHT + " TEXT NOT NULL, "
            + COL_COVER_HASH + " TEXT NOT NULL, "
            + COL_ARTIST + " TEXT NOT NULL, "
            + COL_TITLE + " TEXT NOT NULL, "
            + COL_ALBUM + " TEXT NOT NULL, "
            + COL_GENRE + " TEXT NOT NULL, "
            + COL_RATING + " INTEGER NOT NULL, "
            + COL_ADDED_DATE + " TEXT NOT NULL, "
            + COL_PLAY_COUNTER + " INTEGER NOT NULL, "
            + COL_LAST_PLAYED + " TEXT NOT NULL, "
            + COL_STATUS + " TEXT NOT NULL, "
            + COL_SIZE + " LONG NOT NULL, "
            + COL_LENGTH + " INTEGER NOT NULL, "
            + COL_PATH_MODIF_DATE + " TEXT NOT NULL, "
            + COL_PATH_MB_ID + " TEXT NOT NULL, "
            + COL_COMMENT + " TEXT NOT NULL, "
            + COL_TRACK_GAIN + " REAL, "
            + COL_ALBUM_GAIN + " REAL, "
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
