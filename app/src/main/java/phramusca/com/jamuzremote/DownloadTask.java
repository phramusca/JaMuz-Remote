package phramusca.com.jamuzremote;

import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class DownloadTask extends ProcessAbstract implements Runnable {
    private static final String TAG = DownloadTask.class.getName();
    private int position;
    private final IListenerSyncDown callback; //NON-NLS
    private final ClientInfo clientInfo;
    private final Track track;
    protected OkHttpClient clientDownload;
    private WifiManager.WifiLock wifiLock;

    DownloadTask(Track track, int position, IListenerSyncDown callback, ClientInfo clientInfo, OkHttpClient clientDownload, WifiManager.WifiLock wifiLock) {
        super("DownloadTask idFileServer=" + track.getIdFileServer()); //NON-NLS
        this.track = track;
        this.position = position;
        this.callback = callback;
        this.clientInfo = clientInfo;
        this.clientDownload = clientDownload;
        this.wifiLock = wifiLock;
    }

    @Override
    public void run() {
        String msg = "";
        try {
            File destinationFile = new File(track.getPath());
            File destinationPath = destinationFile.getParentFile();
            //noinspection ResultOfMethodCallIgnored
            Objects.requireNonNull(destinationPath).mkdirs();
            checkAbort();
            if (clientDownload == null) {
                clientDownload = new OkHttpClient.Builder() //NON-NLS
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build();
            }
            HttpUrl.Builder urlBuilder = clientInfo.getUrlBuilder("download"); //NON-NLS
            urlBuilder.addQueryParameter("id", String.valueOf(track.getIdFileServer()));
            Request request = clientInfo.getRequestBuilder(urlBuilder).build();
            if(wifiLock!=null && !wifiLock.isHeld()) {
                wifiLock.acquire();
            }
            Response response = clientDownload.newCall(request).execute();
            if (response.isSuccessful()) {
                if (destinationFile.exists()) {
                    boolean fileDeleted = destinationFile.delete();
                    Log.v("fileDeleted", fileDeleted + "");
                }
                boolean fileCreated = destinationFile.createNewFile();
                Log.v("fileCreated", fileCreated + "");
                BufferedSink sink = Okio.buffer(Okio.sink(destinationFile));
                sink.writeAll(Objects.requireNonNull(response.body()).source());
                Objects.requireNonNull(response.body()).source().close();
                sink.close();
                RepoSync.checkReceivedFile(track);
            } else {
                switch (response.code()) { //NON-NLS
                    case 301:
                        throw new ServiceSync.ServerException(request.header("api-version") + " not supported. " + Objects.requireNonNull(response.body()).string()); //NON-NLS
                    case 410: //Gone
                        //Transcoded file is not available
                        track.setStatus(Track.Status.ERROR);
                        RepoSync.update(track);
                        break;
                    case 404: // File does not exist on server
                        HelperLibrary.musicLibrary.deleteTrack(track.getIdFileServer());
                        track.setStatus(Track.Status.REC); //To be ignored by current sync process
                        RepoSync.update(track);
                        break;
                    default:
                        throw new ServiceSync.ServerException(response.code() + ": " + response.message()); //NON-NLS
                }
            }
        } catch (InterruptedException e) {
            Log.w(TAG, "Download interrupted for " + track.getRelativeFullPath(), e); //NON-NLS
        } catch (IOException | NullPointerException e) {
            Log.e(TAG, "Error downloading " + track.getRelativeFullPath(), e); //NON-NLS
            if (Objects.requireNonNull(e.getMessage()).contains("ENOSPC")) { //NON-NLS
                Log.w(TAG, "ENOSPC for " + track.getRelativeFullPath(), e); //NON-NLS
                track.setStatus(Track.Status.ERROR);
                msg = "No space left on device.";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error downloading " + track.getRelativeFullPath(), e); //NON-NLS
        }
        callback.setStatus(track, msg, position);
    }
}
