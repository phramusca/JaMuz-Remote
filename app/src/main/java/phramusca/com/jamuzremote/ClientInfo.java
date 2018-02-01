/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phramusca.com.jamuzremote;

import java.io.Serializable;

/**
 *
 * @author phramusca
 */
public class ClientInfo implements Serializable {

	private final String address;
	private final int port;
	private final String login;
	private final String password;

	public ClientInfo(String address, int port, String login, String password){
		this.port = port;
		this.login = login;
		this.password = password;
		this.address = address;
	}

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}