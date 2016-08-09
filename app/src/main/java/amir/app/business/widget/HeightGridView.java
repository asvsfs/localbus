package amir.app.business.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Created by amin on 21/07/2015.
 */
public class HeightGridView extends GridView {

    boolean expanded = false;

    public HeightGridView(Context context) {
        super(context);
    }

    public HeightGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeightGridView(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int heightSpec;

        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        } else {
            heightSpec = heightMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, heightSpec);
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
