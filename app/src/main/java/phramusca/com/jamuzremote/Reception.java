package phramusca.com.jamuzremote;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static phramusca.com.jamuzremote.MainActivity.getExtSDcard;

public class Reception  extends ProcessAbstract {

    private static final String TAG = ProcessAbstract.class.getName();
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
                    Log.i(TAG, "RECEIVED null");
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
                        Log.i(TAG, "receivedBitmap");
                    } catch (OutOfMemoryError ex) {
                    } finally {
                        Log.i(TAG, "receivedBitmap: calling callback");
                        callback.receivedBitmap(bitmap);
                    }
                }
				else if (msg.startsWith("SENDING_FILE")) {
                    int idFile = -1;
                    try {

                        idFile = Integer.valueOf(msg.substring("SENDING_FILE".length()));
                        Log.i(TAG, "receivedFile "+idFile);
                        File path =  getExtSDcard("/storage/", "JaMuz");
                        //FIXME: Write to SD card !!
                        /*File path = new File("/storage/3515-1C15/Android/data/jamuzremote.com.phramusca");
                        if(!path.exists()) {
                            path.mkdirs();
                        }*/


                        DataInputStream dis = new DataInputStream(new BufferedInputStream(inputStream));
                        long fileSize = dis.readLong();
                        FileOutputStream fos =  new FileOutputStream(path.getAbsolutePath()+File.separator+idFile);
                        byte[] buf = new byte[1024]; // Adjust if you want
                        int bytesRead;
                        while (fileSize > 0 && (bytesRead = dis.read(buf, 0, (int)Math.min(buf.length, fileSize))) != -1)
                        {
                            fos.write(buf,0,bytesRead);
                            fileSize -= bytesRead;
                        }
                        fos.close();
					}
                    catch (IOException | OutOfMemoryError /* | JSONException*/ ex) {
                        Log.e(TAG, "receivedFile", ex);
                    } finally {
                        Log.i(TAG, "receivedFile: calling callback");
						callback.receivedFile(idFile);
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