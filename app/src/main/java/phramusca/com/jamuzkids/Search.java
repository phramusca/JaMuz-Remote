package phramusca.com.jamuzkids;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by raph on 18/03/18.
 */

public class Search {
    //TODO: Translate vocal search commands
    //TODO: Document this => help page
    private static final ArrayList<SearchKeyWord> searchKeyWords = new ArrayList<>(
            Arrays.asList(
                    new SearchKeyWord("artiste en cours", SearchType.ARTIST_ONGOING),
                    new SearchKeyWord("artist en cours", SearchType.ARTIST_ONGOING),
                    new SearchKeyWord("artiste courant", SearchType.ARTIST_ONGOING),
                    new SearchKeyWord("artist courant", SearchType.ARTIST_ONGOING),
                    new SearchKeyWord("ongoing artist", SearchType.ARTIST_ONGOING),
                    new SearchKeyWord("current artist", SearchType.ARTIST_ONGOING),
                    new SearchKeyWord("on going artist", SearchType.ARTIST_ONGOING),

                    new SearchKeyWord("on going album", SearchType.ALBUM_ONGOING),
                    new SearchKeyWord("current album", SearchType.ALBUM_ONGOING),
                    new SearchKeyWord("ongoing album", SearchType.ALBUM_ONGOING),
                    new SearchKeyWord("album en cours", SearchType.ALBUM_ONGOING),

                    new SearchKeyWord("artiste", SearchType.ARTIST),
                    new SearchKeyWord("artist", SearchType.ARTIST),

                    new SearchKeyWord("album", SearchType.ALBUM),

                    new SearchKeyWord("liste", SearchType.PLAYLIST),
                    new SearchKeyWord("list", SearchType.PLAYLIST),

                    new SearchKeyWord("playlist", SearchType.PLAYLIST),
                    new SearchKeyWord("playliste", SearchType.PLAYLIST)
            ));

    public static SearchKeyWord get(String spokenText) {
        String searchValue=spokenText.toLowerCase().trim();
        SearchType searchType = SearchType.PLAYLIST;
        for(SearchKeyWord word : searchKeyWords) {
            if(searchValue.startsWith(word.getKeyword())) {
                searchType=word.getType();
                searchValue=spokenText.substring(word.getKeyword().length()).trim();
                break;
            }
        }
        return new SearchKeyWord(searchValue, searchType);
    }

    static class SearchKeyWord {
        private String keyword;
        private SearchType type;

        SearchKeyWord(String keyword, SearchType type) {
            this.keyword = keyword;
            this.type = type;
        }

        String getKeyword() {
            return keyword.toLowerCase().trim();
        }

        SearchType getType() {
            return type;
        }
    }

    enum SearchType {
        PLAYLIST,
        ARTIST,
        ARTIST_ONGOING,
        ALBUM,
        ALBUM_ONGOING
    }
}
