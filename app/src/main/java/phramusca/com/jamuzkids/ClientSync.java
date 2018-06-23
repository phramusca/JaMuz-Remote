/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phramusca.com.jamuzkids;

import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 *
 * @author phramusca
 */
public class ClientSync extends Client {
	private static final String TAG = ClientSync.class.getName();
	private final ICallBackSync callback;
    private final SyncStatus syncStatus = new SyncStatus(Status.NOT_CONNECTED, 0);
    private CountDownTimer timerWatchTimeout;
    private static final Object timerLock = new Object();
    private Handler mHandler = new Handler(Looper.getMainLooper());

	ClientSync(ClientInfo clientInfo, ICallBackSync callback){
		super(clientInfo);
		this.callback = callback;
		super.setCallback(new CallBackReception());
	}

    @Override
    public boolean connect() {
        synchronized (syncStatus) {
            logStatus("connect()");
            if (syncStatus.status.equals(Status.NOT_CONNECTED)) {
                syncStatus.status=Status.CONNECTING;
                watchTimeOut(5);
                if (super.connect()) {
                    syncStatus.status=Status.CONNECTED;
                    syncStatus.nbRetries=0;
                    logStatus("Connected");
                    callback.connected();
                    RepoSync.save();
                    request("requestTags");
                    return true;
                }
            }
            return false;
        }
    }

    public void close(boolean reconnect, String msg, long millisInFuture) {
        synchronized (syncStatus) {
            logStatus("close()");
            cancelWatchTimeOut();
            if(!syncStatus.status.equals(Status.NOT_CONNECTED)
                    &&!syncStatus.status.equals(Status.STOPPING)) {
                close();
                syncStatus.status=Status.NOT_CONNECTED;
                syncStatus.nbRetries++;
            }
            if (reconnect) {
                if(syncStatus.nbRetries < 100 //TODO: Make max nbRetries configurable
                        && syncStatus.status.equals(Status.NOT_CONNECTED)) {
                    if (syncStatus.nbRetries < 2) {
                        RepoSync.save();
                    } else {
                        logStatus("Re-connecting in 5s");
                    }
                    new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ignored) {
                            }
                            logStatus("Re-connecting now");
                            connect();
                        }
                    }.start();
                } else {
                    RepoSync.save();
                    msg="Too many retries ("+syncStatus.nbRetries+").";
                    reconnect=false;
                    syncStatus.status=Status.STOPPING;
                }
            } else {
                RepoSync.save();
                syncStatus.status=Status.STOPPING;
            }
            callback.disconnected(reconnect, msg, millisInFuture);
        }
    }

    class CallBackReception implements ICallBackReception {

		@Override
		public void receivedJson(String json) {
		    synchronized (syncStatus) {
                logStatus("receivedJson(" + json + ")");
                cancelWatchTimeOut();
                callback.receivedJson(json);
            }
		}

        @Override
        public void receivedBitmap(Bitmap bitmap) {        }

        @Override
        public void receivingFile(FileInfoReception fileInfoReception) {
            callback.receivingFile(fileInfoReception);
        }

        @Override
        public void receivedFile(FileInfoReception fileInfoReception) {
            synchronized (syncStatus) {
                logStatus("receivedFile(" + fileInfoReception + ")");
                cancelWatchTimeOut();
                callback.receivedFile(fileInfoReception);
            }
        }

        @Override
		public void disconnected(String msg) {
            synchronized (syncStatus) {
                logStatus("disconnected(\""+msg+"\")");
                if(msg.equals("ENOSPC")) {
                    close(false, "No more space on device. Check your playlist limits and available space in your SD card.", -1);
                } else if(syncStatus.status.equals(Status.CONNECTED)
                        || syncStatus.status.equals(Status.CONNECTING)) {
                    close(true, (syncStatus.nbRetries>0?"Attempt "+syncStatus.nbRetries
                            :"Disconnected")+": "+msg, -1);
                }
            }
		}
	}

    public void requestFile(FileInfoReception fileInfoReception) {
        synchronized (syncStatus) {
            //TODO: Make sync timeouts configurable (use bench, to be based on size not nb)
            long minTimeout = 15;  //Min timeout 15s (or 15s by 4Mo)
            long maxTimeout = 120; //Max timeout 2 min

            long timeoutFile = fileInfoReception.size < 4000000
                    ? minTimeout
                    : ((fileInfoReception.size / 4000000) * minTimeout);
            timeoutFile = timeoutFile > maxTimeout ? maxTimeout : timeoutFile;
            watchTimeOut(timeoutFile);
            logStatus("requestFile()");
            if(checkStatus()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("type", "requestFile");
                    obj.put("idFile", fileInfoReception.idFile);
                    send("JSON_" + obj.toString());
                } catch (JSONException e) {
                }
            }
        }
    }

    public void request(String request) {
        synchronized (syncStatus) {
            logStatus("request(\""+request+"\")");
            if(checkStatus()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("type", request);
                    watchTimeOut(10);
                    send("JSON_" + obj.toString());
                } catch (JSONException e) {
                }
            }
        }
    }

    public void requestMerge(List<Track> tracks, File getAppDataPath) {
        synchronized (syncStatus) {
            logStatus("requestFile()");
            if(checkStatus()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("type", "FilesToMerge");
                    //JSONObject jsonAsMap = new JSONObject();
                    JSONArray filesToMerge = new JSONArray();
                    for (Track track : tracks) {
                        filesToMerge.put(track.toJSONObject(getAppDataPath));
                    }
                    obj.put("files", filesToMerge);
                    watchTimeOut(4+tracks.size());
                    send("JSON_" + obj.toString());
                } catch (JSONException e) {
                }
            }
        }
    }

    // FIXME sync and merge: do NOT request genres and tags at every connection but only if required or on demand
    // FIXME sync and merge: avoid double acknowledgement:
    //          - Insert files in JaMuz deviceFiles directly at export
    //          - Use a status in JaMuz as in JaMuzRemote
    // FIXME sync and merge: remove json file lists and insert in db directly

    public void ackFilesReception(List<FileInfoReception> files) {
        synchronized (syncStatus) {
            logStatus("ackFilesReception()");
            if(checkStatus()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("type", "ackFileSReception");
                    JSONArray idFiles = new JSONArray();
                    for (FileInfoReception file : files) {
                        idFiles.put(file.idFile);
                    }
                    obj.put("idFiles", idFiles);
                    watchTimeOut(4+files.size());
                    send("JSON_" + obj.toString());
                } catch (JSONException e) {
                }
            }
        }
    }

    private boolean checkStatus() {
        synchronized (syncStatus) {
            logStatus("checkStatus()");
            return syncStatus.status.equals(Status.CONNECTED);
        }
    }

    private void cancelWatchTimeOut() {
        Log.i(TAG, "timerWatchTimeout.cancel()");
        runOnUiThread(() -> {
            synchronized(timerLock) {
                if(timerWatchTimeout!=null) {
                    timerWatchTimeout.cancel(); //Cancel previous if any
                }
            }
        });
    }

    private void watchTimeOut(final long timeout) {
        synchronized(timerLock) {
            Log.i(TAG, "watchTimeOut(" + timeout + ")");
            cancelWatchTimeOut();
            runOnUiThread(() -> {
                synchronized (timerLock) {
                    timerWatchTimeout = new CountDownTimer(timeout*1000, timeout *1000 / 10) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long seconds = (millisUntilFinished / 1000);
                            Log.i(TAG, "timerWatchTimeout Remaining: " + (seconds>0?seconds+"s":millisUntilFinished+"ms"));
                        }

                        @Override
                        public void onFinish() {
                            close(true, "Timed out waiting.", -1);
                        }
                    };
                    Log.i(TAG, "timerWatchTimeout.start()");
                    timerWatchTimeout.start();
                }
            });
        }
    }

    private enum Status {
        NOT_CONNECTED,
        CONNECTING,
        CONNECTED,
        STOPPING
    }

    private class SyncStatus {
        public Status status;
        public int nbRetries;

        private SyncStatus(Status status, int nbRetries) {
            this.status = status;
            this.nbRetries = nbRetries;
        }

        @Override
        public String toString() {
            return "SyncStatus{" +
                    "status=" + status +
                    ", nbRetries=" + nbRetries +
                    '}';
        }
    }

    private void logStatus(String msg) {
        synchronized (syncStatus) {
            Log.i(TAG, msg + " : " + syncStatus.toString());
        }
    }

    private void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }
}