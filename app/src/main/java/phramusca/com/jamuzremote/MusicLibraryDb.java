package phramusca.com.jamuzremote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * Created by raph on 10/06/17.
 */
public class MusicLibraryDb extends SQLiteOpenHelper {

    private static final int DB_VERSION = 7;

    static final String TABLE_TRACKS = "tracks"; //NON-NLS

    static final String COL_ID_REMOTE = "idFileRemote";
    static final String COL_ID_SERVER = "idFileServer";
    static final String COL_TITLE = "title"; //NON-NLS
    static final String COL_ALBUM = "album"; //NON-NLS
    static final String COL_RATING = "rating"; //NON-NLS
    static final String COL_ARTIST = "artist"; //NON-NLS
    static final String COL_PATH = "path"; //NON-NLS
    static final String COL_GENRE = "genre"; //NON-NLS
    static final String COL_ADDED_DATE = "addedDate";
    static final String COL_LAST_PLAYED = "lastPlayed";
    static final String COL_PLAY_COUNTER = "playCounter";
    static final String COL_STATUS = "status"; //NON-NLS
    static final String COL_SIZE = "size"; //NON-NLS //NON-NLS
    static final String COL_LENGTH = "length"; //NON-NLS
    static final String COL_ID_PATH = "idPath";
    static final String COL_ALBUM_ARTIST = "albumArtist";
    static final String COL_YEAR = "year"; //NON-NLS
    static final String COL_TRACK_NO = "trackNo";
    static final String COL_TRACK_TOTAL = "trackTotal";
    static final String COL_DISC_NO = "discNo";
    static final String COL_DISC_TOTAL = "discTotal"; //NON-NLS
    static final String COL_BITRATE = "bitrate"; //NON-NLS
    static final String COL_FORMAT = "format"; //NON-NLS
    static final String COL_BPM = "bpm"; //NON-NLS
    static final String COL_CHECKED_FLAG = "checkedFlag";
    static final String COL_COPYRIGHT = "copyright"; //NON-NLS
    static final String COL_COVER_HASH = "coverhash"; //NON-NLS
    static final String COL_MODIF_DATE = "modifDate";
    static final String COL_COMMENT = "comment"; //NON-NLS
    static final String COL_PATH_MODIF_DATE = "pathModifDate";
    static final String COL_PATH_MB_ID = "pathMbId";
    static final String COL_TRACK_GAIN = "trackGain";
    static final String COL_ALBUM_GAIN = "albumGain";

    private static final String CREATE_TABLE_TRACKS = "CREATE TABLE " + TABLE_TRACKS + " (" //NON-NLS //NON-NLS
            + COL_ID_REMOTE + " INTEGER PRIMARY KEY AUTOINCREMENT, " //NON-NLS //NON-NLS
            + COL_ID_SERVER + " INTEGER NOT NULL, " //NON-NLS
            + COL_ID_PATH + " INTEGER NOT NULL, " //NON-NLS
            + COL_ALBUM_ARTIST + " TEXT NOT NULL, " //NON-NLS //NON-NLS //NON-NLS
            + COL_YEAR + " TEXT NOT NULL, " //NON-NLS
            + COL_TRACK_NO + " INTEGER NOT NULL, " //NON-NLS
            + COL_TRACK_TOTAL + " INTEGER NOT NULL, " //NON-NLS
            + COL_DISC_NO + " INTEGER NOT NULL, " //NON-NLS
            + COL_DISC_TOTAL + " INTEGER NOT NULL, " //NON-NLS //NON-NLS
            + COL_BITRATE + " TEXT NOT NULL, " //NON-NLS
            + COL_FORMAT + " TEXT NOT NULL, " //NON-NLS
            + COL_BPM + " TEXT NOT NULL, " //NON-NLS
            + COL_MODIF_DATE + " TEXT NOT NULL, " //NON-NLS //NON-NLS
            + COL_CHECKED_FLAG + " TEXT NOT NULL, " //NON-NLS
            + COL_COPYRIGHT + " TEXT NOT NULL, " //NON-NLS
            + COL_COVER_HASH + " TEXT NOT NULL, " //NON-NLS //NON-NLS //NON-NLS
            + COL_ARTIST + " TEXT NOT NULL, " //NON-NLS
            + COL_TITLE + " TEXT NOT NULL, " //NON-NLS
            + COL_ALBUM + " TEXT NOT NULL, " //NON-NLS
            + COL_GENRE + " TEXT NOT NULL, " //NON-NLS //NON-NLS
            + COL_RATING + " INTEGER NOT NULL, " //NON-NLS //NON-NLS
            + COL_ADDED_DATE + " TEXT NOT NULL, " //NON-NLS
            + COL_PLAY_COUNTER + " INTEGER NOT NULL, " //NON-NLS //NON-NLS
            + COL_LAST_PLAYED + " TEXT NOT NULL, " //NON-NLS //NON-NLS
            + COL_STATUS + " TEXT NOT NULL, " //NON-NLS //NON-NLS
            + COL_SIZE + " LONG NOT NULL, " //NON-NLS
            + COL_LENGTH + " INTEGER NOT NULL, " //NON-NLS
            + COL_PATH_MODIF_DATE + " TEXT NOT NULL, " //NON-NLS
            + COL_PATH_MB_ID + " TEXT NOT NULL, " //NON-NLS //NON-NLS //NON-NLS
            + COL_COMMENT + " TEXT NOT NULL, " //NON-NLS //NON-NLS //NON-NLS //NON-NLS
            + COL_TRACK_GAIN + " REAL, " //NON-NLS
            + COL_ALBUM_GAIN + " REAL, " //NON-NLS
            + COL_PATH + " TEXT NOT NULL); "; //NON-NLS

    private static final String TABLE_TAG = "tag";
    static final String COL_TAG_ID = "id";
    static final String COL_TAG_VALUE = "value"; //NON-NLS
    private static final String CREATE_TABLE_TAG = "CREATE TABLE " + TABLE_TAG + " (\n" //NON-NLS //NON-NLS
            + COL_TAG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, \n" //NON-NLS //NON-NLS
            + COL_TAG_VALUE + " TEXT NOT NULL, \n" //NON-NLS
            + " CONSTRAINT name_unique UNIQUE ('" + COL_TAG_VALUE + "'));\n"; //NON-NLS

    private static final String TABLE_TAG_FILE = "tagfile"; //NON-NLS
    static final String COL_TAG_FILE_ID_FILE = "idFile";
    static final String COL_TAG_FILE_ID_TAG = "idTag"; //NON-NLS
    private static final String CREATE_TABLE_TAG_FILE = "CREATE TABLE " + TABLE_TAG_FILE + " (\n" //NON-NLS //NON-NLS
            + COL_TAG_FILE_ID_FILE + " INTEGER NOT NULL, \n" //NON-NLS
            + COL_TAG_FILE_ID_TAG + " INTEGER NOT NULL, \n" //NON-NLS
            + "\tPRIMARY KEY (idFile, 'idTag'),\n" //NON-NLS
            + "\tFOREIGN KEY(" + COL_TAG_FILE_ID_FILE + ") REFERENCES " + TABLE_TRACKS + "(" + COL_ID_REMOTE + "),\n" //NON-NLS //NON-NLS
            + "\tFOREIGN KEY(" + COL_TAG_FILE_ID_TAG + ") REFERENCES " + TABLE_TAG + "(" + COL_TAG_ID + ") ON DELETE CASCADE);"; //NON-NLS //NON-NLS //NON-NLS

    private static final String TABLE_GENRE = "genre"; //NON-NLS
    static final String COL_GENRE_ID = "id"; //NON-NLS
    static final String COL_GENRE_VALUE = "value"; //NON-NLS
    private static final String CREATE_TABLE_GENRE = "CREATE TABLE " + TABLE_GENRE + " (\n" //NON-NLS
            + COL_GENRE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, \n" //NON-NLS
            + COL_GENRE_VALUE + " TEXT NOT NULL);"; //NON-NLS

    MusicLibraryDb(final Context context, File musicLibraryDbFile) {
        super(context, musicLibraryDbFile.getAbsolutePath(), null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TRACKS);
        db.execSQL(CREATE_TABLE_TAG);
        db.execSQL(CREATE_TABLE_TAG_FILE);
        db.execSQL(CREATE_TABLE_GENRE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //NON-NLS
        //TODO: How to warn user he needs to sync before upgrading version ? //NON-NLS //NON-NLS //NON-NLS
        // in case database change and this is called. //NON-NLS
        db.execSQL("DROP TABLE " + TABLE_TRACKS + ";"); //NON-NLS //NON-NLS
        db.execSQL("DROP TABLE " + TABLE_TAG + ";"); //NON-NLS //NON-NLS
        db.execSQL("DROP TABLE " + TABLE_TAG_FILE + ";"); //NON-NLS //NON-NLS
        db.execSQL("DROP TABLE " + TABLE_GENRE + ";"); //NON-NLS //NON-NLS
        onCreate(db);
    }
}
