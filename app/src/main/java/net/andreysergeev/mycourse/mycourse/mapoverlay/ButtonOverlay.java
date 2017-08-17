package net.andreysergeev.mycourse.mycourse.mapoverlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import net.andreysergeev.mycourse.mycourse.utils.Utils;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.Random;

/**
 * Created by andrejsergeev on 17.08.17.
 */

public class ButtonOverlay extends Overlay {

    private Context context;
    private IOnTouchEvent listener;

    private int width;
    private int height;
    private Drawable drawable;
    private Bitmap bitmap;
    private Paint paint;

    private float btnCenterX;
    private float btnCenterY;

    private float mScale;

    private float mButtonFrameCenterX;
    private float mButtonFrameCenterY;

    private Matrix matrix;

    private int id;

    public ButtonOverlay(Context context, @Nullable int id, @Nullable IOnTouchEvent listener) {

        super();

        this.context = context;
        this.listener = listener;
        this.id = getId(id);

        mScale = context.getResources().getDisplayMetrics().density;
        matrix = new Matrix();

        paint = new Paint();
    }

    public void setSize(int width, int height) {
        this.width = (int)(width * mScale);
        this.height = (int)(height * mScale);
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;

        this.bitmap = Utils.drawableToBitmap(drawable, width, height, Color.WHITE);

        mButtonFrameCenterX = this.bitmap.getWidth() / 2 - 0.5f;
        mButtonFrameCenterY = this.bitmap.getHeight() / 2 - 0.5f;
    }

    public void setCenter(float x, float y) {
        this.btnCenterX = x;
        this.btnCenterY = y;
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

            RectF rect = new RectF(0f,0f,width,height);

            matrix.mapRect(rect);

            if (rect.contains(event.getX(), event.getY())) {
                if (this.listener != null) {
                    this.listener.onTouch(this.id);
                    return true;
                }
            }

            return false;
        }

        return super.onTouchEvent(event, mapView);
    }

    private int getId(@Nullable Integer id) {
        if (id == null) {
            return (new Random()).nextInt();
        }

        return id;
    }

    public interface IOnTouchEvent {
        void onTouch(int id);
    }
}
