package phramusca.com.jamuzremote;

import android.util.Log;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
        List<Track> newTracks = HelperLibrary.musicLibrary.getTracks("", "", "", -1);
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
                    || !track.readMetadata()
                    || !HelperLibrary.musicLibrary.insertOrUpdateTrack(track)) {
                Log.w(TAG, "Error with received file. Deleting " + receivedFile.getAbsolutePath());
                //noinspection ResultOfMethodCallIgnored
                receivedFile.delete();
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
            //FIXME: readMetadata often return false (at checkNewFile, when checking new list of files)
            //java.lang.RuntimeException: setDataSource failed: status = 0x80000000
            //which is weird as it has been read at previous reception and was valid.
            // - Does it happen because we overload the file system by checking too much ?
            // => Validate the file using a file hash instead (includes below metadata changes so if metadata changes, it will be re-downloaded)
            //          OR sync album, artist, title and genre instead of reading file metadata
            //            (IF not, unless file size does not change when metadata does we would not get metadata updates on those fields)
            //  (only read file metadata for local files - ie: the one not synced)
//            if (!track.readMetadata()) {
//                Log.w(TAG, "Cannot read tags. Deleting " + file.getAbsolutePath());
//                //noinspection ResultOfMethodCallIgnored
//                file.delete();
//                track.setStatus(Track.Status.NEW);
//            }
        }
        tracks.put(track.getIdFileServer(), track.getStatus(), track);
    }

    public synchronized static int getRemainingSize() {
        return tracks ==null?0:tracks.column(Track.Status.NEW).size();
    }

    public synchronized static int getTotalSize() {
        return tracks==null?0:tracks.column(Track.Status.REC).size();
    }

    static String totalFilesSize;

    public synchronized static String getTotalFileSize() {
        if(totalFilesSize!=null || tracks==null) {
            return totalFilesSize;
        }
        long totalSize=0;
        totalSize+=getRemainingFileSize();
        totalFilesSize=StringManager.humanReadableByteCount(totalSize, false);
        return totalFilesSize;
    }

    public synchronized static long getRemainingFileSize() {
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