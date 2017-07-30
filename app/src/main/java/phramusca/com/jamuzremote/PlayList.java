package phramusca.com.jamuzremote;

/**
 * Created by raph on 11/06/17.
 */
public class PlayList {

    private String name;
    private String query;
    MusicLibrary musicLibrary=null;

    public PlayList(String name) {
        this.name = name;
    }

    public PlayList(String name, String query, MusicLibrary musicLibrary) {
        this(name);
        this.query = query;
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
