package phramusca.com.jamuzremote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by raph on 11/06/17.
 */
public class PlayList {

    private String name;
    private String query;
    private String order;
    private MusicLibrary musicLibrary=null;
    private Map<String, TriStateButton.STATE> tags = new HashMap<>();
    private TriStateButton.STATE unTaggedState = TriStateButton.STATE.ANY;
    private int rating=0;
    private Operator ratingOperator =PlayList.Operator.GREATERTHAN;
    private String genre="";
    private String genreExclude="";

    public PlayList(String name, MusicLibrary musicLibrary) {
        this.name = name;
        this.musicLibrary = musicLibrary;
    }

    public PlayList(String name, String query, String order, MusicLibrary musicLibrary) {
        this(name, musicLibrary);
        this.query = query;
        this.order = order;
    }

    public ArrayList<Track> getTracks() {
        if(query==null) {
            return musicLibrary.getTracks(getWhere(), getHaving(), "");
        } else {
            return musicLibrary.getTracks(query, order);
        }
    }

    public void toggleTag(String value, TriStateButton.STATE state) {
        if(value.equals("null")) {
            unTaggedState = state;
        } else {
            tags.put(value, state);
        }
    }

    public String setRatingOperator() {
        switch (ratingOperator) {
            case GREATERTHAN:
                ratingOperator=Operator.IS;
                break;
            case IS:
                ratingOperator=Operator.LESSTHAN;
                break;
            case LESSTHAN:
                ratingOperator=Operator.GREATERTHAN;
                break;
        }
        return ratingOperator.toString();
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setGenreExclude(String genreExclude) {
        this.genreExclude = genreExclude;
    }

    private String getWhere() {

        //FILTER by RATING
        String in = " WHERE rating ";
        switch (ratingOperator) {
            case GREATERTHAN:
                in += " >= ";
                break;
            case IS:
                in += " = ";
                break;
            case LESSTHAN:
                in += " <= ";
                break;
        }
        in +=rating+" ";

        //FILTER by GENRE
        if(!genre.equals("")) {
            in += "\n AND genre=\""+genre+"\" ";
        }
        if(!genreExclude.equals("")) {
            in += "\n AND genre!=\""+genreExclude+"\" ";
        }

        return in;
    }

    private String getTagClause(Map.Entry<String, TriStateButton.STATE> tag) {
        String name = tag.getKey();
        TriStateButton.STATE state = tag.getValue();
        return state.equals(TriStateButton.STATE.ANY)?" 1 ":
                " tags "+(state.equals(TriStateButton.STATE.FALSE)?"NOT":"")+" LIKE "+"\"%"+name+"%\" ";
    }

    private String getHaving() {
        //FILTER by TAGS
        String in;
        if (unTaggedState.equals(TriStateButton.STATE.TRUE)) {
            in = " HAVING tag.value IS NULL ";
        } else {
            //Include or exclude tags according to states
            in = " HAVING ( ";
            if(tags.size()>0) {
                Iterator<Map.Entry<String, TriStateButton.STATE>> iterator = tags.entrySet().iterator();
                if (iterator.hasNext()) {
                    Map.Entry<String, TriStateButton.STATE> tag = iterator.next();
                    in+=" "+getTagClause(tag);
                }
                while (iterator.hasNext()) {
                    Map.Entry<String, TriStateButton.STATE> tag = iterator.next();
                    in+="\n AND "+getTagClause(tag);
                }
            } else {
                in+=" 1 ";
            }
            in += " ) ";
            //Include or exclude untagged
            if(unTaggedState.equals(TriStateButton.STATE.ANY)) {
                in += "\n OR tags IS NULL ";
            } else if(unTaggedState.equals(TriStateButton.STATE.FALSE)) {
                in += "\n AND tags IS NOT NULL ";
            }
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

    /**
     * Operator for filters
     */
    public enum Operator {
        /**
         * String contains
         */
        CONTAINS("contains"), //NOI18N
        /**
         * String does not contain
         */
        DOESNOTCONTAIN("does not contain"), //NOI18N
        /**
         * Value (text) is
         */
        IS("="), //NOI18N
        /**
         * Value (text) is not
         */
        ISNOT("is not"), //NOI18N
        /**
         * Value (numerical) is
         */
        NUMIS("is"), //NOI18N
        /**
         * Value (numerical) is not
         */
        NUMISNOT("is not"), //NOI18N
        /**
         * String starts with
         */
        STARTSWITH("starts with"), //NOI18N
        /**
         * String ends with
         */
        ENDSWITH("ends with"), //NOI18N
        /**
         * Value is less than
         */
        LESSTHAN("<="), //NOI18N
        /**
         * Value is greater than
         */
        GREATERTHAN(">="), //NOI18N
        /**
         * Value is less than
         */
        DATELESSTHAN("is before"), //NOI18N
        /**
         * Value is greater than
         */
        DATEGREATERTHAN("is after"); //NOI18N

        private final String display;
        private Operator(String display) {
            this.display = display;
        }
        @Override
        public String toString() {
            return display;
        }
    };
}
