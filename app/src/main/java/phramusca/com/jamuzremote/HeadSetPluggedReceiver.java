package phramusca.com.jamuzremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by raph on 17/06/17.
 */
public class HeadSetPluggedReceiver extends BroadcastReceiver {

    private static final String TAG = CallReceiver.class.getName();
    private boolean headsetConnected = false;

    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra("state")){
            if (headsetConnected && intent.getIntExtra("state", 0) == 0){
                headsetConnected = false;
                Log.i(TAG, "headset NOT Connected => pause");
                pause();
            } else if (!headsetConnected && intent.getIntExtra("state", 0) == 1){
                headsetConnected = true;
                Log.i(TAG, "headset IS Connected => resume");
                resume();
            }
        }
    }

    private void resume() {
        if(MainActivity.mediaPlayer!=null && !MainActivity.mediaPlayer.isPlaying()) {
            MainActivity.mediaPlayer.start();
            //FIXME: Start timer
        }
    }

    private void pause() {
        if(MainActivity.mediaPlayer!=null && MainActivity.mediaPlayer.isPlaying()) {
            MainActivity.mediaPlayer.pause();
            //FIXME: Stop timer
        }
    }
}
