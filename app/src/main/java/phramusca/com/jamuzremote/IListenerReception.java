package phramusca.com.jamuzremote;

import android.graphics.Bitmap;

/**
 * @author phramusca
 */
public interface IListenerReception {
    void onReceivedJson(String json);

    void onReceivedBitmap(Bitmap bitmap);

    void onReceivingFile(Track fileInfoReception);

    void onReceivedFile(Track fileInfoReception);

    void onDisconnected(String msg);
}
