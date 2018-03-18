package phramusca.com.jamuzremote;

/**
 * Created by raph on 18/03/18.
 */

public class SearchKeyWord {
    private String keyword;
    private SearchType type;

    public SearchKeyWord(String keyword, SearchType type) {
        this.keyword = keyword;
        this.type = type;
    }

    public String getKeyword() {
        return keyword.toLowerCase().trim();
    }

    public SearchType getType() {
        return type;
    }
}

