package phramusca.com.jamuzremote;

/**
 * Created by raph on 10/06/17.
 */

import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServiceScan extends ServiceBase {

    private static final String TAG = ServiceScan.class.getSimpleName();
    private Notification notificationScan;
    private int nbFiles=0;
    private int nbFilesTotal = 0;
    private ProcessAbstract scanLibray;
    private ProcessAbstract processBrowseFS;
    private ProcessAbstract processBrowseFScount;
    private String userPath;

    @Override
    public void onCreate(){
        notificationScan = new Notification(this, NotificationId.SCAN, "Scan");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        userPath = intent.getStringExtra("userPath");
        scanLibrayInThread();
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        stop();
        super.onDestroy();
    }

    private void scanLibrayInThread() {
        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        helperNotification.notifyBar(notificationScan, "Deleting tracks outside user folder.");
                    }
                });
                HelperLibrary.musicLibrary.deleteTrack(getAppDataPath, userPath);
                //Scan user folder and cleanup library
                File folder = new File(userPath);
                scanFolder(folder);
                waitScanFolder();

                //Scan complete, warn user
                final String msg = "Database updated.";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        helperToast.toastLong(msg);
                        helperNotification.notifyBar(notificationScan, msg, 5000);
                    }
                });
                stopSelf();
            }
        }.start();
    }

    private void waitScanFolder() {
        try {
            if(scanLibray!=null) {
                scanLibray.join();
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "MainActivity onDestroy: UNEXPECTED InterruptedException", e);
        }
    }

    private void scanFolder(final File path) {
        scanLibray = new ProcessAbstract("Thread.MainActivity.scanLibrayInThread") {
            public void run() {
                try {
                    if(!path.getAbsolutePath().equals("/")) {
                        checkAbort();
                        nbFiles = 0;
                        nbFilesTotal = 0;
                        checkAbort();
                        //Scan android filesystem for files
                        processBrowseFS = new ProcessAbstract("Thread.MainActivity.browseFS") {
                            public void run() {
                                try {
                                    browseFS(path);
                                } catch (IllegalStateException | InterruptedException e) {
                                    Log.w(TAG, "Thread.MainActivity.browseFS InterruptedException");
                                    scanLibray.abort();
                                }
                            }
                        };
                        processBrowseFS.start();
                        //Get total number of files
                        processBrowseFScount = new ProcessAbstract("Thread.MainActivity.browseFScount") {
                            public void run() {
                                try {
                                    browseFScount(path);
                                } catch (InterruptedException e) {
                                    Log.w(TAG, "Thread.MainActivity.browseFScount InterruptedException");
                                    scanLibray.abort();
                                }
                            }
                        };
                        processBrowseFScount.start();
                        checkAbort();
                        processBrowseFS.join();
                        processBrowseFScount.join();
                    }

                    //Scan deleted files
                    //This will remove from db files not in filesystem
                    //It IS compatible with ServiceSync
                    checkAbort();
                    List<Track> tracks = new Playlist("ScanFolder", false).getTracks();
                    nbFilesTotal = tracks.size();
                    nbFiles=0;
                    for(Track track : tracks) {
                        checkAbort();
                        File file = new File(track.getPath());
                        if(!file.exists()) {
                            //TODO: File may be currently being inserted by ServiceSync ...
                            // => handle that situation
                            Log.d(TAG, "Remove track from db: "+track);
                            track.delete();
                        }
                        notifyScan("Scanning deleted files ... ", 200);
                    }
                } catch (InterruptedException e) {
                    Log.w(TAG, "Thread.MainActivity.scanLibrayInThread InterruptedException");
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
                                    //getAppDataPath is managed in ServiceSync
                                    if (!absolutePath.startsWith(getAppDataPath.getAbsolutePath())) {
                                        //Scanning extra local folder
                                        List<String> audioExtensions = new ArrayList<>();
                                        audioExtensions.add("mp3");
                                        audioExtensions.add("flac");
                                        /*audioFiles.add("ogg");*/
                                        String ext = absolutePath.substring(absolutePath.lastIndexOf(".")+1);
                                        if(audioExtensions.contains(ext)) {
                                            HelperLibrary.insertOrUpdateTrackInDatabase(absolutePath, null);
                                        }
                                    }
                                    notifyScan("Scanning files ... ", 13);
                                }
                            }
                        } else {
                            Log.i(TAG, "Deleting empty folder "+path.getAbsolutePath());
                            //noinspection ResultOfMethodCallIgnored
                            path.delete();
                        }
                    }
                }
            }

            private void browseFScount(File path) throws InterruptedException {
                checkAbort();
                if (path.isDirectory()) {
                    File[] files = path.listFiles();
                    if (files != null) {
                        if(files.length>0) {
                            for (File file : files) {
                                checkAbort();
                                if (file.isDirectory()) {
                                    browseFScount(file);
                                }
                                else {
                                    nbFilesTotal++;
                                }
                            }
                        }
                    }
                }
            }
        };
        scanLibray.start();
    }

    private void notifyScan(final String action, int every) {
        nbFiles++;
        helperNotification.notifyBar(notificationScan, action, every, nbFiles, nbFilesTotal);
    }

    private void stop() {
        //Abort and wait scanLibrayInThread is aborted
        //So it does not crash if scanLib not completed
        if(processBrowseFS!=null) {
            processBrowseFS.abort();
        }
        if(processBrowseFScount!=null) {
            processBrowseFScount.abort();
        }
        if(scanLibray!=null) {
            scanLibray.abort();
        }
        try {
            if(processBrowseFS!=null) {
                processBrowseFS.join();
            }
            if(processBrowseFScount!=null) {
                processBrowseFScount.join();
            }
            if(scanLibray!=null) {
                scanLibray.join();
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "MainActivity onDestroy: UNEXPECTED InterruptedException", e);
        }
    }

}