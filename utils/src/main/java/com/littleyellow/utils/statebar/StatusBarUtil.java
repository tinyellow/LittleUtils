package com.littleyellow.utils.statebar;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.ColorUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by 小黄 状态栏工具
 */
public class StatusBarUtil {

    public static final int DARK_WHITE_AOTO = 0;

    public static final int DARK_FONT_DEFAULT = 1;

    public static final int DARK_FONT_FORCE = 2;

    /**
     * 生成一个和状态栏大小相同矩形条
     * @param activity 需要设置的activity
     * @return 状态栏矩形条
     */
    public static View createStatusBarView(Activity activity, int color) {
        View statusBarView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(color);
        return statusBarView;
    }

    /**
     * 获取状态栏高度
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    private static int calculateStatusColor(int color, int alpha) {
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    public static boolean isLightColor(@ColorInt int color) {
        return ColorUtils.calculateLuminance(color) >= 0.5;
    }

    public static boolean isTranslucentStatus(final Activity activity) {
        //noinspection SimplifiableIfStatement
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return (activity.getWindow().getAttributes().flags
                    & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) != 0;
        }
        return false;
    }

    public static boolean isFullScreen(final Activity activity) {
        //noinspection SimplifiableIfStatement
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            boolean isFull = (activity.getWindow().getDecorView().getSystemUiVisibility()
//                    & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) != 0;
            final ViewGroup decorView = (ViewGroup) activity.findViewById(android.R.id.content);
            View view0 = (decorView).getChildAt(0);
            boolean isFits = null==view0?false:!view0.getFitsSystemWindows();
            return isFits;
        }
        return false;
    }

    public  static View setFitsSystemWindows(Activity activity, boolean isFits) {
        ViewGroup decorView = (ViewGroup) activity.findViewById(android.R.id.content);
        View view0 = (decorView).getChildAt(0);
        if(null!=view0){
            view0.setFitsSystemWindows(isFits);
        }
        return view0;
    }

}