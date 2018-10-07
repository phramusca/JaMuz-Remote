package phramusca.com.jamuzremote;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by raph on 18/03/18.
 */

public class Commands {
    //TODO: Translate vocal search commands
    //TODO: Document this => help page
    private static final ArrayList<KeyWord> KEY_WORDS = new ArrayList<>(
            Arrays.asList(
                    new KeyWord("artiste en cours", Command.ARTIST_ONGOING),
                    new KeyWord("artist en cours", Command.ARTIST_ONGOING),
                    new KeyWord("artiste courant", Command.ARTIST_ONGOING),
                    new KeyWord("artist courant", Command.ARTIST_ONGOING),
                    new KeyWord("ongoing artist", Command.ARTIST_ONGOING),
                    new KeyWord("current artist", Command.ARTIST_ONGOING),
                    new KeyWord("on going artist", Command.ARTIST_ONGOING),

                    new KeyWord("on going album", Command.ALBUM_ONGOING),
                    new KeyWord("current album", Command.ALBUM_ONGOING),
                    new KeyWord("ongoing album", Command.ALBUM_ONGOING),
                    new KeyWord("album en cours", Command.ALBUM_ONGOING),

                    new KeyWord("artiste", Command.ARTIST),
                    new KeyWord("artist", Command.ARTIST),

                    new KeyWord("album", Command.ALBUM),

                    //It is also the default
                    new KeyWord("liste", Command.PLAYLIST),
                    new KeyWord("list", Command.PLAYLIST),
                    new KeyWord("playliste", Command.PLAYLIST),
                    new KeyWord("playlist", Command.PLAYLIST),

                    new KeyWord("rate it", Command.SET_RATING),
                    new KeyWord("rate", Command.SET_RATING),
                    new KeyWord("noter", Command.SET_RATING),
                    new KeyWord("note", Command.SET_RATING),

                    new KeyWord("tag it", Command.SET_TAGS),
                    new KeyWord("taguer", Command.SET_TAGS),
                    new KeyWord("tag", Command.SET_TAGS)

                    //FIXME: Add a Cancel keywork

            ));

    public static KeyWord get(String spokenText) {
        String searchValue=spokenText.toLowerCase().trim();
        Command command = Command.PLAYLIST;
        for(KeyWord word : KEY_WORDS) {
            if(searchValue.startsWith(word.getKeyword())) {
                command =word.getCommand();
                searchValue=spokenText.substring(word.getKeyword().length()).trim();
                break;
            }
        }
        return new KeyWord(searchValue, command);
    }

    static class KeyWord {
        private String keyword;
        private Command command;

        KeyWord(String keyword, Command command) {
            this.keyword = keyword;
            this.command = command;
        }

        String getKeyword() {
            return keyword.toLowerCase().trim();
        }

        Command getCommand() {
            return command;
        }
    }

    enum Command {
        PLAYLIST,
        ARTIST,
        ARTIST_ONGOING,
        ALBUM,
        ALBUM_ONGOING,
        SET_RATING,
        SET_TAGS
    }
}
