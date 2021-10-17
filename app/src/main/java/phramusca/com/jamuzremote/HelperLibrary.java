package phramusca.com.jamuzremote;

import android.content.Context;

import java.io.File;

/**
 * Created by raph on 10/06/17.
 */
public final class HelperLibrary {

    public static MusicLibrary musicLibrary;

    private HelperLibrary() {
    }

    public static void open(File getAppDataPath, Context context) {
        if (musicLibrary == null || !musicLibrary.db.isOpen()) {
            musicLibrary = new MusicLibrary(getAppDataPath, context);
            musicLibrary.open();
        }
    }

    public static void close() {
        if (musicLibrary != null) {
            musicLibrary.close();
            musicLibrary = null;
        }
    }
}