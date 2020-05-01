package phramusca.com.jamuzkids;

import android.util.Log;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author phramusca
 */
public final class RepoSync {

    private static final String TAG = RepoSync.class.getName();

    private static Table<Integer, Track.Status, Track> tracks = null;

    private synchronized static void updateTracks(Track track) {
        tracks.row(track.getIdFileServer()).clear();
        tracks.put(track.getIdFileServer(), track.getStatus(), track);
    }
    protected synchronized static void read() {
        tracks = HashBasedTable.create();
        read(Track.Status.NEW);
        read(Track.Status.REC);
    }

    private synchronized static void read(Track.Status status) {
        List<Track> newTracks = HelperLibrary.musicLibrary.getTracks(status);
        for(Track track : newTracks) {
            tracks.put(track.getIdFileServer(), track.getStatus(), track);
        }
    }

    /**
     * Sets status to NEW if track does not exists
     * or REC if track exists and has correct size.
     * File is deleted if not requested (not in tracks).
     * @param getAppDataPath application path
     * @param track the one to check
     * @return true if onReceivedFile exists and length()==track.size
     */
    public synchronized static void checkReceivedFile(File getAppDataPath, Track track) {
        File receivedFile = new File(getAppDataPath, track.getRelativeFullPath());
        if(tracks.containsRow(track.getIdFileServer())) {
            track.setStatus(Track.Status.REC);
            if (!checkFile(track, receivedFile)
                    || !track.readTags()
                    || !HelperLibrary.musicLibrary.insertOrUpdateTrack(track)) {
                track.setStatus(Track.Status.NEW);
            }
            updateTracks(track);
        } else {
            Log.w(TAG, "tracks does not contain file. Deleting " + receivedFile.getAbsolutePath());
            //noinspection ResultOfMethodCallIgnored
            receivedFile.delete();
        }
    }

    /**
     * Checks if absolutePath is in tracks. Delete file if not.
     * @param absolutePath relative full path
     */
    public synchronized static boolean checkFile(File getAppDataPath, String absolutePath) {
        Track track = new Track(getAppDataPath, absolutePath);
        if(tracks != null && !tracks.containsValue(track)) {
            Log.i(TAG, "DELETE UNWANTED: "+absolutePath);
            File file = new File(getAppDataPath, track.getRelativeFullPath());
            //noinspection ResultOfMethodCallIgnored
            file.delete();
            return true;
        }
        return false;
    }

    /**
     * @param track the one to check
     * @param receivedFile the corresponding File
     * @return true if onReceivedFile exists and length()==track.size
     */
    private synchronized static boolean checkFile(Track track,
                                                  File receivedFile) {
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

    /**
     * @param track the NEW file to check
     * @return modified track with status set to REC (with tags read) if it exists
     *
     */
    public synchronized static void checkNewFile(Track track) {
        File file = new File(track.getPath());
        if(checkFile(track, file)) {
            track.setStatus(Track.Status.REC);
        }
        tracks.put(track.getIdFileServer(), track.getStatus(), track);
    }

    public synchronized static void reset() {
        tracks = HashBasedTable.create();
    }

    public synchronized static int getRemainingSize() {
        return tracks ==null?0:(tracks.column(Track.Status.NEW).size()
                +tracks.column(Track.Status.DOWN).size());
    }

    public synchronized static int getTotalSize() {
        return tracks==null?0:tracks.size();
    }

    public synchronized static long getRemainingFileSize() {
        if(tracks ==null) {
            return 0;
        }
        long nbRemaining=0;
        nbRemaining+=getRemainingFileSize(Track.Status.NEW);
        nbRemaining+=getRemainingFileSize(Track.Status.DOWN);
        return nbRemaining;
    }

    private synchronized static long getRemainingFileSize(Track.Status status) {
        long nbRemaining=0;
        for(Track track : tracks.column(status).values()) {
            nbRemaining+=track.getSize();
        }
        return nbRemaining;
    }

    public synchronized static Track getNew() {
        Track track=null;
        if(tracks != null) {
            Collection<Track> values = tracks.column(Track.Status.NEW).values();
            if(values.size()>0) {
                track = tracks.column(Track.Status.NEW).entrySet().iterator().next().getValue();
                if(track!=null) {
                    track.setStatus(Track.Status.DOWN);
                    updateTracks(track);
                }
            }
        }
        return track;
    }

    public static List<Track> getDownloadList() {
        return new ArrayList<>(tracks.column(Track.Status.NEW).values());
    }

    public static List<Track> getMergeList() {
        List<Track> trackArrayList = new ArrayList<>(tracks.column(Track.Status.REC).values());
        //trackArrayList.addAll(tracks.column(Track.Status.NEW).values());
        return trackArrayList;
    }
}