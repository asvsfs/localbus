package amir.app.business.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImageViewHeight_Based extends ImageView {
    public boolean isInEditMode() {
        return true;
    }

    public SquareImageViewHeight_Based(Context context) {
        super(context);
    }

    public SquareImageViewHeight_Based(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageViewHeight_Based(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getMeasuredHeight();
        setMeasuredDimension(height, height);
    }

}