package phramusca.com.jamuzremote;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by raph on 18/03/18.
 */

public class VoiceKeyWords {
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
                    new KeyWord("tag", Command.SET_TAGS),

                    new KeyWord("resume", Command.PLAYER_RESUME),
                    new KeyWord("play", Command.PLAYER_RESUME),
                    new KeyWord("continuer", Command.PLAYER_RESUME),
                    new KeyWord("continue", Command.PLAYER_RESUME),
                    new KeyWord("reprendre", Command.PLAYER_RESUME),
                    new KeyWord("lire", Command.PLAYER_RESUME),
                    new KeyWord("lecture", Command.PLAYER_RESUME),

                    new KeyWord("play next track", Command.PLAYER_NEXT),
                    new KeyWord("play next", Command.PLAYER_NEXT),
                    new KeyWord("next track", Command.PLAYER_NEXT),
                    new KeyWord("next", Command.PLAYER_NEXT),
                    new KeyWord("prochaine", Command.PLAYER_NEXT),
                    new KeyWord("prochain", Command.PLAYER_NEXT),
                    new KeyWord("suivante", Command.PLAYER_NEXT),
                    new KeyWord("suivant", Command.PLAYER_NEXT),

                    new KeyWord("pause", Command.PLAYER_PAUSE),
                    new KeyWord("stop", Command.PLAYER_PAUSE),
                    new KeyWord("break", Command.PLAYER_PAUSE),
                    new KeyWord("arret", Command.PLAYER_PAUSE),
                    new KeyWord("arrêt", Command.PLAYER_PAUSE),
                    new KeyWord("arreter", Command.PLAYER_PAUSE),

                    new KeyWord("pullup", Command.PLAYER_PULLUP),
                    new KeyWord("pull up", Command.PLAYER_PULLUP),
                    new KeyWord("pull-up", Command.PLAYER_PULLUP),
                    new KeyWord("poulpe", Command.PLAYER_PULLUP),
                    new KeyWord("replay", Command.PLAYER_PULLUP),
                    new KeyWord("restart", Command.PLAYER_PULLUP),
                    new KeyWord("recommencer", Command.PLAYER_PULLUP),
                    new KeyWord("rejouer", Command.PLAYER_PULLUP)
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
        SET_TAGS,
        PLAYER_RESUME,
        PLAYER_NEXT,
        PLAYER_PAUSE,
        PLAYER_PULLUP,
    }
}
