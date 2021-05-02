package phramusca.com.jamuzremote;

import android.util.Log;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author phramusca
 */
public final class RepoSync {

    private static final String TAG = RepoSync.class.getName();

    private static Table<Integer, Track.Status, Track> tracks = null;

    protected static void read() {
        if(tracks==null) {
            tracks = HashBasedTable.create();
            List<Track> newTracks = HelperLibrary.musicLibrary.getTracks("", "", "", -1);
            for(Track track : newTracks) {
                tracks.put(track.getIdFileServer(), track.getStatus(), track);
            }
        }
    }

    /**
     * Sets status to NEW if track does not exists
     * or REC if track exists and has correct size.
     * File is deleted if not requested (not in tracks).
     * @param track the one to check
     * @return true if onReceivedFile exists and length()==track.size
     */
    public static void checkReceivedFile(Track track) {
        track.setStatus(Track.Status.REC);
        if (!checkFile(track) || !HelperLibrary.musicLibrary.updateStatus(track)) {
            File receivedFile = new File(track.getPath());
            Log.w(TAG, "Error with received file. Deleting " + receivedFile.getAbsolutePath());
            //noinspection ResultOfMethodCallIgnored
            receivedFile.delete();
            track.setStatus(Track.Status.NEW);
        }
        updateStatus(track);
    }

    /**
     * @param track the one to check
     * @return true if onReceivedFile exists and length()==track.size
     */
    public static boolean checkFile(Track track) {
        File receivedFile = new File(track.getPath());
        if(receivedFile.exists()) {
            if (receivedFile.length() == track.getSize()) {
                Log.i(TAG, "Correct file size: " + receivedFile.length());
                return true;
            } else {
                Log.w(TAG, "File has wrong size. Deleting " + receivedFile.getAbsolutePath());
                //noinspection ResultOfMethodCallIgnored
                receivedFile.delete();
            }
        } else {
            Log.w(TAG, "File does not exits. "+receivedFile.getAbsolutePath());
        }
        return false;
    }

    public static void updateStatus(Track track) {
        tracks.row(track.getIdFileServer()).clear();
        tracks.put(track.getIdFileServer(), track.getStatus(), track);
    }

    public static int getRemainingSize() {
        return tracks ==null?0:tracks.column(Track.Status.NEW).size();
    }

    public static int getTotalSize() {
        return tracks==null?0:tracks.column(Track.Status.REC).size()+tracks.column(Track.Status.NEW).size();
    }

    static String totalFilesSize;

    public static String getTotalFileSize() {
        if(totalFilesSize!=null || tracks==null) {
            return totalFilesSize;
        }
        long totalSize=0;
        totalSize+=getRemainingFileSize();
        totalFilesSize=StringManager.humanReadableByteCount(totalSize, false);
        return totalFilesSize;
    }

    public static long getRemainingFileSize() {
        if(tracks ==null) {
            return 0;
        }
        long nbRemaining=0;
        for(Track track : tracks.column(Track.Status.NEW).values()) {
            nbRemaining+=track.getSize();
        }
        return nbRemaining;
    }

    public static List<Track> getDownloadList() {
        return new ArrayList<>(tracks.column(Track.Status.NEW).values());
    }

    public static List<Track> getList() {
        return new ArrayList<>(tracks.values());
    }

    public static List<Track> getMergeList() {
        return new ArrayList<>(tracks.column(Track.Status.REC).values());
    }

    public static Track getFile(int i) {
        if(tracks.containsRow(i)) {
            return tracks.row(i).values().iterator().next();
        }
        return null;
    }
}