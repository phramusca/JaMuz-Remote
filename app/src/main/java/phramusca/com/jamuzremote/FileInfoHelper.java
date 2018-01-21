package phramusca.com.jamuzremote;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by raph on 01/05/17.
 */
public final class FileInfoHelper<T> {

    private static final String TAG = FileInfoHelper.class.getSimpleName();

    private FileInfoHelper() {
    }

    private Map<T, FileInfoReception> read(String readJson) {
        //Read filesToGet file to get list of files to retrieve
        //readJson = HelperTextFile.read(this, "filesToGet.txt");
        Map<T, FileInfoReception> filesToGet = null;
        if(!readJson.equals("")) {
            filesToGet = new HashMap<>();
            Gson gson = new Gson();
            Type mapType = new TypeToken<HashMap<T, FileInfoReception>>(){}.getType();
            try {
                filesToGet = gson.fromJson(readJson, mapType);
            } catch (JsonSyntaxException ex) {
                Log.e(TAG, "", ex);
            }
        }
        return filesToGet;
    }

    public void save(Map<T, FileInfoReception> filesToGet, String fileName) {
        if(filesToGet!=null) {
            Gson gson = new Gson();
            save(gson.toJson(filesToGet), fileName);
        }
    }

    private void save(String text, String fileName) {
        //HelperTextFile.write(this, fileName, text);
    }

}
