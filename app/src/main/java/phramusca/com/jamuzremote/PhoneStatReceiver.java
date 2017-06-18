package phramusca.com.jamuzremote;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by raph on 18/06/17.
 */
public class PhoneStatReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneStatReceiver";
    private static boolean incomingFlag = false;
    private static String incoming_number = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        TelephonyManager tm =
                (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);

        switch (tm.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING:
                incomingFlag = true;
                incoming_number = intent.getStringExtra("incoming_number");
                Log.i(TAG, "RINGING :"+ incoming_number);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if(incomingFlag){
                    Log.i(TAG, "incoming ACCEPT :"+ incoming_number);
                }
                break;

            case TelephonyManager.CALL_STATE_IDLE:
                if(incomingFlag){
                    Log.i(TAG, "incoming IDLE");
                }
                break;
        }
    }
}
