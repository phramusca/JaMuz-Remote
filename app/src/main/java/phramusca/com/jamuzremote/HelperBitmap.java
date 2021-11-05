package phramusca.com.jamuzremote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by raph on 04/03/18.
 */

class HelperBitmap {

    private static Bitmap emptyThumb;

    public static Bitmap getEmptyThumb() {
        if(emptyThumb==null) {
            emptyThumb = textAsBitmap("No cover", 120, 25, 10, 70); //FIXME NOW Translate
        }
        return emptyThumb;
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

    public static Bitmap overlayIcon(Bitmap bitmap, int iconId, Context context) {
        int margin = 15;
        Bitmap bmOverlay = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bitmap, new Matrix(), null); //TODO: Paint in black if cover is too much white
        Bitmap playingBitmap = BitmapFactory.decodeResource(context.getResources(), iconId);
        int newWidth = bmOverlay.getWidth() - (margin * 2);
        int newHeight = bmOverlay.getHeight() - (margin * 2);
        int width = playingBitmap.getWidth();
        int height = playingBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        matrix.postTranslate(margin, margin);
        canvas.drawBitmap(playingBitmap, matrix, null);
        return bmOverlay;
    }
}
