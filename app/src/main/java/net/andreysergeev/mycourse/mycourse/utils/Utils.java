package net.andreysergeev.mycourse.mycourse.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by andrejsergeev on 17.08.17.
 */

final public class Utils {
    public static Bitmap drawableToBitmap (Drawable drawable, int width, int height, int color) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawColor(color, PorterDuff.Mode.MULTIPLY);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return scaledBitmap;
    }

}
