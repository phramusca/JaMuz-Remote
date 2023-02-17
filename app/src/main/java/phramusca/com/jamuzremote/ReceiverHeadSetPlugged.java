package phramusca.com.jamuzremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverHeadSetPlugged extends BroadcastReceiver {

    private static final String TAG = ReceiverHeadSetPlugged.class.getName();
    private boolean headsetConnected = false;
    private IListenerHeadSet listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null
                || !Intent.ACTION_HEADSET_PLUG.equals(intent.getAction())
                || !intent.hasExtra("state")) {
            Log.d(TAG, "Ignore unsupported intent: " + intent);
            return;
        }
        if (headsetConnected && intent.getIntExtra("state", 0) == 0) { //NON-NLS
            headsetConnected = false;
            Log.d(TAG, "headset NOT Connected => pause"); //NON-NLS
            listener.onPause();
        } else if (!headsetConnected && intent.getIntExtra("state", 0) == 1) { //NON-NLS
            headsetConnected = true;
            Log.d(TAG, "headset IS Connected => resume"); //NON-NLS
            listener.onResume();
        }
    }

    public void setListener(IListenerHeadSet listener) {
        this.listener = listener;
    }
}
