package com.yidont.al;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by li4236 on 2016/10/12.
 */

public class WinImage extends LinearLayout implements View.OnTouchListener {

    WindowManager.LayoutParams windowManagerParams;

    private WindowManager mWindowManager;
    //默认的悬浮球
    private int defaultResource;
    //控制球的移动速度
    private int defaultSpeed = 20;

    private int oldRawX, oldRawY;

    private int getX, getY;
    //屏幕的高度
    private int screenWidth;

    private boolean isMove = false;




    private static final int KEEP_TO_SIDE = 0;
    private static final int HIDE = 1;
    private static final int MOVE_SLOWLL = 2;
    private static final int MOVE_SLOWLR = 3;

    private int mViewWidth;

    private Activity mActivity;

    private LinearLayout mLayout;

    //区分左边还是右边
    private boolean isLeftOrRight = false;


    private View mRightView, mLeftView;
    private ImageView mIco;

    private FrameLayout mFrameLayout;

//    View     popupView ;

    public WinImage(Activity context, WindowManager mWindowManager, WindowManager.LayoutParams windowParams) {


        super(context);
        mActivity = context;

        View popupView = View.inflate(context, R.layout.window_left, null);


        initView(popupView);

        this.addView(popupView);

        this.mWindowManager = mWindowManager;

        this.windowManagerParams = windowParams;

        defaultResource = R.mipmap.float_light;


        screenWidth = context.getResources().getDisplayMetrics().widthPixels;

        setOnTouchListener(this);

//        setImageResource(defaultResource);




        //设置window type, 级别太高，dialog弹不出来,设置为LAST_APPLICATION_WINDOW并不需要权限：SYSTEM_ALERT_WINDOW
        windowParams.type = WindowManager.LayoutParams.LAST_APPLICATION_WINDOW;


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


    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {


        if (!isLeftOrRight) {
            mViewWidth = this.getWidth();

            AlLog.e("控件宽度" + mViewWidth);
        }
        oldRawX = (int) event.getRawX();

        oldRawY = (int) event.getRawY();


//        AlLog.eRaw(oldRawX, oldRawY);

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                AlLog.e("ACTION_DOWN");

                getX = (int) event.getX();

                getY = (int) event.getY();


                break;
            case MotionEvent.ACTION_MOVE:
                AlLog.e("ACTION_MOVE");

                if (mRightView.getVisibility() == GONE && mLeftView.getVisibility() == GONE) {//显示的时候不能移动

                    isMove = true;
                    int x = (int) event.getX();

                    int y = (int) event.getY();

//                    AlLog.eXy(x, y);

                    moveView(oldRawX - getY, oldRawY - getY);
                }

                break;

            case MotionEvent.ACTION_UP:

                if (isMove)//是否有在移动
                {
                    isMove = false;//初始化默认设置

                    final Message message = new Message();

                    message.what = MOVE_SLOWLL;

                    message.arg1 = oldRawX - getX;
                    message.arg2 = oldRawY - getY;

                    mHandler.sendMessageDelayed(message, 18);

                } else {//普通的点击事件

                    Toast.makeText(getContext(), "点击事件", Toast.LENGTH_SHORT).show();
//                    if (!mPopViewUtil.isShowing()) {
//                        //显示菜单
//                        mPopViewUtil.showAtLocation(this);
//                    } else {
//                        mPopViewUtil.dismiss();
//                    }

                    if (isLeftOrRight)//右边
                    {

//                        ViewTreeObserver vto = mRightView.getViewTreeObserver();
//                        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                            public boolean onPreDraw() {
//                                int height = mRightView.getMeasuredHeight();
//                                int width = mRightView.getMeasuredWidth();
////                                textView.append("\n"+height+","+width);
//                                AlLog.e("控件高度:" + width);
//
//
//                                moveView(oldRawX - width/2, oldRawY );
//                                return true;
//                            }
//                        });
                        if (mRightView.getVisibility() == GONE) {

                            mRightView.setVisibility(VISIBLE);

                            AlLog.e("大哥");

                            moveView(screenWidth - mRightWidth, oldRawY);


                        } else {

                            AlLog.e(mRightWidth + "哈哈:" + mViewWidth);
                            moveView(screenWidth - mViewWidth, oldRawY);
                            mRightView.setVisibility(GONE);

                        }

                    } else {//左边
                        if (mLeftView.getVisibility() == GONE)

                            mLeftView.setVisibility(VISIBLE);
                        else
                            mLeftView.setVisibility(GONE);
                    }


                }


                break;
        }
        return true;
    }


    //更新view位置
    public void moveView(int delatX, int deltaY) {


        AlLog.e("结束的位置" + delatX + ":" + deltaY);
        windowManagerParams.x = delatX;

        windowManagerParams.y = deltaY;
        // 更新floatView
        mWindowManager.updateViewLayout(this, windowManagerParams);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            switch (msg.what) {

                case MOVE_SLOWLL:


                    int mDistance = msg.arg1;

                    int xMiddle = screenWidth / 2;

                    if (xMiddle > mDistance) //左边
                    {
                        AlLog.e("左边:" + mDistance);

                        isLeftOrRight = false;


                        LinearLayout.LayoutParams mFrameLayoutLayoutParams = (LinearLayout.LayoutParams) mFrameLayout.getLayoutParams();

                        mFrameLayoutLayoutParams.gravity = Gravity.LEFT;

                        mFrameLayout.setLayoutParams(mFrameLayoutLayoutParams);

                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mIco.getLayoutParams();

                        params.gravity = Gravity.LEFT;

                        mIco.setLayoutParams(params);

//                        mLeftView.setVisibility(VISIBLE);
//
//                        mRightView.setVisibility(GONE);

                        initItemView(mLeftView);


//                        if (mDistance != 0) {
                        mDistance = mDistance - defaultSpeed;
                        if (mDistance < 0)//防止超过屏幕
//                            mDistance = -mViewWidth / 2;
                            mDistance = 0;
//                        }


                    } else {//右边

                        isLeftOrRight = true;

                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mIco.getLayoutParams();

                        params.gravity = Gravity.RIGHT;

                        mIco.setLayoutParams(params);


                        LinearLayout.LayoutParams mFrameLayoutLayoutParams = (LinearLayout.LayoutParams) mFrameLayout.getLayoutParams();

                        mFrameLayoutLayoutParams.gravity = Gravity.RIGHT;

                        mFrameLayout.setLayoutParams(mFrameLayoutLayoutParams);


//                        mLeftView.setVisibility(GONE);
//
//                        mRightView.setVisibility(INVISIBLE);

                        AlLog.e("右边:" + mDistance + "屏幕宽度" + screenWidth);

                        initItemView(mRightView);

//                        if (mDistance < screenWidth) {
                        mDistance = mDistance + defaultSpeed;
                        if (mDistance >= screenWidth)//防止超过屏幕
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
    private void initView(View popupView) {

        mIco = (ImageView) popupView.findViewById(R.id.ll_ico);

        mFrameLayout = (FrameLayout) popupView.findViewById(R.id.ll_parent);

//        mRightView = (View) popupView.findViewById(R.id.ll_right);
//        mLeftView = (View) popupView.findViewById(R.id.ll_left);

        if (mRightWidth == 0) {

            mRightView.setVisibility(INVISIBLE);


            ViewTreeObserver vto = mRightView.getViewTreeObserver();

            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {

                    if (mRightWidth == 0) {
                        int height = mRightView.getMeasuredHeight();
                        mRightWidth = mRightView.getMeasuredWidth();
//                                textView.append("\n"+height+","+width);
                        AlLog.e("右边控件宽度:" + mRightWidth);
                        mRightView.setVisibility(GONE);
                    }


                    return true;
                }
            });
        }
    }

    private int mRightWidth = 0;


    // popupWindow的布局及点击事件
    private void initItemView(final View popupView) {

        mLayout = (LinearLayout) popupView.findViewById(R.id.ll_menu);
        LinearLayout ll_custom_service = (LinearLayout) popupView
                .findViewById(R.id.ll_custom_refresh);
        LinearLayout ll_logout = (LinearLayout) popupView
                .findViewById(R.id.ll_custom_share);
        LinearLayout ll_protect = (LinearLayout) popupView
                .findViewById(R.id.ll_custom_exit);
        ll_custom_service.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "您点击了客服中心按钮", Toast.LENGTH_SHORT).show();
//                onDimissImage();

                popupView.setVisibility(GONE);
            }
        });
        ll_logout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "您点击了注销按钮", Toast.LENGTH_SHORT).show();
                popupView.setVisibility(GONE);
            }
        });
        ll_protect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "您点击了账号保护按钮", Toast.LENGTH_SHORT).show();
                popupView.setVisibility(GONE);
            }
        });
    }
}
