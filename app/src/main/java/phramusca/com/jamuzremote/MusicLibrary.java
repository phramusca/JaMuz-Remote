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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MusicLibrary {

    private SQLiteDatabase db;
    private MusicLibraryDb musicLibraryDb;
    private static final String TAG = MusicLibrary.class.getSimpleName();

    public MusicLibrary(Context context){
        musicLibraryDb = new MusicLibraryDb(context);
    }

    public synchronized void open(){
        db = musicLibraryDb.getWritableDatabase();
    }

    public synchronized void close(){
        db.close();
    }

    public synchronized void receive(InputStream inputStream) throws IOException {
        DataInputStream dis = new DataInputStream(
                new BufferedInputStream(inputStream));
        double fileSize = dis.readLong();
        FileOutputStream fos = new FileOutputStream(
                MainActivity.musicLibraryDbFile);
        // TODO: Find best. Make a benchmark
        //https://stackoverflow.com/questions/8748960/how-do-you-decide-what-byte-size-to-use-for-inputstream-read
        byte[] buf = new byte[8192];
        int bytesRead;
        while (fileSize > 0 && (bytesRead = dis.read(buf, 0, (int) Math.min(buf.length, fileSize))) != -1) {
            fos.write(buf, 0, bytesRead);
            fileSize -= bytesRead;
        }
        fos.close();
    }

    public synchronized int getTrack(String path){
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

    public synchronized ArrayList<Track> getTracks(String query, String order) {
        ArrayList<Track> tracks = new ArrayList<>();
        try {
            Cursor cursor = db.query(musicLibraryDb.TABLE_TRACKS,
                    null,
                    query,
                    null, null, null,
                    order, null);

            tracks = getTracks(cursor);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getTracks(\""+query+"\", \""+order+"\")", ex);
        }
        return tracks;
    }

    public synchronized ArrayList<Track> getTracks(String where, String having, String order) {
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
        try {
            String query = "SELECT * \n" +
                    " FROM tracks \n" +
                    " LEFT JOIN tagfile ON tracks.ID=tagfile.idFile \n" +
                    " LEFT JOIN tag ON tag.id=tagfile.idTag \n"+
                    " " + where + " \n" +
                    " GROUP BY tracks.ID \n" +
                    " " + having;
            Cursor cursor = db.rawQuery(query, new String [] {});
            return cursor.getCount();
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "getNb("+where+","+having+")", ex);
        }
        return -1;
    }

    public synchronized long insertTrack(Track track){
        try {
            if(db.insert(musicLibraryDb.TABLE_TRACKS, null, TrackToValues(track))<0) {
                return -1;
            }
            for(String tag : track.getTags()) {
                addTag(track.getId(), tag);
            }

        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "insertTrack("+track+")", ex);
        }
        return -1;
    }

    public synchronized int updateTrack(Track track){
        try {
            return db.update(musicLibraryDb.TABLE_TRACKS, TrackToValues(track), musicLibraryDb.COL_ID + " = " +track.getId(), null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "updateTrack("+track.getId()+","+track+")", ex);
        }
        return -1;
    }

    public synchronized int deleteTrack(String path){
        try {
            return db.delete(musicLibraryDb.TABLE_TRACKS, musicLibraryDb.COL_PATH + " = \"" +path+"\"", null);
        } catch (SQLiteException | IllegalStateException ex) {
            Log.e(TAG, "deleteTrack("+path+")", ex);
        }
        return -1;
    }

    private ArrayList<Track> getTracks(Cursor cursor) {
        ArrayList<Track> tracks = new ArrayList<>();
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

    public synchronized LinkedHashMap<String, Integer> getGenres(String where) {
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

    public synchronized List<String> getGenres() {
        List<String> genres = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT id, value FROM genre", new String [] {});
        if(cursor != null && cursor.moveToFirst())
        {
            do {
                //int id = cursor.getInt(0);
                genres.add(cursor.getString(1));
            } while(cursor.moveToNext());
        }
        cursor.close();
        return genres;
    }

    public synchronized int getNb(String where){
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

    public synchronized Map<Integer, String> getTags() {
        Map<Integer, String> tags = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT id, value FROM tag", new String [] {});
        if(cursor != null && cursor.moveToFirst())
        {
            do {
                tags.put(cursor.getInt(0), cursor.getString(1));
            } while(cursor.moveToNext());
        }
        cursor.close();
        return tags;
    }

    public synchronized ArrayList<String> getTags(int idFile) {
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

    public synchronized boolean addTag(int idFile, String tag){
        try {
            int idTag=getIdTag(tag);
            if(idTag>0) {
                //Add the tag in tagfile
                ContentValues values = new ContentValues();
                values.put("idFile", idFile);
                values.put("idTag", idTag);
                db.insertWithOnConflict("tagfile", BaseColumns._ID, values,
                        SQLiteDatabase.CONFLICT_IGNORE);
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
            values.put(musicLibraryDb.COL_GENRE, track.getGenre());

            return db.update(musicLibraryDb.TABLE_TRACKS,
                    values,
                    musicLibraryDb.COL_ID + " = " +track.getId(), null);
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
}
