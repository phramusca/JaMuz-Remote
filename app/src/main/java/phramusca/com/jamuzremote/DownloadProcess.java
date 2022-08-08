package phramusca.com.jamuzremote;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class DownloadProcess extends ProcessAbstract {

    private static final String TAG = DownloadProcess.class.getName();
    private final Map<Track, Integer> newTracks;
    private List<DownloadTask> downloadServices;
    private ExecutorService pool;
    private int nbRetries = 0;
    private final int maxNbRetries = 10;//TODO: Make number of retries an option eventually
    private final int nbFilesStart;
    private final HelperNotification helperNotification;
    private final Context mContext;
    private final ClientInfo clientInfo;
    private long sizeTotal;
    private long sizeRemaining;
    private int nbFailed;
    private Benchmark bench;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Notification notificationDownload;
    protected HelperToast helperToast;
    private final OkHttpClient clientDownload;
    private IListenerSyncDown callback;

    DownloadProcess(String name, Map<Track, Integer> newTracks, Context context, HelperNotification helperNotification, ClientInfo clientInfo, OkHttpClient clientDownload, String title, IListenerSyncDown callback) {
        super(name);
        mContext = context;
        this.clientInfo = clientInfo;
        this.clientDownload = clientDownload;
        this.callback = callback;
        helperToast = new HelperToast(mContext);
        this.helperNotification = helperNotification;
        notificationDownload = new Notification(mContext, NotificationId.get(), title);
        this.newTracks = newTracks;
        nbFilesStart = newTracks.size();
        downloadServices = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            do {
                checkAbort();
                if (isCompleted() || !startDownloads()) {
                    break;
                }
                checkAbort();
                if (isCompleted()) {
                    break;
                }
                nbRetries++;
                int sleepSeconds = nbRetries * 10;
                runOnUiThread(() -> helperNotification.notifyBar(notificationDownload, //NON-NLS
                        String.format(
                                Locale.getDefault(),
                                "%s %ds %s %d/%d", //NON-NLS
                                mContext.getString(R.string.serviceSyncNotifyDownloadWaiting),
                                sleepSeconds,
                                mContext.getString(R.string.serviceSyncNotifyDownloadBeforeAttempt),
                                nbRetries + 1,
                                maxNbRetries)));
                //noinspection BusyWait
                sleep(sleepSeconds * 1000L);
                checkAbort();
            } while (nbRetries < maxNbRetries - 1);
            runOnUiThread(() -> helperNotification.notifyBar(notificationDownload, //NON-NLS
                    mContext.getString(R.string.serviceSyncNotifySyncComplete), 5000));
        } catch (InterruptedException e) {
            Log.i(TAG, "ProcessDownload received InterruptedException"); //NON-NLS
        }
    }

    private boolean isCompleted() {
        return newTracks.size() <= 0;
    }

    private boolean startDownloads() throws InterruptedException {
        runOnUiThread(() -> helperNotification.notifyBar(notificationDownload, mContext.getString(R.string.serviceSyncNotifyDownloadStarting)));
        bench = new Benchmark(newTracks.size(), 10);
        pool = Executors.newFixedThreadPool(20); //TODO Make number of threads an option
        downloadServices = new ArrayList<>();
        sizeTotal = 0;
        nbFailed = 0;
        for (Map.Entry<Track, Integer> entry : newTracks.entrySet()) {
            Track track = entry.getKey();
            track.getTags(true);
            DownloadTask downloadTask = new DownloadTask(track, entry.getValue(), this::notifyBarProgress, clientInfo, clientDownload);
            downloadServices.add(downloadTask);
            pool.submit(downloadTask);
            sizeTotal += track.getSize();
        }
        pool.shutdown();
        notifyBarProgress(null, "", -1);
        return pool.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
    }

    private void notifyBarProgress(Track track, String msg, Integer position) {
        if (track != null) {
            if (!track.getStatus().equals(Track.Status.REC)) {
                nbFailed++;
                if(!msg.equals("")) {
                    runOnUiThread(() -> helperToast.toastLong(msg));
                    stopDownloads();
                }
            } else {
                sizeRemaining -= track.getSize();
                newTracks.remove(track);
            }
            bench.get(track.getSize());
        } //NON-NLS
        String bigText = String.format(
                Locale.getDefault(),
                "-%d/%d\n%s/%s\n%s %d/%d. %d %s\n", //NON-NLS
                newTracks.size(),
                nbFilesStart,
                StringManager.humanReadableByteCount(sizeRemaining, false),
                StringManager.humanReadableByteCount(sizeTotal, false),
                mContext.getString(R.string.serviceSyncNotifyDownloadAttempt),
                nbRetries + 1,
                maxNbRetries,
                nbFailed,
                mContext.getString(R.string.serviceSyncNotifyDownloadErrors));

        runOnUiThread(() -> helperNotification.notifyBar(notificationDownload, bench.getLast(),
                nbFilesStart, (nbFilesStart - newTracks.size()), false,
                true, true,
                bench.getLast() + "\n" + bigText));

        if(position>-1 && callback!=null) {
            callback.setStatus(track, msg, position);
        }
    }

    void stopDownloads() {
        runOnUiThread(() -> {
            helperNotification.notifyBar(notificationDownload, mContext.getString(R.string.serviceSyncNotifyDownloadStopping)); //, 5000);
        });
        pool.shutdownNow();
        for (DownloadTask downloadService : downloadServices) {
            downloadService.abort();
        }
        abort();
        runOnUiThread(() -> helperNotification.notifyBar(notificationDownload, mContext.getString(R.string.serviceSyncNotifyDownloadStopped), 5000));
    }

    String checkCompleted() {
        int remaining = newTracks.size();
        StringBuilder builder = new StringBuilder()
                .append(mContext.getString(R.string.serviceSyncNotifySyncComplete)).append("\n\n");
        if (remaining < 1) {
            builder.append(mContext.getString(R.string.serviceSyncNotifySyncAll))
                    .append(" ").append(nbFilesStart).append(" ")
                    .append(mContext.getString(R.string.serviceSyncNotifySyncSuccess));
        } else {
            builder.append(mContext.getString(R.string.serviceSyncNotifySyncDownloaded))
                    .append(" ").append(nbFilesStart - remaining).append(". ")
                    .append(mContext.getString(R.string.serviceSyncNotifySyncRemaining))
                    .append(" ").append(remaining).append(".");
        }
        String finalToastMsg = builder.toString();
        Log.i(TAG, finalToastMsg);
        runOnUiThread(() -> helperToast.toastLong(finalToastMsg));
        return finalToastMsg;
    }

    protected void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }
}
