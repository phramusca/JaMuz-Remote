package phramusca.com.jamuzremote;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raph on 10/06/17.
 */
public class ServiceScan extends ServiceBase {

    private static final String TAG = ServiceScan.class.getName();
    private Notification notificationScan;
    private int nbFiles = 0;
    private int nbFilesTotal = 0;
    private ProcessAbstract scanLibrary;
    private ProcessAbstract processBrowseFS;
    private String userPath;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        notificationScan = new Notification(this, NotificationId.get(), getString(R.string.scanTitle));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "jamuzremote:MyPowerWakelockTag"); //NON-NLS
            wakeLock.acquire(24 * 60 * 60 * 1000); //24 hours, enough to scan a lot, if not all !
        }

        userPath = intent.getStringExtra("userPath");

        //https://developer.android.com/training/data-storage/shared/media#check-for-updates
        //FIXME: No need to update library if no changes made

        scanLibraryInThread();
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        stop();
        wakeLock.release();
        super.onDestroy();
    }

    private void scanLibraryInThread() {
        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            public void run() {
                runOnUiThread(() -> helperNotification.notifyBar(notificationScan, getString(R.string.serviceScanNotifyCleaningDatabase)));
                if (HelperLibrary.musicLibrary != null) {
                    //Delete tracks from database that are from another folder than those 2
                    HelperLibrary.musicLibrary.deleteTrack(getAppDataPath, userPath);
                    //Scan user folder and cleanup library
                    File folder = new File(userPath);
                    scanFolder(folder);
                    waitScanFolder();
                    RepoAlbums.reset();

                    //Scan complete, warn user
                    final String msg = getString(R.string.serviceScanNotifyDatabaseUpdated);
                    runOnUiThread(() -> {
                        helperToast.toastLong(msg);
                        helperNotification.notifyBar(notificationScan, msg, 5000);
                    });
                    stopSelf();
                }
            }
        }.start();
    }

    private void waitScanFolder() {
        try {
            if (scanLibrary != null) {
                scanLibrary.join();
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "ActivityMain onDestroy: UNEXPECTED InterruptedException", e); //NON-NLS
        }
    }

    private void scanFolder(final File pathToScan) {
        scanLibrary = new ProcessAbstract("Thread.ActivityMain.scanLibrayInThread") { //NON-NLS
            public void run() {
                try {
                    if (!pathToScan.getAbsolutePath().equals("/")) { //NON-NLS
                        checkAbort();
                        nbFiles = 0;
                        nbFilesTotal = 0;
                        checkAbort();
                        processBrowseFS = new ProcessAbstract("Thread.ActivityMain.browseFS") { //NON-NLS
                            public void run() {
                                try {
                                    browseMediaStore(pathToScan, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                                    browseMediaStore(pathToScan, MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
                                } catch (IllegalStateException | InterruptedException e) {
                                    Log.w(TAG, "Thread.ActivityMain.browseFS InterruptedException"); //NON-NLS
                                    scanLibrary.abort();
                                }
                            }
                        };
                        processBrowseFS.start();
                        checkAbort();
                        processBrowseFS.join();
                    }

                    //Scan deleted files
                    //This will remove from db files not in filesystem
                    checkAbort();
                    List<Track> tracks =
                            new Playlist("ScanFolder", false) //NON-NLS
                                    .getTracks(new ArrayList<Track.Status>() {
                                        {
                                            add(Track.Status.LOCAL);
                                        }
                                    });
                    nbFilesTotal = tracks.size();
                    nbFiles = 0;
                    for (Track track : tracks) {
                        checkAbort();
                        if(!track.getPath().startsWith("content://")) {
                            File file = new File(track.getPath());
                            if (!file.exists()) {
                                Log.d(TAG, "Remove track from db: " + track); //NON-NLS
                                track.delete();
                            }
                        } else {
                            //FIXME: Remove from db if no more in MediaStore
                        }
                        notifyScan(getString(R.string.serviceScanNotifyScanningDeleted), 200);
                    }
                } catch (InterruptedException e) {
                    Log.w(TAG, "Thread.ActivityMain.scanLibrayInThread InterruptedException"); //NON-NLS
                }
            }

            private void browseMediaStore(File folder, Uri collection) throws InterruptedException {
                String[] projection = {
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.DATE_ADDED
                };
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.equals(collection)) {
                        collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                    } else if (MediaStore.Audio.Media.INTERNAL_CONTENT_URI.equals(collection)) {
                        collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_INTERNAL);
                    }
                }
                String selection = MediaStore.Audio.Media.MIME_TYPE + " = ?";
                String[] selectionArgs = new String[] {
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3")
                        //FIXME: Add flac too
                        //,MimeTypeMap.getSingleton().getMimeTypeFromExtension("flac")
                };
                String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";
                Cursor cursor = getApplicationContext().getContentResolver().query(
                        collection,
                        projection ,
                        selection,
                        selectionArgs,
                        sortOrder
                );
                int idColumnId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                int albumIdColumnId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                nbFilesTotal = cursor.getCount();
                while(cursor.moveToNext())
                {
                    checkAbort();
                    long id = cursor.getLong(idColumnId);
                    Uri contentUri = ContentUris.withAppendedId(collection, id);
                    long albumId=cursor.getLong(albumIdColumnId);
                    HelperLibrary.musicLibrary.insertOrUpdateTrack(contentUri.toString(), folder,
                            getApplicationContext(), "MediaStore_" + albumId);
                    notifyScan(getString(R.string.scanNotifyScanning), 13);
                }
                cursor.close();
            }
        };
        scanLibrary.start();
    }

    private void notifyScan(final String action, int every) {
        nbFiles++;
        helperNotification.notifyBar(notificationScan, action, every, nbFiles, nbFilesTotal);
    }

    private void stop() {
        //Abort and wait scanLibrayInThread is aborted
        //So it does not crash if scanLib not completed
        if (processBrowseFS != null) {
            processBrowseFS.abort();
        }
        if (scanLibrary != null) {
            scanLibrary.abort();
        }
        try {
            if (processBrowseFS != null) {
                processBrowseFS.join();
            }
            if (scanLibrary != null) {
                scanLibrary.join();
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "ActivityMain onDestroy: UNEXPECTED InterruptedException", e); //NON-NLS
        }
    }
}