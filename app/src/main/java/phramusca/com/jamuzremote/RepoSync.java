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

    //FIXME: Missing "internal" cleanup now that removed from ServiceScan
    // => Add a loop (in ServiceSync or here) on files in filesystem
    //and remove those if not in files, if files not null
    // => Need to get ServiceScan browser capabilities

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

    private synchronized static void updateFiles(FileInfoReception fileInfoReception) {
        files.row(fileInfoReception.idFile).clear();
        files.put(fileInfoReception.idFile, fileInfoReception.status, fileInfoReception);
    }

    private synchronized static boolean checkFile(FileInfoReception fileInfoReception,
                                                 File receivedFile) {
        if(receivedFile.exists()) {
            if (receivedFile.length() == fileInfoReception.size) {
                Log.i(TAG, "Correct file size: " + receivedFile.length());
                return true;
            } else {
                Log.w(TAG, "File has wrong size. Deleting " + receivedFile.getAbsolutePath());
                receivedFile.delete();
            }
        } else {
            Log.w(TAG, "File does not exits. "+receivedFile.getAbsolutePath());
        }
        return false;
    }

    public synchronized static boolean checkFile(File getAppDataPath,
                                                 FileInfoReception fileInfoReception,
                                                 FileInfoReception.Status status) {

        File receivedFile = new File(getAppDataPath, fileInfoReception.relativeFullPath);

        if(files.containsRow(fileInfoReception.idFile)) {
            if(checkFile(fileInfoReception, receivedFile)) {
                fileInfoReception.status = status;
                updateFiles(fileInfoReception);
                return true;
            } else {
                fileInfoReception.status = FileInfoReception.Status.NEW;
                updateFiles(fileInfoReception);
            }
        } else {
            Log.w(TAG, "files does not contain file. Deleting " + receivedFile.getAbsolutePath());
            receivedFile.delete();
        }
        return false;
    }

    public synchronized static FileInfoReception checkFile(File getAppDataPath, FileInfoReception fileInfoReception) {
        File file = new File(getAppDataPath, fileInfoReception.relativeFullPath);
        if(checkFile(fileInfoReception, file)) {
            if (fileInfoReception.status.equals(FileInfoReception.Status.NEW)) {
                fileInfoReception.status = FileInfoReception.Status.LOCAL;
            }
        } else {
            fileInfoReception.status = FileInfoReception.Status.NEW;
        }
        return fileInfoReception;
    }

    public synchronized static void receivedAck(File getAppDataPath, FileInfoReception fileInfoReception) {
        if(checkFile(getAppDataPath, fileInfoReception,  FileInfoReception.Status.ACK)) {
            //save();
        }
    }

    public synchronized static void set(File getAppDataPath, Map<Integer, FileInfoReception> newTracks) {
        files = HashBasedTable.create();
        for(Map.Entry<Integer, FileInfoReception> entry : newTracks.entrySet()) {
            FileInfoReception fileInfoReception = entry.getValue();
            fileInfoReception=RepoSync.checkFile(getAppDataPath, fileInfoReception);
            files.put(fileInfoReception.idFile, fileInfoReception.status, fileInfoReception);
        }
        save();
    }

    public synchronized static void save() {
        if(files!=null) {
            List<FileInfoReception> filesList = new ArrayList<>();
            for(FileInfoReception file : files.values()) {
                filesList.add(file);
            }
            Gson gson = new Gson();
            HelperFile.write("Sync", "Files.txt", gson.toJson(filesList));
        }
    }

    protected synchronized static void read(File getAppDataPath) {
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
                    for(FileInfoReception fileInfoReception : readList) {
                        fileInfoReception=RepoSync.checkFile(getAppDataPath, fileInfoReception);
                        temp.put(fileInfoReception.idFile, fileInfoReception.status, fileInfoReception);
                    }
                    files = temp;
                }
            }
        }
    }

    public synchronized static int getRemainingSize() {
        return files==null?0:(files.column(FileInfoReception.Status.NEW).size()
                +files.column(FileInfoReception.Status.LOCAL).size()
                +files.column(FileInfoReception.Status.IN_DB).size());
    }

    public synchronized static int getTotalSize() {
        return files==null?0:files.size();
    }

    public synchronized static FileInfoReception take(File getAppDataPath) {
        if (files != null && files.column(FileInfoReception.Status.IN_DB).size() > 0) {
            FileInfoReception fileInfoReception = files.column(FileInfoReception.Status.IN_DB).entrySet().iterator().next().getValue();
            return checkFile(getAppDataPath, fileInfoReception);
        } else if (files != null && files.column(FileInfoReception.Status.LOCAL).size() > 0) {
            FileInfoReception fileInfoReception = files.column(FileInfoReception.Status.LOCAL).entrySet().iterator().next().getValue();
            return checkFile(getAppDataPath, fileInfoReception);
        } else if (files != null && files.column(FileInfoReception.Status.NEW).size() > 0) {
            FileInfoReception fileInfoReception = files.column(FileInfoReception.Status.NEW).entrySet().iterator().next().getValue();
            return checkFile(getAppDataPath, fileInfoReception);
        }
        return null;
    }
}