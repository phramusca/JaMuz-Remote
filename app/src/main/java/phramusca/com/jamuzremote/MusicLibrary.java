package phramusca.com.jamuzremote;
/**
 * Created by raph on 10/06/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ADDED_DATE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ALBUM;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ARTIST;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_COVER_HASH;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_GENRE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ID;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_LAST_PLAYED;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_PATH;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_PLAY_COUNTER;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_RATING;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TITLE;
import static phramusca.com.jamuzremote.MusicLibraryDb.TABLE_TRACKS;

public class MusicLibrary {

    SQLiteDatabase db;
    private MusicLibraryDb musicLibraryDb;
    private static final String TAG = MusicLibrary.class.getSimpleName();

    MusicLibrary(Context context){
        musicLibraryDb = new MusicLibraryDb(context);
    }

    public synchronized void open(){
        db = musicLibraryDb.getWritableDatabase();
    }

    public synchronized void close(){
        db.close();
    }

    synchronized int getTrack(String path){
        try {
            Cursor cursor = db.query(TABLE_TRACKS,
                    new String[] {COL_ID},
                    COL_PATH + " LIKE \"" + path +"\"",
                    null, null, null, null);
            if (cursor.getCount() == 0) {
                return -1;
            }
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
            cursor.close();
            return id;
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getTrack("+path+")", ex);
        }
        return -1;
    }

    synchronized ArrayList<Track> getTracks(String where, String having, String order) {
        ArrayList<Track> tracks = new ArrayList<>();
        try {
            String query = "SELECT GROUP_CONCAT(tag.value) AS tags, tracks.* \n" +
                    " FROM tracks \n" +
                    " LEFT JOIN tagfile ON tracks.ID=tagfile.idFile \n" +
                    " LEFT JOIN tag ON tag.id=tagfile.idTag \n"+
                    " " + where + " \n" +
                    " GROUP BY tracks.ID \n" +
                    " " + having + " \n" +
                    " " + order;
            Log.i(TAG, query);
            Cursor cursor = db.rawQuery(query, new String[] { });
            tracks = getTracks(cursor);
            Log.i(TAG, "getTracks: "+tracks.size()+"//"+cursor.getCount());
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getTracks("+where+","+having+","+order+")", ex);
        }
        return tracks;
    }

    public synchronized int getNb(String where, String having){
        Cursor cursor=null;
        try {
            String query = "SELECT * \n" +
                    " FROM tracks \n" +
                    " LEFT JOIN tagfile ON tracks.ID=tagfile.idFile \n" +
                    " LEFT JOIN tag ON tag.id=tagfile.idTag \n"+
                    " " + where + " \n" +
                    " GROUP BY tracks.ID \n" +
                    " " + having;
            cursor = db.rawQuery(query, new String [] {});
            return cursor.getCount();
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getNb("+where+","+having+")", ex);
        } finally {
            if(cursor!=null) {
                cursor.close();
            }
        }
        return -1;
    }

    public synchronized boolean insertTrack(Track track){
        try {
            int id = (int) db.insert(TABLE_TRACKS, null, TrackToValues(track));
            if(id>0) {
                track.setId(id);
                for(String tag : track.getTags(false)) {
                    if(!addTag(id, tag)) {
                        return false;
                    }
                }
                return true;
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "insertTrack("+track+")", ex);
        }
        return false;
    }

    public synchronized boolean updateTrack(Track track){
        try {
            if(db.update(TABLE_TRACKS, TrackToValues(track),
                    COL_ID + " = " +track.getId(), null)==1) {
                removeTags(track.getId());
                for(String tag : track.getTags(false)) {
                    if(!addTag(track.getId(), tag)) {
                        return false;
                    }
                }
                return true;
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "updateTrack("+track.getId()+","+track+")", ex);
        }
        return false;
    }

    public synchronized int deleteTrack(String path){
        try {
            return db.delete(TABLE_TRACKS, COL_PATH + " = \"" +path+"\"", null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "deleteTrack("+path+")", ex);
        }
        return -1;
    }

    private synchronized ArrayList<Track> getTracks(Cursor cursor) {
        ArrayList<Track> tracks = new ArrayList<>();
        if(cursor != null && cursor.moveToFirst())
        {
            do {
                Track track = cursorToTrack(cursor);
                tracks.add(track);
            } while(cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return tracks;
    }

    private synchronized ContentValues TrackToValues(Track track) {
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, track.getTitle());
        values.put(COL_ALBUM, track.getAlbum());
        values.put(COL_ARTIST, track.getArtist());
        values.put(COL_COVER_HASH, track.getCoverHash());
        values.put(COL_GENRE, track.getGenre());
        values.put(COL_PATH, track.getPath());
        values.put(COL_RATING, track.getRating());
        values.put(COL_ADDED_DATE, track.getFormattedAddedDate());
        values.put(COL_LAST_PLAYED, track.getFormattedLastPlayed());
        values.put(COL_PLAY_COUNTER, track.getPlayCounter());
        return values;
    }

    private synchronized Track cursorToTrack(Cursor c){

        int id = c.getInt(c.getColumnIndex(COL_ID));
        int rating=c.getInt(c.getColumnIndex(COL_RATING));
        String title=c.getString(c.getColumnIndex(COL_TITLE));
        String album=c.getString(c.getColumnIndex(COL_ALBUM));
        String artist=c.getString(c.getColumnIndex(COL_ARTIST));
        String coverHash=c.getString(c.getColumnIndex(COL_COVER_HASH));
        String path=c.getString(c.getColumnIndex(COL_PATH));
        String genre=c.getString(c.getColumnIndex(COL_GENRE));

        Date addedDate=HelperDateTime.parseSqlUtc(
                c.getString(c.getColumnIndex(COL_ADDED_DATE)));
        Date lastPlayed=HelperDateTime.parseSqlUtc(
                c.getString(c.getColumnIndex(COL_LAST_PLAYED)));
        int playCounter=c.getInt(c.getColumnIndex(COL_PLAY_COUNTER));
        return new Track(id, rating, title, album, artist, coverHash, path, genre,
                addedDate, lastPlayed, playCounter);
    }

    public synchronized List<String> getGenres() {
        List<String> genres = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT id, value FROM genre ORDER BY value", new String [] {});
        if(cursor != null && cursor.moveToFirst())
        {
            do {
                //int id = cursor.getInt(0);
                genres.add(cursor.getString(1));
            } while(cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return genres;
    }

    //FIXME TAGS: Order does not work

    public synchronized Map<Integer, String> getTags() {
        Map<Integer, String> tags = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT id, value FROM tag ORDER BY value", new String [] {});
        if(cursor != null && cursor.moveToFirst())
        {
            do {
                tags.put(cursor.getInt(0), cursor.getString(1));
            } while(cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return tags;
    }

    public synchronized ArrayList<String> getTags(int idFile) {
        ArrayList<String> tags = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT value FROM tag T " +
                "JOIN tagFile F ON T.id=F.idTag " +
                "WHERE F.idFile=? " +
                "ORDER BY value", new String[] { String.valueOf(idFile)});

        if(cursor != null && cursor.moveToFirst())
        {
            do {
                tags.add(cursor.getString(0));
            } while(cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return tags;
    }

    public synchronized boolean addTag(int idFile, String tag){
        try {
            int idTag=getIdTag(tag);
            if(idTag>0) {
                //Add the tag in tagfile
                ContentValues values = new ContentValues();
                values.put("idFile", idFile);
                values.put("idTag", idTag);
                db.insertWithOnConflict("tagfile", BaseColumns._ID, values, SQLiteDatabase.CONFLICT_IGNORE);
                return true;
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "addTag("+idFile+","+tag+")", ex);
        }
        return false;
    }

    public synchronized int addTag(String tag) {
        int idTag=-1;
        try {
            //Add the tag in db if it does not exist
            ContentValues values = new ContentValues();
            values.put("value", tag);
            idTag = (int) db.insertWithOnConflict("tag", BaseColumns._ID, values,
                    SQLiteDatabase.CONFLICT_IGNORE);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "addTag("+tag+")", ex);
        }
        return idTag;
    }

    public synchronized int deleteTag(int idTag){
        try {
            return db.delete("tag", "id = \"" +idTag+"\"", null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "deleteTag("+idTag+")", ex);
        }
        return -1;
    }

    public synchronized boolean removeTag(int idFile, String tag){
        try {
            int idTag=getIdTag(tag);
            if (idTag > 0) {
                db.delete("tagfile", "idFile=? AND idTag=?",
                        new String[] { String.valueOf(idFile), String.valueOf(idTag) });
            }
            return true;
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "removeTag("+idFile+","+tag+")", ex);
        }
        return false;
    }

    public synchronized boolean removeTags(int idFile){
        try {
            db.delete("tagfile", "idFile=?",
                    new String[] { String.valueOf(idFile) });
            return true;
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "removeTags("+idFile+")", ex);
        }
        return false;
    }

    private synchronized int getIdTag(String tag){
        int idTag=-1;
        try {
            Cursor cursor = db.query("tag", null, "value=?",
                    new String[] { tag }, "", "", "");
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                idTag = cursor.getInt(0);
                cursor.close();
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getIdTag("+tag+")", ex);
        }
        return idTag;
    }

    public synchronized int updateGenre(Track track){
        try {
            ContentValues values = new ContentValues();
            values.put(COL_GENRE, track.getGenre());

            return db.update(TABLE_TRACKS,
                    values,
                    COL_ID + " = " +track.getId(), null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "updateGenre("+track.getId()+","+track+")", ex);
        }
        return -1;
    }

    public synchronized boolean addGenre(String genre) {
        try {
            //Add the genre in db if it does not exist
            ContentValues values = new ContentValues();
            values.put("value", genre);
            db.insertWithOnConflict("genre", BaseColumns._ID, values,
                    SQLiteDatabase.CONFLICT_IGNORE);
            return true;
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "addGenre("+genre+")", ex);
            return false;
        }
    }

    public synchronized int deleteGenre(String genre) {
        try {
            return db.delete("genre", "value = \"" +genre+"\"", null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "deleteGenre("+genre+")", ex);
        }
        return -1;
    }

    /**
     * @param getAppDataPath Application Folder to exclude from deletion
     * @param userPath User path (new one) to exclude from deletion
     * @return
     */
    public synchronized int deleteTrack(File getAppDataPath, String userPath){
        try {
            return db.delete(TABLE_TRACKS,
                    COL_PATH+" NOT LIKE \""+getAppDataPath.getAbsolutePath()+"%\" " +
                    "AND "+COL_PATH+" NOT LIKE \""+userPath+"%\"", null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "deleteTrack("+getAppDataPath.getAbsolutePath()+"\", \""+userPath+"\")", ex);
        }
        return -1;
    }

    public synchronized boolean getArtist(String artist){
        try {
            Cursor cursor = db.query("tracks", null, "artist=?",
                    new String[] { artist }, "", "", "");
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getArtist("+artist+")", ex);
        }
        return false;
    }

    public synchronized boolean getAlbum(String album){
        try {
            Cursor cursor = db.query("tracks", null, "album=?",
                    new String[] { album }, "", "", "");
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getAlbum("+album+")", ex);
        }
        return false;
    }
}
