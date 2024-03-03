package phramusca.com.jamuzremote;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

import okhttp3.Headers;
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
    private final String appId;
    private final int port;
    private final String login;
    private final String password;
    private final String rootPath;
    private final String model;

    public ClientInfo(String address, int port, String login, String password,
                      String appId, String rootPath, String model) {
        this.port = port;
        this.login = login;
        this.password = password;
        this.address = address;
        this.appId = appId;
        this.rootPath = rootPath;
        this.model = model;
    }

    public HttpUrl.Builder getUrlBuilder(String url) {
        return Objects.requireNonNull(HttpUrl.parse("http://" + getAddress() + ":" + getPort() + "/" + url)).newBuilder(); //NON-NLS
    }

    public Request.Builder getRequestBuilder(HttpUrl.Builder urlBuilder) {
        return new Request.Builder()
                .headers(getHeaders())
                .url(urlBuilder.build());
    }

    public Headers getHeaders() {
        return new Headers.Builder()
                .add("login", getLogin() + "-" + getAppId()) //NON-NLS
                //FIXME Update api version, and check behavior of JaMuz Desktop
                .add("api-version", "2.0") //NON-NLS
                .build();
    }

    public Request getConnectRequest() {
        HttpUrl.Builder urlBuilder = getUrlBuilder("connect"); //NON-NLS
        return getRequestBuilder(urlBuilder)
                .addHeader("password", getPassword())
                .addHeader("rootPath", getRootPath())
                .addHeader("model", getModel())
                .build();
    }

    public String getBodyString(String url, OkHttpClient client) throws IOException, ServerException {
        HttpUrl.Builder urlBuilder = getUrlBuilder(url);
        return getBodyString(urlBuilder, client);
    }

    public String getBodyString(HttpUrl.Builder urlBuilder, OkHttpClient client) throws IOException, ServerException {
        return getBody(urlBuilder, client).string();
    }

    public ResponseBody getBody(HttpUrl.Builder urlBuilder, OkHttpClient client) throws IOException, ServerException {
        Request request = getRequestBuilder(urlBuilder).build();
        return getBody(request, client);
    }

    public String getBodyString(Request request, OkHttpClient client) throws IOException, ServerException {
        return getBody(request, client).string();
    }

    private ResponseBody getBody(Request request, OkHttpClient client) throws IOException, ServerException {
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            if (response.code() == 301) {
                throw new ServerException(request.header("api-version") + " not supported. " + Objects.requireNonNull(response.body()).string()); //NON-NLS
            }
            throw new ServerException(response.code() + ": " + response.message());
        }
        return response.body();
    }

    public static class ServerException extends Exception {
        public ServerException(String errorMessage) {
            super(errorMessage);
        } //NON-NLS
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

    public String getModel() {
        return model;
    }

    public String getAppId() {
        return appId;
    }

    public String getPassword() {
        return password;
    }

    public String getRootPath() {
        return rootPath;
    }
}