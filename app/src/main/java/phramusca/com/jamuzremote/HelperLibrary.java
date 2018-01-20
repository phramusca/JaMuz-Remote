package phramusca.com.jamuzremote;

import android.util.Log;

import java.io.File;

/**
 * Created by raph on 10/06/17.
 */

public final class HelperLibrary {

    private static final String TAG = HelperLibrary.class.getSimpleName();

    private HelperLibrary () {
    }

    public static boolean insertOrUpdateTrackInDatabase(String absolutePath,
                                                        FileInfoReception fileInfoReception) {
        boolean result=false;
        if(MainActivity.musicLibrary!=null) {
            int id = MainActivity.musicLibrary.getTrack(absolutePath);
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
                    MainActivity.musicLibrary.insertTrack(track);
                    result=true;
                } else {
                    //FIXME: Delete track ONLY if it is a song track that appears to be corrupted
                    Log.w(TAG, "browseFS delete file because cannot read tags of " + absolutePath);
                    new File(absolutePath).delete();

                }
            }
        }
        return result;
    }
}