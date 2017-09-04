package phramusca.com.jamuzremote;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import static phramusca.com.jamuzremote.MainActivity.getAppDataPath;

public class Reception  extends ProcessAbstract {

    private static final String TAG = Reception.class.getSimpleName();
	private final BufferedReader bufferedReader;
	private InputStream inputStream;
	private final ICallBackReception callback;
	private final String login;

	
	public Reception(InputStream inputStream, ICallBackReception callback, String login) {
		super("Thread.Client.Reception");
		this.inputStream = inputStream;

		this.callback = callback; 
		this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		this.login = login;
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				checkAbort();
				String msg = bufferedReader.readLine();
                if(msg==null) {
                    Log.d(TAG, "RECEIVED null");
                    callback.disconnected();
                }
                else if (msg.startsWith("MSG_")) {
                    callback.received(msg);
                }
                else if (msg.startsWith("JSON_")) {
                    callback.received(msg.substring(5));
                }
				else if (msg.equals("SENDING_COVER")) {
                    Bitmap bitmap=null;
                    try {
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        Log.d(TAG, "receivedBitmap");
                    } catch (OutOfMemoryError ex) {
                    } finally {
                        Log.d(TAG, "receivedBitmap: calling callback");
                        callback.receivedBitmap(bitmap);
                    }
                }
				else if (msg.startsWith("SENDING_FILE")) {
                    FileInfoReception fileInfoReception = null;
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
                        // FIXME: Find best. Make a benchmark (and use it in notification progres bar)
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
                    catch (IOException | OutOfMemoryError | JSONException e) {
                        Log.e(TAG, "receivedFile", e);
                    }
				} else if (msg.startsWith("SENDING_DB")) {
                    try {
                        Log.i(TAG, "Start database reception");
                        DataInputStream dis = new DataInputStream(new BufferedInputStream(inputStream));
                        double fileSize = dis.readLong();
                        FileOutputStream fos = new FileOutputStream(MainActivity.musicLibraryDbFile);
                        // FIXME: Find best. Make a benchmark
                        //https://stackoverflow.com/questions/8748960/how-do-you-decide-what-byte-size-to-use-for-inputstream-read
                        byte[] buf = new byte[8192];
                        int bytesRead;
                        //FIXME: Need to lock database writing
                        // as writing (scan) fails while receiving
                        while (fileSize > 0 && (bytesRead = dis.read(buf, 0, (int) Math.min(buf.length, fileSize))) != -1) {
                            checkAbort();
                            fos.write(buf, 0, bytesRead);
                            fileSize -= bytesRead;
                        }
                        fos.close();
                        Log.i(TAG, "database received");
                        callback.receivedDatabase();
                        checkAbort();
                    }
                    catch (IOException | OutOfMemoryError e) {
                        Log.e(TAG, "receivedDB", e);
                    }
                }
			}
		} catch (InterruptedException ex) {
		} catch (IOException ex) {
            callback.disconnected();
		}
		finally {
			try {
                inputStream.close();
			} catch (IOException e) {
			}
		}
	}
}