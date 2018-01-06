/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phramusca.com.jamuzremote;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * @author phramusca
 */
public class Client {
	private static final String TAG = Client.class.getSimpleName();

	private final int port;
	private final String login;
	private final String password;
	
	private Socket socket = null;
	private Emission emission;
	private Reception reception;
	private final String address;
	private final ICallBackReception callback;
    private BufferedReader bufferedReader;
	private InputStream inputStream;
    private OutputStream outputStream;

	public Client(String address, int port, String login, String password, ICallBackReception callback){
		this.port = port;
		this.login = login;
		this.password = password;
		this.address = address;
		this.callback = callback;
	}
	
	public boolean connect() {
		try {
			//TODO: Secure connexion
            //http://www.java2s.com/Code/Java/Network-Protocol/SecureCommunicationwithJSSE.htm
			socket = new Socket(address, port);
            if(!socket.isConnected()) {
                socket.connect(new InetSocketAddress(address, port));
				socket.setSoTimeout(10000);
            }

            inputStream = socket.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			//Starting emission thread
            outputStream = socket.getOutputStream();
			emission = new Emission(new PrintWriter(outputStream));
			emission.start();

			//Authenticating
			if(waitPrompt("MSG_ENTER_LOGIN")) {
				send(login);
				if(waitPrompt("MSG_ENTER_PWD")) {
					send(password);
					if(waitPrompt("MSG_CONNECTED")) {
						reception = new Reception(inputStream, callback, login);
						reception.start();
						return true;
					}
				}
			}
			callback.disconnected("Unauthorized");
			return false;
			
		} catch (IOException ex) {
			callbackWithException(ex);
			return false;
		}
	}

	public boolean isConnected() {
		return socket!=null && socket.isConnected();
	}

	private void callbackWithException(Exception ex) {
		callback.disconnected(ex.toString());
        Log.w(TAG, "", ex);
	}

	public void close() {
		try {
			if(emission!=null) {
				emission.abort();
			}
            if(reception!=null) {
                reception.abort();
            }
            if(socket!=null) {
                socket.close();
            }
		} catch (IOException ex) {
			Log.e(TAG, "", ex);
		}
	}

	private boolean waitPrompt(String prompt) {
		try {
			while(true){
				String received = bufferedReader.readLine();
				if(received.equals(prompt)) {
					return true;
				}
				else if(received.startsWith("MSG_ERROR")) {
					return false;
				}
			}
		} catch (IOException ex) {
			callbackWithException(ex);
			return false;
		}
	}

	public void send(String msg) {
		if(emission!=null) {
			Log.i(TAG, "SENDING "+msg);
			emission.send(msg);
		}
	}

	public void sendDatabase() {
        File file = MainActivity.musicLibraryDbFile;
		if(file.exists()&&file.isFile())
		{
			send("SENDING_DB");
			DataOutputStream dos = new DataOutputStream(
					new BufferedOutputStream(outputStream));
			sendFile(file, dos);
		}
	}

	private void sendFile(File file, DataOutputStream dos) {
		if(dos!=null&&file.exists()&&file.isFile())
		{
			try (FileInputStream input = new FileInputStream(file)) {
				Log.i(TAG, "Sending : "+file.getAbsolutePath());
				Log.i(TAG, "Size : "+file.length());
                dos.writeLong(file.length());
				int read = 0;
				while ((read = input.read()) != -1) {
                    dos.writeByte(read);
                }
				dos.flush();
				Log.i(TAG, "File successfully sent!");
			} catch (SocketException ex) {
                Log.e(TAG, "", ex);
				close();
				callback.disconnected(ex.toString());
			} catch (IOException ex) {
                Log.e(TAG, "", ex);
			}
		}
	}
}