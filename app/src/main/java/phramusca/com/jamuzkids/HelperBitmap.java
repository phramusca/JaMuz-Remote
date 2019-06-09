package phramusca.com.jamuzkids;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by raph on 04/03/18.
 */

class HelperBitmap {

    public static Bitmap getEmptyCover() {
        return textAsBitmap("No cover", 500, 35, 180, 250);
    }

    public static Bitmap getEmptyThumb() {
        return textAsBitmap("No cover", 120, 25, 10, 70);
    }

    private static Bitmap textAsBitmap(String text, int size, float textSize, int posX, int posY) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(Color.rgb(192, 192, 192));
        Bitmap image = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(Color.rgb(64, 64, 64));
        canvas.drawText(text, posX, posY, paint);
        return image;
    }
}
