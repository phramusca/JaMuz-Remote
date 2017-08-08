/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phramusca.com.jamuzremote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author phramusca
 */
public class Client {
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
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			emission = new Emission(out);
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
			callback.received("MSG_ERROR_UNAUTHORIZED");
			return false;
			
		} catch (IOException ex) {
			sendException(ex);
			return false;
		}
	}

	private void sendException(Exception ex) {
		callback.received("MSG_ERROR: ".concat(ex.toString()));
		Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
	}

	public void close() {
		try {
            if(reception!=null) {
                reception.abort();
            }
            if(socket!=null) {
                socket.close();
            }
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	//TODO: Add a timeout (and for all other while infinite loops as well)
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
			sendException(ex);
			return false;
		}
	}

	public void send(String msg) {
		emission.send(msg);
	}
}