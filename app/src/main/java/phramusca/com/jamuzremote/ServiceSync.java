package phramusca.com.jamuzremote;

/**
 * Created by raph on 10/06/17.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
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

public class ServiceSync extends Service {

    private static final String TAG = ServiceSync.class.getSimpleName();
    private ClientSync clientSync;
    private NotificationCompat.Builder mBuilderSync;
    private static final int ID_NOTIFIER_SYNC = 1;
    private NotificationManager mNotifyManager;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private File getAppDataPath;
    private HelperNotification helperNotification;
    private HelperToast helperToast = new HelperToast(this);

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
    @Override
    public void onCreate(){

        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        helperNotification= new HelperNotification(getApplicationIntent(), mNotifyManager);


        mBuilderSync = new NotificationCompat.Builder(this);
        mBuilderSync.setContentTitle("Sync")
                .setContentText("Download in progress")
                .setUsesChronometer(true)
                .setSmallIcon(R.drawable.ic_process);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        ClientInfo clientInfo = (ClientInfo)intent.getSerializableExtra("clientInfo");
        getAppDataPath = (File)intent.getSerializableExtra("getAppDataPath");

        readFilesLists();

        clientSync =  new ClientSync(clientInfo, new CallBackSync());
        new Thread() {
            public void run() { clientSync.connect(); }
        }.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        stopSync(false);
        saveFilesLists();
        super.onDestroy();
    }

    private void sendMessage(String msg) {
        Message completeMessage =
                mHandler.obtainMessage(1, msg);
        completeMessage.sendToTarget();
    }

    private void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }

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
                synchronized(timerWatchTimeout) {
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
                synchronized(timerWatchTimeout) {

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
            mNotifyManager.cancel(ID_NOTIFIER_SYNC);
        }
    }

    class CallBackSync implements ICallBackSync {

        private final String TAG = MainActivity.class.getSimpleName()+"."+CallBackSync.class.getSimpleName();

        @Override
        public void receivedJson(final String msg) {
            try {
                JSONObject jObject = new JSONObject(msg);
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
                    case "SEND_DB":
                        clientSync.sendDatabase(); //TODO: Move to ClientSync
                        break;
                    case "FilesToGet":
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
                        //FIXME: Manage stopping current previous sync process, if any
                        requestNextFile(true);
                        break;
                    case "tags":
                        //Adding missing tags
                        final JSONArray jsonTags = (JSONArray) jObject.get("tags");
                        for(int i=0; i<jsonTags.length(); i++) {
                            final String tag = (String) jsonTags.get(i);
                            RepositoryTags.add(tag);
                        }
                        //Deleting tags that have been removed in server
                        final List<String> list = new ArrayList<String>();
                        for(int i = 0; i < jsonTags.length(); i++){
                            list.add((String) jsonTags.get(i));
                        }
                        Iterator<Map.Entry<Integer, String>> it = RepositoryTags.getTags().entrySet().iterator();
                        while (it.hasNext())
                        {
                            Map.Entry<Integer, String> tag = it.next();
                            if(!list.contains(tag.getValue())) {
                                if(MainActivity.musicLibrary!=null) {
                                    int deleted = MainActivity.musicLibrary.deleteTag(tag.getKey());
                                    if(deleted>0) {
                                        it.remove();
                                    }
                                }
                            }
                        }
                        sendMessage("setupTags");
                        break;
                    case "genres":
                        final JSONArray jsonGenres = (JSONArray) jObject.get("genres");
                        for(int i=0; i<jsonGenres.length(); i++) {
                            final String genre = (String) jsonGenres.get(i);
                            RepositoryGenres.add(genre);
                        }
                        sendMessage("setupSpinnerGenre");
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
                        //FIXME: Either tags (user tags) are not sent, or not received,
                        //or overwritten later by scan maybe
                        //Anyhow, user tags are not inserted in db !!
                        if(HelperLibrary.insertOrUpdateTrackInDatabase(receivedFile.getAbsolutePath(), fileInfoReception)) {
                            clientSync.ackFileReception(fileInfoReception.idFile, true);
                            return;
                        } else {
                            Log.w(TAG, "File tags could not be read. Deleting " + receivedFile.getAbsolutePath());
                            receivedFile.delete();
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
            helperNotification.notifyBar(mBuilderSync, ID_NOTIFIER_SYNC, msg, max, progress, false, true, true);
        }

        @Override
        public void receivedDatabase() {
            String msg = "Statistics merged.";
            helperToast.toastLong(msg);
            helperNotification.notifyBar(mBuilderSync, ID_NOTIFIER_SYNC, msg, 5000);

            // TODO MERGE: Update FilesToKeep and FilesToGet
            // as received merged db is the new reference
            // (not urgent since values should only be
            // used again if file has been removed from db
            // somehow, as if db crashes and remade)
        }

        @Override
        public void connected() {
            sendMessage("connectedSync");
            helperNotification.notifyBar(mBuilderSync, ID_NOTIFIER_SYNC, "Connected ... ");
            requestNextFile(false);
        }

        @Override
        public void disconnected(final String msg, boolean disable) {
            if(disable) {
                helperNotification.notifyBar(mBuilderSync, ID_NOTIFIER_SYNC, msg, 5000);
                sendMessage("enableSync");
            } else {
                helperNotification.notifyBar(mBuilderSync, ID_NOTIFIER_SYNC, msg);
            }
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
                            if (!scanLibrary) {
                                try {
                                    //Waits a little after connection
                                    Log.i(TAG, "Waiting 2s");
                                    helperNotification.notifyBar(mBuilderSync, ID_NOTIFIER_SYNC, "Waiting 2s before request ... ");
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    watchTimeOut(fileToGetInfo.size);
                                }
                            });
                            synchronized (timerWatchTimeout) {
                                clientSync.requestFile(fileToGetInfo.idFile);
                            }
                        }
                    }.start();
                }
            } else {
                final String msg = "No more files to download.";
                Log.i(TAG, msg + " Updating library:" + scanLibrary);
                helperNotification.notifyBar(mBuilderSync, ID_NOTIFIER_SYNC, msg);
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
            }
        } else {
            Log.i(TAG, "filesToKeep is null");
            helperToast.toastLong("No files to download.\n\nYou can use JaMuz (Linux/Windows) to " +
                    "export a list of files to retrieve, based on playlists.");
        }
    }

    //This is to have application opened when clicking on notification
    private PendingIntent getApplicationIntent() {
        Intent notificationIntent = new Intent(getApplicationContext(),
                MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, 0);
        return intent;
    }

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