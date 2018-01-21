package phramusca.com.jamuzremote;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by raph on 01/05/17.
 */
public final class HelperFile {

    private static final String TAG = HelperFile.class.getSimpleName();
    private static String path = Environment.getExternalStorageDirectory()+"/JaMuz/";

    private HelperFile() {
    }

    public static File createFolder(String folder) {
        File file = getFolder(folder);
        file.mkdirs();
        return file;
    }

    public static File getFolder(String folder) {
        return new File(path+folder+"/");
    }

    private static File getFile(String folder, String filename) {
        return new File(path+folder+"/"+filename);
    }

    public static String read(String folder, String filename) {
        File file = getFile(folder, filename);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            Log.e(TAG, "Error reading file : " + file.getAbsolutePath(), e);
        }
        return text.toString();
    }

    public static void save(String folder, String filename, String text) {
        File file = getFile(folder, filename);
        createFolder(folder);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file.getAbsolutePath(), false));
            out.write(text);
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e(TAG, "Error saving file : "+file.getAbsolutePath(), e);
        }
    }

    public static void delete(String folder, String filename) {
        getFile(folder, filename).delete();
    }

}
