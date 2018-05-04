package phramusca.com.jamuzremote;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by raph on 11/06/17.
 */
public class Playlist implements Comparable {

    private String name;
    private Map<String, TriStateButton.STATE> tags = new HashMap<>();
    private Map<String, TriStateButton.STATE> genres = new HashMap<>();
    private TriStateButton.STATE unTaggedState = TriStateButton.STATE.ANY;
    private int rating=0;
    private Operator ratingOperator = Playlist.Operator.GREATERTHAN;
    private boolean isLocal;
    private String artist;
    private String album;
    private Order order=Order.PLAYCOUNTER_LASTPLAYED;
    private int limitValue=0;
    private String limitUnit ="minutes";

    Playlist(String name, boolean isLocal) {
        this.name = name;
        this.isLocal = isLocal;
    }

    public List<Track> getTracks() {
        return getTracks(-1);
    }

    public List<Track> getTracks(int limit) {
        return getTracks(limit, new ArrayList<>());
    }

    public List<Track> getTracks(int limit, List<Integer> IDs) {
        if(HelperLibrary.musicLibrary!=null) {
            return HelperLibrary.musicLibrary.getTracks(getWhere(IDs), getHaving(), order.value, limit);
        }
        return new ArrayList<>();
    }

    public Set<Map.Entry<String, TriStateButton.STATE>> getTags() {
        return tags.entrySet();
    }

    private String getRatingString() {
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

    public String getSummary() {
        String in=" ";
        in +=getRatingString();
        Lists tagsLists = new Lists();
        Lists genresLists = new Lists(genres);

        String nullStatus="";
        switch (unTaggedState) {
            case TRUE:
                nullStatus= "null only";
                break;
            case FALSE:
                nullStatus= "Not null";
                tagsLists = new Lists(tags);
                break;
            case ANY:
                nullStatus="null incl.";
                tagsLists = new Lists(tags);
                break;
        }

        in+=" | "+nullStatus;
        int max=20;
        if (tagsLists.isIncluded() || genresLists.isIncluded()) {
            ArrayList<String> included = new ArrayList<>();
            if(tagsLists.isIncluded()){
                included.addAll(tagsLists.getIncluded());
            }
            included.addAll(genresLists.getIncluded());
            in+=" | Incl.: "+getString(included, max);
        }

        if (tagsLists.isExcluded() || genresLists.isExcluded()) {
            ArrayList<String> excluded = new ArrayList<>();
            if(tagsLists.isIncluded()){
                excluded.addAll(tagsLists.getExcluded());
            }
            excluded.addAll(genresLists.getExcluded());
            in+=" | Excl.: "+getString(excluded, max);
        }

        in+=" | "+order.toString();
        in+=" | "+ (limitValue<0?"":limitValue+" "+limitUnit);

        return in;
    }

    public Set<Map.Entry<String, TriStateButton.STATE>> getGenres() {
        return genres.entrySet();
    }

    public void setLimitUnit(String limitUnit) {
        this.limitUnit = limitUnit;
    }

    public String getLimitUnit() {
        return limitUnit;
    }

    public int getLimitValue() {
        return limitValue;
    }

    public void setLimitValue(int limitValue) {
        this.limitValue = limitValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    private class Lists {
        ArrayList<String> include = new ArrayList<>();
        ArrayList<String> exclude = new ArrayList<>();

        Lists() {
        }

        Lists(Map<String, TriStateButton.STATE> stateMap) {
            if(stateMap.size()>0) {
                for (Map.Entry<String, TriStateButton.STATE> entry : stateMap.entrySet()) {
                    switch (entry.getValue()) {
                        case FALSE:
                            exclude.add(entry.getKey()); break;
                        case TRUE:
                            include.add(entry.getKey()); break;
                    }
                }
            }
        }

        ArrayList<String> getIncluded() {
            return include;
        }

        ArrayList<String> getExcluded() {
            return exclude;
        }

        boolean isIncluded() {
            return include.size()>0;
        }

        boolean isExcluded() {
            return exclude.size()>0;
        }
    }

    @SuppressWarnings("SameParameterValue")
    private String getString(ArrayList<String> strings, int max) {
        StringBuilder out = new StringBuilder();
        if(strings.size()>0) {
            for(int i=0; i<max; i++) {
                if(strings.size()>i) {
                    out.append(strings.get(i)).append(", ");
                }
            }
            out = new StringBuilder(out.substring(0, out.length()-2));
            if(strings.size()>max) {
                out.append(" (+").append(strings.size()-max).append(")");
            }
        }
        return out.toString();
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

    private String getWhere(List<Integer> IDs) {

        String in = "WHERE rating "+getRatingString()+" ";

        if(limitValue>0) {
            in += "\n AND lastPlayed < datetime(datetime('now'), '-" + limitValue + " " + limitUnit + "')";
        }

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

        if(artist!=null) {
            in += "\n AND artist LIKE \"%"+artist+"%\" ";
        }

        if(album!=null) {
            in += "\n AND album LIKE \"%"+album+"%\" ";
        }

        in+=getCSVlist(IDs);

        return in;
    }

    private static String getCSVlist(List<Integer> IDs) {
        StringBuilder builder = new StringBuilder();
        if(IDs.size()>0) {
            builder.append("\n AND tracks.ID NOT IN (");
            for (int integer : IDs) {
                builder.append(integer).append(",");
            }
            builder.deleteCharAt(builder.length() - 1).append(") ");
        }
        return builder.toString();
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
        StringBuilder in = new StringBuilder();
        for(String entry : include) {
            in.append("\"").append(entry).append("\",");
        }
        return in.toString().substring(0, in.length()-1);
    }

    private String getInClause(ArrayList<String> include, int length) {
        StringBuilder in = new StringBuilder();
        if(include.size()>0) {
            in.append(" sum(case when tag.value IN (");
            for(String entry : include) {
                in.append("\"").append(entry).append("\",");
            }
            in = new StringBuilder(in.toString().substring(0, in.length()-1));
            in.append(" ) then 1 else 0 end) = ").append(length);
        }  else {
            in.append(" 1 ");
        }
        return in.toString();
    }

    public String getName() {
        return name;
    }

    private int nbFiles=-1;

    @Override
    public String toString() {
        return isLocal?
                name+" ("+nbFiles+")"
                :name;
    }

    public void getNbFiles() {
        if(HelperLibrary.musicLibrary!=null) {
            nbFiles=HelperLibrary.musicLibrary.getNb(getWhere(new ArrayList<>()), getHaving());
        }
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return (this.name.compareTo(((Playlist) o).name));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Playlist other = (Playlist) obj;
        return Objects.equals(this.name, other.name);
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    /**
     * Operator for filters
     */
    public enum Operator {
        IS("="), //NOI18N
        LESSTHAN("<="), //NOI18
        GREATERTHAN(">="); //NOI18N

        private final String display;
        Operator(String display) {
            this.display = display;
        }
        @Override
        public String toString() {
            return display;
        }
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public enum Order {
        RANDOM("ORDER BY RANDOM()", "Random"), //NOI18N
        PLAYCOUNTER_LASTPLAYED("ORDER BY playCounter, lastPlayed", "Least played first"); //NOI18N

        private final String value;
        private final String display;
        Order(String value, String display) {
            this.value = value;
            this.display = display;
        }
        @Override
        public String toString() {
            return display;
        }
    }

}
