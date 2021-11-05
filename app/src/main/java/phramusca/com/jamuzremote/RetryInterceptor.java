package phramusca.com.jamuzremote;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryInterceptor implements Interceptor {
    private static final String TAG = RetryInterceptor.class.getName();
    protected HelperNotification helperNotification;
    private final Notification notification;
    private Context mContext;
    private final int sleepSeconds;
    private final int maxNbRetries;

    RetryInterceptor(Context context, int sleepSeconds, int maxNbRetries, HelperNotification helperNotification, Notification notification) {
        mContext = context;
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
                Log.d(TAG, "CALLING: " + request.toString()); //NON-NLS
                response = chain.proceed(request);
                break;
            } catch (Exception e) {
                msg = e.getLocalizedMessage();
                Log.d(TAG, "ERROR: " + msg); //NON-NLS
                helperNotification.notifyBar(notification, String.format(
                        "%ds %s %d/%d : %s", //NON-NLS
                        sleepSeconds,
                        mContext.getString(R.string.globalLabelBefore),
                        nbRetries + 1,
                        maxNbRetries,
                        msg));
                try {
                    sleep(sleepSeconds * 1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        } while (nbRetries < maxNbRetries - 1);
        if (response == null) {
            throw new IOException(msg);
        }
        return response;
    }
}
