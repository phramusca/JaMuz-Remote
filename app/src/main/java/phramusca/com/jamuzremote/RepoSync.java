package phramusca.com.jamuzremote;

import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ADDED_DATE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_GENRE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ID_PATH;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ID_REMOTE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ID_SERVER;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_LAST_PLAYED;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_LENGTH;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_PATH;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_PLAY_COUNTER;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_RATING;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_SIZE;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_STATUS;

import android.database.Cursor;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;
import android.util.Pair;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author phramusca
 */
public final class RepoSync {
    private static final String TAG = RepoSync.class.getName();

    private static Table<Integer, Track.Status, Integer> tracks = null;
    private static Cursor tracksCursor;

    protected synchronized static void read() {
        tracks = HashBasedTable.create();
        tracksCursor = HelperLibrary.musicLibrary.getTracksCursor(true,
                "WHERE status!=\"" + Track.Status.LOCAL.name() + "\"",
                "",
                "",
                -1);
        if (tracksCursor != null && tracksCursor.moveToFirst()) {
            do {
                int idFileServer = tracksCursor.getInt(tracksCursor.getColumnIndex(COL_ID_SERVER));
                String status = tracksCursor.getString(tracksCursor.getColumnIndex(COL_STATUS));
                tracks.put(idFileServer, Track.Status.valueOf(status), tracksCursor.getPosition());
            } while (tracksCursor.moveToNext());
        }

        // DO NOT FIXME: Try converting Cursor to ContentValues)
        // => TAKES WAY TOO LONG AND MEMORY LEAK
        //  -> it can be modified and use for db insert/update
        //      so could be used in a RepoTrack and RepoAlbums maybe
        //  -> no need for AdapterCursorAlbumTrack.newStatuses
        //  https://stackoverflow.com/a/8709408/755759

//        java.lang.OutOfMemoryError: Failed to allocate a 44 byte allocation with 2952456 free bytes and 2MB until OOM; failed due to fragmentation (required continguous free 4096 bytes for a new buffer where largest contiguous free 0 bytes)
//        at java.util.HashMap.inflateTable(HashMap.java:287)
//        at java.util.HashMap.put(HashMap.java:419)
//        at android.content.ContentValues.put(ContentValues.java:96)
//        at android.database.DatabaseUtils.cursorRowToContentValues(DatabaseUtils.java:740)
//        at phramusca.com.jamuzremote.RepoSync.read(RepoSync.java:32)
        Cursor c = HelperLibrary.musicLibrary.getTracksCursor(true, "WHERE status!=\"" + Track.Status.LOCAL.name() + "\"", "", "", -1);
        ArrayList<ContentValues> retVal = new ArrayList<ContentValues>();
        ContentValues map;
        if(c.moveToFirst()) {
            do {
                map = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(c, map);
                retVal.add(map);
            } while(c.moveToNext());
        }
        c.close();
    }

    /**
     * Sets status to REC if track exists and has correct size.
     * Otherwise, file is deleted and status set back to NEW
     *
     * @param track the one to check
     */
    public synchronized static void checkReceivedFile(Track track) {
        track.setStatus(Track.Status.REC);
        if (!checkFile(track) || !HelperLibrary.musicLibrary.updateStatus(track)) {
            File receivedFile = new File(track.getPath());
            Log.w(TAG, "Error with received file. Deleting " + receivedFile.getAbsolutePath()); //NON-NLS
            //noinspection ResultOfMethodCallIgnored
            receivedFile.delete();
            track.setStatus(Track.Status.NEW);
        }
        update(track);
    }

    /**
     * @param track the one to check
     * @return true if receivedFile.length() = size
     */
    public synchronized static boolean checkFile(Track track) {
        File receivedFile = new File(track.getPath());
        if (receivedFile.exists()) {
            if (receivedFile.length() == track.getSize()) {
                Log.i(TAG, "Correct file size: " + receivedFile.length()); //NON-NLS
                return true;
            } else {
                Log.w(TAG, "File has wrong size. Deleting " + receivedFile.getAbsolutePath()); //NON-NLS
                //noinspection ResultOfMethodCallIgnored
                receivedFile.delete();
            }
        } else {
            Log.w(TAG, "File does not exits. " + receivedFile.getAbsolutePath()); //NON-NLS
        }
        return false;
    }

    public synchronized static void update(Track track) {
        if (tracks != null && tracks.containsRow(track.getIdFileServer())) {
            int position = tracks.row(track.getIdFileServer()).values().iterator().next();
            tracks.row(track.getIdFileServer()).clear();
            tracks.put(track.getIdFileServer(), track.getStatus(), position);
        }
    }

    public synchronized static Cursor getTrackCursor(int idFileServer) {
        if (tracks.containsRow(idFileServer)) {
            int position = tracks.row(idFileServer).values().iterator().next();
            if (tracksCursor.moveToPosition(position)) {
                return tracksCursor;
            }
        }
        return null;
    }

    public synchronized static Pair<Integer, String> getMergeList(File getAppDataPath) throws JSONException {
        Collection<Integer> mergeList = tracks.column(Track.Status.REC).values();
        JSONArray filesToMerge = new JSONArray();
        for(int position : tracks.column(Track.Status.REC).values()) {
            if (tracksCursor.moveToPosition(position)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("idFile", tracksCursor.getInt(tracksCursor.getColumnIndex(COL_ID_SERVER))); //NON-NLS
                jsonObject.put("path", tracksCursor.getString(tracksCursor.getColumnIndex(COL_PATH)).substring(getAppDataPath.getAbsolutePath().length() + 1)); //NON-NLS
                jsonObject.put("rating", (int) tracksCursor.getDouble(tracksCursor.getColumnIndex(COL_RATING))); //NON-NLS
                jsonObject.put("addedDate", tracksCursor.getString(tracksCursor.getColumnIndex(COL_ADDED_DATE)));
                jsonObject.put("lastPlayed", tracksCursor.getString(tracksCursor.getColumnIndex(COL_LAST_PLAYED))); //NON-NLS
                jsonObject.put("playCounter", tracksCursor.getInt(tracksCursor.getColumnIndex(COL_PLAY_COUNTER))); //NON-NLS
                jsonObject.put("genre", tracksCursor.getString(tracksCursor.getColumnIndex(COL_GENRE))); //NON-NLS
                JSONArray tagsAsMap = new JSONArray();
                //FIXME: Get tags in tracksCursor (csv group_concat(tag) => List<String>) to avoid another db query
                List<String> tags = HelperLibrary.musicLibrary.getTags(tracksCursor.getInt(tracksCursor.getColumnIndex(COL_ID_REMOTE)));
                for (String tag : tags) { //NON-NLS
                    tagsAsMap.put(tag);
                }
                jsonObject.put("tags", tagsAsMap); //NON-NLS
                filesToMerge.put(jsonObject);
            }
        }
        JSONObject obj = new JSONObject();
        obj.put("type", "FilesToMerge");
        obj.put("files", filesToMerge); //NON-NLS
        return new Pair<>(mergeList.size(), obj.toString());
    }

    public synchronized static List<Track> getDownloadList() {
        List<Track> list = new ArrayList<>();
        for(int position : tracks.column(Track.Status.NEW).values()) {
            list.add(getTrack(position));
        }
        return list;
    }

    public synchronized static List<Track> getNotSyncedList() {
        List<Track> trackList = new ArrayList<>();
        for (int position : tracks.values()) {
            Track track = getTrack(position);
            if (!track.isSync()) {
                trackList.add(track);
            }
        }
        return trackList;
    }

    //FIXME NOW Remove this one
    private static Track getTrack(int position) {
        Track track = null;
        if (tracksCursor.moveToPosition(position)) {
            track = HelperLibrary.musicLibrary.cursorToTrack(tracksCursor, true);
        }
        return track;
    }
}