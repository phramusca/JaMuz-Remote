package phramusca.com.jamuzremote;

import android.content.Context;
import android.util.Log;

/**
 * Created by raph on 10/06/17.
 */

public final class HelperLibrary {

    private static final String TAG = HelperLibrary.class.getSimpleName();
    public static MusicLibrary musicLibrary;

    private HelperLibrary () {
    }

    public static void open(Context context) {
        if(musicLibrary==null || !musicLibrary.db.isOpen()) {
            musicLibrary = new MusicLibrary(context);
            musicLibrary.open();
        }
    }

    public static void close() {
        if(musicLibrary!=null) {
            musicLibrary.close();
            musicLibrary=null;
        }
    }

    public static Track getTrack(String absolutePath, FileInfoReception fileInfoReception) {
        Track track = new Track(absolutePath);
        if(fileInfoReception!=null) {
            track.setRating(fileInfoReception.rating);
            track.setAddedDate(fileInfoReception.addedDate);
            track.setLastPlayed(fileInfoReception.lastPlayed);
            track.setPlayCounter(fileInfoReception.playCounter);
            track.setTags(fileInfoReception.tags);
            track.setGenre(fileInfoReception.genre);
        }
        return track;
    }

    public static boolean insertOrUpdateTrackInDatabase(String absolutePath,
                                                        FileInfoReception fileInfoReception, boolean doUpdate) {
        boolean result=false;
        if(musicLibrary!=null) {
            Track track = getTrack(absolutePath, fileInfoReception);
            int id = musicLibrary.getTrack(absolutePath);
            if(id>=0) {
                track.setId(id);
                if(doUpdate) {
                    //TODO: For user path only: update only if file is modified:
                    //based on lastModificationDate and/or size (not on content as longer than updateTrack)
                    Log.d(TAG, "browseFS updateTrack " + absolutePath);
                    result = musicLibrary.updateTrack(track);
                } else {
                    Log.d(TAG, "browseFS inDb: " + absolutePath);
                    result=true;
                }
            } else {
                Log.d(TAG, "browseFS insertTrack " + absolutePath);
                result=musicLibrary.insertTrack(track);
            }
        }
        return result;
    }
}