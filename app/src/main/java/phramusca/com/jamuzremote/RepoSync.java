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

    protected synchronized static void read() {
        //FIXME NOW: Update this repo for each track change (rating, genre) to avoid reading at every sync
        //  Otherwise tracks stats updates (except tags as re-read before merge request) are not available for merge !!!
        //   WARNING: Need to reset isSync to false too before a new sync
        //if(tracks==null) {
            tracks = HashBasedTable.create();
            List<Track> newTracks = HelperLibrary.musicLibrary.getTracks(true, "WHERE status!=\""+ Track.Status.LOCAL.name()+"\"", "", "", -1);
            for(Track track : newTracks) {
                tracks.put(track.getIdFileServer(), track.getStatus(), track);
            }
        //}
    }

    /**
     * Sets status to REC if track exists and has correct size.
     * Otherwise, file is deleted and status set back to NEW
     * @param track the one to check
     */
    public synchronized static void checkReceivedFile(Track track) {
        track.setStatus(Track.Status.REC);
        if (!checkFile(track) || !HelperLibrary.musicLibrary.updateStatus(track)) {
            File receivedFile = new File(track.getPath());
            Log.w(TAG, "Error with received file. Deleting " + receivedFile.getAbsolutePath());
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

    public synchronized static void update(Track track) {
        if(tracks!=null) {
            if(tracks.containsRow(track.getIdFileServer())) {
                tracks.row(track.getIdFileServer()).clear();
            }
            tracks.put(track.getIdFileServer(), track.getStatus(), track);
        }
    }

    public synchronized static List<Track> getDownloadList() {
        return new ArrayList<>(tracks.column(Track.Status.NEW).values());
    }

    public synchronized static List<Track> getMergeList() {
        return new ArrayList<>(tracks.column(Track.Status.REC).values());
    }

    public synchronized static Track getFile(int i) {
        if(tracks.containsRow(i)) {
            return tracks.row(i).values().iterator().next();
        }
        return null;
    }

    public synchronized static List<Track> getNotSyncedList() {
        List<Track> trackList = new ArrayList<>();
        for(Track track : tracks.values()) {
            if(!track.isSync()) {
                trackList.add(track);
            }
        }
        return trackList;
    }
}