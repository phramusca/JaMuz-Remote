/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phramusca.com.jamuzremote;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

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
    private final String rootPath;

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

    public HttpUrl.Builder getUrlBuilder(String url) {
        return HttpUrl.parse("http://"+getAddress()+":"+(getPort()+1)+"/"+url).newBuilder();
    }

    public Request.Builder getRequestBuilder(HttpUrl.Builder urlBuilder) {
        return new Request.Builder()
                .addHeader("login", getLogin()+"-"+getAppId())
                .addHeader("api-version", "1.0")
                .url(urlBuilder.build());
    }

    public String getBodyString(String url, OkHttpClient client) throws IOException, ServiceSync.ServerException {
        HttpUrl.Builder urlBuilder = getUrlBuilder(url);
        return getBodyString(urlBuilder, client);
    }

    public String getBodyString(HttpUrl.Builder urlBuilder, OkHttpClient client) throws IOException, ServiceSync.ServerException {
        return getBody(urlBuilder, client).string();
    }

    public ResponseBody getBody(HttpUrl.Builder urlBuilder, OkHttpClient client) throws IOException, ServiceSync.ServerException {
        Request request = getRequestBuilder(urlBuilder).build();
        return getBody(request, client);
    }

    public String getBodyString(Request request, OkHttpClient client) throws IOException, ServiceSync.ServerException {
        return getBody(request, client).string();
    }

    private ResponseBody getBody(Request request, OkHttpClient client) throws IOException, ServiceSync.ServerException {
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful()) {
            switch (response.code()) {
                case 301:
                    throw new ServiceSync.ServerException(request.header("api-version")+" not supported. "+response.body().string());
                default:
                    throw new ServiceSync.ServerException(response.code()+": "+response.message());
            }
        }
        return response.body();
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

    public String getAppId() {
        return appId;
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