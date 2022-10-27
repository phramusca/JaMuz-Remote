package phramusca.com.jamuzremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by raph on 12/06/17.
 */
public class ReceiverMediaButton extends BroadcastReceiver {
    private static final String TAG = ReceiverMediaButton.class.getName();
    private static final int DOUBLE_CLICK_DELAY = 0; //TODO: Make this an option (0 to desactivate, 1000 to activate)
    private static long sLastClickTime = 0;
    private static Timer timer;
    private static TimerTask timerTask;

    @Override
    public void onReceive(Context context, Intent intent) {
        final KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if(keyEvent==null) {
            return;
        }
        final int action = keyEvent.getAction();
        if (action == KeyEvent.ACTION_UP) {
            final String keyExtraEvent = KeyEvent.keyCodeToString(keyEvent.getKeyCode());
            long time = SystemClock.uptimeMillis();
            Log.i(TAG, "------- time           = " + time); //NON-NLS
            Log.i(TAG, "sLastClickTime         = " + sLastClickTime); //NON-NLS
            Log.i(TAG, "(time - sLastClickTime)= " + (time - sLastClickTime)); //NON-NLS
            Log.i(TAG, "                       ( " + DOUBLE_CLICK_DELAY + " )");
            if ((time - sLastClickTime) < DOUBLE_CLICK_DELAY) {
                Log.i(TAG, "DOUBLE Click"); //NON-NLS
                if (timer != null) {
                    timer.cancel();
                    Log.i(TAG, "Number of cancelled tasks purged: " + timer.purge()); //NON-NLS
                    timer = null;
                }
                if (timerTask != null) {
                    Log.i(TAG, "Tracking cancellation status: " + timerTask.cancel()); //NON-NLS
                    timerTask = null;
                }
                sendMessage("displaySpeechRecognizer");
            } else {
                Log.i(TAG, "First Click"); //NON-NLS
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        Log.i(TAG, "timer MediaButton performed"); //NON-NLS
                        switch (keyEvent.getKeyCode()) {
                            case KeyEvent.KEYCODE_MEDIA_NEXT:
                                Log.i(TAG, keyExtraEvent + " => playNext"); //NON-NLS
                                sendMessage("playNext");
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                                Log.i(TAG, keyExtraEvent + " => playPrevious"); //NON-NLS
                                sendMessage("playPrevious");
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PLAY:
                                Log.i(TAG, keyExtraEvent + " => play"); //NON-NLS //NON-NLS
                                sendMessage("play"); //NON-NLS
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            case KeyEvent.KEYCODE_MEDIA_STOP: //NON-NLS
                                Log.i(TAG, keyExtraEvent + " => pause"); //NON-NLS
                                sendMessage("pause"); //NON-NLS
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            case KeyEvent.KEYCODE_HEADSETHOOK: //NON-NLS
                                Log.i(TAG, keyExtraEvent + " => togglePlay"); //NON-NLS
                                sendMessage("togglePlay");
                                break;
                            default:
                                Log.i(TAG, keyExtraEvent + " => NOTHING :("); //NON-NLS
                                break;
                        }
                    }
                };
                timer.schedule(timerTask, DOUBLE_CLICK_DELAY);
            }
            sLastClickTime = time; //NON-NLS
            Log.i(TAG, "------- sLastClickTime = " + sLastClickTime); //NON-NLS
        }
    }

    private void sendMessage(String msg) {
        Message completeMessage =
                ActivityMain.mHandler.obtainMessage(1, msg);
        completeMessage.sendToTarget();
    }
}
