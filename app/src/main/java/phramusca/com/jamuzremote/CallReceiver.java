package phramusca.com.jamuzremote;

import android.content.Context;
import android.util.Log;

import java.util.Date;

/**
 * Created by raph on 17/06/17.
 */
public class CallReceiver extends PhonecallReceiver {

    private static final String TAG = CallReceiver.class.getName();

    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        Log.i(TAG, "onIncomingCallReceived => pause");
        pause();
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start)
    {
        Log.i(TAG, "onIncomingCallAnswered => pause");
        pause();
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
        Log.i(TAG, "onMissedCall => nothing");
    }

    private void playPause() {
        if(MainActivity.mediaPlayer ==null) {
            return;
        }
        else if(MainActivity.mediaPlayer.isPlaying()) {
            MainActivity.mediaPlayer.pause();
            //FIXME: Stop timer
        } else {
            MainActivity.mediaPlayer.start();
            //FIXME: Start timer
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
