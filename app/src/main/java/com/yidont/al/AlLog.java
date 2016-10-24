package com.yidont.al;

import android.util.Log;

/**
 * Created by li4236 on 2016/10/12.
 */

public class AlLog {



    public static void e(int oldRawX,int oldRawY,int x,int y)
    {
        Log.e("oldRawX:"+oldRawX +" oldRawY:"+ oldRawY,
                "x:"+x +" y:"+ y);
    }

    public static void eRaw(int oldRawX,int oldRawY)
    {
        Log.e("li4236","oldRawX:"+oldRawX +" oldRawY:"+ oldRawY
                );
    }


    public static void eXy(int oldRawX,int oldRawY)
    {
        Log.e("li4236","x:"+oldRawX +" y:"+ oldRawY
        );
    }
    public static void e(String type)
    {
        Log.e("li4236",type);
    }
}
