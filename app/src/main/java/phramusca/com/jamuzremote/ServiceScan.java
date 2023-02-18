package phramusca.com.jamuzremote;

import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by raph on 10/06/17.
 */
public class ServiceScan extends ServiceBase {

    private static final String TAG = ServiceScan.class.getName();
    private static final String MEDIA_STORE_VERSION = "mediaStoreVersion";
    private Notification notificationScan;
    private int nbFiles = 0;
    private int nbFilesTotal = 0;
    private ProcessAbstract processScan;
    private ProcessAbstract processBrowse;
    private PowerManager.WakeLock wakeLock;
    private SharedPreferences defaultSharedPreferences;
    boolean forceRefresh;

    // https://developer.android.com/guide/topics/media/media-formats#audio-formats
    private static final List<String> SUPPORTED_MIME_TYPES =
            Arrays.asList("audio/3gpp", "audio/3gpp2", "audio/3gp2", "audio/mp4", "audio/aac",
                    "audio/flac", "audio/mpeg", "audio/mp3", "audio/ogg", "audio/wav");

    @Override
    public void onCreate() {
        notificationScan = new Notification(this, NotificationId.get(), getString(R.string.scanTitle), "Scan service", "Progress on reading android media store.");
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        forceRefresh = (boolean) intent.getSerializableExtra("forceRefresh");
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "jamuzremote:MyPowerWakelockTag"); //NON-NLS
            wakeLock.acquire(24 * 60 * 60 * 1000); //24 hours, enough to scan a lot, if not all !
        }
        String previousMediaStoreVersion = defaultSharedPreferences.getString(MEDIA_STORE_VERSION, "");
        String mediaStoreVersion = MediaStore.getVersion(getApplicationContext());
        if(forceRefresh || !previousMediaStoreVersion.equals(mediaStoreVersion)) {
            scanInThread();
        } else {
            stopSelf();
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        stop();
        wakeLock.release();
        super.onDestroy();
    }

    private void scanInThread() {
        new Thread() {
            public void run() {
                runOnUiThread(() -> helperNotification.notifyBar(notificationScan, getString(R.string.serviceScanNotifyCleaningDatabase)));
                if (HelperLibrary.musicLibrary != null) {
                    //Delete tracks from database that are from another folder than those 2
                    HelperLibrary.musicLibrary.deleteTrack(getAppDataPath, "content://");
                    //Scan MediaStore and cleanup library
                    scan();
                    waitScan();

                    RepoAlbums.reset();
                    SharedPreferences.Editor editor = defaultSharedPreferences.edit();
                    String mediaStoreVersion = MediaStore.getVersion(getApplicationContext());
                    editor.putString(MEDIA_STORE_VERSION, mediaStoreVersion);
                    editor.apply();
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

    private void waitScan() {
        try {
            if (processScan != null) {
                processScan.join();
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "ActivityMain onDestroy: UNEXPECTED InterruptedException", e); //NON-NLS
        }
    }

    private void scan() {
        processScan = new ProcessAbstract("Thread.ActivityMain.scanInThread") { //NON-NLS
            public void run() {
                try {
                    checkAbort();
                    nbFiles = 0;
                    nbFilesTotal = 0;
                    checkAbort();
                    processBrowse = new ProcessAbstract("Thread.ActivityMain.browseFS") { //NON-NLS
                        public void run() {
                            try {
                                browseMediaStore(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                                browseMediaStore(MediaStore.Audio.Media.INTERNAL_CONTENT_URI);
                            } catch (IllegalStateException | InterruptedException e) {
                                Log.w(TAG, "Thread.ActivityMain.browseFS InterruptedException"); //NON-NLS
                                processScan.abort();
                            }
                        }
                    };
                    processBrowse.start();
                    checkAbort();
                    processBrowse.join();

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
                        Uri uriContent = Uri.parse(track.getPath());
                        if (!HelperFile.checkUriExist(getApplicationContext(), uriContent)) {
                            Log.d(TAG, "Remove track from db: " + track); //NON-NLS
                            track.delete();
                        }
                        notifyScan(getString(R.string.serviceScanNotifyScanningDeleted), 200);
                    }
                } catch (InterruptedException e) {
                    Log.w(TAG, "Thread.ActivityMain.scanInThread InterruptedException"); //NON-NLS
                }
            }

            private void browseMediaStore(Uri collection) throws InterruptedException {
                String[] projection = {
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.DATE_ADDED,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.IS_MUSIC
                };
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.equals(collection)) {
                        collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                    } else if (MediaStore.Audio.Media.INTERNAL_CONTENT_URI.equals(collection)) {
                        collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_INTERNAL);
                    }
                }
                String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 "
                        + "AND "+ MediaStore.Audio.Media.DATA + " LIKE '%/Music/%' "
                        + "AND " + MediaStore.Audio.Media.MIME_TYPE + " IN (" + getSupportedMimeTypes() + ")";
                String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";
                Cursor cursor = getApplicationContext().getContentResolver().query(
                        collection,
                        projection ,
                        selection,
                        null,
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
                    long albumId = cursor.getLong(albumIdColumnId);
                    //We could have used track metadata from MediaStore instead of reading file
                    // but not all fields are available depending on android version
                    HelperLibrary.musicLibrary.insertOrUpdateTrack(contentUri.toString(),
                            getApplicationContext(), "MediaStore_" + albumId);
                    notifyScan(getString(R.string.scanNotifyScanning), 1);
                }
                cursor.close();
            }
        };
        processScan.start();
    }

    private String getSupportedMimeTypes() {
        StringBuilder sb = new StringBuilder();
        for (String mime : SUPPORTED_MIME_TYPES) {
            sb.append("'").append(mime).append("',");
        }
        return sb.substring(0, sb.length() - 1);
    }

    private void notifyScan(final String action, int every) {
        nbFiles++;
        helperNotification.notifyBar(notificationScan, action, every, nbFiles, nbFilesTotal);
    }

    private void stop() {
        if (processBrowse != null) {
            processBrowse.abort();
        }
        if (processScan != null) {
            processScan.abort();
        }
        try {
            if (processBrowse != null) {
                processBrowse.join();
            }
            if (processScan != null) {
                processScan.join();
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "ActivityMain onDestroy: UNEXPECTED InterruptedException", e); //NON-NLS
        }
    }
}