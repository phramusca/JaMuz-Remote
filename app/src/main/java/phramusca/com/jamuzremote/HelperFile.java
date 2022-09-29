package phramusca.com.jamuzremote;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

//https://stackoverflow.com/questions/39444607/a-sure-shot-method-to-find-if-sdcard-is-present
public final class HelperFile {

    private static final String TAG = HelperFile.class.getName();
    private static File selectedApplicationFilesDir;

    //TODO: What if external SD card is removed or unmounted ?
    // => selectedStorage will change
    // => offer user to move from a storage to another, or exit application until SD card is mounted

    /**
     * Selects appropriate application folder:
     * <p><ul>
     * <li>IF AVAILABLE: the real removable sd card, /storage/xxxx-xxxx/Android/com.phramusca.jamuz/files</li>
     * <li>OR BY DEFAULT, the "external" card, the emulated one : /storage/emulated/0/Android/com.phramusca.jamuz/files</li>
     * </ul>
     * If it does not yet exist, it is created.
     * @return true if a writable application folder is available.
     */
    static boolean init(Context context) {
        File[] externalFilesDir = context.getExternalFilesDirs(null);
        if(externalFilesDir.length > 1 && externalFilesDir[1] != null) {
            selectedApplicationFilesDir = externalFilesDir[1]; //External SD card
        } else if(externalFilesDir.length > 0 && externalFilesDir[0] != null) {
            selectedApplicationFilesDir = externalFilesDir[0]; //Internal SD card
        }
        if (selectedApplicationFilesDir != null && !selectedApplicationFilesDir.exists()) {
            if(!selectedApplicationFilesDir.mkdirs()) {
                return false;
            }
        }
        return selectedApplicationFilesDir != null;
    }

    static File getAudioRootFolder() {
        return getFolder("audio");
    }

    static File getFolder(String... folders) {
        File file = selectedApplicationFilesDir;
        for (String subFolder : folders) {
            file = new File(file, subFolder);
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        return file;
    }

    static File getFile(String filename, String... folders) {
        return new File(getFolder(folders), filename);
    }

    static String readTextFile(String filename, String... folders) {
        File file = getFile(filename, folders);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading file : " + file.getAbsolutePath(), e); //NON-NLS
        }
        return text.toString();
    }

    static boolean writeTextFile(String filename, String text, String... folders) {
        File file = getFile(filename, folders);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file.getAbsolutePath(), false));
            out.write(text);
            out.flush();
            out.close();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error saving file : " + file.getAbsolutePath(), e); //NON-NLS
            return false;
        }
    }

    static void delete(String filename, String... folders) {
        //noinspection ResultOfMethodCallIgnored
        getFile(filename, folders).delete();
    }

    //Writes to internal memory application folder. File is removed when application is uninstalled
    static void writeTextFileToInternalMemory(Context context, String filename, String text) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            PrintWriter printWriter = new PrintWriter(fos);
            Log.i(TAG, "Writing " + filename + "\n" + (text.length() < 150 ? text : text.substring(0, 150)) + "\n"); //NON-NLS
            printWriter.write(text);
            printWriter.flush();
            printWriter.close();
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "write", e); //NON-NLS
        }
    }

    //reads from internal memory application folder. File is removed when application is uninstalled
    static String readTextFileFromInternalMemory(Context context, String filename) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput(filename);
            Log.i(TAG, "Reading " + filename); //NON-NLS
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
                Log.d(TAG, "Read \n" + ret + "\n"); //NON-NLS
            }
        } catch (IOException e) {
            Log.e(TAG, "read" + e); //NON-NLS
        }
        return ret;
    }
}
