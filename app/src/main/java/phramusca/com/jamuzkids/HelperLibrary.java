package phramusca.com.jamuzkids;

import android.content.Context;

/**
 * Created by raph on 10/06/17.
 */
public final class HelperLibrary {

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
}