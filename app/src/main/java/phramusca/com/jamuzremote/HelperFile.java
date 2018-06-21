package phramusca.com.jamuzremote;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
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

/**
 * Created by raph on 01/05/17.
 */
public final class HelperFile {

    private static final String TAG = HelperFile.class.getName();
    private static String path = Environment.getExternalStorageDirectory()+"/JaMuzKids/";

    private HelperFile() {
    }

    public static File createFolder(String folder) {
        File file = getFolder(folder);
        if(!file.exists()) {
            if(!file.mkdirs()) {
                return null;
            }
        }
        return file;
    }

    @NonNull
    private static File getFolder(String folder) {
        return new File(path+folder+"/");
    }

    @NonNull
    private static File getFile(String folder, String filename) {
        return new File(path+folder+"/"+filename);
    }

    @NonNull
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

    public static boolean write(String folder, String filename, String text) {
        File file = getFile(folder, filename);
        createFolder(folder);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file.getAbsolutePath(), false));
            out.write(text);
            out.flush();
            out.close();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error saving file : "+file.getAbsolutePath(), e);
            return false;
        }
    }

    public static void delete(String folder, String filename) {
        //noinspection ResultOfMethodCallIgnored
        getFile(folder, filename).delete();
    }

    //Writes to internal memory application folder. File is removed when application is uninstalled
    public static void write(Context context, String filename, String text) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            PrintWriter printWriter = new PrintWriter(fos);
            Log.i(TAG, "Writing "+filename+"\n"+(text.length()<150?text:text.substring(0, 150))+"\n");
            printWriter.write(text);
            printWriter.flush();
            printWriter.close();
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "write", e);
        }
    }

    //reads from internal memory application folder. File is removed when application is uninstalled
    public static String read(Context context, String filename) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput(filename);
            Log.i(TAG, "Reading "+filename);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
                Log.d(TAG, "Read \n"+ret+"\n");
            }
        } catch (IOException e) {
            Log.e(TAG, "read" + e.toString());
        }
        return ret;
    }

}
