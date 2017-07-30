package phramusca.com.jamuzremote;

/**
 * Created by raph on 11/06/17.
 */
public class PlayList {

    private String name;
    private String query;
    MusicLibrary musicLibrary=null;

    public PlayList(String name, String query) {
        this.name = name;
        this.query = query;
    }

    public PlayList(String name, String query, MusicLibrary musicLibrary) {
        this(name, query);
        this.musicLibrary = musicLibrary;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return musicLibrary==null?name:name+" ("+ musicLibrary.getNb(query) +")";
    }
}
