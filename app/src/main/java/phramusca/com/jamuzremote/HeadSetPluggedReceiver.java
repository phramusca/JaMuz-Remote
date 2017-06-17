package phramusca.com.jamuzremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by raph on 17/06/17.
 */
public class HeadSetPluggedReceiver extends BroadcastReceiver {

    private boolean headsetConnected = false;

    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra("state")){
            if (headsetConnected && intent.getIntExtra("state", 0) == 0){
                headsetConnected = false;
                pause();
            } else if (!headsetConnected && intent.getIntExtra("state", 0) == 1){
                headsetConnected = true;
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
