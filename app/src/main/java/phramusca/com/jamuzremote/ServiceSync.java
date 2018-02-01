package phramusca.com.jamuzremote;

/**
 * Created by raph on 10/06/17.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceSync extends ServiceBase {

    private static final String TAG = ServiceSync.class.getSimpleName();
    public static final String USER_STOP_SERVICE_REQUEST = "USER_STOP_SERVICE";

    private ClientSync clientSync;
    private static final Object timerLock = new Object();
    private CountDownTimer timerWatchTimeout= new CountDownTimer(0, 0) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {

        }
    };
    private Benchmark bench;
    private Notification notificationSync;
    private BroadcastReceiver userStopReceiver;
    private int nbFiles;
    private int nbDeleted;

    @Override
    public void onCreate(){
        notificationSync = new Notification(this, 1, "Sync");
        userStopReceiver=new UserStopServiceReceiver();
        registerReceiver(userStopReceiver,  new IntentFilter(USER_STOP_SERVICE_REQUEST));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        final ClientInfo clientInfo = (ClientInfo)intent.getSerializableExtra("clientInfo");
        new Thread() {
            public void run() {
                helperNotification.notifyBar(notificationSync, "Reading list ... ");
                RepoSync.read(getAppDataPath);
                bench = new Benchmark(RepoSync.getRemainingSize());
                helperNotification.notifyBar(notificationSync, "Connecting ... ");
                clientSync =  new ClientSync(clientInfo, new CallBackSync());
                clientSync.connect();
            }
        }.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        unregisterReceiver(userStopReceiver);
        super.onDestroy();
    }

    public class UserStopServiceReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "UserStopServiceReceiver.onReceive()");
            stopSync(false, "User stopped.", 5000);
        }
    }

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

                    //TODO: Make sync timeouts configurable (use bench, to be based on size not nb)
                    long minTimeout =  15 * 1000;  //Min timeout 15s (or 15s by 4Mo)
                    long maxTimeout =  120 * 1000; //Max timeout 2 min

                    long timeout = size<4000000?minTimeout:((size / 4000000) * minTimeout);
                    timeout = timeout>maxTimeout?maxTimeout:timeout;
                    timerWatchTimeout = new CountDownTimer(timeout, timeout/10) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            Log.i(TAG, "Seconds Remaining: "+ (millisUntilFinished/1000));
                        }

                        @Override
                        public void onFinish() {
                            stopSync(true, "Timed out waiting on file.", -1);
                        }
                    };
                    Log.i(TAG, "timerWatchTimeout.start()");
                    timerWatchTimeout.start();
                }
            }
        });
    }

    private void stopSync(boolean reconnect, String msg, long millisInFuture) {
        cancelWatchTimeOut();
        if(clientSync!=null) {
            clientSync.close(reconnect, msg, millisInFuture);
        }
    }

    class CallBackSync implements ICallBackSync {

        private final String TAG = MainActivity.class.getSimpleName()+"."+CallBackSync.class.getSimpleName();

        @Override
        public void receivedJson(final String json) {
            try {
                final JSONObject jObject = new JSONObject(json);
                String type = jObject.getString("type");
                switch(type) {
                    case "insertDeviceFileAck":
                        String status = jObject.getString("status");
                        boolean requestNextFile = jObject.getBoolean("requestNextFile");
                        if(status.equals("OK")) {
                            sendMessage("refreshSpinner(true)");
                            cancelWatchTimeOut();
                            FileInfoReception fileReceived = new FileInfoReception(jObject.getJSONObject("file"));
                            RepoSync.receivedAck(getAppDataPath, fileReceived);
                            bench.get();
                        } else {
                            // FIXME: Store a new FAIL status ?
                            // If so, add other failures too (make different statuses ?)
                            // What to do with it ? Retry ? How many times ? How to report ?
                        }
                        notifyBar(bench.getLast()+" | 4/4 | Acknowledged");
                        if(requestNextFile) {
                            requestNextFile();
                        }
                        break;
                    case "StartSync":
                        requestNextFile(false);
                        break;
                    case "SEND_DB":
                        helperNotification.notifyBar(notificationSync, "Sending database ... ");
                        clientSync.sendDatabase(); //TODO: Move to ClientSync
                        break;
                    case "FilesToGet":
                        helperNotification.notifyBar(notificationSync, "Received new list of files to get ... ");
                        Map<Integer, FileInfoReception> newTracks = new HashMap<>();
                        JSONArray files = (JSONArray) jObject.get("files");
                        for(int i=0; i<files.length(); i++) {
                            FileInfoReception fileReceived = new FileInfoReception((JSONObject) files.get(i));
                            newTracks.put(fileReceived.idFile, fileReceived);
                        }
                        RepoSync.set(getAppDataPath, newTracks);
                        bench = new Benchmark(RepoSync.getRemainingSize());
                        requestNextFile();
                        break;
                    case "tags":
                        helperNotification.notifyBar(notificationSync, "Received tags ... ");
                        new Thread() {
                            public void run() {
                                try {
                                    final JSONArray jsonTags = (JSONArray) jObject.get("tags");
                                    final List<String> newTags = new ArrayList<>();
                                    for(int i = 0; i < jsonTags.length(); i++){
                                        newTags.add((String) jsonTags.get(i));
                                    }
                                    RepoTags.set(newTags);
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
                                    final List<String> newGenres = new ArrayList<>();
                                    for(int i=0; i<jsonGenres.length(); i++) {
                                        final String genre = (String) jsonGenres.get(i);
                                        newGenres.add(genre);
                                    }
                                    RepoGenres.set(newGenres);
                                    sendMessage("setupGenres");
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
                    +"\nRemaining : "+ RepoSync.getRemainingSize()+"/"+ RepoSync.getTotalSize());
            notifyBar("3/4", fileInfoReception);
            RepoSync.checkFile(getAppDataPath, fileInfoReception,  FileInfoReception.Status.LOCAL);
            requestNextFile();
        }

        @Override
        public void receivingFile(final FileInfoReception fileInfoReception) {
            notifyBar("2/4", fileInfoReception);
        }

        @Override
        public void receivedDatabase() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String msg = "Statistics merged.";
                    helperToast.toastLong(msg);
                    helperNotification.notifyBar(notificationSync, msg, 5000);
                }
            });

            // TODO MERGE: Update RepoSync
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
        public void disconnected(boolean reconnect, final String msg, final long millisInFuture) {
            cancelWatchTimeOut();
            if(!msg.equals("")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        helperNotification.notifyBar(notificationSync, msg, millisInFuture);
                    }
                });
            }
            if(!reconnect) {
                sendMessage("enableSync");
                stopSelf();
            }
        }
    }

    private void notifyBar(String text, FileInfoReception fileInfoReception) {
        notifyBar(bench.getLast()+" | "+text+" | "+StringManager.humanReadableByteCount(
                fileInfoReception.size, false)
                +" | "+fileInfoReception.relativeFullPath);
    }

    private void notifyBar(String text) {
        String msg = "- "+ RepoSync.getRemainingSize() + "/" + RepoSync.getTotalSize()
                + " | "+text;
        int max= RepoSync.getTotalSize();
        int progress=max- RepoSync.getRemainingSize();
        helperNotification.notifyBar(notificationSync, msg, max, progress, false, true, true);
    }

    private void requestNextFile() {
        requestNextFile(true);
    }

    private void requestNextFile(final boolean scanLibrary) {
        final FileInfoReception fileInfoReception = RepoSync.take(getAppDataPath);
        Log.i(TAG, "requestNextFile file: \n"+fileInfoReception);
        if (fileInfoReception != null) {
            switch (fileInfoReception.status) {
                case NEW:
                    new Thread() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    notifyBar("1/4", fileInfoReception);
                                    watchTimeOut(fileInfoReception.size);
                                }
                            });
                            synchronized (timerLock) {
                                clientSync.requestFile(fileInfoReception.idFile);
                            }
                        }
                    }.start();
                    break;
                case LOCAL:
                    if(HelperLibrary.insertOrUpdateTrackInDatabase(new File(getAppDataPath,
                            fileInfoReception.relativeFullPath).getAbsolutePath(), fileInfoReception)) {
                        if(RepoSync.checkFile(getAppDataPath, fileInfoReception, FileInfoReception.Status.IN_DB)) {
                            clientSync.ackFileReception(fileInfoReception.idFile, true);
                        }
                    }
                    break;
                case IN_DB:
                    clientSync.ackFileReception(fileInfoReception.idFile, true);
                    break;
                case ACK:
                    //We should not get there !!! as NOT taking from "ACK" list
                    //TODO: Manage this case
                    break;
            }
        } else if(RepoSync.getTotalSize()>0) {
            final String msg = "No more files to download.";
            final String msg2 = "All " + RepoSync.getTotalSize() + " files" +
                    " have been retrieved successfully.";
            Log.i(TAG, msg);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    helperToast.toastLong(msg + "\n\n" + msg2);
                }
            });
            //Scan unwanted files (not in files but existing in "internal" folder)
            scanFolder(getAppDataPath);

            //NOT stopping to be able to merge statistics
            //stopSync(false, msg2, 10000);

            //FIXME: Delete from db whenever deleting a file in "internal" folder
            //          (may already be done, check that FIRST)
            //So that it is not required to scan (deleted) after sync (below)

            Log.i(TAG, "Updating library:" + scanLibrary);
            if (scanLibrary) {
                //TODO: Scan only "internal" folder (scan deleted only), not the user folder
                sendMessage("checkPermissionsThenScanLibrary");
            }
        } else {
            Log.i(TAG, "No files to download.");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    helperToast.toastLong("No files to download.\n\nYou can use JaMuz (Linux/Windows) to " +
                            "export a list of files to retrieve, based on playlists.");
                }
            });
            stopSync(false, "No files to download.", 5000);
        }
    }

    private void scanFolder(final File path) {
        ProcessAbstract processAbstract = new ProcessAbstract("Thread.MainActivity.ScanUnWantedRepoSync") {
            public void run() {
                try {
                    if(!path.equals("/")) {
                        nbFiles = 0;
                        nbDeleted = 0;
                        browseFS(path);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                helperNotification.notifyBar(notificationSync, "Sync complete.", 10000);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.w(TAG, "Thread.MainActivity.ScanUnWantedRepoSync InterruptedException");
                }
            }

            private void browseFS(File path) throws InterruptedException {
                checkAbort();
                if (path.isDirectory()) {
                    File[] files = path.listFiles();
                    if (files != null) {
                        if(files.length>0) {
                            for (File file : files) {
                                checkAbort();
                                if (file.isDirectory()) {
                                    browseFS(file);
                                }
                                else {
                                    String absolutePath=file.getAbsolutePath();
                                    String relativeFullPath = absolutePath.substring(getAppDataPath.getAbsolutePath().length()+1);
                                    if(!RepoSync.checkFile(getAppDataPath, relativeFullPath)) {
                                        //RepoSync.checkFile deletes file. Not couting deleted
                                        //to match RepoSync.getTotalSize() at the end (hopefully)
                                        nbFiles++;
                                    } else { nbDeleted++; }
                                    helperNotification.notifyBar(notificationSync, "Deleting not requested files: "+nbDeleted+" so far.", 50,
                                            nbFiles, RepoSync.getTotalSize());
                                }
                            }
                        } else {
                            Log.i(TAG, "Deleting empty folder "+path.getAbsolutePath());
                            path.delete();
                        }
                    }
                }
            }
        };
        processAbstract.start();
    }
}