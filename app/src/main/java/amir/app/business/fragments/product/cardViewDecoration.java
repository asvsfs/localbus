package amir.app.business.fragments.product;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Amir on 5/20/2017.
 */

public class cardViewDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;

    public cardViewDecoration(int space) {
        mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets( outRect,  view,  parent, state) ;

        outRect.left = 0;
        outRect.right = 0;
        outRect.bottom = 0;
        outRect.top = 0 ;
        // Add top margin only for the first item to avoid double space between items
//        if (parent.getChildLayoutPosition(view) == 0) {
//            outRect.top = mSpace;
//        } else {
//            outRect.top = 0;
//        }
    }
}