package phramusca.com.jamuzremote;

import android.content.Context;
import android.util.Log;

import java.util.Date;

/**
 * Created by raph on 17/06/17.
 */
public class ReceiverPhoneCall extends PhonecallReceiver {

    private static final String TAG = ReceiverPhoneCall.class.getName();

    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        Log.i(TAG, "onIncomingCallReceived => pause");
        MainActivity.audioPlayer.pause();
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start)
    {
        Log.i(TAG, "onIncomingCallAnswered => pause");
        MainActivity.audioPlayer.pause();
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end)
    {
        Log.i(TAG, "onIncomingCallEnded => resume");
        MainActivity.audioPlayer.resume();
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start)
    {
        Log.i(TAG, "onOutgoingCallStarted => pause");
        MainActivity.audioPlayer.pause();
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end)
    {
        Log.i(TAG, "onOutgoingCallEnded => resume");
        MainActivity.audioPlayer.resume();
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start)
    {
        Log.i(TAG, "onMissedCall => nothing");
    }

}
