package phramusca.com.jamuzremote;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by raph on 10/06/17.
 */

//FIXME: Make this a real RepositorySync (only moved out of ServiceSync for now)
public final class RepoSync {

    private static final String TAG = RepoSync.class.getSimpleName();

    private static Map<Integer, FileInfoReception> filesToGet = null;
    //FIXME: private static BlockingQueue<FileInfoReception> filesToGet = new LinkedBlockingQueue<>();

    private static Map<String, FileInfoReception> filesToKeep = null;

    private RepoSync() {
    }

    public synchronized static Map<Integer, FileInfoReception> getFilesToGet() {
        return filesToGet;
    }

    public static Map<String, FileInfoReception> getFilesToKeep() {
        return filesToKeep;
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
        saveFilesLists();
    }

    /*
    public synchronized static void set(Map<String, Track> newTracks) {
        //Adding missing tracks
        for(Map.Entry<String, Track> tag : newTracks.entrySet()) {
            if(tracks.containsKey(tag.getKey())) {

            }
            add(tag);
        }
        //Removing tracks not in input list
        final Iterator<Map.Entry<Integer, String>> it = get().entrySet().iterator();
        while (it.hasNext())
        {
            final Map.Entry<Integer, String> tag = it.next();
            if(HelperLibrary.musicLibrary!=null && !newTags.contains(tag.getValue())) {
                new Thread() {
                    public void run() {
                        int deleted = HelperLibrary.musicLibrary.deleteTag(tag.getKey());
                        if(deleted>0) {
                            it.remove();
                        }
                    }
                }.start();
            }
        }

    }*/

    //FIXME: Read lists separately as they change not in sync

    protected synchronized static void saveFilesLists() {
        //Write list of files to maintain in db
        if(filesToKeep!=null) {
            Gson gson = new Gson();
            HelperFile.write("Sync", "FilesToKeep.txt", gson.toJson(filesToKeep));
        }
        //Write list of files to retrieve
        if(filesToGet!=null) {
            Gson gson = new Gson();
            HelperFile.write("Sync", "filesToGet.txt", gson.toJson(filesToGet));
        }
    }

    protected static synchronized void readFilesLists() {
        String readJson;
        if(filesToKeep==null) {
            //Read FilesToKeep file to get list of files to maintain in db
            readJson = HelperFile.read("Sync", "FilesToKeep.txt");
            if (!readJson.equals("")) {
                filesToKeep = new HashMap<>();
                Gson gson = new Gson();
                Type mapType = new TypeToken<HashMap<String, FileInfoReception>>() {
                }.getType();
                try {
                    filesToKeep = gson.fromJson(readJson, mapType);
                } catch (JsonSyntaxException ex) {
                    Log.e(TAG, "", ex);
                }
            }
        }

        if(filesToGet==null) {
            //Read filesToGet file to get list of files to retrieve
            readJson = HelperFile.read("Sync", "filesToGet.txt");
            if (!readJson.equals("")) {
                filesToGet = new HashMap<>();
                Gson gson = new Gson();
                Type mapType = new TypeToken<HashMap<Integer, FileInfoReception>>() {
                }.getType();
                try {
                    filesToGet = gson.fromJson(readJson, mapType);
                } catch (JsonSyntaxException ex) {
                    Log.e(TAG, "", ex);
                }
            }
        }
    }

    public static void received(int idFile) {
        if(filesToGet.containsKey(idFile)) {
            filesToGet.remove(idFile);
        }
    }
}