package phramusca.com.jamuzremote;

/**
 * Created by raph on 11/06/17.
 */
public class PlayList {

    private String name;
    private String query;
    private int value;

    public PlayList(String name, String query, int value) {
        this.name = name;
        this.query = query;
        this.value = value;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return name+" ("+value+")";
    }

    public String getValue() {
        return name;
    }
}
