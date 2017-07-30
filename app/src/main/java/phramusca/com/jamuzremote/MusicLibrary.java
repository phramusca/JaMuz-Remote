package phramusca.com.jamuzremote;

/**
 * Created by raph on 10/06/17.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MusicLibrary {

    private SQLiteDatabase db;
    private MusicLibraryDb musicLibraryDb;

    public MusicLibrary(Context context){
        musicLibraryDb = new MusicLibraryDb(context);
    }

    public void open(){
        db = musicLibraryDb.getWritableDatabase();
    }

    public void close(){
        db.close();
    }

    public SQLiteDatabase getBDD(){
        return db;
    }

    public int getTrack(String path){
        Cursor cursor = db.query(musicLibraryDb.TABLE_TRACKS,
                new String[] {musicLibraryDb.COL_ID},
                musicLibraryDb.COL_PATH + " LIKE \"" + path +"\"",
                null, null, null, null);
        if (cursor.getCount() == 0)
            return -1;

        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex(musicLibraryDb.COL_ID));
        cursor.close();
        return id;
    }

    public ArrayList<Track> getTracks(PlayList playlist) {

        ArrayList<Track> tracks = new ArrayList<>();
        Cursor cursor = db.query(musicLibraryDb.TABLE_TRACKS,
                null,
                playlist.getQuery(),
                null, null, null, null, null);
        if(cursor != null && cursor.moveToFirst())
        {
            do {
                Track track = cursorToTrack(cursor);
                tracks.add(track);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return tracks;
    }

    public long insertTrack(Track track){
        try {
            return db.insert(musicLibraryDb.TABLE_TRACKS, null, TrackToValues(track, true));
        } catch (SQLiteException ex) {
        }
        return -1;
    }

    public int updateTrack(int id, Track track, boolean setRating){
        return db.update(musicLibraryDb.TABLE_TRACKS, TrackToValues(track, setRating), musicLibraryDb.COL_ID + " = " +id, null);
    }

    public int deleteTrack(String path){
        return db.delete(musicLibraryDb.TABLE_TRACKS, musicLibraryDb.COL_PATH + " = \"" +path+"\"", null);
    }

    private ContentValues TrackToValues(Track track, boolean setRating) {
        ContentValues values = new ContentValues();
        values.put(musicLibraryDb.COL_TITLE, track.getTitle());
        values.put(musicLibraryDb.COL_ALBUM, track.getAlbum());
        values.put(musicLibraryDb.COL_ARTIST, track.getArtist());
        values.put(musicLibraryDb.COL_COVER_HASH, track.getCoverHash());
        values.put(musicLibraryDb.COL_GENRE, track.getGenre());
        values.put(musicLibraryDb.COL_PATH, track.getPath());
        if(setRating) {
            values.put(musicLibraryDb.COL_RATING, track.getRating());
        }
        return values;
    }

    private Track cursorToTrack(Cursor c){

        int id = c.getInt(c.getColumnIndex(musicLibraryDb.COL_ID));
        int rating=c.getInt(c.getColumnIndex(musicLibraryDb.COL_RATING));
        String title=c.getString(c.getColumnIndex(musicLibraryDb.COL_TITLE));
        String album=c.getString(c.getColumnIndex(musicLibraryDb.COL_ALBUM));
        String artist=c.getString(c.getColumnIndex(musicLibraryDb.COL_ARTIST));
        String coverHash=c.getString(c.getColumnIndex(musicLibraryDb.COL_COVER_HASH));
        String path=c.getString(c.getColumnIndex(musicLibraryDb.COL_PATH));
        String genre=c.getString(c.getColumnIndex(musicLibraryDb.COL_GENRE));
        Track track = new Track(id, rating, title, album,artist, coverHash, path, genre);
        return track;
    }

    public LinkedHashMap<String, Integer> getGenres(String where) {
        LinkedHashMap<String, Integer> genres = new LinkedHashMap<>();
        Cursor cursor = db.rawQuery("SELECT " + musicLibraryDb.COL_GENRE + ", count(*) FROM " + musicLibraryDb.TABLE_TRACKS +
                " WHERE " + where +
                " GROUP BY " + musicLibraryDb.COL_GENRE +
                " HAVING count(*)>10" +
                " ORDER BY count(*) desc," + musicLibraryDb.COL_GENRE, new String [] {});

        if(cursor != null && cursor.moveToFirst())
        {
            do {
                Integer nb = cursor.getInt(1);
                genres.put(cursor.getString(0), nb);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return genres;
    }

    public int getNb(String where){
        Cursor cursor = db.rawQuery("SELECT count(*) FROM "+musicLibraryDb.TABLE_TRACKS +
                " WHERE " + where, new String [] {});
        if (cursor.getCount() == 0)
            return 0;

        cursor.moveToFirst();
        Integer nb = cursor.getInt(0);
        cursor.close();
        return nb;
    }
}
