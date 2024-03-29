package phramusca.com.jamuzremote;

import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_ADDED_DATE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_ALBUM;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_ALBUM_ARTIST;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_ALBUM_GAIN;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_ARTIST;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_BITRATE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_BPM;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_CHECKED_FLAG;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_COMMENT;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_COPYRIGHT;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_COVER_HASH;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_DISC_NO;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_DISC_TOTAL;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_FORMAT;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_GENRE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_GENRE_VALUE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_ID_PATH;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_ID_REMOTE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_ID_SERVER;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_LAST_PLAYED;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_LENGTH;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_MODIF_DATE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_PATH;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_PATH_MB_ID;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_PATH_MODIF_DATE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_PLAY_COUNTER;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_RATING;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_SIZE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_STATUS;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TAG_FILE_ID_FILE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TAG_FILE_ID_TAG;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TAG_ID;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TAG_VALUE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_TITLE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_TRACK_GAIN;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_TRACK_NO;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_TRACK_TOTAL;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_TRACKS_YEAR;
import static phramusca.com.jamuzremote.MusicLibraryDb.TABLE_GENRE;
import static phramusca.com.jamuzremote.MusicLibraryDb.TABLE_TAG;
import static phramusca.com.jamuzremote.MusicLibraryDb.TABLE_TAG_FILE;
import static phramusca.com.jamuzremote.MusicLibraryDb.TABLE_TRACKS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by raph on 12/06/17.
 */
public class MusicLibrary {
    SQLiteDatabase db;
    private final File getAppDataPath;
    private final MusicLibraryDb musicLibraryDb;
    private static final String TAG = MusicLibrary.class.getName();
    private static final List<String> NOT_SUPPORTED_FORMATS =
            Arrays.asList("audio/alac");

    MusicLibrary(Context context, File getAppDataPath, File musicLibraryDbFile) {
        this.getAppDataPath = getAppDataPath;
        musicLibraryDb = new MusicLibraryDb(context, musicLibraryDbFile);
    }

    public synchronized void open() {
        db = musicLibraryDb.getWritableDatabase();
    }

    synchronized void close() {
        db.close();
    }

    private synchronized AbstractMap.SimpleEntry<Integer, Date> getTrackIdFileRemote(String path) {
        try (Cursor cursor = db.query(TABLE_TRACKS,
                new String[]{ COL_TRACKS_ID_REMOTE, COL_TRACKS_MODIF_DATE },
                COL_TRACKS_PATH + " LIKE \"" + path + "\"", //NON-NLS
                null, null, null, null)) {
            if (cursor.getCount() == 0) {
                return new AbstractMap.SimpleEntry<>(-1, new Date(0));
            }
            cursor.moveToFirst();
            Date modifDate = HelperDateTime.parseSqlUtc(
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_TRACKS_MODIF_DATE)));
            return new AbstractMap.SimpleEntry<>(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRACKS_ID_REMOTE)), modifDate);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getTrackIdFileRemote(" + path + ")", ex); //NON-NLS
        }
        return new AbstractMap.SimpleEntry<>(-1, new Date(0));
    }

    private synchronized int getTrackIdFileRemote(int idFileServer) {
        try (Cursor cursor = db.query(TABLE_TRACKS,
                new String[]{ COL_TRACKS_ID_REMOTE },
                COL_TRACKS_ID_SERVER + "=" + idFileServer,
                null, null, null, null)) {
            if (cursor.getCount() == 0) {
                return -1;
            }
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRACKS_ID_REMOTE));
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getTrackIdFileRemote(" + idFileServer + ")", ex); //NON-NLS
        }
        return -1;
    }

    List<Track> getTracks(String where, String having, String order, int limit) {
        return getTracks(false, where, having, order, limit);
    }

    List<Track> getTracks(boolean statsOnly, String where, String having, String order, int limit) {
        Cursor cursor = getTracksCursor(statsOnly, where, having, order, limit);
        List<Track> tracks = getTracks(cursor, statsOnly);
        if (limit > 0) {
            Collections.shuffle(tracks);
        }
        if (cursor != null) {
            cursor.close();
        }
        return tracks;
    }

    Cursor getTracksCursor(boolean statsOnly, String where, String having, String order, int limit) {
        Cursor cursor = null;
        try {
            String select = "GROUP_CONCAT("+TABLE_TAG+"."+COL_TAG_VALUE+") AS tags, "+TABLE_TRACKS+".*"; //NON-NLS
            if (statsOnly) {
                select = TABLE_TRACKS+"."+ COL_TRACKS_ID_REMOTE +", "+TABLE_TRACKS+"."+ COL_TRACKS_ID_SERVER +", " +
                        TABLE_TRACKS+"."+ COL_TRACKS_RATING +", "+TABLE_TRACKS+"."+ COL_TRACKS_ADDED_DATE +", " +
                        TABLE_TRACKS+"."+ COL_TRACKS_LAST_PLAYED +", "+TABLE_TRACKS+"."+ COL_TRACKS_PLAY_COUNTER +", " +
                        TABLE_TRACKS+"."+ COL_TRACKS_GENRE +", "+TABLE_TRACKS+"."+ COL_TRACKS_PATH +", " +
                        TABLE_TRACKS+"."+ COL_TRACKS_SIZE +", "+TABLE_TRACKS+"."+ COL_TRACKS_STATUS +", " +
                        TABLE_TRACKS+"."+ COL_TRACKS_LENGTH +", "+TABLE_TRACKS+"."+ COL_TRACKS_ID_PATH +", "+TABLE_TRACKS+"."+ COL_TRACKS_MODIF_DATE; //NON-NLS
            } //NON-NLS
            String query = "SELECT " + select + " \n" + //NON-NLS
                    " FROM "+TABLE_TRACKS+" \n" + //NON-NLS
                    " LEFT JOIN "+TABLE_TAG_FILE+" ON "+TABLE_TRACKS+"."+ COL_TRACKS_ID_REMOTE +"="+TABLE_TAG_FILE+"."+COL_TAG_FILE_ID_FILE+" \n" + //NON-NLS //NON-NLS
                    " LEFT JOIN "+TABLE_TAG+" ON "+TABLE_TAG+"."+COL_TAG_ID+"="+TABLE_TAG_FILE+"."+COL_TAG_FILE_ID_TAG+" \n" + //NON-NLS
                    " " + where + " \n" +
                    " GROUP BY tracks.idFileRemote \n" + //NON-NLS
                    " " + having + " \n" +
                    " " + order + " \n" + //NON-NLS
                    " " + (limit > 0 ? "LIMIT " + limit : ""); //NON-NLS
            Log.i(TAG, query);
            cursor = db.rawQuery(query, new String[]{});
            Log.i(TAG, "getTracks(" + where + "," + having + "," + order + "): " + cursor.getCount()); //NON-NLS //NON-NLS
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getTracks(" + where + "," + having + "," + order + ")", ex); //NON-NLS
        }
        return cursor;
    }

    public Triplet<Integer, Long, Long> getNb(String where, String having) {
        Cursor cursor = null;
        try {
            String query = "SELECT count(*), SUM("+ COL_TRACKS_SIZE +") AS sizeTotal, SUM("+ COL_TRACKS_LENGTH +") AS lengthTotal \n" + //NON-NLS
                    " FROM (SELECT " + COL_TRACKS_SIZE + ", "+ COL_TRACKS_LENGTH +" FROM "+TABLE_TRACKS+" \n" + //NON-NLS
                    " LEFT JOIN "+TABLE_TAG_FILE+" ON "+TABLE_TRACKS+".idFileRemote=tagfile.idFile \n" + //NON-NLS
                    " LEFT JOIN "+TABLE_TAG+" ON "+TABLE_TAG+"."+COL_TAG_ID+"="+TABLE_TAG_FILE+"."+COL_TAG_FILE_ID_TAG+" \n" + //NON-NLS
                    " " + where + " \n" + //NON-NLS
                    " GROUP BY "+TABLE_TRACKS+"."+ COL_TRACKS_ID_REMOTE +" \n" + //NON-NLS
                    " " + having + ")";
            cursor = db.rawQuery(query, new String[]{});
            if (cursor != null && cursor.moveToNext()) {
                int count = cursor.getInt(0);
                long sizeTotal = cursor.getLong(1);
                long lengthTotal = cursor.getLong(2);
                return new Triplet<>(count, sizeTotal, lengthTotal);
            }
        } catch (SQLiteException | IllegalStateException ex) { //NON-NLS
            Log.e(TAG, "getNb(" + where + "," + having + ")", ex); //NON-NLS
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return new Triplet<>(-1, (long) -1, (long) -1);
    }

    synchronized boolean insertOrUpdateTrack(Uri contentUri, Context context, String idPath, Date dateModified, long size) {
        Track track = new Track(contentUri.toString(), idPath);
        track.setModifDate(dateModified);
        track.setSize(size);
        if (track.read(context) && !NOT_SUPPORTED_FORMATS.contains(track.getFormat())) {
            return insertOrUpdateTrack(track, dateModified);
        }
        return false;
    }

    synchronized boolean insertOrUpdateTrack(Track track, Date dateModified) {
        AbstractMap.SimpleEntry<Integer, Date> fileRemote = getTrackIdFileRemote(track.getPath());
        boolean result;
        if (fileRemote.getKey() >= 0) {
            track.setIdFileRemote(fileRemote.getKey());
            if(dateModified.after(fileRemote.getValue())) {
                result = updateTrack(track, false);
            } else {
                return true;
            }
        } else {
            result = insertTrack(track);
        }
        return result;
    } //NON-NLS

    synchronized boolean insertTrack(Track track) {
        try { //NON-NLS
            Log.d(TAG, "insertTrack " + track.getPath()); //NON-NLS
            int id = (int) db.insert(TABLE_TRACKS, null, TrackToValues(track, false));
            if (id > 0) {
                track.setIdFileRemote(id);
                for (String tag : track.getTags(false)) {
                    if (!addTag(id, tag)) {
                        return false;
                    } //NON-NLS
                }
                return true;
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "insertTrack(" + track + ")", ex); //NON-NLS
        }
        return false;
    }

    synchronized boolean insertTrackOrUpdateStatus(Collection<Track> tracks) { //NON-NLS
        db.beginTransaction();
        try {
            String sqlTracks = "INSERT OR IGNORE INTO " + TABLE_TRACKS + " (" //NON-NLS
                    + COL_TRACKS_TITLE + ", " + COL_TRACKS_ALBUM + ", "
                    + COL_TRACKS_ARTIST + ", " + COL_TRACKS_STATUS + ", "
                    + COL_TRACKS_GENRE + ", " + COL_TRACKS_PATH + ", "
                    + COL_TRACKS_RATING + ", " + COL_TRACKS_ADDED_DATE + ", "
                    + COL_TRACKS_LAST_PLAYED + ", " + COL_TRACKS_PLAY_COUNTER + ", "
                    + COL_TRACKS_ID_SERVER + ", " + COL_TRACKS_SIZE + ", " + COL_TRACKS_LENGTH + ") " + //NON-NLS //NON-NLS
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; //NON-NLS
            String sqlUpdateStatus = "UPDATE " + TABLE_TRACKS + " " + //NON-NLS
                    "SET " + COL_TRACKS_STATUS + "=?, " + COL_TRACKS_PATH + "=? WHERE " + COL_TRACKS_ID_SERVER + "=?"; //NON-NLS
            String sqlTagsDelete = "DELETE FROM "+TABLE_TAG_FILE+" WHERE "+COL_TAG_FILE_ID_FILE+"=?"; //NON-NLS //NON-NLS
            String sqlTags = "INSERT OR REPLACE INTO "+TABLE_TAG_FILE+" ("+COL_TAG_FILE_ID_FILE+ ", "+ COL_TAG_FILE_ID_TAG + ") " + //NON-NLS
                    "VALUES (?, (SELECT "+COL_TAG_ID+" FROM "+TABLE_TAG+" WHERE "+COL_TAG_VALUE+"=?))"; //NON-NLS
            SQLiteStatement stmtTracks = db.compileStatement(sqlTracks);
            SQLiteStatement stmtStatus = db.compileStatement(sqlUpdateStatus);
            SQLiteStatement stmtTags = db.compileStatement(sqlTags);
            SQLiteStatement stmtTagsDelete = db.compileStatement(sqlTagsDelete);
            for (Track track : tracks) {
                int idFile = getTrackIdFileRemote(track.getIdFileServer());
                if (idFile >= 0) {
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
                for (String tag : track.getTags(false)) {
                    stmtTags.bindLong(1, idFile);
                    stmtTags.bindString(2, tag);
                    stmtTags.execute();
                    stmtTags.clearBindings();
                } //NON-NLS
            }
            db.setTransactionSuccessful();
            return true;
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "insertTrackOrUpdateStatus(" + tracks + ")", ex); //NON-NLS
            return false;
        } finally {
            db.endTransaction();
        }
    }

    synchronized boolean addTag(int idFile, String tag) {
        try {
            int idTag = getIdTag(tag);
            if (idTag > 0) {
                ContentValues values = new ContentValues();
                values.put(COL_TAG_FILE_ID_FILE, idFile);
                values.put(COL_TAG_FILE_ID_TAG, idTag); //NON-NLS
                db.insertWithOnConflict("tagfile", BaseColumns._ID, values, SQLiteDatabase.CONFLICT_IGNORE); //NON-NLS
                return true;
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "addTag(" + idFile + "," + tag + ")", ex); //NON-NLS
        }
        return false;
    }

    synchronized boolean updateTrack(Track track, boolean statsOnly) {
        try {
            if (track.getIdFileRemote() >= 0 && db.update(TABLE_TRACKS, TrackToValues(track, statsOnly),
                    COL_TRACKS_ID_REMOTE + " = " + track.getIdFileRemote(), null) == 1) {
                removeTags(track.getIdFileRemote());
                for (String tag : track.getTags(false)) {
                    if (!addTag(track.getIdFileRemote(), tag)) { //NON-NLS
                        return false;
                    }
                }
                return true;
            }
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "updateTrack(" + track.getIdFileRemote() + "," + track + ")", ex); //NON-NLS
        }
        return false;
    }

    synchronized int deleteTrack(String path) {
        try {
            return db.delete(TABLE_TRACKS, COL_TRACKS_PATH + " = \"" + path + "\"", null); //NON-NLS
        } catch (SQLiteException | IllegalStateException ex) { //NON-NLS //NON-NLS
            Log.e(TAG, "deleteTrack(" + path + ")", ex); //NON-NLS
        }
        return -1;
    }

    synchronized int deleteTrack(int idFileServer) {
        try {
            return db.delete(TABLE_TRACKS, COL_TRACKS_ID_SERVER + " = " + idFileServer, null);
        } catch (SQLiteException | IllegalStateException ex) { //NON-NLS //NON-NLS
            Log.e(TAG, "deleteTrack(" + idFileServer + ")", ex); //NON-NLS
        }
        return -1;
    }

    private List<Track> getTracks(Cursor cursor, boolean statsOnly) {
        List<Track> tracks = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Track track = cursorToTrack(cursor, statsOnly);
                tracks.add(track);
            } while (cursor.moveToNext());
        }
        return tracks;
    }

    private synchronized ContentValues TrackToValues(Track track, boolean statsOnly) {
        ContentValues values = new ContentValues();
        values.put(COL_TRACKS_GENRE, track.getGenre());
        values.put(COL_TRACKS_RATING, track.getRating());
        values.put(COL_TRACKS_ADDED_DATE, track.getFormattedAddedDate());
        values.put(COL_TRACKS_LAST_PLAYED, track.getFormattedLastPlayed());
        values.put(COL_TRACKS_PLAY_COUNTER, track.getPlayCounter());
        if (!statsOnly) {
            values.put(COL_TRACKS_ID_SERVER, track.getIdFileServer());
            values.put(COL_TRACKS_PATH, track.getPath());
            values.put(COL_TRACKS_TITLE, track.getTitle());
            values.put(COL_TRACKS_ALBUM, track.getAlbum());
            values.put(COL_TRACKS_ARTIST, track.getArtist());
            values.put(COL_TRACKS_SIZE, track.getSize());
            values.put(COL_TRACKS_LENGTH, track.getLength());
            values.put(COL_TRACKS_STATUS, track.getStatus().name());
            values.put(COL_TRACKS_ID_PATH, track.getIdPath());
            values.put(COL_TRACKS_ALBUM_ARTIST, track.getAlbumArtist());
            values.put(COL_TRACKS_YEAR, track.getYear());
            values.put(COL_TRACKS_TRACK_NO, track.getTrackNo());
            values.put(COL_TRACKS_TRACK_TOTAL, track.getTrackTotal());
            values.put(COL_TRACKS_DISC_NO, track.getDiscNo());
            values.put(COL_TRACKS_DISC_TOTAL, track.getDiscTotal());
            values.put(COL_TRACKS_BITRATE, track.getBitrate());
            values.put(COL_TRACKS_FORMAT, track.getFormat());
            values.put(COL_TRACKS_BPM, track.getBPM());
            values.put(COL_TRACKS_MODIF_DATE, HelperDateTime.formatUTCtoSqlUTC(track.getModifDate()));
            values.put(COL_TRACKS_CHECKED_FLAG, track.getCheckedFlag());
            values.put(COL_TRACKS_COPYRIGHT, track.getCopyRight());
            values.put(COL_TRACKS_COVER_HASH, track.getCoverHash());
            values.put(COL_TRACKS_PATH_MODIF_DATE, HelperDateTime.formatUTCtoSqlUTC(track.getPathModifDate()));
            values.put(COL_TRACKS_PATH_MB_ID, track.getPathMbId());
            values.put(COL_TRACKS_COMMENT, track.getComment());
            ReplayGain.GainValues gainValues = track.getReplayGain();
            values.put(COL_TRACKS_TRACK_GAIN, gainValues.getTrackGain());
            values.put(COL_TRACKS_ALBUM_GAIN, gainValues.getAlbumGain());
        }
        return values;
    }

    public Track cursorToTrack(Cursor c, boolean statsOnly) {
        int idFileRemote = c.getInt(c.getColumnIndexOrThrow(COL_TRACKS_ID_REMOTE));
        int idFileServer = c.getInt(c.getColumnIndexOrThrow(COL_TRACKS_ID_SERVER));
        double rating = c.getDouble(c.getColumnIndexOrThrow(COL_TRACKS_RATING));
        String status = c.getString(c.getColumnIndexOrThrow(COL_TRACKS_STATUS));
        long size = c.getLong(c.getColumnIndexOrThrow(COL_TRACKS_SIZE));
        int length = c.getInt(c.getColumnIndexOrThrow(COL_TRACKS_LENGTH));
        String path = c.getString(c.getColumnIndexOrThrow(COL_TRACKS_PATH));
        String genre = c.getString(c.getColumnIndexOrThrow(COL_TRACKS_GENRE));
        Date addedDate = HelperDateTime.parseSqlUtc(
                c.getString(c.getColumnIndexOrThrow(COL_TRACKS_ADDED_DATE)));
        Date lastPlayed = HelperDateTime.parseSqlUtc(
                c.getString(c.getColumnIndexOrThrow(COL_TRACKS_LAST_PLAYED)));
        int playCounter = c.getInt(c.getColumnIndexOrThrow(COL_TRACKS_PLAY_COUNTER));
        String idPath = c.getString(c.getColumnIndexOrThrow(COL_TRACKS_ID_PATH));
        Date modifDate = HelperDateTime.parseSqlUtc(
                c.getString(c.getColumnIndexOrThrow(COL_TRACKS_MODIF_DATE)));

        String title = "";
        String album = "";
        String artist = "";
        String albumArtist = "";
        String year = "";
        int trackNo = -1;
        int trackTotal = -1;
        int discNo = -1;
        int discTotal = -1;
        String bitRate = "";
        String format = "";
        double bpm = -1;
        String checkedFlag = "";
        String copyRight = "";
        String coverHash = "";
        Date pathModifDate = new Date(0);
        String pathMbid = "";
        String comment = "";
        float trackGain = -1;
        float albumGain = -1;

        if (!statsOnly) {
            title = c.getString(c.getColumnIndexOrThrow(COL_TRACKS_TITLE));
            album = c.getString(c.getColumnIndexOrThrow(COL_TRACKS_ALBUM));
            artist = c.getString(c.getColumnIndexOrThrow(COL_TRACKS_ARTIST));
            albumArtist = c.getString(c.getColumnIndexOrThrow(COL_TRACKS_ALBUM_ARTIST));
            year = c.getString(c.getColumnIndexOrThrow(COL_TRACKS_YEAR));
            trackNo = c.getInt(c.getColumnIndexOrThrow(COL_TRACKS_TRACK_NO));
            trackTotal = c.getInt(c.getColumnIndexOrThrow(COL_TRACKS_TRACK_TOTAL));
            discNo = c.getInt(c.getColumnIndexOrThrow(COL_TRACKS_DISC_NO));
            discTotal = c.getInt(c.getColumnIndexOrThrow(COL_TRACKS_DISC_TOTAL));
            bitRate = c.getString(c.getColumnIndexOrThrow(COL_TRACKS_BITRATE));
            format = c.getString(c.getColumnIndexOrThrow(COL_TRACKS_FORMAT));
            bpm = c.getDouble(c.getColumnIndexOrThrow(COL_TRACKS_BPM));
            checkedFlag = c.getString(c.getColumnIndexOrThrow(COL_TRACKS_CHECKED_FLAG));
            copyRight = c.getString(c.getColumnIndexOrThrow(COL_TRACKS_COPYRIGHT));
            coverHash = c.getString(c.getColumnIndexOrThrow(COL_TRACKS_COVER_HASH));
            pathModifDate = HelperDateTime.parseSqlUtc(
                    c.getString(c.getColumnIndexOrThrow(COL_TRACKS_PATH_MODIF_DATE)));
            pathMbid = c.getString(c.getColumnIndexOrThrow(COL_TRACKS_PATH_MB_ID));
            comment = c.getString(c.getColumnIndexOrThrow(COL_TRACKS_COMMENT));
            trackGain = c.getFloat(c.getColumnIndexOrThrow(COL_TRACKS_TRACK_GAIN));
            albumGain = c.getFloat(c.getColumnIndexOrThrow(COL_TRACKS_ALBUM_GAIN));
        }

        //TODO Use below in sync or merge processes (DO NOT store in db, or values from remote)
//        boolean deleted=c.getString(c.getColumnIndexOrThrow(COL_));
//        int previousPlayCounter=c.getInt(c.getColumnIndexOrThrow(COL_));
//        Date genreModifDate=c.getString(c.getColumnIndexOrThrow(COL_));
//        Date tagsModifDate=c.getString(c.getColumnIndexOrThrow(COL_));
//        Date ratingModifDate=c.getString(c.getColumnIndexOrThrow(COL_));

        return new Track(pathModifDate, pathMbid, comment, idPath, albumArtist, year,
                trackNo, trackTotal, discNo, discTotal, bitRate, format, bpm, modifDate, checkedFlag,
                copyRight, getAppDataPath, idFileRemote, idFileServer, rating, title, album, artist,
                coverHash, path, genre, addedDate, lastPlayed, playCounter, status, size, length, //NON-NLS
                trackGain, albumGain);
    }

    List<String> getGenres() {
        List<String> genres = new ArrayList<>();
        Cursor cursor = db.query(TABLE_GENRE, new String[]{ COL_GENRE_VALUE },"",null, null, null, COL_GENRE_VALUE);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                genres.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return genres;
    }

    public Map<Integer, String> getTags() {
        Map<Integer, String> tags = new LinkedHashMap<>();
        Cursor cursor = db.query(TABLE_TAG, new String[]{ COL_TAG_ID, COL_TAG_VALUE },"",null, null, null, COL_TAG_VALUE);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                tags.put(cursor.getInt(0), cursor.getString(1));
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return tags;
    }

    ArrayList<String> getTags(int idFile) {
        ArrayList<String> tags = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT "+COL_TAG_VALUE+" FROM "+TABLE_TAG+" T " + //NON-NLS //NON-NLS //NON-NLS
                "JOIN "+TABLE_TAG_FILE+" F ON T."+COL_TAG_ID+"=F."+COL_TAG_FILE_ID_TAG+" " + //NON-NLS //NON-NLS
                "WHERE F."+COL_TAG_FILE_ID_FILE+"=? " + //NON-NLS
                "ORDER BY "+COL_TAG_VALUE, new String[]{String.valueOf(idFile)}); //NON-NLS
        if (cursor != null && cursor.moveToFirst()) {
            do {
                tags.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return tags;
    }

    synchronized int addTag(String tag) {
        int idTag = -1;
        try {
            //Add the tag in db if it does not exist
            ContentValues values = new ContentValues(); //NON-NLS //NON-NLS //NON-NLS //NON-NLS //NON-NLS
            values.put(COL_TAG_VALUE, tag); //NON-NLS
            idTag = (int) db.insertWithOnConflict(TABLE_TAG, BaseColumns._ID, values, //NON-NLS
                    SQLiteDatabase.CONFLICT_IGNORE);
        } catch (SQLiteException | IllegalStateException ex) { //NON-NLS //NON-NLS
            Log.e(TAG, "addTag(" + tag + ")", ex); //NON-NLS
        }
        return idTag; //NON-NLS
    }

    synchronized int deleteTag(int idTag) { //NON-NLS
        try {
            return db.delete(TABLE_TAG, COL_TAG_ID+" = \"" + idTag + "\"", null); //NON-NLS
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "deleteTag(" + idTag + ")", ex); //NON-NLS //NON-NLS
        }
        return -1;
    }

    synchronized boolean removeTag(int idFile, String tag) {
        try {
            int idTag = getIdTag(tag);
            if (idTag > 0) { //NON-NLS
                db.delete(TABLE_TAG_FILE, COL_TAG_FILE_ID_FILE+"=? AND "+COL_TAG_FILE_ID_TAG+"=?", //NON-NLS
                        new String[]{String.valueOf(idFile), String.valueOf(idTag)});
            }
            return true; //NON-NLS //NON-NLS
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "removeTag(" + idFile + "," + tag + ")", ex); //NON-NLS
        }
        return false;
    }

    private synchronized boolean removeTags(int idFile) { //NON-NLS
        try {
            db.delete(TABLE_TAG_FILE, COL_TAG_FILE_ID_FILE+"=?", //NON-NLS
                    new String[]{String.valueOf(idFile)});
            return true;
        } catch (SQLiteException | IllegalStateException ex) { //NON-NLS //NON-NLS
            Log.e(TAG, "removeTags(" + idFile + ")", ex); //NON-NLS
        }
        return false;
    }
 //NON-NLS
    private synchronized int getIdTag(String tag) { //NON-NLS
        int idTag = -1;
        try (Cursor cursor = db.query(TABLE_TAG, new String[]{ COL_TAG_ID }, COL_TAG_VALUE+"=?", //NON-NLS //NON-NLS
                new String[]{tag}, "", "", "")) {
            if (cursor.getCount() > 0) { //NON-NLS
                cursor.moveToFirst();
                idTag = cursor.getInt(0);
            }
        } catch (SQLiteException | IllegalStateException ex) { //NON-NLS //NON-NLS //NON-NLS //NON-NLS
            Log.e(TAG, "getIdTag(" + tag + ")", ex); //NON-NLS
        }
        return idTag; //NON-NLS
    }

    synchronized boolean updateStatus(Track track) {
        try {
            Log.d(TAG, "updateStatus(" + track.getIdFileServer() + "): " + track.getStatus()); //NON-NLS
            ContentValues values = new ContentValues();
            values.put(COL_TRACKS_STATUS, track.getStatus().name());
            db.update(TABLE_TRACKS,
                    values,
                    COL_TRACKS_ID_SERVER + " = " + track.getIdFileServer(), null);
            return true;
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "updateStatus(" + track.getIdFileServer() + "): " + track.getStatus(), ex); //NON-NLS
        }
        return false;
    }

    synchronized int updateGenre(Track track) {
        try {
            ContentValues values = new ContentValues();
            values.put(COL_TRACKS_GENRE, track.getGenre());
            return db.update(TABLE_TRACKS, //NON-NLS
                    values,
                    COL_TRACKS_ID_REMOTE + " = " + track.getIdFileRemote(), null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "updateGenre(" + track.getIdFileRemote() + ")", ex); //NON-NLS //NON-NLS
        }
        return -1;
    }

    synchronized int updateRating(Track track) {
        try {
            ContentValues values = new ContentValues();
            values.put(COL_TRACKS_RATING, track.getRating());
            return db.update(TABLE_TRACKS, //NON-NLS
                    values,
                    COL_TRACKS_ID_REMOTE + " = " + track.getIdFileRemote(), null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "updateRating(" + track.getIdFileRemote() + ")", ex); //NON-NLS //NON-NLS
        }
        return -1;
    }

    synchronized int updatePlayCounterAndLastPlayed(Track track) {
        try {
            ContentValues values = new ContentValues();
            values.put(COL_TRACKS_PLAY_COUNTER, track.getPlayCounter());
            values.put(COL_TRACKS_LAST_PLAYED, track.getFormattedLastPlayed());
            return db.update(TABLE_TRACKS, //NON-NLS
                    values,
                    COL_TRACKS_ID_REMOTE + " = " + track.getIdFileRemote(), null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "updatePlayCounterAndLastPlayed(" + track.getIdFileRemote() + ")", ex); //NON-NLS //NON-NLS
        }
        return -1;
    }

    synchronized boolean addGenre(String genre) { //NON-NLS
        try {
            //Add the genre in db if it does not exist //NON-NLS //NON-NLS //NON-NLS
            ContentValues values = new ContentValues(); //NON-NLS //NON-NLS
            values.put(COL_GENRE_VALUE, genre); //NON-NLS
            db.insertWithOnConflict(TABLE_GENRE, BaseColumns._ID, values, //NON-NLS
                    SQLiteDatabase.CONFLICT_IGNORE);
            return true; //NON-NLS //NON-NLS
        } catch (SQLiteException | IllegalStateException ex) { //NON-NLS //NON-NLS
            Log.e(TAG, "addGenre(" + genre + ")", ex); //NON-NLS
            return false; //NON-NLS
        }
    }

    synchronized int deleteGenre(String genre) { //NON-NLS //NON-NLS //NON-NLS
        try {
            return db.delete(TABLE_GENRE, COL_GENRE_VALUE+" = \"" + genre + "\"", null); //NON-NLS //NON-NLS
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "deleteGenre(" + genre + ")", ex); //NON-NLS //NON-NLS //NON-NLS
        }
        return -1;
    }

    /**
     * @param getAppDataPath Application Folder to exclude from deletion
     * @param userPath       User path (new one) to exclude from deletion //NON-NLS
     * @return the number of rows affected.
     */
    synchronized int deleteTrack(File getAppDataPath, String userPath) { //NON-NLS //NON-NLS
        try {
            return db.delete(TABLE_TRACKS,
                    COL_TRACKS_PATH + " NOT LIKE \"" + getAppDataPath.getAbsolutePath() + "%\" " + //NON-NLS
                            "AND " + COL_TRACKS_PATH + " NOT LIKE \"" + userPath + "%\"", null); //NON-NLS
        } catch (SQLiteException | IllegalStateException ex) { //NON-NLS
            Log.e(TAG, "deleteTrack(" + getAppDataPath.getAbsolutePath() + "\", \"" + userPath + "\")", ex); //NON-NLS
        }
        return -1;
    }

    Cursor getAlbums() {
        return getAlbums(""); //NON-NLS
    }

    Cursor getAlbums(String search) {
        Cursor cursor = null;
        ArrayList<Track.Status> statuses = ActivityMain.getScope(true);
        if(statuses.size()>0) {
            try {
                String query = "SELECT status, count(" + COL_TRACKS_ID_REMOTE + ") AS " + COL_TRACKS_PLAY_COUNTER + ", \n" + //NON-NLS //NON-NLS //NON-NLS
                        "round(avg(" + COL_TRACKS_RATING + "), 2) AS " + COL_TRACKS_RATING + ", \n" + //NON-NLS //NON-NLS //NON-NLS
                        "group_concat(distinct " + COL_TRACKS_GENRE + ") AS " + COL_TRACKS_GENRE + ", \n" + //NON-NLS //NON-NLS //NON-NLS //NON-NLS
                        "group_concat(distinct " + COL_TRACKS_ARTIST + ") AS " + COL_TRACKS_ARTIST + ", \n" + //NON-NLS //NON-NLS
                        COL_TRACKS_ALBUM + ", " + COL_TRACKS_COVER_HASH + ", " + COL_TRACKS_PATH + ", " + COL_TRACKS_ID_PATH + " \n"+ //NON-NLS
                        "FROM tracks \n" +//NON-NLS
                        " WHERE " + Playlist.getWhereStatus(statuses) +
                        (search.isEmpty()?"":
                                " AND (" + COL_TRACKS_ALBUM + " LIKE \"%"+search+"%\" " + //NON-NLS //NON-NLS //NON-NLS
                                "OR " + COL_TRACKS_ARTIST + " LIKE \"%"+search+"%\" " + //NON-NLS //NON-NLS
                                "OR " + COL_TRACKS_ALBUM_ARTIST + " LIKE \"%"+search+"%\" " + //NON-NLS
                                "OR " + COL_TRACKS_TITLE + " LIKE \"%"+search+"%\") \n") + //NON-NLS
                        " GROUP BY " + COL_TRACKS_ID_PATH + " \n" + //NON-NLS //NON-NLS
                        " HAVING MIN(CASE status WHEN 'REC' THEN 1 WHEN 'LOCAL' THEN 2 ELSE 3 END) \n" +
                        " ORDER BY " + COL_TRACKS_RATING + " DESC, " + COL_TRACKS_PLAY_COUNTER + " DESC, " + COL_TRACKS_ALBUM + ", " + COL_TRACKS_ARTIST; //NON-NLS
                Log.i(TAG, query);
                cursor = db.rawQuery(query, new String[]{}); //NON-NLS
                Log.i(TAG, "getAlbums(): " + cursor.getCount()); //NON-NLS //NON-NLS
            } catch (SQLiteException | IllegalStateException ex) { //NON-NLS
                Log.e(TAG, "getAlbums()", ex); //NON-NLS
            }
        }
        return cursor;
    }
}
