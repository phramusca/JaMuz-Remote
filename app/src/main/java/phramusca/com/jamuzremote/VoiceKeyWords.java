package phramusca.com.jamuzremote;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by raph on 18/03/18.
 */

//TODO: Make keywords configurable in application

public class VoiceKeyWords {
    private static final ArrayList<KeyWord> KEY_WORDS = new ArrayList<>();

    public static void set(Context context) {
        for (Command command : Command.values()) {
            if(!command.equals(Command.UNKNOWN)) {
                for (String keyword : context.getResources().getStringArray(command.resId)) {
                    KEY_WORDS.add(new KeyWord(keyword, command));
                }
            }
        }
    }

    public static KeyWord get(String spokenText) {
        String searchValue = spokenText.toLowerCase().trim();
        Command command = Command.UNKNOWN;
        for (KeyWord word : KEY_WORDS) {
            if (searchValue.startsWith(word.getKeyword())) {
                command = word.getCommand();
                searchValue = spokenText.substring(word.getKeyword().length()).trim();
                break;
            }
        }
        return new KeyWord(searchValue, command);
    }

    public static ArrayList<KeyWord> get() {
        return KEY_WORDS;
    }

    static class KeyWord {
        private final String keyword;
        private final Command command;

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
        PLAY_ARTIST_ONGOING(R.array.voiceCommands_PLAY_ARTIST_ONGOING),
        PLAY_ALBUM_ONGOING(R.array.voiceCommands_PLAY_ALBUM_ONGOING),
        PLAY_ARTIST(R.array.voiceCommands_PLAY_ARTIST),
        PLAY_ALBUM(R.array.voiceCommands_PLAY_ALBUM),

        PLAY_PLAYLIST(R.array.voiceCommands_PLAY_PLAYLIST), //Also the default

        SET_RATING(R.array.voiceCommands_SET_RATING),
        SET_TAGS(R.array.voiceCommands_SET_TAGS),
        SET_GENRE(R.array.voiceCommands_SET_GENRE),

        PLAYER_RESUME(R.array.voiceCommands_PLAYER_RESUME),
        PLAYER_NEXT(R.array.voiceCommands_PLAYER_NEXT),
        PLAYER_PREVIOUS(R.array.voiceCommands_PLAYER_PREVIOUS),
        PLAYER_PAUSE(R.array.voiceCommands_PLAYER_PAUSE),
        PLAYER_PULLUP(R.array.voiceCommands_PLAYER_PULLUP),

        UNKNOWN(-1);

        private final int resId;

        Command(int resId) {
            this.resId = resId;
        }
    }
}
