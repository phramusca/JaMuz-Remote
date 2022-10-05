package phramusca.com.jamuzremote;

import android.graphics.Bitmap;

/**
 * @author phramusca
 */
public interface IListenerRemote {
    void onReceivedJson(String json);

    void onReceivedBitmap(Bitmap bitmap);

    void onDisconnected(String msg);
}
