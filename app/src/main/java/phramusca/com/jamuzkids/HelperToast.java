package phramusca.com.jamuzkids;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by raph on 10/06/17.
 */

public class HelperToast {

    private static final String TAG = HelperToast.class.getName();
    private Context context;

    HelperToast(Context context) {
        this.context = context;
    }

    public void toastLong(final String msg) {
        toast(msg, Toast.LENGTH_LONG);
    }

    public void toastShort(final String msg) {
        toast(msg, Toast.LENGTH_SHORT);
    }

    private void toast(final String msg, int duration) {
        if(!msg.equals("")) {
            Log.i(TAG, "Toast makeText "+msg);
            Toast.makeText(context, msg, duration).show();
        }
    }
}