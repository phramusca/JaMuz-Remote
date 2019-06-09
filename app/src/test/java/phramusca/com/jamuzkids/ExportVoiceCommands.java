package phramusca.com.jamuzkids;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//To export Voice Commands for documentation on wiki
public class ExportVoiceCommands { //FIXME: Continue, split by language, export to wiki style, ...

    @Test
    public void main() {
        ArrayList<VoiceKeyWords.KeyWord> voiceKeyWords = VoiceKeyWords.get();

        //Map list of keywords to commands
        Map<VoiceKeyWords.Command, ArrayList<String>> exportMap = new HashMap<>();
        for (VoiceKeyWords.KeyWord keyWord : voiceKeyWords) {
            ArrayList<String> keyWords;
            if(exportMap.containsKey(keyWord.getCommand())) {
                keyWords = exportMap.get(keyWord.getCommand());
            } else {
                keyWords = new ArrayList<>();
            }
            keyWords.add(keyWord.getKeyword());
            exportMap.put(keyWord.getCommand(), keyWords);
        }

        //Display Command \ list of keywords
        for(VoiceKeyWords.Command command : exportMap.keySet()) {
            System.out.println(command.name());
            for(String keyWord : exportMap.get(command)) {
                System.out.println("\t\t"+keyWord);
            }
        }


    }
}
