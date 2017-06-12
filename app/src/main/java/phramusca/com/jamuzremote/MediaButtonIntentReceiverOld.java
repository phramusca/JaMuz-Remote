package phramusca.com.jamuzremote;

import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by raph on 12/06/17.
 */
public class MediaButtonIntentReceiverOld extends MediaButtonReceiver
{
    private static final String TAG = "JaMuz ButtonReceiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        KeyEvent keyEvent = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        String keyExtraEvent = KeyEvent.keyCodeToString(keyEvent.getKeyCode());

        int action = keyEvent.getAction();
        if (action == KeyEvent.ACTION_UP) {
            Log.i(TAG, intent.getAction()+" : "+keyExtraEvent);

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    //FIXME: H2 do this ??
                    //doAction("nextTrack");
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                case KeyEvent.KEYCODE_MEDIA_STOP:
                case KeyEvent.KEYCODE_HEADSETHOOK:  //Play/Pause on Wired HeadSet
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS: //Yes, also with Previous as 1. N/A 2. handy in car
                    playPause(); break;
            }
        }
    }

    private void playPause() {
        if(MainActivity.mediaPlayer ==null) {
            return;
        }
        else if(MainActivity.mediaPlayer.isPlaying()) {
            MainActivity.mediaPlayer.pause();
        } else {
            MainActivity.mediaPlayer.start();
        }
    }
}
