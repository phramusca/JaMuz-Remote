package phramusca.com.jamuzremote.utils;

import android.content.Context;

import java.io.File;

//https://stackoverflow.com/questions/39444607/a-sure-shot-method-to-find-if-sdcard-is-present
public class ExternalFilesDirs {
    private static File selectedApplicationFilesDir;
    private static File[] externalFilesDir;

    //TODO: What if external SD card is removed or unmounted ?
    // => selectedStorage will change
    // => offer user to move from a storage to another, or exit application until SD card is mounted

    public static void init(Context context) {
        externalFilesDir = context.getExternalFilesDirs(null);
    }

    /**
     * Returns selected application folder:
     * <p><ul>
     * <li>IF AVAILABLE: the real removable sd card, /storage/xxxx-xxxx/Android/com.phramusca.jamuz/files</li>
     * <li>OR BY DEFAULT, the "external" card, the emulated one : /storage/emulated/0/Android//com.phramusca.jamuz/files</li>
     * </ul>
     * If it does not yet exist, it is created.
     * @return selected application folder
     */
    public static File getSelected() {
        if(selectedApplicationFilesDir != null) {
            return selectedApplicationFilesDir;
        }
        if(externalFilesDir.length > 1 && externalFilesDir[1] != null) {
            selectedApplicationFilesDir = externalFilesDir[1]; //External SD card
        } else if(externalFilesDir.length > 0 && externalFilesDir[0] != null) {
            selectedApplicationFilesDir = externalFilesDir[0]; //Internal SD card
        }
        if (selectedApplicationFilesDir != null && !selectedApplicationFilesDir.exists()) {
            if(!selectedApplicationFilesDir.mkdirs()) {
                selectedApplicationFilesDir = null;
            }
        }
        return selectedApplicationFilesDir;
    }
}
