package phramusca.com.jamuzkids;

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

/**
 *
 * @author phramusca
 */
public final class RepoSync {

    private static final String TAG = RepoSync.class.getName();

    private static Table<Integer, FileInfoReception.Status, FileInfoReception> files = null;

    private synchronized static void updateFiles(FileInfoReception fileInfoReception) {
        files.row(fileInfoReception.idFile).clear();
        files.put(fileInfoReception.idFile, fileInfoReception.status, fileInfoReception);
    }

    /**
     * @param fileInfoReception the one to check
     * @param receivedFile the corresponding File
     * @return true if receivedFile exists and length()==fileInfoReception.size
     */
    private synchronized static boolean checkFile(FileInfoReception fileInfoReception,
                                                  File receivedFile) {
        if(receivedFile.exists()) {
            if (receivedFile.length() == fileInfoReception.size) {
                Log.i(TAG, "Correct file size: " + receivedFile.length());
                return true;
            } else {
                Log.w(TAG, "File has wrong size. Deleting " + receivedFile.getAbsolutePath());
                //noinspection ResultOfMethodCallIgnored
                receivedFile.delete();
            }
        } else {
            Log.w(TAG, "File does not exits. "+receivedFile.getAbsolutePath());
        }
        return false;
    }

    /**
     * Sets status to NEW if fileInfoReception does not exists
     * or to given status if fileInfoReception exists and has correct size.
     * File is deleted if not requested (not in files).
     * @param getAppDataPath application path
     * @param fileInfoReception the one to check
     * @param status status to set if returns true
     * @return true if receivedFile exists and length()==fileInfoReception.size
     */
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
            //noinspection ResultOfMethodCallIgnored
            receivedFile.delete();
        }
        return false;
    }

    /**
     * @param getAppDataPath application path
     * @param fileInfoReception the one to check
     * @return modified fileInfoReception with status to LOCAL if it exists and status was NEW
     *
     */
    private synchronized static FileInfoReception checkFile(File getAppDataPath, FileInfoReception fileInfoReception) {
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

    public synchronized static void receivedAck(FileInfoReception fileInfoReception) {
        if(files.containsRow(fileInfoReception.idFile)) {
            fileInfoReception.status = FileInfoReception.Status.ACK;
            updateFiles(fileInfoReception);
        }
    }

    public synchronized static void receivedInDb(FileInfoReception fileInfoReception) {
        if(files.containsRow(fileInfoReception.idFile)) {
            fileInfoReception.status = FileInfoReception.Status.IN_DB;
            updateFiles(fileInfoReception);
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
            Gson gson = new Gson();
            HelperFile.write("Sync", "Files.txt", gson.toJson(files.values()));
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
                +files.column(FileInfoReception.Status.LOCAL).size());
    }

    public synchronized static long getRemainingFileSize() {
        if(files==null) {
            return 0;
        }
        long nbRemaining=0;
        nbRemaining+=getRemainingFileSize(FileInfoReception.Status.NEW);
        nbRemaining+=getRemainingFileSize(FileInfoReception.Status.LOCAL);
        return nbRemaining;
    }

    private synchronized static long getRemainingFileSize(FileInfoReception.Status status) {
        long nbRemaining=0;
        for(FileInfoReception fileInfoReception : files.column(status).values()) {
            nbRemaining+=fileInfoReception.size;
        }
        return nbRemaining;
    }

    public synchronized static int getTotalSize() {
        return files==null?0:files.size();
    }

    public synchronized static FileInfoReception takeNew() {
        if (files != null && files.column(FileInfoReception.Status.NEW).size() > 0) {
            return files.column(FileInfoReception.Status.NEW)
                    .entrySet().iterator().next().getValue();
        }
        return null;
    }

    public synchronized static ArrayList<FileInfoReception> getInDb() {
        return files==null?new ArrayList<>():new ArrayList<>(files.column(FileInfoReception.Status.IN_DB).values());
    }

    public synchronized static List<FileInfoReception> getLocal() {
        return files==null?new ArrayList<>():new ArrayList<>(files.column(FileInfoReception.Status.LOCAL).values());
    }

    /**
     * Checks if relativeFullPath is in files. Delete file if not.
     * @param relativeFullPath relative full path
     */
    public synchronized static boolean checkFile(File getAppDataPath, String relativeFullPath) {
        FileInfoReception fileInfoReception = new FileInfoReception();
        fileInfoReception.relativeFullPath=relativeFullPath;
        if(files != null && !files.containsValue(fileInfoReception)) {
            Log.i(TAG, "DELETE UNWANTED: "+relativeFullPath);
            File file = new File(getAppDataPath, fileInfoReception.relativeFullPath);
            //noinspection ResultOfMethodCallIgnored
            file.delete();
            return true;
        }
        return false;
    }
}