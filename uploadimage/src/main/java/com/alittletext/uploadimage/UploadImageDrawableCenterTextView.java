package com.alittletext.uploadimage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * 解决TextView添加Drawable图片无法和Text一起居中显示
 */
public class UploadImageDrawableCenterTextView extends android.support.v7.widget.AppCompatTextView {

    public UploadImageDrawableCenterTextView(Context context, AttributeSet attrs,
                                             int defStyle) {
        super(context, attrs, defStyle);
    }

    public UploadImageDrawableCenterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UploadImageDrawableCenterTextView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        Drawable drawableLeft = drawables[0];
        if (drawableLeft != null) {
            float textWidth = getPaint().measureText(getText().toString());
            int drawablePadding = getCompoundDrawablePadding();
            int drawableWidth = drawableLeft.getIntrinsicWidth();
            float bodyWidth = textWidth + drawableWidth + drawablePadding;
            canvas.translate((getWidth() - bodyWidth) / 2, 0);
        }

        Drawable drawableTop = drawables[1];
        if (drawableTop != null) {
            int height = getHeight();
            float textSize = getTextSize();
            int drawablePadding = getCompoundDrawablePadding();
            int drawableHeight = drawableTop.getIntrinsicHeight();
            float allViewHeight = textSize + drawablePadding + drawableHeight;
            float translateY = (height - allViewHeight) / 2;
            canvas.translate(0, translateY);
        }

        Drawable drawableRight = drawables[2];
        if (drawableRight != null) {
            float textWidth = getPaint().measureText(getText().toString());
            int drawablePadding = getCompoundDrawablePadding();
            int drawableWidth = drawableRight.getIntrinsicWidth();
            float bodyWidth = textWidth + drawableWidth + drawablePadding;
            setPadding(0, 0, (int) (getWidth() - bodyWidth), 0);
            canvas.translate((getWidth() - bodyWidth) / 2, 0);
        }
        super.onDraw(canvas);
    }


}