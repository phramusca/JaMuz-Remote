package phramusca.com.jamuzremote;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static java.lang.Thread.sleep;

public class RetryInterceptor implements Interceptor {
    private static final String TAG = RetryInterceptor.class.getName();
    protected HelperNotification helperNotification;
    private final Notification notification;
    private final int sleepSeconds;
    private final int maxNbRetries;

    RetryInterceptor(int sleepSeconds, int maxNbRetries, HelperNotification helperNotification, Notification notification) {
        this.sleepSeconds = sleepSeconds;
        this.maxNbRetries = maxNbRetries;
        this.helperNotification = helperNotification;
        this.notification = notification;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        int nbRetries = 0;
        Response response = null;
        String msg = "";
        do {
            nbRetries++;
            try {
                Log.d(TAG, "CALLING: "+ request.toString());
                response = chain.proceed(request);
                break;
            } catch (Exception e) {
                msg = e.getLocalizedMessage();
                Log.d(TAG, "ERROR: "+ msg);
                helperNotification.notifyBar(notification, sleepSeconds + "s before " +
                        (nbRetries + 1) + "/" + maxNbRetries + " : " + msg);
                try {
                    sleep(sleepSeconds*1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        } while (nbRetries < maxNbRetries-1);
        if(response==null) {
            throw new IOException(msg);
        }
        return response;
    }
}
