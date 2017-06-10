package phramusca.com.jamuzremote;

/**
 * Created by raph on 10/06/17.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class MusicLibrary {

    private SQLiteDatabase db;
    private MusicLibraryDb musicLibraryDb;

    public MusicLibrary(Context context){
        musicLibraryDb = new MusicLibraryDb(context, null);
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

    public ArrayList<Track> getTracks() {

        ArrayList<Track> tracks = new ArrayList<>();

        Cursor cursor = db.query(musicLibraryDb.TABLE_TRACKS,
                null,
                null,
                null, null, null, null);

        if(cursor != null && cursor.moveToFirst())
        {
            do {
                tracks.add(cursorToTrack(cursor));
            } while(cursor.moveToNext());
        }
        cursor.close();
        return tracks;
    }

    public long insertTrack(Track track){
        return db.insert(musicLibraryDb.TABLE_TRACKS, null, TrackToValues(track, true));
    }

    public int updateTrack(int id, Track track, boolean setRating){
        return db.update(musicLibraryDb.TABLE_TRACKS, TrackToValues(track, false), musicLibraryDb.COL_ID + " = " +id, null);
    }

    public int deleteTrack(int id){
        return db.delete(musicLibraryDb.TABLE_TRACKS, musicLibraryDb.COL_ID + " = " +id, null);
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
}
