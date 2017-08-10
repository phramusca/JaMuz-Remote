package phramusca.com.jamuzremote;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by raph on 10/06/17.
 */

public class HelperTextFile {

    private static final String TAG = HelperTextFile.class.getName();

    public static void write(Context context, String filename, String json) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            PrintWriter printWriter = new PrintWriter(fos);
            Log.i(TAG, "Writing "+filename+"\n"+json+"\n");
            printWriter.write(json);
            printWriter.flush();
            printWriter.close();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "write", e);
        } catch (IOException e) {
            Log.e(TAG, "write", e);
        }
    }

    public static String read(Context context, String filename) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput(filename);
            Log.i(TAG, "Reading "+filename);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
                Log.i(TAG, "Read \n"+ret+"\n");
            }
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "read" + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "read" + e.toString());
        }
        return ret;
    }
}