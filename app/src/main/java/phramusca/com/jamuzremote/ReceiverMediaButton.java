package phramusca.com.jamuzremote;

import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.MediaButtonReceiver;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by raph on 12/06/17.
 */
public class ReceiverMediaButton extends MediaButtonReceiver
{
    private static final String TAG = ReceiverMediaButton.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        String keyExtraEvent = KeyEvent.keyCodeToString(keyEvent.getKeyCode());
        int action = keyEvent.getAction();
        if (action == KeyEvent.ACTION_UP) {
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    Log.i(TAG, keyExtraEvent+" => playNext");
                    MainActivity.audioPlayer.playNext();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    //TODO: Allow this as an option as handy in car with no pause/play button
                    boolean usePreviousButtonAsPauseButton=false;
                    if(usePreviousButtonAsPauseButton) {
                        Log.i(TAG, keyExtraEvent+" => togglePlay");
                        MainActivity.audioPlayer.togglePlay();
                    } else {
                        Log.i(TAG, keyExtraEvent+" => playPrevious");
                        MainActivity.audioPlayer.playPrevious();
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    Log.i(TAG, keyExtraEvent+" => play");
                    MainActivity.audioPlayer.play();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    Log.i(TAG, keyExtraEvent+" => pause");
                    MainActivity.audioPlayer.pause();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                case KeyEvent.KEYCODE_HEADSETHOOK:
                    Log.i(TAG, keyExtraEvent+" => togglePlay");
                    MainActivity.audioPlayer.togglePlay();
                    break;
                default:
                    Log.i(TAG, keyExtraEvent+" => NOTHING :(");
                    break;
            }
        }
    }
}
