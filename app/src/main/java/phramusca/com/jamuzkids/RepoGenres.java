package phramusca.com.jamuzkids;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by raph on 10/06/17.
 */

public final class RepoGenres {

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
                int deleted = HelperLibrary.musicLibrary.deleteGenre(genre);
                if(deleted>0) {
                    it.remove();
                }
            }
        }
    }

    private synchronized static void add(final String genre) {
        if(HelperLibrary.musicLibrary!=null && !get().contains(genre)) {
            if (HelperLibrary.musicLibrary.addGenre(genre)) {
                genres.add(genre);
            }
        }
    }
}