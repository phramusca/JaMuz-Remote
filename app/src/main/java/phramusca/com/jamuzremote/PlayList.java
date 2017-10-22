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
    private int rating=0;
    private Operator ratingOperator =PlayList.Operator.GREATERTHAN;
    private String genre="";
    private String genreExclude="";

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

    /**
     *Set rating operator
     */
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

    private String getWhere(ArrayList<String> tags) {

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
            in += " AND genre=\""+genre+"\" ";
        }
        if(!genreExclude.equals("")) {
            in += " AND genre!=\""+genreExclude+"\" ";
        }

        //FILTER by TAGS
        in += " AND ( ";
        if(tags.size()>0) {
            in += " tag.value IN (";
            for(String entry : tags) {
                in+="\""+entry+"\",";
            }
            in = in.substring(0, in.length()-1);
            in += " )";
            if(includeUnTagged) {
                in += " OR tag.value IS NULL ";
            }
        } else if(includeUnTagged) {
            in += " tag.value IS NULL ";
        } else {
            in += " 1 ";
        }
        in += " ) ";
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
