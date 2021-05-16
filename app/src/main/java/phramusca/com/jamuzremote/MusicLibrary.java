package phramusca.com.jamuzremote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ADDED_DATE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ALBUM;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ALBUM_ARTIST;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ARTIST;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_BITRATE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_BPM;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_CHECKED_FLAG;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_COPYRIGHT;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_COVER_HASH;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_DISC_NO;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_DISC_TOTAL;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_FORMAT;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_GENRE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ID_PATH;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ID_REMOTE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ID_SERVER;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_LAST_PLAYED;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_LENGTH;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_MODIF_DATE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_PATH;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_PLAY_COUNTER;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_RATING;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_SIZE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_STATUS;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TITLE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACK_NO;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACK_TOTAL;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_YEAR;
import static phramusca.com.jamuzremote.MusicLibraryDb.TABLE_TRACKS;

//TODO: Use the COL_xxx everywhere !!

/**
 * Created by raph on 12/06/17.
 */
public class MusicLibrary {
    SQLiteDatabase db;
    private final File getAppDataPath;
    private final MusicLibraryDb musicLibraryDb;
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
        try (Cursor cursor = db.query(TABLE_TRACKS,
                new String[]{COL_ID_REMOTE},
                COL_PATH + " LIKE \"" + path + "\"",
                null, null, null, null)) {
            if (cursor.getCount() == 0) {
                return -1;
            }
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(COL_ID_REMOTE));
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getTrackIdFileRemote(" + path + ")", ex);
        }
        return -1;
    }

    private synchronized int getTrackIdFileRemote(int idFileServer) {
        try (Cursor cursor = db.query(TABLE_TRACKS,
                new String[]{COL_ID_REMOTE},
                COL_ID_SERVER + "=" + idFileServer,
                null, null, null, null)) {
            if (cursor.getCount() == 0) {
                return -1;
            }
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(COL_ID_REMOTE));
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getTrackIdFileRemote(" + idFileServer + ")", ex);
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
                    " LEFT JOIN tag ON tag.id=tagfile.idTag \n" +
                    " " + where + " \n" +
                    " GROUP BY tracks.idFileRemote \n" +
                    " " + having + " \n" +
                    " " + order + " \n" +
                    " " + (limit > 0 ? "LIMIT " + limit : "");
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

    public synchronized Triplet<Integer, Long, Long> getNb(String where, String having){
        Cursor cursor=null;
        try {
            String query = "SELECT count(*), SUM(size) AS sizeTotal, SUM(length) AS lengthTotal \n" +
                    " FROM (SELECT size, length FROM tracks \n" +
                    " LEFT JOIN tagfile ON tracks.idFileRemote=tagfile.idFile \n" +
                    " LEFT JOIN tag ON tag.id=tagfile.idTag \n"+
                    " " + where + " \n" +
                    " GROUP BY tracks.idFileRemote \n" +
                    " " + having + ")";
            cursor = db.rawQuery(query, new String [] {});

            if(cursor != null && cursor.moveToNext())
            {
                int count = cursor.getInt(0);
                long sizeTotal = cursor.getLong(1);
                long lengthTotal = cursor.getLong(2);
                return new Triplet<>(count, sizeTotal, lengthTotal);
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getNb("+where+","+having+")", ex);
        } finally {
            if(cursor!=null) {
                cursor.close();
            }
        }
        return new Triplet<>(-1, (long) -1, (long) -1);
    }

    synchronized boolean insertOrUpdateTrack(String absolutePath) {
        Track track = new Track(getAppDataPath, absolutePath);
        if(track.readMetadata()) {
            return insertOrUpdateTrack(track, false);
        }
        return false;
    }

    synchronized boolean insertOrUpdateTrack(Track track, boolean statsOnly) {
        int idFileRemote = getTrackIdFileRemote(track.getPath());
        boolean result;
        if(idFileRemote>=0) {
            track.setIdFileRemote(idFileRemote);
            //TODO, for user path only: update only if file is modified:
            //based on lastModificationDate and/or size (not on content as longer than updateTrack)
            Log.d(TAG, "updateTrack " + track.getPath());
            result=updateTrack(track, statsOnly);
        } else {
            Log.d(TAG, "insertTrack " + track.getPath());
            result=insertTrack(track);
        }
        return result;
    }

     synchronized boolean insertTrack(Track track){
        try {
            Log.d(TAG, "insertTrack " + track.getPath());
            int id = (int) db.insert(TABLE_TRACKS, null, TrackToValues(track, false));
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

    synchronized boolean insertTrackOrUpdateStatus(Collection<Track> tracks) {
        db.beginTransaction();
        try {
            String sqlTracks = "INSERT OR IGNORE INTO "+TABLE_TRACKS+" ("
                    +COL_TITLE+", "+COL_ALBUM+", "
                    +COL_ARTIST+", "+COL_STATUS+", "
                    +COL_GENRE+", "+COL_PATH+", "
                    +COL_RATING+", "+COL_ADDED_DATE+", "
                    +COL_LAST_PLAYED+", "+COL_PLAY_COUNTER+", "
                    +COL_ID_SERVER+", "+COL_SIZE+", "+COL_LENGTH+") " +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String sqlUpdateStatus = "UPDATE "+TABLE_TRACKS+" " +
                    "SET "+COL_STATUS+"=?, "+COL_PATH+"=? WHERE "+COL_ID_SERVER+"=?";
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
                    stmtStatus.bindString(2, track.getPath());
                    stmtStatus.bindLong(3, track.getIdFileServer());
                    stmtStatus.executeUpdateDelete();
                    stmtStatus.clearBindings();
                } else {
                    stmtTracks.bindString(1, track.getTitle());
                    stmtTracks.bindString(2, track.getAlbum());
                    stmtTracks.bindString(3, track.getArtist());
                    stmtTracks.bindString(4, track.getStatus().name());
                    stmtTracks.bindString(5, track.getGenre());
                    stmtTracks.bindString(6, track.getPath());
                    stmtTracks.bindDouble(7, track.getRating());
                    stmtTracks.bindString(8, track.getFormattedAddedDate());
                    stmtTracks.bindString(9, track.getFormattedLastPlayed());
                    stmtTracks.bindLong(10, track.getPlayCounter());
                    stmtTracks.bindLong(11, track.getIdFileServer());
                    stmtTracks.bindLong(12, track.getSize());
                    stmtTracks.bindLong(13, track.getLength());
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
            Log.e(TAG, "insertTrackOrUpdateStatus("+tracks+")", ex);
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

    synchronized boolean updateTrack(Track track, boolean statsOnly){
        try {
            if(track.getIdFileRemote()>=0 && db.update(TABLE_TRACKS, TrackToValues(track, statsOnly),
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

    synchronized int deleteTrack(int idFileServer){
        try {
            return db.delete(TABLE_TRACKS, COL_ID_SERVER + " = " + idFileServer, null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "deleteTrack("+idFileServer+")", ex);
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

    private synchronized ContentValues TrackToValues(Track track, boolean statsOnly) {
        ContentValues values = new ContentValues();
        values.put(COL_GENRE, track.getGenre());
        values.put(COL_RATING, track.getRating());
        values.put(COL_ADDED_DATE, track.getFormattedAddedDate());
        values.put(COL_LAST_PLAYED, track.getFormattedLastPlayed());
        values.put(COL_PLAY_COUNTER, track.getPlayCounter());
        if(!statsOnly) {
            //TODO: track.getIdFileServer() and track.getPath() are available if statsOnly
            values.put(COL_ID_SERVER, track.getIdFileServer());
            values.put(COL_PATH, track.getPath());

            values.put(COL_TITLE, track.getTitle());
            values.put(COL_ALBUM, track.getAlbum());
            values.put(COL_ARTIST, track.getArtist());
            values.put(COL_SIZE, track.getSize());
            values.put(COL_LENGTH, track.getLength());
            values.put(COL_STATUS, track.getStatus().name());
            values.put(COL_ID_PATH, track.getIdPath());
            values.put(COL_ALBUM_ARTIST, track.getAlbumArtist());
            values.put(COL_YEAR, track.getYear());
            values.put(COL_TRACK_NO, track.getTrackNo());
            values.put(COL_TRACK_TOTAL, track.getTrackTotal());
            values.put(COL_DISC_NO, track.getDiscNo());
            values.put(COL_DISC_TOTAL, track.getDiscTotal());
            values.put(COL_BITRATE, track.getBitrate());
            values.put(COL_FORMAT, track.getFormat());
            values.put(COL_BPM, track.getBPM());
            values.put(COL_MODIF_DATE, HelperDateTime.formatUTCtoSqlUTC(track.getModifDate()));
            values.put(COL_CHECKED_FLAG, track.getCheckedFlag());
            values.put(COL_COPYRIGHT, track.getCopyRight());
            values.put(COL_COVER_HASH, track.getCoverHash());
        }
        return values;
    }

    private synchronized Track cursorToTrack(Cursor c){
        int idFileRemote = c.getInt(c.getColumnIndex(COL_ID_REMOTE));
        int idFileServer = c.getInt(c.getColumnIndex(COL_ID_SERVER));
        double rating=c.getDouble(c.getColumnIndex(COL_RATING));
        String title=c.getString(c.getColumnIndex(COL_TITLE));
        String album=c.getString(c.getColumnIndex(COL_ALBUM));
        String artist=c.getString(c.getColumnIndex(COL_ARTIST));
        String status=c.getString(c.getColumnIndex(COL_STATUS));
        long size=c.getLong(c.getColumnIndex(COL_SIZE));
        int length=c.getInt(c.getColumnIndex(COL_LENGTH));
        String path=c.getString(c.getColumnIndex(COL_PATH));
        String genre=c.getString(c.getColumnIndex(COL_GENRE));
        Date addedDate=HelperDateTime.parseSqlUtc(
                c.getString(c.getColumnIndex(COL_ADDED_DATE)));
        Date lastPlayed=HelperDateTime.parseSqlUtc(
                c.getString(c.getColumnIndex(COL_LAST_PLAYED)));
        int playCounter=c.getInt(c.getColumnIndex(COL_PLAY_COUNTER));
        int idPath = c.getInt(c.getColumnIndex(COL_ID_PATH));
        String albumArtist=c.getString(c.getColumnIndex(COL_ALBUM_ARTIST));
        String year=c.getString(c.getColumnIndex(COL_YEAR));
        int trackNo = c.getInt(c.getColumnIndex(COL_TRACK_NO));
        int trackTotal = c.getInt(c.getColumnIndex(COL_TRACK_TOTAL));
        int discNo = c.getInt(c.getColumnIndex(COL_DISC_NO));
        int discTotal = c.getInt(c.getColumnIndex(COL_DISC_TOTAL));
        String bitRate=c.getString(c.getColumnIndex(COL_BITRATE));
        String format=c.getString(c.getColumnIndex(COL_FORMAT));
        double bpm=c.getDouble(c.getColumnIndex(COL_BPM));
        Date modifDate=HelperDateTime.parseSqlUtc(
                c.getString(c.getColumnIndex(COL_MODIF_DATE)));
        String checkedFlag=c.getString(c.getColumnIndex(COL_CHECKED_FLAG));
        String copyRight=c.getString(c.getColumnIndex(COL_COPYRIGHT));
        String coverHash=c.getString(c.getColumnIndex(COL_COVER_HASH));
        return new Track(idPath, albumArtist, year, trackNo, trackTotal, discNo, discTotal, bitRate,
                format, bpm, modifDate, checkedFlag, copyRight, getAppDataPath, idFileRemote,
                idFileServer, rating, title, album, artist, coverHash, path, genre, addedDate,
                lastPlayed, playCounter, status, size, length);
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
        try (Cursor cursor = db.query("tag", null, "value=?",
                new String[]{tag}, "", "", "")) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                idTag = cursor.getInt(0);
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getIdTag(" + tag + ")", ex);
        }
        return idTag;
    }

    synchronized boolean updateStatus(Track track){
        try {
            Log.d(TAG, "updateStatus("+track.getIdFileServer()+"): "+track.getStatus());
            ContentValues values = new ContentValues();
            values.put(COL_STATUS, track.getStatus().name());
            db.update(TABLE_TRACKS,
                    values,
                    COL_ID_SERVER + " = " +track.getIdFileServer(), null);
            return true;
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "updateStatus("+track.getIdFileServer()+"): "+track.getStatus(), ex);
        }
        return false;
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
        try (Cursor cursor = db.query("tracks", null, "artist=?",
                new String[]{artist}, "", "", "")) {
            if (cursor.getCount() > 0) {
                return true;
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getArtist(" + artist + ")", ex);
        }
        return false;
    }

    synchronized boolean getAlbum(String album){
        try (Cursor cursor = db.query("tracks", null, "album=?",
                new String[]{album}, "", "", "")) {
            if (cursor.getCount() > 0) {
                return true;
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getAlbum(" + album + ")", ex);
        }
        return false;
    }

    synchronized List<Track> getAlbums(int offset) {
        List<Track> tracks = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT count(" + COL_ID_REMOTE + ") AS " + COL_PLAY_COUNTER + ", \n" +
                    "round(avg(" + COL_RATING + "), 2) AS " + COL_RATING + ", \n" +
                    "group_concat(distinct " + COL_GENRE + ") AS " + COL_GENRE + ", \n" +
                    "group_concat(distinct " + COL_ARTIST + ") AS " + COL_ARTIST + ", \n" +
                    ", " + COL_ALBUM +
                    ", " + COL_ID_REMOTE +
                    ", " + COL_ID_SERVER +
                    ", " + COL_TITLE +
                    ", " + COL_ADDED_DATE +
                    ", " + COL_LAST_PLAYED +
                    ", " + COL_STATUS +
                    ", " + COL_SIZE +
                    ", " + COL_PATH +
                    ", " + COL_LENGTH +
                    ", " + COL_ID_PATH +
                    ", " + COL_ALBUM_ARTIST +
                    ", " + COL_YEAR +
                    ", " + COL_TRACK_NO +
                    ", " + COL_TRACK_TOTAL +
                    ", " + COL_DISC_NO +
                    ", " + COL_DISC_TOTAL +
                    ", " + COL_BITRATE +
                    ", " + COL_FORMAT +
                    ", " + COL_BPM +
                    ", " + COL_MODIF_DATE +
                    ", " + COL_CHECKED_FLAG +
                    ", " + COL_COPYRIGHT +
                    ", " + COL_COVER_HASH +
                    " FROM tracks \n" +
                    " GROUP BY " + COL_ALBUM + " " +
                    " ORDER BY " + COL_RATING + " DESC, " + COL_PLAY_COUNTER + " DESC, " + COL_ALBUM + ", " + COL_ARTIST + "" +
                    " LIMIT 20 OFFSET " +
                    offset;
            Log.i(TAG, query);
            cursor = db.rawQuery(query, new String[] { });
            tracks = getTracks(cursor);
            Log.i(TAG, "getAlbums(): "+tracks.size()+"//"+cursor.getCount());
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getAlbums()", ex);
        } finally {
            if(cursor!=null) {
                cursor.close();
            }
        }
        return tracks;
    }
}
