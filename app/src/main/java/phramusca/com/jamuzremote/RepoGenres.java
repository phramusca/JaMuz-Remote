package phramusca.com.jamuzremote;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by raph on 10/06/17.
 */

public final class RepoGenres {

    private static final String TAG = RepoGenres.class.getSimpleName();
    private static List<String> genres = new ArrayList<>();

    private RepoGenres() {
    }

    public synchronized static List<String> get() {
        if(HelperLibrary.musicLibrary!=null && genres.size()<=0) {
            genres = new ArrayList<>();
            genres = HelperLibrary.musicLibrary.getGenres();
        }
        return genres;
    }

    public synchronized static void set(final List<String> newGenres) {
        //Adding missing tags
        for(String tag : newGenres) {
            add(tag);
        }
        //Removing tags not in input list
        final Iterator<String> it = get().iterator();
        while (it.hasNext())
        {
            final String genre = it.next();
            if(HelperLibrary.musicLibrary!=null && !newGenres.contains(genre)) {
                new Thread() {
                    public void run() {
                        int deleted = HelperLibrary.musicLibrary.deleteGenre(genre);
                        if(deleted>0) {
                            it.remove();
                        }
                    }
                }.start();
            }
        }
    }

    private synchronized static void add(final String genre) {
        if(HelperLibrary.musicLibrary!=null && !get().contains(genre)) {
            new Thread() {
                public void run() {
                    if (HelperLibrary.musicLibrary.addGenre(genre)) {
                        genres.add(genre);
                    }
                }
            }.start();
        }
    }
}