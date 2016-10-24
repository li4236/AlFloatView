package com.yidont.al;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.y;

/**
 * Created by li4236 on 2016/10/12.
 */

public class RIghtWinImage extends LinearLayout {


    private Timer timer;
    private TimerTask timerTask;

    WindowManager.LayoutParams windowManagerParams;

    private WindowManager mWindowManager;


    public RIghtWinImage(int x, onMenuClick onMenuClick, Activity context, final WindowManager mWindowManager, final WindowManager.LayoutParams windowParams) {

        super(context);

        View popupView = View.inflate(context, R.layout.window_right, null);

        this.addView(popupView);

        this.initItemView(popupView, onMenuClick);

        this.mWindowManager = mWindowManager;

        this.windowManagerParams = windowParams;

        AlLog.e("最新控件宽度:" + popupView.getWidth() + "x:" + x + "/Y:" + y);


//        // 以屏幕左上角为原点，设置x、y初始值
        windowManagerParams.x = x;
//        windowManagerParams.y = y;


        mWindowManager.addView(this, windowManagerParams);
        //启动计时,如果没有操作自动隐藏
        startTimerCount();

    }

    public void remoView() {
        cancelTimerCount();

        mWindowManager.removeView(this);
    }

    // popupWindow的布局及点击事件
    private void initItemView(final View popupView, final onMenuClick mOnMenuClick) {

        LinearLayout ll_custom_refresh = (LinearLayout) popupView
                .findViewById(R.id.ll_custom_refresh);

        LinearLayout ll_custom_share = (LinearLayout) popupView
                .findViewById(R.id.ll_custom_share);

        LinearLayout ll_custom_exit = (LinearLayout) popupView
                .findViewById(R.id.ll_custom_exit);

        ll_custom_refresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                onDimissImage();
                cancelTimerCount();
                remoView();
                mOnMenuClick.onRefresh();
                mOnMenuDiss.onDismiss();


            }
        });
        ll_custom_share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                cancelTimerCount();
                remoView();

                mOnMenuClick.onShare();
                mOnMenuDiss.onDismiss();

            }
        });
        ll_custom_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                cancelTimerCount();
                remoView();

                mOnMenuClick.onExit();
                mOnMenuDiss.onDismiss();
            }
        });

        findViewById(R.id.ll_ico).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                cancelTimerCount();
                remoView();

                mOnMenuDiss.onDismiss();
            }
        });


    }


    public void setMenuDismiss(onMenuDiss onMenuDiss) {
        this.mOnMenuDiss = onMenuDiss;
    }

    private onMenuDiss mOnMenuDiss;


    //开启第一个定时器
    public void startTimerCount() {


//        isCancel = false;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {

                mHandler.sendEmptyMessage(0);

            }
        };
        timer.schedule(timerTask, 3000);
    }

    //关闭第一个定时器
    public void cancelTimerCount() {
//        isCancel = true;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            cancelTimerCount();
            remoView();
            mOnMenuDiss.onDismiss();
        }
    };
}
