package phramusca.com.jamuzremote;

/**
 * Created by raph on 10/06/17.
 */

import android.util.Log;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RepoSync {

    private static final String TAG = RepoSync.class.getSimpleName();

    private static Table<Integer, FileInfoReception.Status, FileInfoReception> files = null;

    //FIXME: Use the following for the "internal" scan replacement
/*    public synchronized static void scannedFile(File getAppDataPath, File file) {
        String absolutePath=file.getAbsolutePath();
        String relativeFullPath = absolutePath.substring(getAppDataPath.getAbsolutePath().length()+1);
        if(files!=null && !files.containsRow(relativeFullPath)) {
            Log.i(TAG, "Deleting file "+absolutePath);
            file.delete();
        } else if(files!=null && files.containsRow(relativeFullPath)) {
            Map<FileInfoReception.Status, FileInfoReception> map = files.row(relativeFullPath);
            FileInfoReception fileInfoReception = map.entrySet().iterator().next().getValue();
            HelperLibrary.insertOrUpdateTrackInDatabase(absolutePath, fileInfoReception);
        } else {
            HelperLibrary.insertOrUpdateTrackInDatabase(absolutePath, null);
        }
    }*/

    public synchronized static boolean received(File getAppDataPath, FileInfoReception fileInfoReception) {
        File receivedFile = new File(getAppDataPath.getAbsolutePath()+File.separator
                +fileInfoReception.relativeFullPath);
        if(files.containsRow(fileInfoReception.idFile)) {
            if(receivedFile.exists()) {
                if (receivedFile.length() == fileInfoReception.size) {
                    Log.i(TAG, "Saved file size: " + receivedFile.length());

                    //FIXME: fileInfoReception.relativeFullPath may have changed and so be different to files
                    //=> Update files ? Is this needed ?


                    HelperLibrary.insertOrUpdateTrackInDatabase(receivedFile.getAbsolutePath(), fileInfoReception);
                    return true;
                } else {
                    Log.w(TAG, "File has wrong size. Deleting " + receivedFile.getAbsolutePath());
                    receivedFile.delete();
                }
            } else {
                Log.w(TAG, "File does not exits. "+receivedFile.getAbsolutePath());
            }
        } else {
            //TODO: Pb (unlikely): file has been requested (taken from files) BUT not found in files ! => Looping on that file
            receivedFile.delete();
        }
        return false;
    }

    public synchronized static void receivedAck(FileInfoReception fileInfoReception) {
        if(files.containsRow(fileInfoReception.idFile)) {
            files.row(fileInfoReception.idFile).clear();
            fileInfoReception.status=FileInfoReception.Status.ACK;
            files.put(fileInfoReception.idFile, fileInfoReception.status, fileInfoReception);
            saveFiles();
        }
    }

    public synchronized static void set(File getAppDataPath, Map<Integer, FileInfoReception> newTracks) {
        files = HashBasedTable.create();
        for(Map.Entry<Integer, FileInfoReception> entry : newTracks.entrySet()) {
            FileInfoReception fileReceived = entry.getValue();
            File localFile = new File(getAppDataPath, fileReceived.relativeFullPath);
            fileReceived.status = localFile.exists()?FileInfoReception.Status.LOCAL:FileInfoReception.Status.NEW;
            files.put(fileReceived.idFile, fileReceived.status, fileReceived);
        }
        saveFiles();
    }

    //FIXME: Save less often, especially NOT after each ack
    //=> It should not be important if status is not saved for any reason
    //At read(), status should be checked:
    // - NEW => NEW or LOCAL
    // - LOCAL => LOCAL or NEW
    // - ACK => ACK or NEW
    public synchronized static void saveFiles() {
        if(files!=null) {
            List<FileInfoReception> filesList = new ArrayList<>();
            for(FileInfoReception file : files.values()) {
                filesList.add(file);
            }
            Gson gson = new Gson();
            HelperFile.write("Sync", "Files.txt", gson.toJson(filesList));
        }
    }

    protected synchronized static void read() {
        String readJson;
        if(files==null) {
            readJson = HelperFile.read("Sync", "Files.txt");
            if (!readJson.equals("")) {
                List<FileInfoReception> readList = new ArrayList<>();
                Gson gson = new Gson();
                Type mapType = new TypeToken<List<FileInfoReception>>() {}.getType();
                try {
                    readList = gson.fromJson(readJson, mapType);
                } catch (JsonSyntaxException ex) {
                    Log.e(TAG, "", ex);
                }
                if(readList.size()>0) {
                    Table<Integer, FileInfoReception.Status, FileInfoReception> temp = HashBasedTable.create();
                    for(FileInfoReception file : readList) {
                        temp.put(file.idFile, file.status, file);
                    }
                    files = temp;
                }
            }
        }
    }

    public synchronized static int getRemainingSize() {
        return files==null?0:files.column(FileInfoReception.Status.NEW).size();
    }

    public synchronized static int getTotalSize() {
        return files==null?0:files.size();
    }

    public synchronized static FileInfoReception take() {
        if (files != null && files.column(FileInfoReception.Status.NEW).size() > 0) {
            return files.column(FileInfoReception.Status.NEW).entrySet().iterator().next().getValue();
        }
        return null;
    }
}