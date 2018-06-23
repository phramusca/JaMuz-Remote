/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phramusca.com.jamuzkids;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 *
 * @author phramusca
 */
public class ClientInfo implements Serializable {

	private final String address;
    private final boolean isRemote;
    private final String appId;
    private final int port;
	private final String login;
	private final String password;

	public ClientInfo(String address, int port, String login, String password, boolean isRemote, String appId){
		this.port = port;
		this.login = login;
		this.password = password;
		this.address = address;
        this.isRemote = isRemote;
        this.appId = appId;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("login", login);
            jsonObject.put("password", password);
            jsonObject.put("isRemote", isRemote);
            jsonObject.put("appId", appId);
        } catch (JSONException e) {
        }
        return jsonObject;
    }
}