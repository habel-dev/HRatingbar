package in.habel.iratingbar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by habel on 22-Jan-17.
 */

@SuppressWarnings("UnusedReturnValue")
abstract class IRatingBar extends RelativeLayout {
    static final String TAG = "IRatingBar";
    float rating;
    float prevRating;
    float stepSize = 1.0f;
    int imageHeight;
    int imageWidth;
    int imageMaxHeight;
    int imageMinHeight;
    int barPadding;
    int measuredWidth;
    boolean compressOnDemand;
    boolean readOnly;
    int ratingMax;
    List<RatingResource> images = new ArrayList<>();
    private int displayWidth = -1;

    public IRatingBar(Context context) {
        super(context);
    }

    public IRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public IRatingBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private IRatingBar addResource(RatingResource resource) {
        images.add(resource);
        return this;
    }

    float dpToPixels(int pixels) {
        Resources r = getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, r.getDisplayMetrics());
    }

    int getDisplayWidth(Context context) {
        if (displayWidth != -1) return displayWidth;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = Math.min(size.x, size.y);
        Log.d(TAG, String.format("getDisplayWidth() width:%1$s   height:%2$s", size.x, size.y));
        return displayWidth;
    }

    Bitmap generateDrawable(float threshold, int empty, int full) {

        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), empty);
        Bitmap mBitmapFull = BitmapFactory.decodeResource(getResources(), full);
        mBitmap = Bitmap.createBitmap(mBitmap);
        mBitmapFull = Bitmap.createBitmap(mBitmapFull, 0, 0, (int) (mBitmap.getWidth() * threshold), mBitmap.getHeight());

        Bitmap mutableBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawBitmap(mBitmap, new Matrix(), null);
        canvas.drawBitmap(mBitmapFull, 0, 0, null);
        mBitmap.recycle();
        mBitmapFull.recycle();
        return mutableBitmap;
    }

    float getSinglePrecisionFloat(float value) {
        float result = 0;
        if (stepSize == 1) result = (float) Math.ceil(value);
        else {
            float decimal = value % 1;
            for (float i = 0; i <= 1; i += stepSize) {
                // Log.v(TAG, "getSinglePrecisionFloat i: " + i + "  decimal:" + decimal);
                if (decimal <= i && decimal + stepSize > i) result = (value - decimal) + i;
            }
        }
        //Log.v(TAG, String.format("getSinglePrecisionFloat  %1$f  =>  %2$f  stepSize:%3$f ", value, result, stepSize));
        return result;
    }

    void addDefaultSmileyResources() {
        images = new ArrayList<>();
        addResource(new RatingResource(R.drawable.ir1, R.drawable.ir1c));
        addResource(new RatingResource(R.drawable.ir2, R.drawable.ir2c));
        addResource(new RatingResource(R.drawable.ir3, R.drawable.ir3c));
        addResource(new RatingResource(R.drawable.ir4, R.drawable.ir4c));
        addResource(new RatingResource(R.drawable.ir5, R.drawable.ir5c));
        addResource(new RatingResource(R.drawable.ir6, R.drawable.ir6c));
        addResource(new RatingResource(R.drawable.ir7, R.drawable.ir7c));
        addResource(new RatingResource(R.drawable.ir8, R.drawable.ir8c));
        addResource(new RatingResource(R.drawable.ir9, R.drawable.ir9c));
        addResource(new RatingResource(R.drawable.ir10, R.drawable.ir10c));
    }

    void addDefaultHalfResources() {
        images = new ArrayList<>();
        addResource(new RatingResource(R.drawable.ir2, R.drawable.ir2c));
        addResource(new RatingResource(R.drawable.ir4, R.drawable.ir4c));
        addResource(new RatingResource(R.drawable.ir6, R.drawable.ir6c));
        addResource(new RatingResource(R.drawable.ir8, R.drawable.ir8c));
        addResource(new RatingResource(R.drawable.ir10, R.drawable.ir10c));
    }

    void addDefault3Resources() {
        images = new ArrayList<>();
        addResource(new RatingResource(R.drawable.ir2, R.drawable.ir2c));
        addResource(new RatingResource(R.drawable.ir6, R.drawable.ir6c));
        addResource(new RatingResource(R.drawable.ir10, R.drawable.ir10c));
    }

    void addDefaultResources() {
        images = new ArrayList<>();
    }
}
