package com.android.customer.music.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/10/31 10:04
 */
public class WidthEqualHeightImageView extends AppCompatImageView {
    public WidthEqualHeightImageView(Context context) {
        super(context);
    }

    public WidthEqualHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WidthEqualHeightImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
