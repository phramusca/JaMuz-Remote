package phramusca.com.jamuzremote;

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

    private static Table<Integer, Track.Status, Track> tracks = null;

    protected synchronized static void read() {
        tracks = HashBasedTable.create();
        Cursor cursor = HelperLibrary.musicLibrary.getTracksCursor(true,
                "WHERE status!=\"" + Track.Status.LOCAL.name() + "\"",  //NON-NLS
                "", "", -1); //NON-NLS
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Track track = HelperLibrary.musicLibrary.cursorToTrack(cursor, true);
                tracks.put(track.getIdFileServer(), track.getStatus(), track);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
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
        if (tracks != null) {
            if (tracks.containsRow(track.getIdFileServer())) {
                tracks.row(track.getIdFileServer()).clear();
            }
            tracks.put(track.getIdFileServer(), track.getStatus(), track);
        }
    }

    public synchronized static Track getFile(int idFileServer) {
        if (tracks.containsRow(idFileServer)) {
            return tracks.row(idFileServer).values().iterator().next();
        }
        return null;
    }

    public synchronized static List<Track> getDownloadList() {
        return new ArrayList<>(tracks.column(Track.Status.NEW).values());
    }

    public synchronized static List<Track> getMergeList() {
        return new ArrayList<>(tracks.column(Track.Status.REC).values());
    }

    public synchronized static List<Track> getNotSyncedList() {
        List<Track> trackList = new ArrayList<>();
        for (Track track : tracks.values()) {
            if (!track.isSync()) {
                trackList.add(track);
            }
        }
        return trackList;
    }
}