package phramusca.com.jamuzkids;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.system.ErrnoException;
import android.system.OsConstants;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static phramusca.com.jamuzkids.MainActivity.getAppDataPath;

public class ClientReception extends ProcessAbstract {

    private static final String TAG = ClientReception.class.getName();
	private final BufferedReader bufferedReader;
	private InputStream inputStream;
	private final ICallBackReception callback;

	ClientReception(InputStream inputStream, ICallBackReception callback) {
		super("Thread.Client.ClientReception");
		this.inputStream = inputStream;

		this.callback = callback; 
		this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	}
	
	@Override
	public void run() {
		try {
            //noinspection InfiniteLoopStatement
            while(true) {
				checkAbort();
				String msg = bufferedReader.readLine();
                if(msg==null) {
                    Log.d(TAG, "RECEIVED null");
                    callback.disconnected("Socket closed (received null)");
                }
                else if (msg.startsWith("JSON_")) {
                    callback.receivedJson(msg.substring(5));
                }
				else if (msg.equals("SENDING_COVER")) {
                    Bitmap bitmap=null;
                    try {
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        Log.d(TAG, "receivedBitmap");
                    } catch (OutOfMemoryError ignored) {
                        //Handed in callback
                    } finally {
                        Log.d(TAG, "receivedBitmap: calling callback");
                        callback.receivedBitmap(bitmap);
                    }
                }
				else if (msg.startsWith("SENDING_FILE")) {
                    FileInfoReception fileInfoReception;
                    try {
                        String json = msg.substring("SENDING_FILE".length());
                        fileInfoReception = new FileInfoReception(json);
                        File path = getAppDataPath();
                        File destinationPath = new File(path.getAbsolutePath()+File.separator
                                +new File(fileInfoReception.relativeFullPath).getParent());
                        destinationPath.mkdirs();
                        Log.i(TAG, "Start file reception: \n"+fileInfoReception);
                        DataInputStream dis = new DataInputStream(new BufferedInputStream(inputStream));
                        double fileSize = fileInfoReception.size;
                        FileOutputStream fos = new FileOutputStream(path.getAbsolutePath() + File.separator +
                                fileInfoReception.relativeFullPath);
                        callback.receivingFile(fileInfoReception);
                        // TODO: Find best. Make a benchmark (and use it in notification progres bar)
                        //https://stackoverflow.com/questions/8748960/how-do-you-decide-what-byte-size-to-use-for-inputstream-read
                        byte[] buf = new byte[8192];
                        int bytesRead;
                        while (fileSize > 0 && (bytesRead = dis.read(buf, 0, (int) Math.min(buf.length, fileSize))) != -1) {
                            checkAbort();
                            fos.write(buf, 0, bytesRead);
                            fileSize -= bytesRead;
                        }
                        fos.close();
                        checkAbort();
                        callback.receivedFile(fileInfoReception);
					}
                    catch (OutOfMemoryError | JSONException e) {
                        Log.e(TAG, "receivedFile", e);
                    }
				}
			}
		} catch (InterruptedException ignored) {
        } catch (IOException ex) {
            boolean isENOSPC = false;
            if (ex.getCause() instanceof ErrnoException) {
                int errno = ((ErrnoException)ex.getCause()).errno;
                isENOSPC = errno == OsConstants.ENOSPC;
                //FIXME: sync and merge: Manage errors like ENOENT (No such file or directory)") : SyncStatus{status=CONNECTED, nbRetries=0}
                // 5-13 21:28:32.452 I/phramusca.com.jamuzremote.ClientSync:
                // disconnected("/storage/extSdCard/Android/data/org.phramusca.jamuz/files/Autres/DJ Little Tune/
                // New Remix Maquette 2007 /02 track2 .mp3:
                // open failed: ENOENT (No such file or directory)") : SyncStatus{status=CONNECTED, nbRetries=0}
                //OsConstants.ENOENT

                //NOTE the SPACE in the path !! "New Remix Maquette 2007 /02 track2 .mp3"
                //(it has been fixed by removing space in path only => "New Remix Maquette 2007/02 track2 .mp3" and in db)
            }
            if (isENOSPC) {
                //Ex: java.io.IOException: write failed: ENOSPC (No space left on device)
                callback.disconnected("ENOSPC");
            } else {
                // Other IOExceptions incl. SocketException
                callback.disconnected(ex.getMessage());
            }
		}
		finally {
			try {
                inputStream.close();
			} catch (IOException ignored) {
			}
		}
	}
}