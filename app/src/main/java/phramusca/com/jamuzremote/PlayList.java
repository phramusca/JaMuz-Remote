package phramusca.com.jamuzremote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by raph on 11/06/17.
 */
public class PlayList {

    private String name;
    private String query;
    private String order;
    private Map<String, TriStateButton.STATE> tags = new HashMap<>();
    private Map<String, TriStateButton.STATE> genres = new HashMap<>();
    private TriStateButton.STATE unTaggedState = TriStateButton.STATE.ANY;
    private int rating=0;
    private Operator ratingOperator = PlayList.Operator.GREATERTHAN;
    private boolean isLocal;

    public PlayList(String name, boolean isLocal) {
        this.name = name;
        this.isLocal = isLocal;
    }

    public PlayList(String name, String query, String order) {
        this(name, true);
        this.query = query;
        this.order = order;
    }

    public ArrayList<Track> getTracks() {
        if(query==null) {
            return MainActivity.musicLibrary.getTracks(getWhere(), getHaving(), "ORDER BY playCounter, lastPlayed");
        } else {
            return MainActivity.musicLibrary.getTracks(query, order);
        }
    }

    public Set<Map.Entry<String, TriStateButton.STATE>> getTags() {
        return tags.entrySet();
    }

    public String getTagsString() {
        return getString(tags);
    }

    public Set<Map.Entry<String, TriStateButton.STATE>> getGenres() {
        return genres.entrySet();
    }

    public String getGenresString() {
        return getString(genres);
    }

    private String getString(Map<String, TriStateButton.STATE> stateMap) {
        String in = "In: ";
        String out = "Out: ";
        if(stateMap.size()>0) {
            ArrayList<String> include = new ArrayList<>();
            ArrayList<String> exclude = new ArrayList<>();
            for (Map.Entry<String, TriStateButton.STATE> entry : stateMap.entrySet()) {
                switch (entry.getValue()) {
                    case FALSE:
                        exclude.add(entry.getKey()); break;
                    case TRUE:
                        include.add(entry.getKey()); break;
                }
            }
            in+=getString(include, 1);
            out+=getString(exclude, 1);
        }
        return in+"\n"+out;
    }

    private String getString(ArrayList<String> strings, int max) {
        String out = "";
        if(strings.size()>0) {
            for(int i=0; i<max; i++) {
                if(strings.size()>i) {
                    out+=strings.get(i)+", ";
                }
            }
            out = out.substring(0, out.length()-2);
            if(strings.size()>max) {
                out+=" ...";
            }
        }
        return out;
    }

    public TriStateButton.STATE getUnTaggedState() {
        return unTaggedState;
    }

    public int getRating() {
        return rating;
    }

    public String getRatingOperator() {
        return ratingOperator.toString();
    }

    public void toggleTag(String value, TriStateButton.STATE state) {
        if(value.equals("null")) {
            unTaggedState = state;
        } else {
            tags.put(value, state);
        }
    }

    public void toggleGenre(String value, TriStateButton.STATE state) {
        genres.put(value, state);
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

    private String getWhere() {

        //FILTER by RATING
        String in = " WHERE rating "+getRatingString()+" ";

        //FILTER by GENRE
        ArrayList<String> include = new ArrayList<>();
        ArrayList<String> exclude = new ArrayList<>();
        for (Map.Entry<String, TriStateButton.STATE> entry : genres.entrySet()) {
            switch (entry.getValue()) {
                case FALSE:
                    exclude.add(entry.getKey()); break;
                case TRUE:
                    include.add(entry.getKey()); break;
            }
        }

        if(include.size()>0) {
            in += "\n AND genre IN ("+getInClause(include)+") ";
        }
        if(exclude.size()>0) {
            in += "\n AND genre NOT IN ("+getInClause(exclude)+") ";
        }

        return in;
    }

    public String getRatingString() {
        String in="";
        switch (ratingOperator) {
            case GREATERTHAN:
                in += ">=";
                break;
            case IS:
                in += "=";
                break;
            case LESSTHAN:
                in += "<=";
                break;
        }
        in +=rating;
        return in;
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
                ArrayList<String> include = new ArrayList<>();
                ArrayList<String> exclude = new ArrayList<>();
                for (Map.Entry<String, TriStateButton.STATE> entry : tags.entrySet()) {
                    switch (entry.getValue()) {
                        case FALSE:
                            exclude.add(entry.getKey()); break;
                        case TRUE:
                            include.add(entry.getKey()); break;
                    }
                }
                in += getInClause(include, include.size());
                in += "\n AND " + getInClause(exclude, 0);
            } else {
                in+=" 1 ";
            }
            in += " ) ";
            //Include or exclude untagged
            if(unTaggedState.equals(TriStateButton.STATE.ANY)) {
                in += "\n OR tag.value IS NULL ";
            } else if(unTaggedState.equals(TriStateButton.STATE.FALSE)) {
                in += "\n AND tag.value IS NOT NULL ";
            }
        }
        return in;
    }

    private String getInClause(ArrayList<String> include) {
        String in="";
        for(String entry : include) {
            in+="\""+entry+"\",";
        }
        in = in.substring(0, in.length()-1);
        return in;
    }

    private String getInClause(ArrayList<String> include, int length) {
        String in;
        if(include.size()>0) {
            in = " sum(case when tag.value IN (";
            for(String entry : include) {
                in+="\""+entry+"\",";
            }
            in = in.substring(0, in.length()-1);
            in += " ) then 1 else 0 end) = "+length;
        }  else {
            in = " 1 ";
        }
        return in;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return isLocal?name
                +" ("+(query==null?
                    MainActivity.musicLibrary.getNb(getWhere(), getHaving())
                    :MainActivity.musicLibrary.getNb(query))
                +")"
                :name;
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
