package phramusca.com.jamuzremote;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Reception  extends ProcessAbstract {

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
                System.out.println("____________________________________________________");
				checkAbort();
				String msg = bufferedReader.readLine();
                if(msg!=null) {
                    System.out.println(msg);
                }
                if(msg==null) {
                    System.out.println("null");
                    callback.disconnected();
                }
                else if (msg.startsWith("MSG_")) {
                    callback.received(msg);
                }
                else if (msg.startsWith("JSON_")) {
                    callback.received(msg.substring(5));
                } else if (msg.equals("SENDING_COVER")) {
                    try {
                        //FIXME: works locally but not (well) over wifi:
                        // either gets stuck in decodeStream, either gets "SkImageDecoder::Factory returned null" and then data without catching
						//Once an error occurred, have to close/connect to make it work again (if it does)
                        Bitmap bMap = BitmapFactory.decodeStream(inputStream);
                        System.out.println("receivedBitmap");
                        callback.receivedBitmap(bMap);
                    } catch (OutOfMemoryError ex) {
                        Logger.getLogger(Reception.class.getName()).log(Level.SEVERE, null, ex);
                        callback.received("MSG_ERROR_OUT_OF_MEMORY");
                    }
                }
			}
		} catch (InterruptedException ex) {
//			Logger.getLogger(Reception.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
//			Logger.getLogger(Reception.class.getName()).log(Level.SEVERE, null, ex);
		}
		finally {
			try {
                inputStream.close();
			} catch (IOException e) {
//				Logger.getLogger(Reception.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}