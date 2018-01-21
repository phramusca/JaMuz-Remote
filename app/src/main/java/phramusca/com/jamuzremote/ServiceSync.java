package phramusca.com.jamuzremote;

/**
 * Created by raph on 10/06/17.
 */

import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ServiceSync extends ServiceBase {

    private static final String TAG = ServiceSync.class.getSimpleName();
    private ClientSync clientSync;
    private Notification notificationSync;

    @Override
    public void onCreate(){
        notificationSync = new Notification(this, 1, "Sync");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        final ClientInfo clientInfo = (ClientInfo)intent.getSerializableExtra("clientInfo");
        new Thread() {
            public void run() {
                helperNotification.notifyBar(notificationSync, "Reading lists ... ");
                readFilesLists();
                clientSync =  new ClientSync(clientInfo, new CallBackSync());
                helperNotification.notifyBar(notificationSync, "Connecting ... ");
                clientSync.connect();
            }
        }.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        stopSync(false);
        saveFilesLists();
        super.onDestroy();
    }

    private static final Object timerLock = new Object();

    private CountDownTimer timerWatchTimeout= new CountDownTimer(0, 0) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {

        }
    };

    private void cancelWatchTimeOut() {
        Log.i(TAG, "timerWatchTimeout.cancel()");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized(timerLock) {
                    if(timerWatchTimeout!=null) {
                        timerWatchTimeout.cancel(); //Cancel previous if any
                    }
                }
            }
        });
    }

    private void watchTimeOut(final long size) {
        cancelWatchTimeOut();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized(timerLock) {

                    long minTimeout =  15 * 1000;  //Min timeout 15s (+ 15s by Mo)
                    long maxTimeout =  120 * 1000; //Max timeout 2 min

                    long timeout = size<1000000?minTimeout:((size / 1000000) * minTimeout);
                    timeout = timeout>maxTimeout?maxTimeout:timeout;
                    timerWatchTimeout = new CountDownTimer(timeout, timeout/10) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            Log.i(TAG, "Seconds Remaining: "+ (millisUntilFinished/1000));
                        }

                        @Override
                        public void onFinish() {
                            stopSync(true);
                        }
                    };
                    Log.i(TAG, "timerWatchTimeout.start()");
                    timerWatchTimeout.start();
                }
            }
        });
    }

    private void stopSync(boolean reconnect) {
        cancelWatchTimeOut();
        if(clientSync!=null) {
            clientSync.close(reconnect);
        }
        if(!reconnect) {
            sendMessage("enableSync");
            stopSelf();
        }
    }

    class CallBackSync implements ICallBackSync {

        private final String TAG = MainActivity.class.getSimpleName()+"."+CallBackSync.class.getSimpleName();

        @Override
        public void receivedJson(final String msg) {
            try {
                final JSONObject jObject = new JSONObject(msg);
                String type = jObject.getString("type");
                switch(type) {
                    case "insertDeviceFileAck":
                        String status = jObject.getString("status");
                        int idFile = jObject.getInt("idFile");
                        boolean requestNextFile = jObject.getBoolean("requestNextFile");
                        if(status.equals("OK")) {
                            sendMessage("refreshSpinner(true)");

                            //FIXME: Store status to manage what to do at any stage
                            //1-TOGET
                            //2-GOT
                            //3-InsertOK

                            //e-InsertKO
                            //e-ERROR (reading tags for instance; to be read at last with max retry count)
                            if(filesToGet.containsKey(idFile)) {
                                filesToGet.remove(idFile);
                                cancelWatchTimeOut();
                            }
                        }
                        if(requestNextFile) {
                            requestNextFile(true);
                        }
                        break;
                    case "StartSync":
                        helperNotification.notifyBar(notificationSync, "Requested first file ... ");
                        requestNextFile(false);
                        break;
                    case "SEND_DB":
                        helperNotification.notifyBar(notificationSync, "Sending database ... ");
                        clientSync.sendDatabase(); //TODO: Move to ClientSync
                        break;
                    case "FilesToGet":
                        helperNotification.notifyBar(notificationSync, "Received new list of files to get ... ");
                        filesToGet = new HashMap<>();
                        filesToKeep = new HashMap<>();
                        JSONArray files = (JSONArray) jObject.get("files");
                        for(int i=0; i<files.length(); i++) {
                            FileInfoReception fileReceived = new FileInfoReception((JSONObject) files.get(i));
                            filesToKeep.put(fileReceived.relativeFullPath, fileReceived);
                            File localFile = new File(getAppDataPath, fileReceived.relativeFullPath);
                            if(!localFile.exists()) {
                                filesToGet.put(fileReceived.idFile, fileReceived);
                            }
                            else {
                                clientSync.ackFileReception(fileReceived.idFile, false);
                            }
                        }
                        requestNextFile(true);
                        break;
                    case "tags":
                        helperNotification.notifyBar(notificationSync, "Received tags ... ");
                        new Thread() {
                            public void run() {
                                try {
                                    //Adding missing tags
                                    final JSONArray jsonTags = (JSONArray) jObject.get("tags");
                                    for(int i=0; i<jsonTags.length(); i++) {
                                        final String tag = (String) jsonTags.get(i);
                                        RepositoryTags.add(tag);
                                    }
                                    //Deleting tags that have been removed in server
                                    final List<String> list = new ArrayList<>();
                                    for(int i = 0; i < jsonTags.length(); i++){
                                        list.add((String) jsonTags.get(i));
                                    }
                                    Iterator<Map.Entry<Integer, String>> it = RepositoryTags.getTags().entrySet().iterator();
                                    while (it.hasNext())
                                    {
                                        Map.Entry<Integer, String> tag = it.next();
                                        if(!list.contains(tag.getValue())) {
                                            if(HelperLibrary.musicLibrary!=null) {
                                                int deleted = HelperLibrary.musicLibrary.deleteTag(tag.getKey());
                                                if(deleted>0) {
                                                    it.remove();
                                                }
                                            }
                                        }
                                    }
                                    sendMessage("setupTags");
                                } catch (JSONException e) {
                                    Log.e(TAG, e.toString());
                                }
                            }
                        }.start();
                        break;
                    case "genres":
                        helperNotification.notifyBar(notificationSync, "Received genres ... ");
                        new Thread() {
                            public void run() {
                                try {
                                    final JSONArray jsonGenres = (JSONArray) jObject.get("genres");
                                    for(int i=0; i<jsonGenres.length(); i++) {
                                        final String genre = (String) jsonGenres.get(i);
                                        RepositoryGenres.add(genre);
                                    }
                                    sendMessage("setupSpinnerGenre");
                                } catch (JSONException e) {
                                    Log.e(TAG, e.toString());
                                }
                            }
                        }.start();
                        break;
                }
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        }

        @Override
        public void receivedFile(final FileInfoReception fileInfoReception) {
            Log.i(TAG, "Received file\n"+fileInfoReception
                    +"\nRemaining : "+filesToGet.size()+"/"+filesToKeep.size());
            File receivedFile = new File(getAppDataPath.getAbsolutePath()+File.separator
                    +fileInfoReception.relativeFullPath);
            if(filesToGet.containsKey(fileInfoReception.idFile)) {
                if(receivedFile.exists()) {
                    if (receivedFile.length() == fileInfoReception.size) {
                        Log.i(TAG, "Saved file size: " + receivedFile.length());
                        if(HelperLibrary.insertOrUpdateTrackInDatabase(receivedFile.getAbsolutePath(), fileInfoReception)) {
                            clientSync.ackFileReception(fileInfoReception.idFile, true);
                        } else {
                            Log.w(TAG, "File tags could not be read. Deleting " + receivedFile.getAbsolutePath());
                            receivedFile.delete();
                            //NOTES:
                            // - File is already deleted
                            // - Can happen also if database is null (not only if tags are not read)

                            //FIXME: Cannot read tags of received file : What to do in this case
                            //to avoid it to be requested over and over ?
                            //=> merge filesToGet and filesToKeep
                            //=> add a status in FileInfoReception (refer to other FIX-ME)
                            //=> add a retry counter
                        }
                    } else {
                        Log.w(TAG, "File has wrong size. Deleting " + receivedFile.getAbsolutePath());
                        receivedFile.delete();
                    }
                } else {
                    Log.w(TAG, "File does not exits. "+receivedFile.getAbsolutePath());
                }
            } else {
                Log.w(TAG, "File not requested. Deleting "+receivedFile.getAbsolutePath());
                receivedFile.delete();
            }
        }

        @Override
        public void receivingFile(final FileInfoReception fileInfoReception) {
            String msg = "- "+filesToGet.size() + "/" + filesToKeep.size()
                    + " | "+StringManager.humanReadableByteCount(
                    fileInfoReception.size, false)
                    +" | "+fileInfoReception.relativeFullPath;
            int max=filesToKeep.size();
            int progress=max-filesToGet.size();
            helperNotification.notifyBar(notificationSync, msg, max, progress, false, true, true);
        }

        @Override
        public void receivedDatabase() {
            String msg = "Statistics merged.";
            helperToast.toastLong(msg);
            helperNotification.notifyBar(notificationSync, msg, 5000);

            // TODO MERGE: Update FilesToKeep and FilesToGet
            // as received merged db is the new reference
            // (not urgent since values should only be
            // used again if file has been removed from db
            // somehow, as if db crashes and remade)
        }

        @Override
        public void connected() {
            sendMessage("connectedSync");
            helperNotification.notifyBar(notificationSync, "Connected ... ");
            //Server will send tags, genres and list of new files to get
            //Then, we will request next file
        }

        @Override
        public void disconnected(final String msg, boolean disable) {
            if(disable) {
                sendMessage("enableSync");
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    helperNotification.notifyBar(notificationSync, msg);
                }
            });
        }
    }

    private Map<Integer, FileInfoReception> filesToGet = null;
    private Map<String, FileInfoReception> filesToKeep = null;

    private void requestNextFile(final boolean scanLibrary) {
        if (filesToKeep != null) {
            saveFilesLists();
            if (filesToGet.size() > 0) {
                final FileInfoReception fileToGetInfo = filesToGet.entrySet().iterator().next().getValue();
                File fileToGet = new File(getAppDataPath, fileToGetInfo.relativeFullPath);
                if (fileToGet.exists() && fileToGet.length() == fileToGetInfo.size) {
                    Log.i(TAG, "File already exists. Remove from filesToGet list: " + fileToGetInfo);
                    clientSync.ackFileReception(fileToGetInfo.idFile, true);
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            /*if (!scanLibrary) {
                                try {
                                    //Waits a little after connection
                                    Log.i(TAG, "Waiting 2s");
                                    helperNotification.notifyBar(notificationSync, "Waiting 2s before request ... ");
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                }
                            }*/
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    watchTimeOut(fileToGetInfo.size);
                                }
                            });
                            synchronized (timerLock) {
                                clientSync.requestFile(fileToGetInfo.idFile);
                            }
                        }
                    }.start();
                }
            } else {
                final String msg = "No more files to download.";
                Log.i(TAG, msg + " Updating library:" + scanLibrary);
                helperNotification.notifyBar(notificationSync, msg);
                helperToast.toastLong(msg+"\n\nAll " + filesToKeep.size() + " files" +
                        " have been retrieved successfully.");
                //Not disconnecting to be able to receive a new list
                //sent by the server. User can still close
                //enableClient(true);
                //enableClient(clientSync,buttonSync, R.drawable.connect_off, true);



                //FIXME: Only send if not already (need to store ackFileReception status)
                //=> !! Check first if still necessary since we (should)
                //          request ack from server now

                //Resend add request in case missed for some reason
                /*if(filesToKeep!=null) {
                    for(FileInfoReception file : filesToKeep.values()) {
                        if(!filesToGet.containsKey(file.idFile)) {
                            ackFileReception(file.idFile, false);
                        }
                    }
                }*/

                if (scanLibrary) {
                    sendMessage("checkPermissionsThenScanLibrary");
                }
                stopSelf();
            }
        } else {
            Log.i(TAG, "filesToKeep is null");
            helperToast.toastLong("No files to download.\n\nYou can use JaMuz (Linux/Windows) to " +
                    "export a list of files to retrieve, based on playlists.");
            stopSelf();
        }
    }

    //FIXME: Read and write in threads (make sure they are read before starting)
    //Do this in an Helper to have access in MainActivity too (to be ServiceScan)

    //TODO: Do not saveFilesLists ALL everytime !! (not in receivedFile at least)
    private void saveFilesLists() {
        //Write list of files to maintain in db
        if(filesToKeep!=null) {
            Gson gson = new Gson();
            HelperTextFile.write(this, "FilesToKeep.txt", gson.toJson(filesToKeep));
        }
        //Write list of files to retrieve
        if(filesToGet!=null) {
            Gson gson = new Gson();
            HelperTextFile.write(this, "filesToGet.txt", gson.toJson(filesToGet));
        }
    }

    private void readFilesLists() {
        //Read FilesToKeep file to get list of files to maintain in db
        String readJson = HelperTextFile.read(this, "FilesToKeep.txt");
        if(!readJson.equals("")) {
            filesToKeep = new HashMap<>();
            Gson gson = new Gson();
            Type mapType = new TypeToken<HashMap<String, FileInfoReception>>(){}.getType();
            try {
                filesToKeep = gson.fromJson(readJson,mapType);
            } catch (JsonSyntaxException ex) {
                Log.e(TAG, "", ex);
            }
        }
        //Read filesToGet file to get list of files to retrieve
        readJson = HelperTextFile.read(this, "filesToGet.txt");
        if(!readJson.equals("")) {
            filesToGet = new HashMap<>();
            Gson gson = new Gson();
            Type mapType = new TypeToken<HashMap<Integer, FileInfoReception>>(){}.getType();
            try {
                filesToGet = gson.fromJson(readJson, mapType);
            } catch (JsonSyntaxException ex) {
                Log.e(TAG, "", ex);
            }
        }
    }

}