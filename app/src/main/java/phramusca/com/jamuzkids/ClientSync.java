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

import java.util.List;

/**
 *
 * @author phramusca
 */
public class ClientSync extends Client {
	private static final String TAG = ClientSync.class.getName();
	private final IListenerSync callback;
    private final boolean doReconnect;
    private final SyncStatus syncStatus = new SyncStatus(Status.NOT_CONNECTED, 0);
    private CountDownTimer timerWatchTimeout;
    private static final Object timerLock = new Object();
    private Handler mHandler = new Handler(Looper.getMainLooper());

	ClientSync(ClientInfo clientInfo, IListenerSync callback, boolean doReconnect){
		super(clientInfo);
		this.callback = callback;
        this.doReconnect = doReconnect;
        super.setCallback(new ListenerReception());
	}

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
                    callback.onConnected();
                    return true;
                }
            }
            return false;
        }
    }

    public void close(boolean reconnect, String msg, long millisInFuture, boolean enable) {
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
                    logStatus("Re-connecting in 5s");
                    new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ignored) {
                            }
                            logStatus("Re-connecting now");
                            if(connect()) {
                                request("requestNewFiles");
                            }
                        }
                    }.start();
                } else {
                    msg="Too many retries ("+syncStatus.nbRetries+").";
                    reconnect=false;
                    syncStatus.status=Status.STOPPING;
                }
            } else {
                syncStatus.status=Status.STOPPING;
            }
            callback.onDisconnected(reconnect, msg, millisInFuture, enable);
        }
    }

    class ListenerReception implements IListenerReception {

		@Override
		public void onReceivedJson(String json) {
		    synchronized (syncStatus) {
                logStatus("onReceivedJson(" + json + ")");
                cancelWatchTimeOut();
                callback.onReceivedJson(json);
            }
		}

        @Override
        public void onReceivedBitmap(Bitmap bitmap) {        }

        @Override
        public void onReceivingFile(Track fileInfoReception) {
            callback.onReceivingFile(fileInfoReception);
        }

        @Override
        public void onReceivedFile(Track fileInfoReception) {
            synchronized (syncStatus) {
                logStatus("onReceivedFile(" + fileInfoReception + ")");
                cancelWatchTimeOut();
                callback.onReceivedFile(fileInfoReception);
            }
        }

        @Override
		public void onDisconnected(String msg) {
            synchronized (syncStatus) {
                logStatus("onDisconnected(\""+msg+"\")");
                if(msg.equals("ENOSPC")) {
                    close(false, "No more space on device. Check your playlist limits and available space in your SD card.", -1, true);
                } else if(syncStatus.status.equals(Status.CONNECTED)
                        || syncStatus.status.equals(Status.CONNECTING)) {
                    close(doReconnect, (syncStatus.nbRetries>0?"Attempt "+syncStatus.nbRetries
                            :"Disconnected")+": "+msg, -1, true);
                }
            }
		}
	}

    public void requestFile(Track track) {
        synchronized (syncStatus) {
            //TODO: Make sync timeouts configurable (use bench, to be based on size not nb)
            /*long minTimeout = 15;  //Min timeout 15s (or 15s by 4Mo)
            long maxTimeout = 120; //Max timeout 2 min

            long timeoutFile = track.getSize() < 4000000
                    ? minTimeout
                    : ((track.getSize() / 4000000) * minTimeout);
            timeoutFile = timeoutFile > maxTimeout ? maxTimeout : timeoutFile;
            watchTimeOut(timeoutFile);*/
            logStatus("requestFile()");
            if(checkStatus()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("type", "requestFile");
                    obj.put("idFile", track.getIdFileServer());
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

    public void requestMerge(List<Track> tracks) {
        synchronized (syncStatus) {
            logStatus("requestFile()");
            if(checkStatus()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("type", "FilesToMerge");
                    //JSONObject jsonAsMap = new JSONObject();
                    JSONArray filesToMerge = new JSONArray();
                    for (Track track : tracks) {
                        filesToMerge.put(track.toJSONObject());
                    }
                    obj.put("files", filesToMerge);
                    watchTimeOut(4+tracks.size());
                    send("JSON_" + obj.toString());
                } catch (JSONException e) {
                }
            }
        }
    }

    // TODO => sync and merge: would even be better to merge genres and tags instead of getting, especially for tags

    private boolean checkStatus() {
        synchronized (syncStatus) {
            logStatus("checkStatus()");
            return syncStatus.status.equals(Status.CONNECTED);
        }
    }

    private void cancelWatchTimeOut() {
        /*Log.i(TAG, "timerWatchTimeout.cancel()");
        runOnUiThread(() -> {
            synchronized(timerLock) {
                if(timerWatchTimeout!=null) {
                    timerWatchTimeout.cancel(); //Cancel previous if any
                }
            }
        });*/
    }

    private void watchTimeOut(final long timeout) {
        /*synchronized(timerLock) {
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
                            close(doReconnect, "Timed out waiting.", doReconnect?-1:5000);
                        }
                    };
                    Log.i(TAG, "timerWatchTimeout.start()");
                    timerWatchTimeout.start();
                }
            });
        }*/
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