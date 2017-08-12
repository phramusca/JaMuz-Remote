package phramusca.com.jamuzremote;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.Looper;
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
import java.io.OutputStream;

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
                        long fileMax=200000000L; // > 200MB is big enough. Not sure it would even work
                        if(fileSize<fileMax && fileSize>0) {
                            FileOutputStream fos = new FileOutputStream(path.getAbsolutePath() + File.separator +
                                    fileInfoReception.relativeFullPath);
                            byte[] buf = new byte[1024]; // Adjust if you want
                            int bytesRead;
                            callback.timeout();
                            while (fileSize > 0 && (bytesRead = dis.read(buf, 0, (int) Math.min(buf.length, fileSize))) != -1) {
                                fos.write(buf, 0, bytesRead);
                                fileSize -= bytesRead;
                                //Log.v(TAG, "receivedFile chunk. Size is now: " + fileSize);
                            }
                            fos.close();
                        } else {
                            Log.e(TAG, "Size over limits (0 - "+fileMax+") !!! Aborting. Waiting 5s (does this helps ???)");
                            Thread.sleep(5000);
                            //FIXME: Even if aborting the buffer is corrupted !!
                            //Needs to close and reopen connection
                        }
					}
                    catch (IOException | OutOfMemoryError | JSONException e) {
                        Log.e(TAG, "receivedFile", e);
                    } finally {
                        Log.i(TAG, "receivedFile: calling callback");
						callback.receivedFile(fileInfoReception);
					}
				}
			}
		} catch (InterruptedException ex) {
		} catch (IOException ex) {
		}
		finally {
			try {
                inputStream.close();
			} catch (IOException e) {
			}
		}
	}

    /**
     *
     * @param input
     * @param output
     * @throws IOException
     */
    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024]; // Adjust if you want
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1)
        {
            output.write(buffer, 0, bytesRead);
            Log.i(TAG, String.valueOf(bytesRead));
        }
    }

}