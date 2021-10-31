package phramusca.com.jamuzremote;

import android.database.Cursor;

public final class RepoAlbums {

    private static Cursor cursor;

    private RepoAlbums() {
    }

    public synchronized static Cursor get() {
        if (HelperLibrary.musicLibrary != null && cursor == null) {
            cursor = HelperLibrary.musicLibrary.getAlbums();
        }
        return cursor;
    }

    public synchronized static void reset() {
        cursor = null;
    }
}