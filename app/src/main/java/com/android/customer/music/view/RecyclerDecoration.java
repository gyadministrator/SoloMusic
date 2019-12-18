package com.android.customer.music.view;

import android.graphics.Rect;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/10/31 10:13
 */
public class RecyclerDecoration extends RecyclerView.ItemDecoration {
    private int mSpace;

    public RecyclerDecoration(int mSpace) {
        this.mSpace = mSpace;
    }


    /**
     * @param outRect item的矩形边界
     * @param view    itemView
     * @param parent  recyclerView
     * @param state   recyclerView
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = mSpace;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) parent.getLayoutParams();
        layoutParams.leftMargin = -mSpace;
        parent.setLayoutParams(layoutParams);
    }
}
