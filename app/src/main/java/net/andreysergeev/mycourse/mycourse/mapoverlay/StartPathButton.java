package net.andreysergeev.mycourse.mycourse.mapoverlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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

public class StartPathButton extends Overlay {

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

    public StartPathButton(Context context, @Nullable IOnTouchListener listener) {
        this.context = context;
        this.paint = new Paint();
        this.paint.setColor(Color.BLUE);

        Drawable drawable =  this.context.getDrawable(R.drawable.ic_directions_walk_black_24dp);

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
    public void draw(Canvas c, MapView osmv, boolean shadow) {

        if (shadow || !isEnabled()) {
            return;
        }

        final float centerX = btnCenterX * mScale;
        final float centerY = btnCenterY * mScale;

        matrix.setTranslate(-mButtonFrameCenterX, -mButtonFrameCenterY);
        matrix.postTranslate(centerX, centerY);

        c.save();
        c.concat(osmv.getProjection().getInvertedScaleRotateCanvasMatrix());
        c.concat(matrix);
        c.drawBitmap(bitmap, 0, 0, paint);
        c.restore();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {

        if (!isEnabled()) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            RectF rect = new RectF(0f,0f,120f,120f);

            matrix.mapRect(rect);

            if (rect.contains(event.getX(), event.getY())) {
                if (this.listener != null) {
                    this.listener.onTouchStartBtn();
                }
            }

            return false;
        }

        return super.onTouchEvent(event, mapView);
    }

    @Override
    public void onDetach(MapView mapView) {

        this.bitmap.recycle();

        super.onDetach(mapView);
    }

    public interface IOnTouchListener {
        void onTouchStartBtn();
    }
}
