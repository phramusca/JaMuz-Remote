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

    public static boolean insertOrUpdateTrackInDatabase(String absolutePath,
                                                        FileInfoReception fileInfoReception) {
        boolean result=false;
        if(musicLibrary!=null) {
            int id = musicLibrary.getTrack(absolutePath);
            Track track = new Track(absolutePath);
            Log.d(TAG, "browseFS insertTrack " + absolutePath);
            if(fileInfoReception!=null) {
                track.setRating(fileInfoReception.rating);
                track.setAddedDate(fileInfoReception.addedDate);
                track.setLastPlayed(fileInfoReception.lastPlayed);
                track.setPlayCounter(fileInfoReception.playCounter);
                track.setTags(fileInfoReception.tags);
                track.setGenre(fileInfoReception.genre);
            }
            if(id>=0) {
                Log.d(TAG, "browseFS updateTrack " + absolutePath);
                //TODO: For user path only: update only if file is modified:
                //based on lastModificationDate and/or size (not on content as longer than updateTrack)
                track.setId(id);
                result=musicLibrary.updateTrack(track);
            } else {
                result=musicLibrary.insertTrack(track);
            }
        }
        return result;
    }
}