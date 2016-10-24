package com.yidont.al;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.x;
import static android.R.attr.y;

/**
 * Created by li4236 on 2016/10/12.
 */

public class NewWinImage extends LinearLayout implements View.OnTouchListener {

    private WindowManager.LayoutParams windowManagerParams = new WindowManager.LayoutParams();

//    WindowManager.LayoutParams windowManagerParams;

    private WindowManager mWindowManager;
    //默认的悬浮球
    private int defaultResource;
    //控制球的移动速度
    private int defaultSpeed = 20;

    private int oldRawX, oldRawY;

    private int getX, getY;
    //屏幕的高度
    private int screenWidth, screenHeight;

    private boolean isMove = false;

    private static final int KEEP_TO_SIDE = 0;
    private static final int HIDE = 1;
    private static final int MOVE_SLOWLL = 2;
    private static final int MOVE_SLOWLR = 3;

    private int mViewWidth, mViewHeiht;

    private Activity mActivity;

    //区分左边还是右边
    private boolean isLeftOrRight = false;

    //是否靠边隐藏
    private boolean isHide;

    private View mLeftView;


    private ImageView mIcoView;

    //变暗的悬浮球
    private int darkResource;

    //右边视图
    private RIghtWinImage rIghtWinImage;


    public NewWinImage(Activity context) {


        super(context);

        mActivity = context;

        View popupView = View.inflate(context, R.layout.window_left, null);

        initView(popupView);

        this.addView(popupView);

        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        ;

//        this.windowManagerParams = windowParams;
        //默认图片
        defaultResource = R.mipmap.float_light;
        //变暗图片
        darkResource = R.mipmap.float_dark;
        //屏幕宽度
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        //屏幕高度
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        setOnTouchListener(this);


        //设置window type, 级别太高，dialog弹不出来,设置为LAST_APPLICATION_WINDOW并不需要权限：SYSTEM_ALERT_WINDOW
        windowManagerParams.type = WindowManager.LayoutParams.LAST_APPLICATION_WINDOW;

        windowManagerParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

        // 设置Window flag
        windowManagerParams.flags = windowManagerParams.flags |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        // 调整悬浮窗口至左上角，便于调整坐标
        windowManagerParams.gravity = Gravity.LEFT | Gravity.TOP;

//        // 以屏幕左上角为原点，设置x、y初始值
//        windowManagerParams.x = (int)mPreferenceManager.getFloatX();
//        windowManagerParams.y = (int)mPreferenceManager.getFloatY();
        // 设置悬浮窗口长宽数据
        windowManagerParams.width = LayoutParams.WRAP_CONTENT;
        windowManagerParams.height = LayoutParams.WRAP_CONTENT;

        // 显示FloatView悬浮球
        mWindowManager.addView(this, windowManagerParams);


        startTimerCount();//启动倒计时,变成半透明
    }


    @Override
    public boolean onTouch(final View v, MotionEvent event) {

        mViewWidth = mIcoView.getWidth();

        mViewHeiht = mIcoView.getHeight();

        oldRawX = (int) event.getRawX();

        oldRawY = (int) event.getRawY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                AlLog.e("ACTION_DOWN");

                getX = (int) event.getX();

                getY = (int) event.getY();

                cancelTimerCount();

                cancelTimerViewCount();

                cancelSecondTimerCount();//按下的时候取消全部计时

                mIcoView.setImageResource(defaultResource);

                break;
            case MotionEvent.ACTION_MOVE:
                AlLog.e("ACTION_MOVE");

                int xMove = Math.abs((int) (event.getX() - getX));
                int yMove = Math.abs((int) (event.getY() - getY));
                if(xMove > 10 || yMove > 10) {
                    //x轴或y轴方向的移动距离大于5个像素，视为拖动，否则视为点击
                    if (mLeftView.getVisibility() == GONE) {//显示的时候不能移动

                        cancelTimerCount();

                        cancelTimerViewCount();

                        cancelSecondTimerCount();//按下的时候取消全部计时
//                    mIcoView.setImageResource(defaultResource);
                        isMove = true;

                        int x, y;

                        x = oldRawX - getX;

                        y = oldRawY - getY;

                        if (x < 0)//处理越界屏幕问题
                            x = 0;
                        if (x > screenWidth - mViewWidth)
                            x = screenWidth - mViewWidth;


                        if (y < 0)
                            y = 0;
                        if (y > screenHeight - mViewHeiht - mViewWidth / 2)
                            y = screenHeight - mViewHeiht - mViewWidth / 2;


//                    AlLog.e("Y :" + y + "屏幕高度:" + screenHeight);
                        moveView(x, y);
                    }
                }



                break;

            case MotionEvent.ACTION_UP:

                if (isMove)//是否有在移动
                {

                    AlLog.e("亿动ACTION_UP");
                    isMove = false;//初始化默认设置

                    final Message message = new Message();

                    message.what = MOVE_SLOWLL;

                    message.arg1 = oldRawX - getX;

                    message.arg2 = oldRawY - getY;


                    mHandler.sendMessageDelayed(message, 18);

                } else {//普通的点击事件


                    AlLog.e("点击事件ACTION_UP");
//                    Toast.makeText(getContext(), "点击事件", Toast.LENGTH_SHORT).show();

                    if (isHide)//是否半边隐藏,如果是先移出来,在执行接下来的操作
                    {
                        if (isLeftOrRight)//右边
                        {
                            moveHalfView(screenWidth - mViewWidth);
                        } else {

                            moveHalfView(0);
                        }
                    }

                    if (isLeftOrRight)//右边
                    {

//
//                        mWindowManager.removeView(this);
//                        mIcoView.setVisibility(GONE);
//                                //回调显示窗口
//                        mOnViewAble.onView(screenWidth - mRightWidth, (int) v.getY());


                        rIghtWinImage = new RIghtWinImage(screenWidth - mRightWidth,

                                mOnMenuClick, mActivity, mWindowManager, windowManagerParams);

                        rIghtWinImage.setMenuDismiss(new onMenuDiss() {
                            @Override
                            public void onDismiss() {
                                //点击消失的时候
                                startTimerCount();//从新计时

                                rIghtWinImage = null;
                            }
                        });


                        mHandler.postDelayed(new Runnable() {//移出来之后在消失,而不是马上消失效果
                            @Override
                            public void run() {
                                mIcoView.setVisibility(INVISIBLE);
                            }
                        }, 18);


                    } else {

                        controlIco();

                    }


                }


                break;
        }
        return true;
    }


    //更新view位置
    public void moveView(int delatX, int deltaY) {


        AlLog.e("结束的位置" + delatX + ":" + deltaY);

        if (deltaY < 0)//防止超过边界处理
            deltaY = 0;
        if (deltaY > screenHeight - mViewHeiht - mViewWidth / 2)
            deltaY = screenHeight - mViewHeiht - mViewWidth / 2;


        windowManagerParams.x = delatX;

        windowManagerParams.y = deltaY;
        // 更新floatView
        mWindowManager.updateViewLayout(this, windowManagerParams);


        if (delatX == 0 || delatX == screenWidth - mViewWidth)//达到了屏幕的边缘
        {
            startTimerCount();//启动倒计时,变成半透明
        }

        isHide = false;
    }

    //更新屏幕半边view位置
    public void moveHalfView(int delatX) {

        windowManagerParams.x = delatX;

        // 更新floatView
        mWindowManager.updateViewLayout(this, windowManagerParams);

        AlLog.e(" //更新屏幕半边view位置:" + delatX);

        isHide = true;
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            switch (msg.what) {

                case MOVE_SLOWLR:

                    controlIco();

                    break;

                case KEEP_TO_SIDE://三秒之后图片变暗
                    mIcoView.setImageResource(darkResource);

                    cancelTimerCount();
                    //启动移动到半边计时器
                    startSecondTimerCount();

                    break;
                case HIDE://半边隐藏

                    cancelSecondTimerCount();

                    if (isLeftOrRight) {//右边
//                        isHide = true;

                        moveHalfView(screenWidth - mViewWidth / 2);
                    } else {//左边

                        moveHalfView(-mViewWidth / 2);
                    }
                    break;

                case MOVE_SLOWLL://动画效果


                    int mDistance = msg.arg1;

                    int xMiddle = screenWidth / 2;

                    if (xMiddle > mDistance) //左边
                    {
//                        AlLog.e("左边:" + mDistance);

                        isLeftOrRight = false;
                        mDistance = mDistance - defaultSpeed;
                        if (mDistance < 0)//防止超过屏幕
//                            mDistance = -mViewWidth / 2;
                            mDistance = 0;
//                        }


                    } else {//右边

                        isLeftOrRight = true;

                        mDistance = mDistance + defaultSpeed;
                        if (mDistance > screenWidth - mViewWidth)//防止超过屏幕
                            mDistance = screenWidth - mViewWidth;
//                        mDistance = screenWidth ;

//                        }
                    }


                    moveView(mDistance, msg.arg2);
//                    if (mDistance == -mViewWidth / 2) //结束动画
//                        return;
                    if (mDistance >= screenWidth - mViewWidth)//结束动画
                        return;

                    if (mDistance == 0) //结束动画
                        return;
//                    if (mDistance >= screenWidth )//结束动画
//                        return;


                    final Message message = new Message();
                    message.what = MOVE_SLOWLL;
                    message.arg1 = mDistance;
                    message.arg2 = msg.arg2;

                    message.obj = msg.obj;


                    mHandler.sendMessageDelayed(message, 18);


                    break;


            }


        }
    };


    // popupWindow的布局及点击事件
    private void initView(final View popupView) {

        mIcoView = (ImageView) popupView.findViewById(R.id.ll_ico);

        mLeftView = (View) popupView.findViewById(R.id.ll_layout_left);

        if (mRightWidth == 0) {

            mLeftView.setVisibility(INVISIBLE);

            ViewTreeObserver vto = popupView.getViewTreeObserver();

            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                public boolean onPreDraw() {

                    if (mRightWidth == 0) {

                        mRightWidth = popupView.getMeasuredWidth();
                        AlLog.e("成功:" + mRightWidth);

                        mLeftView.setVisibility(GONE);

                        mViewWidth = mIcoView.getMeasuredWidth();
                    }

                    return true;
                }
            });
        }

        initItemView(popupView);
    }

    private int mRightWidth = 0;


    // popupWindow的布局及点击事件
    private void initItemView(final View popupView) {

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

                controlIco();

                mOnMenuClick.onRefresh();

            }
        });
        ll_custom_share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                controlIco();
                mOnMenuClick.onShare();
            }
        });
        ll_custom_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                controlIco();

                mOnMenuClick.onExit();
            }
        });
    }


    private Timer timer;
    private TimerTask timerTask;


    private Timer secondTimer;
    private TimerTask secondTask;


    private Timer timerView;
    private TimerTask timerTaskView;

//    public void setIcoView() {
//
//        mIcoView.setVisibility(VISIBLE);
//    }


    //开启第一个定时器
    public void startTimerCount() {


        if (mIcoView.getVisibility() == INVISIBLE)
            mIcoView.setVisibility(VISIBLE);

//        isCancel = false;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isMove) {//移动的时候不触发功能
                    mHandler.sendEmptyMessage(KEEP_TO_SIDE);
                }
            }
        };
        timer.schedule(timerTask, 3000);
    }

    //开启第一个定时器
    public void startTimerViewCount() {


//        isCancel = false;
        timerView = new Timer();
        timerTaskView = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MOVE_SLOWLR);
            }
        };
        timerView.schedule(timerTaskView, 3000);
    }

    //关闭第一个定时器
    public void cancelTimerViewCount() {
//        isCancel = true;
        if (timerView != null) {
            timerView.cancel();
            timerView = null;
        }
        if (timerTaskView != null) {
            timerTaskView.cancel();
            timerTaskView = null;
        }
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

    //开启第二个定时器
    public void startSecondTimerCount() {
//        isSecondCancel = false;
        secondTimer = new Timer();
        secondTask = new TimerTask() {
            @Override
            public void run() {
//                if (!isSecondCancel) {
                mHandler.sendEmptyMessage(HIDE);
            }
//            }
        };
        secondTimer.schedule(secondTask, 2000);
    }

    //关闭第二个定时器
    public void cancelSecondTimerCount() {
//        isSecondCancel = true;
        if (secondTimer != null) {
            secondTimer.cancel();
            secondTimer = null;
        }
        if (secondTask != null) {
            secondTask.cancel();
            secondTask = null;
        }
    }


    public void removeView() {

        cancelTimerCount();
        cancelSecondTimerCount();
        cancelTimerViewCount();

        if (rIghtWinImage != null)
            rIghtWinImage.remoView();

        mWindowManager.removeView(this);
    }

    public void controlIco() {
        if (mLeftView.getVisibility() == GONE) {

            mLeftView.setVisibility(VISIBLE);

            startTimerViewCount();//自动隐藏菜单

        } else {

            mLeftView.setVisibility(GONE);


            cancelTimerViewCount();
            startTimerCount();
        }

    }


    private onMenuClick mOnMenuClick;

    public void setOnMenuClick(onMenuClick onMenuClick) {
        this.mOnMenuClick = onMenuClick;
    }
}
