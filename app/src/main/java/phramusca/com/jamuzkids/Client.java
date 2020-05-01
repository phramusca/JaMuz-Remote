/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phramusca.com.jamuzkids;

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

/**
 *
 * @author phramusca
 */
public class Client {
	private static final String TAG = Client.class.getName();
	
	private Socket socket = null;
	private ClientEmission emission;
	private ClientReception reception;

	private IListenerReception callback;
    private BufferedReader bufferedReader;
	private OutputStream outputStream;
    private ClientInfo clientInfo;

    Client(ClientInfo clientInfo){
        this.clientInfo = clientInfo;
    }

    void setCallback(IListenerReception callback) {
        this.callback = callback;
    }

    public boolean connect() {
		try {
            //TODO: Secure connexion
            //http://www.java2s.com/Code/Java/Network-Protocol/SecureCommunicationwithJSSE.htm
            socket = new Socket(clientInfo.getAddress(), clientInfo.getPort());
            if(!socket.isConnected()) {
                socket.connect(new InetSocketAddress(clientInfo.getAddress(), clientInfo.getPort()));
                socket.setSoTimeout(10000);
            }
			InputStream inputStream = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            outputStream = socket.getOutputStream();
            emission = new ClientEmission(new PrintWriter(outputStream));
            emission.start();

            //Authenticating
            if(waitPrompt("MSG_AUTHENTICATE")) {
                send(clientInfo.toJSONObject().toString());
                if(waitPrompt("MSG_CONNECTED")) {
                    reception = new ClientReception(inputStream, callback);
                    reception.start();
                    return true;
                }
            }
            callback.onDisconnected("Authentication failed.");
            return false;
		} catch (IOException ex) {
            //Includes SocketException
            Log.w(TAG, "", ex);
            callback.onDisconnected(ex.getMessage());
			return false;
		}
	}

	public boolean isConnected() {
		return socket!=null && socket.isConnected();
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
		} catch (NullPointerException | IOException ex) {
            //Includes SocketException
            Log.w(TAG, "", ex);
			return false;
		}
	}

	public void send(String msg) {
		if(emission!=null) {
			Log.i(TAG, "SENDING "+msg);
			emission.send(msg);
		}
	}

	protected void sendFile(File file) {
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(outputStream));
		if(file.exists() && file.isFile())
		{
			try (FileInputStream input = new FileInputStream(file)) {
				Log.i(TAG, "Sending : "+file.getAbsolutePath());
				Log.i(TAG, "Size : "+file.length());
                dos.writeLong(file.length());
				int read;
				while ((read = input.read()) != -1) {
                    dos.writeByte(read);
                }
				dos.flush();
				Log.i(TAG, "File successfully sent!");
			} catch (IOException ex) {
                //This includes SocketException
                Log.e(TAG, "", ex);
                callback.onDisconnected(ex.getMessage());
			}
		}
	}
}