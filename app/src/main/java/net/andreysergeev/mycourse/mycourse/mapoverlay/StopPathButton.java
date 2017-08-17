package net.andreysergeev.mycourse.mycourse.mapoverlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import net.andreysergeev.mycourse.mycourse.R;
import net.andreysergeev.mycourse.mycourse.utils.Utils;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

/**
 * Created by andrejsergeev on 17.08.17.
 */

public class StopPathButton extends Overlay {

    private Context context;
    private Paint paint;
    private Bitmap bitmap = null;
    private Matrix matrix = null;

    private float btnCenterX = 35f;
    private float btnCenterY = 35f;

    private final float mButtonFrameCenterX;
    private final float mButtonFrameCenterY;

    private float mScale;

    private IOnTouchListener listener = null;

    public StopPathButton(Context context, @Nullable IOnTouchListener listener) {
        this.context = context;

        this.context = context;
        this.paint = new Paint();
        this.paint.setColor(Color.BLUE);

        Drawable drawable =  this.context.getDrawable(R.drawable.ic_transfer_within_a_station_black_24dp);

        this.bitmap = Utils.drawableToBitmap(drawable, 120, 120, Color.WHITE);

        mButtonFrameCenterX = bitmap.getWidth() / 2 - 0.5f;
        mButtonFrameCenterY = bitmap.getHeight() / 2 - 0.5f;

        mScale = context.getResources().getDisplayMetrics().density;
        matrix = new Matrix();

        this.listener = listener;
    }

    public void setCenter(float x, float y) {
        btnCenterX = x;
        btnCenterY = y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {

        /*if (!isEnabled()) {
            return true;
        }

        if (this.listener != null) {
            this.listener.onTouchStopBtn();
            return false;
        }*/

        return super.onTouchEvent(event, mapView);
    }

    @Override
    public void draw(Canvas c, MapView osmv, boolean shadow) {

        if (shadow || !isEnabled()) {
            return;
        }

        final float centerX = btnCenterX * mScale;
        final float centerY = btnCenterY * mScale;

        matrix.setTranslate(-mButtonFrameCenterX, mButtonFrameCenterY);
        matrix.postTranslate(centerX, centerY);

        c.save();
        c.concat(osmv.getProjection().getInvertedScaleRotateCanvasMatrix());
        c.concat(matrix);
        c.drawBitmap(bitmap, 0, 0, paint);
        c.restore();

    }

    public interface IOnTouchListener {
        void onTouchStopBtn();
    }
}
