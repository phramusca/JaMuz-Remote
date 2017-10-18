package phramusca.com.jamuzremote;

import java.util.ArrayList;

/**
 * Created by raph on 11/06/17.
 */
public class PlayList {

    private String name;
    private String query;
    private String order;
    private MusicLibrary musicLibrary=null;
    private ArrayList<String> tags = new ArrayList<>();
    private boolean includeUnTagged = true;

    public PlayList(String name, MusicLibrary musicLibrary) {
        this.name = name;
        this.musicLibrary = musicLibrary;
    }

    public PlayList(String name, MusicLibrary musicLibrary, ArrayList<String> tags) {
        this(name, musicLibrary);
        this.tags = tags;
    }

    public PlayList(String name, String query, String order, MusicLibrary musicLibrary) {
        this(name, musicLibrary);
        this.query = query;
        this.order = order;
    }

    public ArrayList<Track> getTracks() {
        if(query==null) {
            return musicLibrary.getTracks(getWhere(tags));
        } else {
            return musicLibrary.getTracks(query, order);
        }
    }

    /**
     *
     * @param value
     */
    public void toggleTag(String value) {
        if(value.equals("null")) {
            includeUnTagged=!includeUnTagged;
        } else {
            if(tags.contains(value)) {
                tags.remove(value);
            }
            else {
                tags.add(value);
            }
        }
    }

    private String getWhere(ArrayList<String> tags) {
        String in = "";
        //FIXME: When no tags, what tracks are returned ?
        if(tags.size()>0) {
            in += "WHERE tag.value IN (";
            for(String entry : tags) {
                in+="\""+entry+"\",";
            }
            in = in.substring(0, in.length()-1);
            in += ")";
            if(includeUnTagged) {
                in += " OR tag.value IS NULL";
            }
        } else if(!includeUnTagged) {
            in += "WHERE tag.value NOT NULL";
        }
        return in;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        //FIXME: getNb for "Selected" playlist
        return query==null?name:name+" ("+ musicLibrary.getNb(query) +")";
    }
}
