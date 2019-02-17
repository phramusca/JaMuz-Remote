package phramusca.com.jamuzkids;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by raph on 10/06/17.
 */

public final class RepoTags {

    private static Map<Integer, String> tags = new HashMap<>();

    private RepoTags() {
    }

    public synchronized static Map<Integer, String> get() {
        if(HelperLibrary.musicLibrary!=null && tags.size()<=0) {
            tags = new HashMap<>();
            tags = HelperLibrary.musicLibrary.getTags();
        }
        return tags;
    }

    public synchronized static void set(final List<String> newTags) {
        //Adding missing tags
        for(String tag : newTags) {
            add(tag);
        }
        //Removing tags not in input list
        final Iterator<Map.Entry<Integer, String>> it = get().entrySet().iterator();
        while (it.hasNext())
        {
            final Map.Entry<Integer, String> tag = it.next();
            if(HelperLibrary.musicLibrary!=null && !newTags.contains(tag.getValue())) {
                int deleted = HelperLibrary.musicLibrary.deleteTag(tag.getKey());
                if(deleted>0) {
                    it.remove();
                }
            }
        }
    }

    private synchronized static void add(final String tag) {
        if(HelperLibrary.musicLibrary!=null && !get().values().contains(tag)) {
            int idTag = HelperLibrary.musicLibrary.addTag(tag);
            if(idTag>0) {
                tags.put(idTag, tag);
            }
        }
    }
}