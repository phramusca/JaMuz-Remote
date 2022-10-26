package phramusca.com.jamuzremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by raph on 17/06/17.
 */
public class ReceiverHeadSetPlugged extends BroadcastReceiver {

    private static final String TAG = ReceiverHeadSetPlugged.class.getName();
    private boolean headsetConnected = false;

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
            Log.i(TAG, "headset NOT Connected => pause"); //NON-NLS
            ActivityMain.audioPlayer.pause();
        } else if (!headsetConnected && intent.getIntExtra("state", 0) == 1) { //NON-NLS
            headsetConnected = true;
            Log.i(TAG, "headset IS Connected => resume"); //NON-NLS
            ActivityMain.audioPlayer.resume();
        }
    }
}
