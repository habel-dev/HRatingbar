/*
 * Copyright (c) 2017. Instio Experiences Pvt. Ltd  - All Rights Reserved
 *   Unauthorized copying of this file, via any medium is strictly prohibited
 *   Proprietary and confidential
 *   Written/Updated  by Habel Philip <habel@instio.co> on 2/21/17 11:02 AM
 */

package in.habel.iratingbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by habel on 22-Jan-17.
 */

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class IRatingBarLayout extends IRatingBar implements View.OnTouchListener {
    private final ViewGroup bar;
    private HashSet<IRatingListener> listeners = new HashSet<>();
    private boolean isUpdating;

    public IRatingBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // if (isInEditMode()) return;
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.IRatingBarLayout, 0, 0);
        imageHeight = a.getDimensionPixelSize(R.styleable.IRatingBarLayout_barHeight, 0);
        imageMinHeight = a.getDimensionPixelSize(R.styleable.IRatingBarLayout_barMinHeight, imageHeight);
        imageMaxHeight = a.getDimensionPixelSize(R.styleable.IRatingBarLayout_barMaxHeight, imageHeight);
        readOnly = a.getBoolean(R.styleable.IRatingBarLayout_readOnly, false);
        if (imageHeight > imageMaxHeight) imageMaxHeight = imageHeight;
        imageWidth = a.getDimensionPixelSize(R.styleable.IRatingBarLayout_barWidth, 0);
        stepSize = a.getFloat(R.styleable.IRatingBarLayout_stepSize, 1f);
        if (stepSize <= 0 || stepSize >= 1) stepSize = 1;
        ratingMax = a.getInt(R.styleable.IRatingBarLayout_ratingMax, 10);
        if (ratingMax > 10 || ratingMax <= 0) ratingMax = 10;
        rating = a.getFloat(R.styleable.IRatingBarLayout_rating, 0);
        if (rating < 0 || rating > ratingMax) rating = 0;
        Log.d(TAG, String.format("ratingMax:%1$2s   rating:%2$s   imageWidth:%3$s  imageHeight:%4$s   imageMinHeight:%5$s   imageMaxHeight:%6$s", ratingMax, rating, imageWidth, imageHeight, imageMinHeight, imageMaxHeight));
        Log.d(TAG, String.format("dpToPixels(40):%1$s", dpToPixels(40)));
        if (ratingMax * imageMinHeight > getDisplayWidth(context) - dpToPixels(40)) {
            ratingMax /= 2;
            stepSize /= 2;
            rating /= 2;
            if (stepSize < .5) stepSize = .5f;
            Log.d(TAG, String.format("ratingMax:%1$2s   rating:%2$s   imageWidth:%3$s  imageHeight:%4$s   imageMinHeight:%5$s   imageMaxHeight:%6$s", ratingMax, rating, imageWidth, imageHeight, imageMinHeight, imageMaxHeight));
        }
        prevRating = rating;
        compressOnDemand = a.getBoolean(R.styleable.IRatingBarLayout_compressOnDemand, true);

        barPadding = a.getDimensionPixelSize(R.styleable.IRatingBarLayout_padding, 0);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ir_layout, this, true);
        bar = (ViewGroup) getChildAt(0);
        if (images.size() == 0)
            if (ratingMax <= 5) addDefaultHalfResources();
            else addDefaultSmileyResources();

        for (int i = 0; i < bar.getChildCount(); i++) {
            processImages((ImageView) bar.getChildAt(i), i);
        }
        if (!readOnly)
            bar.setOnTouchListener(this);

    }

    public IRatingBarLayout(Context context) {
        this(context, null);
    }
/*

    @BindingAdapter(value = {"app:rating", "app:ratingMax"}, requireAll = false)
    public static void setFilterBinding(IRatingBarLayout positionView, float rating,
                                        int ratingMax) {
        positionView.rating = rating;
        positionView.ratingMax = ratingMax;

    }
*/

    public void addOnRatingChangeListener(IRatingListener listener) {
        if (listeners == null) listeners = new HashSet<>();
        listeners.add(listener);
    }

    public void removeOnRatingChangeListener(IRatingListener listener) {
        listeners.remove(listener);
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
        update();
    }

    public float getRatingMax() {
        return ratingMax;
    }

    public void setRatingMax(float ratingMax) {
        this.ratingMax = (int) ratingMax;
        update();
    }

    private IRatingBarLayout update() {
        for (int i = 0; i < bar.getChildCount(); i++) {
            try {
                processImages((ImageView) bar.getChildAt(i), i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            for (IRatingListener iRatingListener : listeners) {
                iRatingListener.onRatingChanged(this, rating, ratingMax);
            }
        } catch (Exception ignored) {
        }
        return this;
    }

    private void processImages(ImageView image, int position) {
        if (image == null) return;
        if (position < ratingMax) {
            if (barPadding > 0) image.setPadding(barPadding, barPadding, barPadding, barPadding);
            if (imageHeight > 0) {
                image.getLayoutParams().height = imageHeight;
                image.getLayoutParams().width = imageWidth;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                    if (!isInLayout()) image.requestLayout();
            }
            int size = images.size();
            int index = size > position ? position : (position) % size;

            float threshold = getSinglePrecisionFloat((float) (rating - Math.floor(rating)));
            //  Log.v(TAG, "threshold  => " + threshold);
            if ((int) rating != index || threshold == 0) {
                int resource;
                if (position + 1 > rating) resource = images.get(index).getRatingEmpty();
                else resource = images.get(index).getRatingFull();

                image.setImageResource(resource);
            } else {
                Bitmap bmp = generateDrawable(threshold, images.get(index).getRatingEmpty(), images.get(index).getRatingFull());
                image.setImageBitmap(bmp);
            }
            //  image.setOnClickListener(this);
        } else {
            image.setVisibility(GONE);
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measuredWidth = bar.getMeasuredWidth();
        //if (measuredWidth > dpToPixels(400)) measuredWidth = (int) dpToPixels(400);
        if (compressOnDemand && measuredWidth < (imageWidth * ratingMax)) {
            int widthNeeded = imageWidth * ratingMax;
            if (widthNeeded > measuredWidth * 2) {
                ratingMax /= 2;
                stepSize /= 2;
                addDefaultHalfResources();
            }
            imageWidth = (measuredWidth / ratingMax) - 1;
            if (imageWidth > imageMaxHeight) imageWidth = imageMaxHeight;
            if (imageWidth < imageMinHeight) imageWidth = imageMinHeight;
            widthNeeded = imageWidth * ratingMax;
            if (widthNeeded > measuredWidth) {
                ratingMax /= 2;
                stepSize /= 2;
                addDefaultHalfResources();
            }
            if (stepSize < .5) stepSize = .5f;
            setMeasuredDimension(widthMeasureSpec, imageWidth);
            update();
        }
    }

    public IRatingBarLayout setResource(List<RatingResource> resource) {
        images = resource;
        return this;
    }

    public IRatingBarLayout addResource(RatingResource resource) {
        if (images == null) images = new ArrayList<>();
        images.add(resource);
        return this;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isUpdating) {
            Log.v(TAG, "onTouch running... return;");
            return super.onTouchEvent(event);
        }
        isUpdating = true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_SCROLL:
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_HOVER_MOVE:
            case MotionEvent.ACTION_UP:
                if (v.getId() == bar.getId()) {
                    float x = event.getX();
                    if (x < imageWidth / 4) rating = 0;
                    else {
                        rating = x / imageWidth;
                    }
                }
                rating = getSinglePrecisionFloat(rating);
                if (rating > ratingMax) rating = ratingMax;
                if (rating != prevRating) {
                    Log.v(TAG, "rating : " + rating);
                    prevRating = rating;
                    update();
                }
                isUpdating = false;
                return true;
            default:
                isUpdating = false;
                return super.onTouchEvent(event);
        }

    }


}
