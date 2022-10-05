package phramusca.com.jamuzremote;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * @author phramusca
 */
public class ClientRemote extends Client {

    private final IListenerRemote callback;
    private boolean userStop = false;

    public ClientRemote(ClientInfo clientInfo, IListenerRemote callback, Context context) {
        super(clientInfo, context);
        this.callback = callback;
        super.setCallback(new ListenerReception());
    }

    @Override
    public boolean connect() {
        userStop = false;
        return super.connect();
    }

    @Override
    public void close() {
        userStop = true;
        super.close();
    }

    class ListenerReception implements IListenerReception {

        @Override
        public void onReceivedJson(String json) {
            callback.onReceivedJson(json);
        }

        @Override
        public void onReceivedBitmap(Bitmap bitmap) {
            callback.onReceivedBitmap(bitmap);
        }

        @Override
        public void onReceivingFile(Track fileInfoReception) {
        }

        @Override
        public void onReceivedFile(Track fileInfoReception) {
        }

        @Override
        public void onDisconnected(String msg) {
            callback.onDisconnected(userStop ? "" : msg);
        }
    }
}