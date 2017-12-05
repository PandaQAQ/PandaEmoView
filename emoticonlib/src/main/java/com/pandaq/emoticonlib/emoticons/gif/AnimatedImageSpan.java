package com.pandaq.emoticonlib.emoticons.gif;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;


public class AnimatedImageSpan extends DynamicDrawableSpan {
    private AnimatedGifDrawable mGifDrawable;

    public AnimatedImageSpan(AnimatedGifDrawable gifDrawable, GifRunnable runnable) {
        super();
        mGifDrawable = gifDrawable;
        if (!runnable.isRunning()) {
            runnable.resumeHandler(gifDrawable.getContainerTag());
        }
    }

    /*
     * Return current frame from animated drawable. Also acts as replacement for super.getCachedDrawable(),
     * since we can't cache the 'image' of an animated image.
     */

    @Override
    public Drawable getDrawable() {
        return mGifDrawable.getDrawable();
    }

    /*
         * Copy-paste of super.getSize(...) but use getDrawable() to get the image/frame to calculate the size,
         * in stead of the cached drawable.
         */
    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        Drawable d = getDrawable();
        if (d == null) {
            return 0;
        }
        Rect rect = d.getBounds();
        if (fm != null) {
            fm.ascent = -rect.bottom;
            fm.descent = 0;

            fm.top = fm.ascent;
            fm.bottom = 0;
        }

        return rect.right;
    }

    /*
     * Copy-paste of super.draw(...) but use getDrawable() to get the image/frame to draw, in stead of
     * the cached drawable.
     */
    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        Drawable b = getDrawable();
        canvas.save();

        int transY = bottom - b.getBounds().bottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= paint.getFontMetricsInt().descent;
        }
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }

}
