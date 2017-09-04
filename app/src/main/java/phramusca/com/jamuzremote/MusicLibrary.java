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
import android.provider.BaseColumns;
import android.util.Log;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MusicLibrary {

    private SQLiteDatabase db;
    private MusicLibraryDb musicLibraryDb;
    private static final String TAG = MusicLibrary.class.getSimpleName();

    public MusicLibrary(Context context){
        musicLibraryDb = new MusicLibraryDb(context);
    }

    public void open(){
        db = musicLibraryDb.getWritableDatabase();
    }

    public void close(){
        db.close();
    }

    public int getTrack(String path){
        try {
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
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getTrack("+path+")", ex);
        }
        return -1;
    }

    public ArrayList<Track> getTracks(PlayList playlist) {
        ArrayList<Track> tracks = new ArrayList<>();
        try {
            Cursor cursor = db.query(musicLibraryDb.TABLE_TRACKS,
                    null,
                    playlist.getQuery(),
                    null, null, null, "playCounter, lastPlayed", null);
            if(cursor != null && cursor.moveToFirst())
            {
                do {
                    Track track = cursorToTrack(cursor);
                    tracks.add(track);
                } while(cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getTracks("+playlist+")", ex);
        }
        return tracks;
    }

    public long insertTrack(Track track){
        try {
            return db.insert(musicLibraryDb.TABLE_TRACKS, null, TrackToValues(track));
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "insertTrack("+track+")", ex);
        }
        return -1;
    }

    public int updateTrack(Track track){
        try {
            return db.update(musicLibraryDb.TABLE_TRACKS, TrackToValues(track), musicLibraryDb.COL_ID + " = " +track.getId(), null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "updateTrack("+track.getId()+","+track+")", ex);
        }
        return -1;
    }

    public int deleteTrack(String path){
        try {
            return db.delete(musicLibraryDb.TABLE_TRACKS, musicLibraryDb.COL_PATH + " = \"" +path+"\"", null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "deleteTrack("+path+")", ex);
        }
        return -1;
    }

    private ContentValues TrackToValues(Track track) {
        ContentValues values = new ContentValues();
        values.put(musicLibraryDb.COL_TITLE, track.getTitle());
        values.put(musicLibraryDb.COL_ALBUM, track.getAlbum());
        values.put(musicLibraryDb.COL_ARTIST, track.getArtist());
        values.put(musicLibraryDb.COL_COVER_HASH, track.getCoverHash());
        values.put(musicLibraryDb.COL_GENRE, track.getGenre());
        values.put(musicLibraryDb.COL_PATH, track.getPath());
        values.put(musicLibraryDb.COL_RATING, track.getRating());
        values.put(musicLibraryDb.COL_ADDED_DATE, track.getFormattedAddedDate());
        values.put(musicLibraryDb.COL_LAST_PLAYED, track.getFormattedLastPlayed());
        values.put(musicLibraryDb.COL_PLAY_COUNTER, track.getPlayCounter());
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

        Date addedDate=HelperDateTime.parseSqlUtc(
                c.getString(c.getColumnIndex(musicLibraryDb.COL_ADDED_DATE)));
        Date lastPlayed=HelperDateTime.parseSqlUtc(
                c.getString(c.getColumnIndex(musicLibraryDb.COL_LAST_PLAYED)));
        int playCounter=c.getInt(c.getColumnIndex(musicLibraryDb.COL_PLAY_COUNTER));
        Track track = new Track(id, rating, title, album, artist, coverHash, path, genre,
                addedDate, lastPlayed, playCounter);
        return track;
    }

    public LinkedHashMap<String, Integer> getGenres(String where) {
        LinkedHashMap<String, Integer> genres = new LinkedHashMap<>();
        Cursor cursor = db.rawQuery("SELECT " + musicLibraryDb.COL_GENRE + ", count(*) " +
                " FROM " + musicLibraryDb.TABLE_TRACKS +
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
        try {
            Cursor cursor = db.rawQuery("SELECT count(*) FROM "+musicLibraryDb.TABLE_TRACKS +
                    " WHERE " + where, new String [] {});
            if (cursor.getCount() == 0)
                return 0;

            cursor.moveToFirst();
            Integer nb = cursor.getInt(0);
            cursor.close();
            return nb;
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getNb("+where+")", ex);
        }
        return -1;
    }

    public ArrayList<AbstractMap.SimpleEntry<Integer, String>> getTags() {
        ArrayList<AbstractMap.SimpleEntry<Integer, String>> tags = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT id, value FROM tag", new String [] {});
        if(cursor != null && cursor.moveToFirst())
        {
            do {
                tags.add(new AbstractMap.SimpleEntry<>(cursor.getInt(0), cursor.getString(1)));
            } while(cursor.moveToNext());
        }
        cursor.close();
        return tags;
    }

    public ArrayList<String> getTags(int idFile) {
        ArrayList<String> tags = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT value FROM tag T " +
                "JOIN tagFile F ON T.id=F.idTag " +
                "WHERE F.idFile=?", new String[] { String.valueOf(idFile)});

        if(cursor != null && cursor.moveToFirst())
        {
            do {
                tags.add(cursor.getString(0));
            } while(cursor.moveToNext());
        }
        cursor.close();
        return tags;
    }

    public boolean addTag(int idFile, String tag){
        try {
            int idTag=addTag(tag);
            if(idTag>0) {
                //Add the tag in tagfile
                ContentValues values = new ContentValues();
                values.put("idFile", idFile);
                values.put("idTag", idTag);
                db.insertWithOnConflict("tagfile", BaseColumns._ID, values,
                        SQLiteDatabase.CONFLICT_REPLACE);
                return true;
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "addTag("+idFile+","+tag+")", ex);
        }
        return false;
    }

    public int addTag(String tag) {
        int idTag=-1;
        try {
            //Add the tag in db if it does not exist
            ContentValues values = new ContentValues();
            values.put("value", tag);
            idTag = (int) db.insertWithOnConflict("tag", BaseColumns._ID, values,
                    SQLiteDatabase.CONFLICT_REPLACE);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "addTag("+tag+")", ex);
        }
        return idTag;
    }

    public boolean removeTag(int idFile, String tag){
        try {
            Cursor cursor = db.query("tag", null, "value=?",
                    new String[] { tag }, "", "", "");
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                Integer idTag = cursor.getInt(0);
                cursor.close();
                db.delete("tagfile", "idFile=? AND idTag=?",
                        new String[] { String.valueOf(idFile), String.valueOf(idTag) });
            }
            return true;
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "removeTag("+idFile+","+tag+")", ex);
        }
        return false;
    }
}
