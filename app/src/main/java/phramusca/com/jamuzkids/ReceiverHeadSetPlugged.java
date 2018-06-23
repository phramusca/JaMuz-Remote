package phramusca.com.jamuzkids;

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

    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra("state")){
            if (headsetConnected && intent.getIntExtra("state", 0) == 0){
                headsetConnected = false;
                Log.i(TAG, "headset NOT Connected => pause");
                MainActivity.audioPlayer.pause();
            } else if (!headsetConnected && intent.getIntExtra("state", 0) == 1){
                headsetConnected = true;
                Log.i(TAG, "headset IS Connected => resume");
                MainActivity.audioPlayer.resume();
            }
        }
    }
}
