package phramusca.com.jamuzremote;

/**
 * Created by raph on 10/06/17.
 */

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class RepoSync {

    private static final String TAG = RepoSync.class.getSimpleName();

    private static Map<Integer, FileInfoReception> filesToGet = null;
    private static Map<String, FileInfoReception> filesToKeep = null;

    private RepoSync() {
    }

    public synchronized static void scannedFile(File getAppDataPath, File file) {
        String absolutePath=file.getAbsolutePath();
        String fileKey = absolutePath.substring(getAppDataPath.getAbsolutePath().length()+1);
        if(filesToKeep!=null && !filesToKeep.containsKey(fileKey)) {
            Log.i(TAG, "Deleting file "+absolutePath);
            file.delete();
        } else if(filesToKeep!=null && filesToKeep.containsKey(fileKey)) {
            HelperLibrary.insertOrUpdateTrackInDatabase(absolutePath, filesToKeep.get(fileKey));
        } else {
            HelperLibrary.insertOrUpdateTrackInDatabase(absolutePath, null);
        }
    }

    public static boolean received(File getAppDataPath, FileInfoReception fileInfoReception) {
        File receivedFile = new File(getAppDataPath.getAbsolutePath()+File.separator
                +fileInfoReception.relativeFullPath);
        if(filesToGet.containsKey(fileInfoReception.idFile)) {
            if(receivedFile.exists()) {
                if (receivedFile.length() == fileInfoReception.size) {
                    Log.i(TAG, "Saved file size: " + receivedFile.length());
                    HelperLibrary.insertOrUpdateTrackInDatabase(receivedFile.getAbsolutePath(), fileInfoReception);
                    return true;
                } else {
                    Log.w(TAG, "File has wrong size. Deleting " + receivedFile.getAbsolutePath());
                    receivedFile.delete();
                }
            } else {
                Log.w(TAG, "File does not exits. "+receivedFile.getAbsolutePath());
            }
        } else if(!filesToKeep.containsKey(fileInfoReception.relativeFullPath)) {
            Log.w(TAG, "File not requested. Deleting "+receivedFile.getAbsolutePath());
            receivedFile.delete();
        }
        return false;
    }

    public static void receivedAck(int idFile) {
        if(filesToGet.containsKey(idFile)) {
            filesToGet.remove(idFile);
            saveFilesToGet();
        }
    }

    public synchronized static void set(File getAppDataPath, Map<Integer, FileInfoReception> newTracks) {
        filesToGet = new HashMap<>();
        filesToKeep = new HashMap<>();
        for(Map.Entry<Integer, FileInfoReception> entry : newTracks.entrySet()) {
            FileInfoReception fileReceived = entry.getValue();
            filesToKeep.put(fileReceived.relativeFullPath, fileReceived);
            File localFile = new File(getAppDataPath, fileReceived.relativeFullPath);
            if(!localFile.exists()) {
                filesToGet.put(fileReceived.idFile, fileReceived);
            }
        }
        saveBothLists();
    }

    protected synchronized static void saveBothLists() {
        saveFileToKeep();
        saveFilesToGet();
    }

    public static void saveFilesToGet() {
        //Write list of files to retrieve
        if(filesToGet!=null) {
            Gson gson = new Gson();
            HelperFile.write("Sync", "filesToGet.txt", gson.toJson(filesToGet));
        }
    }

    private synchronized static void saveFileToKeep() {
        //Write list of files to maintain in db
        if(filesToKeep!=null) {
            Gson gson = new Gson();
            HelperFile.write("Sync", "FilesToKeep.txt", gson.toJson(filesToKeep));
        }
    }

    protected static synchronized void read() {
        String readJson;
        if(filesToKeep==null) {
            //Read FilesToKeep file to get list of files to maintain in db
            readJson = HelperFile.read("Sync", "FilesToKeep.txt");
            if (!readJson.equals("")) {
                Map<String, FileInfoReception> readList = new HashMap<>();
                Gson gson = new Gson();
                Type mapType = new TypeToken<HashMap<String, FileInfoReception>>() {}.getType();
                try {
                    readList = gson.fromJson(readJson, mapType);
                } catch (JsonSyntaxException ex) {
                    Log.e(TAG, "", ex);
                }
                if(readList.size()>0) {
                    filesToKeep=readList;
                }
            }
        }

        if(filesToGet==null) {
            //Read filesToGet file to get list of files to retrieve
            readJson = HelperFile.read("Sync", "filesToGet.txt");
            if (!readJson.equals("")) {
                Map<Integer, FileInfoReception> readList = new HashMap<>();
                Gson gson = new Gson();
                Type mapType = new TypeToken<HashMap<Integer, FileInfoReception>>() {
                }.getType();
                try {
                    readList = gson.fromJson(readJson, mapType);
                } catch (JsonSyntaxException ex) {
                    Log.e(TAG, "", ex);
                }
                if(readList.size()>0) {
                    filesToGet=readList;
                }
            }
        }
    }

    public static int getRemainingSize() {
        return filesToGet==null?0:filesToGet.size();
    }

    public static int getTotalSize() {
        return filesToKeep==null?0:filesToKeep.size();
    }

    public static FileInfoReception take() {
        if (filesToKeep != null && filesToGet != null) {
            if (filesToGet.size() > 0) {
                return filesToGet.entrySet().iterator().next().getValue();
            }
        }
        return null;
    }
}