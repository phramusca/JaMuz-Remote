package phramusca.com.jamuzkids;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.media.session.MediaButtonReceiver;
import android.util.Log;
import android.view.KeyEvent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by raph on 12/06/17.
 */
public class ReceiverMediaButton extends MediaButtonReceiver
{
    private static final String TAG = ReceiverMediaButton.class.getName();
    private static final int DOUBLE_CLICK_DELAY = 0; //TODO: Make this an option (0 to desactivate, 1000 to activate)
    private static long sLastClickTime = 0;
    private static Timer timer;
    private static TimerTask timerTask;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        final KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        final int action = keyEvent.getAction();
        if (action == KeyEvent.ACTION_UP) {
            final String keyExtraEvent = KeyEvent.keyCodeToString(keyEvent.getKeyCode());
            long time = SystemClock.uptimeMillis();
            Log.i(TAG, "------- time           = "+time);
            Log.i(TAG, "sLastClickTime         = "+sLastClickTime);
            Log.i(TAG, "(time - sLastClickTime)= "+(time - sLastClickTime));
            Log.i(TAG, "                       ( "+ DOUBLE_CLICK_DELAY+" )");
            if ((time - sLastClickTime) < DOUBLE_CLICK_DELAY) {
                Log.i(TAG, "DOUBLE Click");
                if(timer != null)
                {
                    timer.cancel();
                    Log.i(TAG,"Number of cancelled tasks purged: " + timer.purge());
                    timer = null;
                }
                if(timerTask != null)
                {
                    Log.i(TAG,"Tracking cancellation status: " + timerTask.cancel());
                    timerTask = null;
                }
                MainActivity.audioPlayer.speech();
            } else {
                Log.i(TAG, "First Click");
                timer = new Timer();
                timerTask = new TimerTask(){
                    @Override
                    public void run() {
                        Log.i(TAG, "timer MediaButton performed");
                        switch (keyEvent.getKeyCode()) {
                            case KeyEvent.KEYCODE_MEDIA_NEXT:
                                Log.i(TAG, keyExtraEvent+" => playNext");
                                sendMessage("playNext");
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                                Log.i(TAG, keyExtraEvent+" => playPrevious");
                                sendMessage("playPrevious");
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PLAY:
                                Log.i(TAG, keyExtraEvent+" => play");
                                sendMessage("play");
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            case KeyEvent.KEYCODE_MEDIA_STOP:
                                Log.i(TAG, keyExtraEvent+" => pause");
                                sendMessage("pause");
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            case KeyEvent.KEYCODE_HEADSETHOOK:
                                Log.i(TAG, keyExtraEvent+" => togglePlay");
                                sendMessage("togglePlay");
                                break;
                            default:
                                Log.i(TAG, keyExtraEvent+" => NOTHING :(");
                                break;
                        }
                    }
                };
                timer.schedule(timerTask, DOUBLE_CLICK_DELAY);
            }
            sLastClickTime = time;
            Log.i(TAG, "------- sLastClickTime = "+sLastClickTime);
        }
    }

    private void sendMessage(String msg) {
        Message completeMessage =
                MainActivity.mHandler.obtainMessage(1, msg);
        completeMessage.sendToTarget();
    }
}
