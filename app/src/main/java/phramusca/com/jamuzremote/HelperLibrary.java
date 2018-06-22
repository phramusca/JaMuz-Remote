package phramusca.com.jamuzremote;

import android.content.Context;
import android.util.Log;

/**
 * Created by raph on 10/06/17.
 */

public final class HelperLibrary {

    private static final String TAG = HelperLibrary.class.getName();
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

    //FIXME !!!!!!!! tags (artist, album, title) can be empty in database
    //Ex: /storage/3515-1C15/Android/data/org.phramusca.jamuz/files/Nosfell/Pomaïe Klokochazia Balek/04 Sladinji the Grinning Tree.mp3
    //ID=3072
    /*{
        "addedDate":"Mar 12, 2011 19:11:30",
            "genre":"Chanson",
            "idFile":13098,
            "lastPlayed":"May 11, 2018 19:34:51",
            "playCounter":16,
            "rating":5,
            "relativeFullPath":"Nosfell/Pomaïe Klokochazia Balek/01 Children of Windaklo.mp3",
            "size":4751525,
            "status":"ACK",
            "tags":["Normal"]
    }*/
    //THOUGH file includes proper tags, from which those 3 tags are read from
    //(these tags are NOT included in sync Files.txt)
    //= > Was there an error reading tags from file ? file not completely downloaded due to some cache ?
    //=> Or, is it simply overwritten here with info from Files.txt that does not include those 3 tags ?

    //FIXME This should move to musicLibrary in order to be synchronized !!
    public static boolean insertOrUpdateTrackInDatabase(String absolutePath,
                                                        FileInfoReception fileInfoReception,
                                                        boolean doUpdate) {
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