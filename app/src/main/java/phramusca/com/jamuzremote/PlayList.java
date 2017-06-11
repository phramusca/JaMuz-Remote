package phramusca.com.jamuzremote;

/**
 * Created by raph on 11/06/17.
 */
public class PlayList {

    private String name;
    private String query;

    public PlayList(String name, String query) {
        this.name = name;
        this.query = query;
    }

    //public String getName() {
    //    return name;
    //}

    public String getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return name;
    }
}
