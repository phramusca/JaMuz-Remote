package com.kiddoware.kidsplace.sdk.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.kiddoware.kidsplace.sdk.R;


/**
 * Created by Shardul on 29/03/16.
 */
public class CircularBackImageView extends androidx.appcompat.widget.AppCompatImageView {

    public CircularBackImageView(Context context) {
        super(context);
    }

    public CircularBackImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularBackImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);

        if (drawable instanceof BitmapDrawable) {
            GradientDrawable ovalShapeDrawable = (GradientDrawable)
                    getContext().getResources().getDrawable(R.drawable.app_background);

            ovalShapeDrawable.setColor(0xFF5A83AC);

            setBackgroundDrawable(ovalShapeDrawable);
        }
    }
}
