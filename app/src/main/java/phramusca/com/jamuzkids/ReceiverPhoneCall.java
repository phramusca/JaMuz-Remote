package phramusca.com.jamuzkids;

import android.content.Context;
import android.util.Log;

import java.util.Date;

/**
 * Created by raph on 17/06/17.
 * https://stackoverflow.com/questions/15563921/how-to-detect-incoming-calls-in-an-android-device
 */
public class ReceiverPhoneCall extends PhonecallReceiver {

    private static final String TAG = ReceiverPhoneCall.class.getName();
    private static boolean wasPlaying = false;

    private void pause() {
        if(ActivityMain.audioPlayer!=null) {
            wasPlaying = ActivityMain.audioPlayer.isPlaying();
            Log.i(TAG, "wasPlaying="+wasPlaying+"");
            ActivityMain.audioPlayer.pause();
        }
    }

    private void resume() {
        if(ActivityMain.audioPlayer!=null) {
            if (wasPlaying) {
                Log.i(TAG, "wasPlaying => audioPlayer.resume");
                ActivityMain.audioPlayer.resume();
                wasPlaying = false;
            } else {
                Log.i(TAG, "was NOT Playing => nothing");
            }
        }
    }

    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        Log.i(TAG, "onIncomingCallReceived => pause");
        pause();
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start)
    {
        //TODO: Pause in this case but only set volume low in onIncomingCallReceived
        Log.i(TAG, "onIncomingCallAnswered => nothing");
        //pause();
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end)
    {
        Log.i(TAG, "onIncomingCallEnded => resume");
        resume();
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start)
    {
        Log.i(TAG, "onOutgoingCallStarted => pause");
        pause();
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end)
    {
        Log.i(TAG, "onOutgoingCallEnded => resume");
        resume();
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start)
    {
        Log.i(TAG, "onMissedCall => resume");
        resume();
    }

}
