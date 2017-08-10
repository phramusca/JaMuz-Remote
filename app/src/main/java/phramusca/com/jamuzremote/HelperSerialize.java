package phramusca.com.jamuzremote;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.Map;

/**
 * Created by raph on 10/06/17.
 */

public class HelperSerialize {

    private static final String TAG = HelperSerialize.class.getName();
    //In internal SD emulated storage:
    //FIXME: Store in ext SD card
    //private static final String PATH = Environment.getExternalStorageDirectory()+File.separator+"JaMuz";


    public static <T extends Map> void writeToFile(Context context, T object, String filename, Class<T> type) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(type.cast(object));
            os.close();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "", e);
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }
    }

    public static <T> T getFromFile(Context context, String filename, Class<T> type) {
        FileInputStream fis = null;
        T map=null;
        try {
            fis = context.openFileInput(filename);
            ObjectInputStream is = new ObjectInputStream(fis);
            map = type.cast(is.readObject());
            is.close();
            fis.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "", e);
        } catch (OptionalDataException e) {
            Log.e(TAG, "", e);
        } catch (StreamCorruptedException e) {
            Log.e(TAG, "", e);
        } catch (IOException e) {
            Log.e(TAG, "", e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "", e);
        }
        return map;
    }
}