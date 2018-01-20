package phramusca.com.jamuzremote;

import android.content.Context;
import android.util.Log;

import java.io.File;

/**
 * Created by raph on 10/06/17.
 */

public final class HelperLibrary {

    private static final String TAG = HelperLibrary.class.getSimpleName();
    public static MusicLibrary musicLibrary;

    private HelperLibrary () {
    }

    public static void open(Context context) {
        if(musicLibrary==null || musicLibrary.db.isOpen()) {
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
            if(id>=0) {
                Log.d(TAG, "browseFS updateTrack " + absolutePath);
                //TODDO: Update if file is modified only:
                //based on lastModificationDate and/or size (not on content as longer than updateTrack)
                //musicLibrary.updateTrack(id, track, false);
                //Warning with genre now that it is part of merge
                result=true;
            } else {
                Track track = new Track(absolutePath);
                if(track!=null) {
                    Log.d(TAG, "browseFS insertTrack " + absolutePath);
                    if(fileInfoReception!=null) {
                        track.setRating(fileInfoReception.rating);
                        track.setAddedDate(fileInfoReception.addedDate);
                        track.setLastPlayed(fileInfoReception.lastPlayed);
                        track.setPlayCounter(fileInfoReception.playCounter);
                        track.setTags(fileInfoReception.tags);
                        track.setGenre(fileInfoReception.genre); //TODO Do not if genre read from file is better
                    }
                    musicLibrary.insertTrack(track);
                    result=true;
                } else {
                    //TODO: Delete track ONLY if it is a song track that appears to be corrupted
                    Log.w(TAG, "insertOrUpdateTrackInDatabase: delete file because cannot read tags of " + absolutePath);
                    new File(absolutePath).delete();
                }
            }
        }
        return result;
    }
}