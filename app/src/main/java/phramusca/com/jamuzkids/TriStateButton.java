package phramusca.com.jamuzkids;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by raph on 17/06/17.
 */
public class TriStateButton extends Button {

    //private final int MAX_STATES=3;
    int state;
    Context context;

    public enum STATE {
        ANY, TRUE, FALSE
    }

    public TriStateButton(Context context) {
        super(context);
        this.context=context;

    }

    public TriStateButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }

    @Override
    public boolean performClick() {
        nextState();
        super.performClick();
        return true;

    }

    private void nextState() {
        state++;

        if (state == STATE.values().length) {
            state = 0;
        }
    }

    public STATE getState() {

        switch (state) {
            case 0:
                return STATE.ANY;
            case 1:
                return STATE.TRUE;
            case 2:
                return STATE.FALSE;
            default:
                return STATE.ANY;
        }
    }

    public void setState(STATE state) {

        switch (state) {
            case ANY:
                this.state = 0;
                break;
            case TRUE:
                this.state = 1;
                break;
            case FALSE:
                this.state = 2;
                break;
            default:
                break;
        }
    }
}
