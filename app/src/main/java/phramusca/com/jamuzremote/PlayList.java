package phramusca.com.jamuzremote;

/**
 * Created by raph on 11/06/17.
 */
public class PlayList {

    private String name;
    private String query;
    private String order;
    MusicLibrary musicLibrary=null;

    public PlayList(String name) {
        this.name = name;
    }

    public PlayList(String name, String query, String order, MusicLibrary musicLibrary) {
        this(name);
        this.query = query;
        this.order = order;
        this.musicLibrary = musicLibrary;
    }

    public String getQuery() {
        return query;
    }

    public String getOrder() {
        return order;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return musicLibrary==null?name:name+" ("+ musicLibrary.getNb(query) +")";
    }
}
