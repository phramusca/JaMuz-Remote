package phramusca.com.jamuzkids;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static phramusca.com.jamuzkids.MusicLibraryDb.COL_ADDED_DATE;
import static phramusca.com.jamuzkids.MusicLibraryDb.COL_ALBUM;
import static phramusca.com.jamuzkids.MusicLibraryDb.COL_ARTIST;
import static phramusca.com.jamuzkids.MusicLibraryDb.COL_GENRE;
import static phramusca.com.jamuzkids.MusicLibraryDb.COL_ID_REMOTE;
import static phramusca.com.jamuzkids.MusicLibraryDb.COL_ID_SERVER;
import static phramusca.com.jamuzkids.MusicLibraryDb.COL_LAST_PLAYED;
import static phramusca.com.jamuzkids.MusicLibraryDb.COL_PATH;
import static phramusca.com.jamuzkids.MusicLibraryDb.COL_PLAY_COUNTER;
import static phramusca.com.jamuzkids.MusicLibraryDb.COL_RATING;
import static phramusca.com.jamuzkids.MusicLibraryDb.COL_SIZE;
import static phramusca.com.jamuzkids.MusicLibraryDb.COL_STATUS;
import static phramusca.com.jamuzkids.MusicLibraryDb.COL_TITLE;
import static phramusca.com.jamuzkids.MusicLibraryDb.TABLE_TRACKS;

/**
 * Created by raph on 12/06/17.
 */
public class MusicLibrary {

    SQLiteDatabase db;
    private final File getAppDataPath;
    private MusicLibraryDb musicLibraryDb;
    private static final String TAG = MusicLibrary.class.getName();

    MusicLibrary(File getAppDataPath, Context context){
        this.getAppDataPath = getAppDataPath;
        musicLibraryDb = new MusicLibraryDb(context);
    }

    public synchronized void open(){
        db = musicLibraryDb.getWritableDatabase();
    }

    synchronized void close(){
        db.close();
    }

    private synchronized int getTrackIdFileRemote(String path){
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_TRACKS,
                    new String[] {COL_ID_REMOTE},
                    COL_PATH + " LIKE \"" + path +"\"",
                    null, null, null, null);
            if (cursor.getCount() == 0) {
                return -1;
            }
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(COL_ID_REMOTE));
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getTrackIdFileRemote("+path+")", ex);
        } finally {
            if(cursor!=null) {
                cursor.close();
            }
        }
        return -1;
    }

    private synchronized int getTrackIdFileRemote(int idFileServer) {
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_TRACKS,
                    new String[] {COL_ID_REMOTE},
                    COL_ID_SERVER+"=" + idFileServer,
                    null, null, null, null);
            if (cursor.getCount() == 0) {
                return -1;
            }
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(COL_ID_REMOTE));
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getTrackIdFileRemote("+idFileServer+")", ex);
        } finally {
            if(cursor!=null) {
                cursor.close();
            }
        }
        return -1;
    }

    synchronized List<Track> getTracks(String where, String having, String order, int limit) {
        List<Track> tracks = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT GROUP_CONCAT(tag.value) AS tags, tracks.* \n" +
                    " FROM tracks \n" +
                    " LEFT JOIN tagfile ON tracks.idFileRemote=tagfile.idFile \n" +
                    " LEFT JOIN tag ON tag.id=tagfile.idTag \n"+
                    " " + where + " \n" +
                    " GROUP BY tracks.idFileRemote \n" +
                    " " + having + " \n" +
                    " " + order + " \n" +
                    " " + (limit>0?"LIMIT "+limit:"");
            Log.i(TAG, query);
            cursor = db.rawQuery(query, new String[] { });
            tracks = getTracks(cursor);
            if(limit>0) {
                Collections.shuffle(tracks);
            }
            Log.i(TAG, "getTracks("+where+","+having+","+order+"): "+tracks.size()+"//"+cursor.getCount());
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getTracks("+where+","+having+","+order+")", ex);
        } finally {
            if(cursor!=null) {
                cursor.close();
            }
        }
        return tracks;
    }

    synchronized List<Track> getTracks(Track.Status status) {
        return getTracks(status, false);
    }

    synchronized List<Track> getTracks(Track.Status status, boolean negative) {
        List<Track> tracks = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT GROUP_CONCAT(tag.value) AS tags, tracks.* \n" +
                    " FROM tracks \n" +
                    " LEFT JOIN tagfile ON tracks.idFileRemote=tagfile.idFile \n" +
                    " LEFT JOIN tag ON tag.id=tagfile.idTag \n"+
                    " WHERE " + COL_STATUS + " "+(negative?"!":"")+"= \""+status.name()+"\" \n"+
                    " GROUP BY tracks.idFileRemote";
            Log.i(TAG, query);
            cursor = db.rawQuery(query, new String[] { });
            tracks = getTracks(cursor);
            Log.i(TAG, "getTracks(): "+tracks.size()+"//"+cursor.getCount());
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getTracks()", ex);
        } finally {
            if(cursor!=null) {
                cursor.close();
            }
        }
        return tracks;
    }

    public synchronized Pair<Integer, Long> getNb(String where, String having){
        Cursor cursor=null;
        try {
            String query = "SELECT count(*), SUM(size) AS sizeTotal \n" +
                    " FROM (SELECT size FROM tracks \n" +
                    " LEFT JOIN tagfile ON tracks.idFileRemote=tagfile.idFile \n" +
                    " LEFT JOIN tag ON tag.id=tagfile.idTag \n"+
                    " " + where + " \n" +
                    " GROUP BY tracks.idFileRemote \n" +
                    " " + having + ")";
            cursor = db.rawQuery(query, new String [] {});

            if(cursor != null && cursor.moveToNext())
            {
                int count = cursor.getInt(0); //cursor.getCount()
                long sizeTotal = cursor.getLong(1); //cursor.getLong(cursor.getColumnIndex("sizeTotal"));
                //FIXME: Introduce length in tracks table
                //to be able to:
                // SELECT SUM(length) AS lengthTotal
                //long lengthTotal = cursor.getLong(3);

                return new Pair<>(count, sizeTotal);
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getNb("+where+","+having+")", ex);
        } finally {
            if(cursor!=null) {
                cursor.close();
            }
        }
        return new Pair<>(-1, (long) -1);
    }

    synchronized boolean insertOrUpdateTrackInDatabase(String absolutePath) {
        Track track = new Track(getAppDataPath, absolutePath);
        track.readTags();
        return insertOrUpdateTrackInDatabase(track);
    }

    synchronized boolean insertOrUpdateTrackInDatabase(Track track) {
        int idFileRemote = getTrackIdFileRemote(track.getPath());
        boolean result;
        if(idFileRemote>=0) {
            track.setIdFileRemote(idFileRemote);
            //TODO, for user path only: update only if file is modified:
            //based on lastModificationDate and/or size (not on content as longer than updateTrack)
            Log.d(TAG, "updateTrack " + track.getPath());
            result=updateTrack(track);
        } else {
            Log.d(TAG, "insertTrack " + track.getPath());
            result=insertTrack(track);
        }
        return result;
    }

    private synchronized boolean insertTrack(Track track){
        try {
            int id = (int) db.insert(TABLE_TRACKS, null, TrackToValues(track));
            if(id>0) {
                track.setIdFileRemote(id);
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

    synchronized boolean insertTracks(Collection<Track> tracks) {
        db.beginTransaction();
        try {
            String sqlTracks = "INSERT OR IGNORE INTO "+TABLE_TRACKS+" ("
                    +COL_TITLE+", "+COL_ALBUM+", "
                    +COL_ARTIST+", "+COL_STATUS+", "
                    +COL_GENRE+", "+COL_PATH+", "
                    +COL_RATING+", "+COL_ADDED_DATE+", "
                    +COL_LAST_PLAYED+", "+COL_PLAY_COUNTER+", "
                    +COL_ID_SERVER+", "+COL_SIZE+") " +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String sqlUpdateStatus = "UPDATE "+TABLE_TRACKS+" " +
                    "SET "+COL_STATUS+"=? WHERE "+COL_ID_SERVER+"=?";
            String sqlTagsDelete = "DELETE FROM tagFile WHERE idFile=?";
            String sqlTags = "INSERT OR REPLACE INTO tagfile (idFile, idTag) " +
                    "VALUES (?, (SELECT id FROM tag WHERE value=?))";
            SQLiteStatement stmtTracks = db.compileStatement(sqlTracks);
            SQLiteStatement stmtStatus = db.compileStatement(sqlUpdateStatus);
            SQLiteStatement stmtTags = db.compileStatement(sqlTags);
            SQLiteStatement stmtTagsDelete = db.compileStatement(sqlTagsDelete);
            for (Track track : tracks) {
                int idFile = getTrackIdFileRemote(track.getIdFileServer());
                if(idFile>=0) {
                    stmtStatus.bindString(1, track.getStatus().name());
                    stmtStatus.bindLong(2, track.getIdFileServer());
                    stmtStatus.executeUpdateDelete();
                    stmtStatus.clearBindings();
                } else {
                    stmtTracks.bindString(1, track.getTitle());
                    stmtTracks.bindString(2, track.getAlbum());
                    stmtTracks.bindString(3, track.getArtist());
                    stmtTracks.bindString(4, track.getStatus().name());
                    stmtTracks.bindString(5, track.getGenre());
                    stmtTracks.bindString(6, track.getPath());
                    stmtTracks.bindLong(7, track.getRating());
                    stmtTracks.bindString(8, track.getFormattedAddedDate());
                    stmtTracks.bindString(9, track.getFormattedLastPlayed());
                    stmtTracks.bindLong(10, track.getPlayCounter());
                    stmtTracks.bindLong(11, track.getIdFileServer());
                    stmtTracks.bindLong(12, track.getSize());
                    idFile = (int) stmtTracks.executeInsert();
                    stmtTracks.clearBindings();
                }
                stmtTagsDelete.bindLong(1, idFile);
                stmtTagsDelete.execute();
                stmtTagsDelete.clearBindings();
                for(String tag : track.getTags(false)) {
                    stmtTags.bindLong(1, idFile);
                    stmtTags.bindString(2, tag);
                    stmtTags.execute();
                    stmtTags.clearBindings();
                }
            }
            db.setTransactionSuccessful();
            return true;
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "insertTracks("+tracks+")", ex);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    synchronized boolean addTag(int idFile, String tag){
        try {
            int idTag=getIdTag(tag);
            if(idTag>0) {
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

    synchronized boolean updateTrack(Track track){
        try {
            if(db.update(TABLE_TRACKS, TrackToValues(track),
                    COL_ID_REMOTE + " = " +track.getIdFileRemote(), null)==1) {
                removeTags(track.getIdFileRemote());
                for(String tag : track.getTags(false)) {
                    if(!addTag(track.getIdFileRemote(), tag)) {
                        return false;
                    }
                }
                return true;
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "updateTrack("+track.getIdFileRemote()+","+track+")", ex);
        }
        return false;
    }

    synchronized int deleteTrack(String path){
        try {
            return db.delete(TABLE_TRACKS, COL_PATH + " = \"" +path+"\"", null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "deleteTrack("+path+")", ex);
        }
        return -1;
    }

    private synchronized List<Track> getTracks(Cursor cursor) {
        List<Track> tracks = new ArrayList<>();
        if(cursor != null && cursor.moveToFirst())
        {
            do {
                Track track = cursorToTrack(cursor);
                tracks.add(track);
            } while(cursor.moveToNext());
        }
        return tracks;
    }

    private synchronized ContentValues TrackToValues(Track track) {
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, track.getTitle());
        values.put(COL_ALBUM, track.getAlbum());
        values.put(COL_ARTIST, track.getArtist());
        values.put(COL_SIZE, track.getSize());
        values.put(COL_STATUS, track.getStatus().name());
        values.put(COL_GENRE, track.getGenre());
        values.put(COL_PATH, track.getPath());
        values.put(COL_RATING, track.getRating());
        values.put(COL_ADDED_DATE, track.getFormattedAddedDate());
        values.put(COL_LAST_PLAYED, track.getFormattedLastPlayed());
        values.put(COL_PLAY_COUNTER, track.getPlayCounter());
        return values;
    }

    private synchronized Track cursorToTrack(Cursor c){

        int idFileRemote = c.getInt(c.getColumnIndex(COL_ID_REMOTE));
        int idFileServer = c.getInt(c.getColumnIndex(COL_ID_SERVER));
        int rating=c.getInt(c.getColumnIndex(COL_RATING));
        String title=c.getString(c.getColumnIndex(COL_TITLE));
        String album=c.getString(c.getColumnIndex(COL_ALBUM));
        String artist=c.getString(c.getColumnIndex(COL_ARTIST));
        String status=c.getString(c.getColumnIndex(COL_STATUS));
        long size=c.getLong(c.getColumnIndex(COL_SIZE));
        String path=c.getString(c.getColumnIndex(COL_PATH));
        String genre=c.getString(c.getColumnIndex(COL_GENRE));

        Date addedDate=HelperDateTime.parseSqlUtc(
                c.getString(c.getColumnIndex(COL_ADDED_DATE)));
        Date lastPlayed=HelperDateTime.parseSqlUtc(
                c.getString(c.getColumnIndex(COL_LAST_PLAYED)));
        int playCounter=c.getInt(c.getColumnIndex(COL_PLAY_COUNTER));
        return new Track(getAppDataPath, idFileRemote, idFileServer, rating, title, album, artist,
                "coverHash", path, genre,
                addedDate, lastPlayed, playCounter, status, size);
    }

    synchronized List<String> getGenres() {
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

    public synchronized Map<Integer, String> getTags() {
        Map<Integer, String> tags = new LinkedHashMap<>();
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

    synchronized ArrayList<String> getTags(int idFile) {
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

    synchronized int addTag(String tag) {
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

    synchronized int deleteTag(int idTag){
        try {
            return db.delete("tag", "id = \"" +idTag+"\"", null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "deleteTag("+idTag+")", ex);
        }
        return -1;
    }

    synchronized boolean removeTag(int idFile, String tag){
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

    private synchronized boolean removeTags(int idFile){
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
        Cursor cursor = null;
        try {
            cursor = db.query("tag", null, "value=?",
                    new String[] { tag }, "", "", "");
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                idTag = cursor.getInt(0);
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getIdTag("+tag+")", ex);
        } finally {
            if(cursor!=null) {
                cursor.close();
            }
        }
        return idTag;
    }

    synchronized int updateStatus(Track track){
        try {
            ContentValues values = new ContentValues();
            values.put(COL_STATUS, track.getStatus().name());
            return db.update(TABLE_TRACKS,
                    values,
                    COL_ID_SERVER + " = " +track.getIdFileServer(), null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "updateStatus("+track.getIdFileServer()+")", ex);
        }
        return -1;
    }

    /**
     * Set Status DEL where Status!="NULL"
     * @return the number of rows affected
     */
    synchronized int updateStatus(){
        try {
            ContentValues values = new ContentValues();
            values.put(COL_STATUS, Track.Status.DEL.name());
            return db.update(TABLE_TRACKS,
                    values,
                    COL_STATUS+"!=\"NULL\"", null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "updateStatus()", ex);
        }
        return -1;
    }

    synchronized int updateGenre(Track track){

        try {
            ContentValues values = new ContentValues();
            values.put(COL_GENRE, track.getGenre());
            return db.update(TABLE_TRACKS,
                    values,
                    COL_ID_REMOTE + " = " +track.getIdFileRemote(), null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "updateGenre("+track.getIdFileRemote()+")", ex);
        }
        return -1;
    }

    synchronized boolean addGenre(String genre) {
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

    synchronized int deleteGenre(String genre) {
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
     * @return the number of rows affected if a whereClause is passed in, 0 otherwise.
     * To remove all rows and get a count pass "1" as the whereClause.
     */
    synchronized int deleteTrack(File getAppDataPath, String userPath){
        try {
            return db.delete(TABLE_TRACKS,
                    COL_PATH+" NOT LIKE \""+getAppDataPath.getAbsolutePath()+"%\" " +
                    "AND "+COL_PATH+" NOT LIKE \""+userPath+"%\"", null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "deleteTrack("+getAppDataPath.getAbsolutePath()+"\", \""+userPath+"\")", ex);
        }
        return -1;
    }

    synchronized boolean getArtist(String artist){
        Cursor cursor = null;
        try {
            cursor = db.query("tracks", null, "artist=?",
                    new String[] { artist }, "", "", "");
            if (cursor.getCount() > 0) {
                return true;
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getArtist("+artist+")", ex);
        } finally {
            if(cursor!=null) {
                cursor.close();
            }
        }
        return false;
    }

    synchronized boolean getAlbum(String album){
        Cursor cursor = null;
        try {
            cursor = db.query("tracks", null, "album=?",
                    new String[] { album }, "", "", "");
            if (cursor.getCount() > 0) {
                return true;
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getAlbum("+album+")", ex);
        } finally {
            if(cursor!=null) {
                cursor.close();
            }
        }
        return false;
    }
}
