package phramusca.com.jamuzremote;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
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
                      int canal, String appId, String rootPath) {
        this.port = port;
        this.login = login;
        this.password = password;
        this.address = address;
        this.canal = canal;
        this.appId = appId;
        this.rootPath = rootPath;
    }

    public HttpUrl.Builder getUrlBuilder(String url) {
        return Objects.requireNonNull(HttpUrl.parse("http://" + getAddress() + ":" + (getPort() + 1) + "/" + url)).newBuilder(); //NON-NLS
    }

    public Request.Builder getRequestBuilder(HttpUrl.Builder urlBuilder) {
        return new Request.Builder()
                .addHeader("login", getLogin() + "-" + getAppId()) //NON-NLS
                .addHeader("api-version", "1.0") //NON-NLS
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
        if (!response.isSuccessful()) {
            if (response.code() == 301) {
                throw new ServiceSync.ServerException(request.header("api-version") + " not supported. " + Objects.requireNonNull(response.body()).string()); //NON-NLS
            }
            throw new ServiceSync.ServerException(response.code() + ": " + response.message());
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
            jsonObject.put("login", login); //NON-NLS
            jsonObject.put("password", password); //NON-NLS
            jsonObject.put("canal", canal); //NON-NLS
            jsonObject.put("appId", appId);
            jsonObject.put("rootPath", rootPath);
        } catch (JSONException ignored) {
        }
        return jsonObject;
    }
}