package phramusca.com.jamuzremote;

import static phramusca.com.jamuzremote.MusicLibraryDb.COL_ID_SERVER;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_RATING;
import static phramusca.com.jamuzremote.MusicLibraryDb.COL_STATUS;

import android.database.Cursor;
import android.util.Log;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    }

    public static Track getTrack(int position) {
        Track track = null;
        if (tracksCursor.moveToPosition(position)) {
            track = HelperLibrary.musicLibrary.cursorToTrack(tracksCursor, true);
        }
        return track;
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

    public synchronized static Track getFile(int i) {
        if (tracks.containsRow(i)) {
            return getTrack(tracks.row(i).values().iterator().next());
        }
        return null;
    }

    public synchronized static List<Track> getDownloadList() {
        List<Track> list = new ArrayList<>();
        for(int position : tracks.column(Track.Status.NEW).values()) {
            list.add(getTrack(position));
        }
        return list;
    }

    public synchronized static List<Track> getMergeList() {
        List<Track> list = new ArrayList<>();
        for(int position : tracks.column(Track.Status.REC).values()) {
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
}