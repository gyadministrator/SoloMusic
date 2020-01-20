package com.android.customer.music.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.customer.music.R;
import com.android.customer.music.activity.DrawerActivity;
import com.android.customer.music.activity.MainActivity;
import com.bumptech.glide.Glide;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/10/30 15:58
 */
public class TitleView extends LinearLayout {
    private boolean showBack;
    private boolean showRight;
    private String title;
    private int srcBack;
    private int srcRight;
    private TextView tvTitle;
    private LinearLayout ll;
    private int bgColor;
    private Context mContext;
    private ImageView iv_back;
    private ImageView iv_right;

    public TitleView(Context context) {
        this(context, null);
    }

    public TitleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleView);
        title = typedArray.getString(R.styleable.TitleView_title);
        showBack = typedArray.getBoolean(R.styleable.TitleView_showBack, true);
        showRight = typedArray.getBoolean(R.styleable.TitleView_showRight, true);
        srcBack = typedArray.getResourceId(R.styleable.TitleView_srcBack, 0);
        srcRight = typedArray.getResourceId(R.styleable.TitleView_srcRight, 0);
        bgColor = typedArray.getResourceId(R.styleable.TitleView_bgColor, 0);
        typedArray.recycle();
        init(context);
    }

    public void setSrcBack(String url) {
        Glide.with(mContext).load(url).into(iv_back);
    }

    public void setSrcRight(String url) {
        Glide.with(mContext).load(url).into(iv_right);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    private void init(final Context context) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.title_view, this);
        iv_back = view.findViewById(R.id.iv_back);
        iv_right = findViewById(R.id.iv_right);
        ll = findViewById(R.id.ll);
        tvTitle = findViewById(R.id.tv_title);

        if (bgColor != 0) {
            ll.setBackgroundColor(bgColor);
        }
        if (!"".equals(title)) {
            tvTitle.setText(title);
        }

        if (!showBack) {
            iv_back.setVisibility(GONE);
        }
        if (!showRight) {
            iv_right.setVisibility(GONE);
        }

        if (showBack) {
            if (srcBack != 0) {
                iv_back.setImageResource(srcBack);
            }
            iv_back.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof MainActivity) {
                        if (rightClickListener != null) {
                            rightClickListener.clickLeft(view);
                            return;
                        }
                    } else if (context instanceof DrawerActivity) {
                        if (rightClickListener != null) {
                            rightClickListener.clickLeft(view);
                            return;
                        }
                    }
                    ((Activity) context).onBackPressed();
                }
            });
        }

        if (showRight) {
            if (srcRight != 0) {
                iv_right.setImageResource(srcRight);
            }
            iv_right.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rightClickListener != null) {
                        rightClickListener.clickRight(view);
                    }
                }
            });
        }
    }

    private OnRightClickListener rightClickListener;

    public void setRightClickListener(OnRightClickListener rightClickListener) {
        this.rightClickListener = rightClickListener;
    }

    public interface OnRightClickListener {
        void clickRight(View view);

        void clickLeft(View view);
    }
}
