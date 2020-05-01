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
    private final int canal;
    private final String appId;
    private final int port;
	private final String login;
	private final String password;
    private String rootPath;

    public ClientInfo(String address, int port, String login, String password,
                      int canal, String appId, String rootPath){
		this.port = port;
		this.login = login;
		this.password = password;
		this.address = address;
        this.canal = canal;
        this.appId = appId;
        this.rootPath = rootPath;
    }

    public ClientInfo(ClientInfo clientInfo, int canal) {
        this(clientInfo.address, clientInfo.port, clientInfo.login, clientInfo.password,
                canal, clientInfo.appId, clientInfo.rootPath);
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
            jsonObject.put("canal", canal);
            jsonObject.put("appId", appId);
            jsonObject.put("rootPath", rootPath);
        } catch (JSONException e) {
        }
        return jsonObject;
    }
}