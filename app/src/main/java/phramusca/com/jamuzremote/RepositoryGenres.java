package phramusca.com.jamuzremote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raph on 10/06/17.
 */

public final class RepositoryGenres {

    private static final String TAG = RepositoryGenres.class.getSimpleName();
    private static List<String> genres = new ArrayList<>();

    private RepositoryGenres() {
    }

    public synchronized static List<String> read() {
        if(MainActivity.musicLibrary!=null && genres.size()<=0) {
            genres = new ArrayList<>();
            genres = MainActivity.musicLibrary.getGenres();
        }
        return genres;
    }

    public synchronized static List<String> get() {
        return genres;
    }

    public synchronized static void add(final String genre) {
        if(MainActivity.musicLibrary!=null && !genres.contains(genre)) {
            new Thread() {
                public void run() {
                    if (MainActivity.musicLibrary.addGenre(genre)) {
                        genres.add(genre);
                    }
                }
            }.start();
        }
    }
}