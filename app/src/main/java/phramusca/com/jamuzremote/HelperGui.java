package phramusca.com.jamuzremote;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.widget.AppCompatButton;

public class HelperGui {
    public static void setColor(Context context, int resId, View view) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(resId, typedValue, true);
        if (typedValue.resourceId != 0) {
            view.setBackgroundResource(typedValue.resourceId);
        } else {
            view.setBackgroundColor(typedValue.data);
        }
    }

    public static void setTextColor(Context context, int resId, TextView view) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(resId, typedValue, true);
        if (typedValue.resourceId != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.setTextColor(context.getResources().getColor(typedValue.resourceId, theme));
            }
        } else {
            view.setTextColor(typedValue.data);
        }
    }
}
