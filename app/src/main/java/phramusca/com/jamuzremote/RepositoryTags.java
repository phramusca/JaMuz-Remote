package phramusca.com.jamuzremote;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by raph on 10/06/17.
 */

public final class RepositoryTags {

    private static final String TAG = RepositoryTags.class.getSimpleName();
    private static Map<Integer, String> tags = new HashMap<>();

    private RepositoryTags () {
    }

    public synchronized static Map<Integer, String> read() {
        if(HelperLibrary.musicLibrary!=null && tags.size()<=0) {
            tags = new HashMap<>();
            tags = HelperLibrary.musicLibrary.getTags();
        }
        return tags;
    }

    public synchronized static Map<Integer, String> getTags() {
        return tags;
    }

    public synchronized static void add(final String tag) {
        if(!RepositoryTags.getTags().values().contains(tag)) {
            if(HelperLibrary.musicLibrary!=null) {
                new Thread() {
                    public void run() {
                        int idTag = HelperLibrary.musicLibrary.addTag(tag);
                        if(idTag>0) {
                            tags.put(idTag, tag);
                        }
                    }
                }.start();
            }
        }
    }
}