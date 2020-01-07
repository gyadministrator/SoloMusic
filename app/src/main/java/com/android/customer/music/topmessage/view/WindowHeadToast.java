package com.android.customer.music.topmessage.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.android.customer.music.R;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.tencent.imsdk.TIMMessage;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 用于自定义一个Toast，模仿类似于QQ和微信来消息时候在顶部弹出的消息提示框
 */
public class WindowHeadToast implements View.OnTouchListener {
    private Context mContext;
    private LinearLayout linearLayout;
    private final static int ANIM_DURATION = 600;
    private final static int ANIM_DISMISS_DURATION = 4000;
    private final static int ANIM_CLOSE = 20;
    @SuppressLint("HandlerLeak")
    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == ANIM_CLOSE) {
                animDismiss();
            }
        }
    };
    private WindowManager wm;
    private int downX;
    private int downY;

    public WindowHeadToast(Context context) {
        mContext = context;
    }

    public void showCustomToast(EMMessage emMessage, String header) {
        initHeadToastView(emMessage, header);
        setHeadToastViewAnim();
        // 延迟4s后关闭
        mHandler.sendEmptyMessageDelayed(ANIM_CLOSE, ANIM_DISMISS_DURATION);
    }

    public void showCustomToast(TIMMessage timMessage, String header) {
        initHeadToastView(timMessage, header);
        setHeadToastViewAnim();
        // 延迟4s后关闭
        mHandler.sendEmptyMessageDelayed(ANIM_CLOSE, ANIM_DISMISS_DURATION);
    }

    private void setHeadToastViewAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(linearLayout, "translationY", -700, 0);
        animator.setDuration(ANIM_DURATION);
        animator.start();
    }

    private void animDismiss() {
        if (linearLayout == null || linearLayout.getParent() == null) {
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(linearLayout, "translationY", 0, -700);
        animator.setDuration(ANIM_DURATION);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
            }

            @Override
            public void onAnimationResume(Animator animation) {
                super.onAnimationResume(animation);
            }
        });
    }

    /**
     * 移除HeaderToast  (一定要在动画结束的时候移除,不然下次进来的时候由于wm里边已经有控件了，所以会导致卡死)
     */
    private void dismiss() {
        if (null != linearLayout && null != linearLayout.getParent()) {
            wm.removeView(linearLayout);
        }
    }

    private void initHeadToastView(TIMMessage timMessage, String header) {
        //准备Window要添加的View
        linearLayout = new LinearLayout(mContext);
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        View headToastView = View.inflate(mContext, R.layout.header_toast, null);

        CircleImageView head_image = headToastView.findViewById(R.id.header_toast_smallimg);
        if (!TextUtils.isEmpty(header)) {
            Glide.with(mContext).load(header).into(head_image);
        }
        TextView header_toast_title = headToastView.findViewById(R.id.header_toast_title);
        header_toast_title.setText(timMessage.getConversation().getPeer());
        TextView header_toast_name = headToastView.findViewById(R.id.header_toast_name);
        header_toast_name.setText(timMessage.getConversation().getLastMsg().getCustomStr());


        // 为headToastView设置Touch事件
        headToastView.setOnTouchListener(this);
        linearLayout.addView(headToastView);
        // 定义WindowManager 并且将View添加到WindowManager中去
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wm_params = new WindowManager.LayoutParams();
        wm_params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        //wm_params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wm_params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wm_params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        }
        wm_params.x = 0;
        wm_params.y = 100;
        wm_params.format = -3;  // 会影响Toast中的布局消失的时候父控件和子控件消失的时机不一致，比如设置为-1之后就会不同步
        wm_params.alpha = 1f;
        wm.addView(linearLayout, wm_params);
    }

    private void initHeadToastView(EMMessage emMessage, String header) {
        //准备Window要添加的View
        linearLayout = new LinearLayout(mContext);
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        View headToastView = View.inflate(mContext, R.layout.header_toast, null);

        CircleImageView head_image = headToastView.findViewById(R.id.header_toast_smallimg);
        Glide.with(mContext).load(header).into(head_image);
        TextView header_toast_title = headToastView.findViewById(R.id.header_toast_title);
        header_toast_title.setText(emMessage.getUserName());
        TextView header_toast_name = headToastView.findViewById(R.id.header_toast_name);
        EMMessageBody messageBody = emMessage.getBody();
        switch (emMessage.getType()) {
            //文本
            case TXT:
                EMTextMessageBody emTextMessageBody = (EMTextMessageBody) messageBody;
                String content = emTextMessageBody.getMessage();
                if (!TextUtils.isEmpty(content)) {
                    if (content.length() > 20) {
                        content = content.substring(0, 20) + "...";
                    }
                    header_toast_name.setText(content);
                }
                break;
            //图片
            case IMAGE:
                header_toast_name.setText("收到一张[图片]");
                break;
            //语音
            case VOICE:
                header_toast_name.setText("收到一段[语音]");
                break;
        }


        // 为headToastView设置Touch事件
        headToastView.setOnTouchListener(this);
        linearLayout.addView(headToastView);
        // 定义WindowManager 并且将View添加到WindowManager中去
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wm_params = new WindowManager.LayoutParams();
        wm_params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        //wm_params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wm_params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wm_params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        }
        wm_params.x = 0;
        wm_params.y = 100;
        wm_params.format = -3;  // 会影响Toast中的布局消失的时候父控件和子控件消失的时机不一致，比如设置为-1之后就会不同步
        wm_params.alpha = 1f;
        wm.addView(linearLayout, wm_params);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getRawX();
                downY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int currentX = (int) event.getRawX();
                int currentY = (int) event.getRawY();
                if (Math.abs(currentX - downX) > 50 || Math.abs(currentY - downY) > 40) {
                    animDismiss();
                }
                break;
            default:
                break;
        }
        return true;
    }
}
