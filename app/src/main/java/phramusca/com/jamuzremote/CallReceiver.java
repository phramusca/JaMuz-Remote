package phramusca.com.jamuzremote;

import android.content.Context;

import java.util.Date;

/**
 * Created by raph on 17/06/17.
 */
public class CallReceiver extends PhonecallReceiver {

    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {

    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start)
    {
        pause();
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end)
    {
        resume();
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start)
    {
        pause();
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end)
    {
        resume();
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start)
    {
        //
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
