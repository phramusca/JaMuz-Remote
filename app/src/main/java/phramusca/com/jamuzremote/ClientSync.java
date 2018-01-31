/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phramusca.com.jamuzremote;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;

/**
 *
 * @author phramusca
 */
public class ClientSync extends Client {
	private static final String TAG = ClientSync.class.getSimpleName();

	private final ICallBackSync callback;

    private final SyncStatus syncStatus = new SyncStatus(Status.NOT_CONNECTED, 0);

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

	public ClientSync(ClientInfo clientInfo, ICallBackSync callback){
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
                if (super.connect()) {
                    syncStatus.status=Status.CONNECTED;
                    syncStatus.nbRetries=0;
                    logStatus("Connected");
                    callback.connected();
                    RepoSync.save();
                    return true;
                }
            }
            return false;
        }
    }

    public void close(boolean reconnect, String msg, long millisInFuture) {
        synchronized (syncStatus) {
            logStatus("close()");
            if(!syncStatus.status.equals(Status.NOT_CONNECTED)) {
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
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                        }
                    }
                    logStatus("Re-connecting now");
                    new Thread() {
                        public void run() {
                            connect();
                        }
                    }.start();
                } else {
                    RepoSync.save();
                    msg="Too many retries ("+syncStatus.nbRetries+").";
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
			callback.receivedJson(json);
		}

        @Override
        public void receivedBitmap(Bitmap bitmap) {        }

        @Override
        public void receivingFile(FileInfoReception fileInfoReception) {
            callback.receivingFile(fileInfoReception);
        }

        @Override
        public void receivedFile(FileInfoReception fileInfoReception) {
            callback.receivedFile(fileInfoReception);
        }

        @Override
        public void receivedDatabase() {
            callback.receivedDatabase();
        }

        @Override
		public void disconnected(String msg) {
            synchronized (syncStatus) {
                logStatus("disconnected()");
                if(syncStatus.status.equals(Status.CONNECTED)
                        || syncStatus.status.equals(Status.CONNECTING)) {
                    close(true, (syncStatus.nbRetries>0?"Attempt "+syncStatus.nbRetries
                            :"Disconnected")+": "+msg, -1);
                }
            }
		}
	}

    public void requestFile(int idFile) {
        synchronized (syncStatus) {
            logStatus("requestFile()");
            if(checkStatus()) {
                send("sendFile" + idFile);
            }
        }
    }

    //FIXME: Add timeout for every request AND use FileInfoReception for merge/ack:
    // => Then,  make all communications (genres and tags especially, if not only) request/answer
    // (all requests being initiated by client (JaMuzRemote) of course)
    // => Then, do NOT request genres and tags at every connection but only if required or on demand
    // => Then, avoid double acknowledgement:
    //          - Insert files in deviceFiles directly at export
    //          - Use a status as in JaMuzRemote
    // => Then, use this and FileInfoReception to merge statistics instead of current merge
    // => Then, preserve current merge for user chosen folder only

    public void ackFileReception(int idFile, boolean requestNextFile) {
        synchronized (syncStatus) {
            logStatus("ackFileReception()");
            if(checkStatus()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("type", "ackFileReception");
                    obj.put("idFile", idFile);
                    //TODO: Remove requestNextFile as (apparently) no more used
                    obj.put("requestNextFile", requestNextFile);
                    send("JSON_" + obj.toString());
                } catch (JSONException e) {
                }
            }
        }
    }

	public void sendDatabase() {
        synchronized (syncStatus) {
            logStatus("sendDatabase()");
            if(checkStatus()) {
                File file = MainActivity.musicLibraryDbFile;
                if (file.exists() && file.isFile()) {
                    send("SENDING_DB");
                    DataOutputStream dos = new DataOutputStream(
                            new BufferedOutputStream(outputStream));
                    sendFile(file, dos);
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
}