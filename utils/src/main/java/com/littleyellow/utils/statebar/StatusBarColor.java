package com.littleyellow.utils.statebar;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import androidx.annotation.ColorInt;
import android.view.View;
import android.view.WindowManager;

import static android.os.Build.VERSION_CODES.KITKAT;
import static com.littleyellow.utils.statebar.StatusBarUtil.DARK_FONT_FORCE;
import static com.littleyellow.utils.statebar.StatusBarUtil.DARK_WHITE_AOTO;
import static com.littleyellow.utils.statebar.StatusBarUtil.setFitsSystemWindows;

/**
 * Created by 小黄 on 2018/9/26.
 * 纯颜色设置状态栏，没有自己创建假状态栏控件方式
 * 在setContentView(R.layout.X)方法后调用
 */
public class StatusBarColor {

    public static void setColor(Activity activity, @ColorInt int color){
        setColor(activity,color, DARK_WHITE_AOTO, Color.BLACK,Color.WHITE);
    }

    /**
     *
     * @param activity
     * @param color 状态栏颜色
     * @param fontDarkMode 字体颜色,5.0系统下设置无效
     *             LIGHT_BAR_OFF:白色，
     *             LIGHT_BAR_ON:黑色(6.0系统下设置无效,"最初的想法是针对小米和魅族分别处理，其他系统在Android 6.0及以上才处理。经过查看用户分布(2018-02-23)Android 6.0之下占33.27%，小米用户6.69%，魅族用户1.08%。这样一看都没有必要对小米、魅族分别处理了，那就都统一到Android 6.0去设置吧。")，
     *             LIGHT_BAR_AOTO:根据颜色值自动选择
     * @param failedColor 设置状态栏颜色失败时的颜色
     * @param contentColorKITKAT 4.4<=system<5.0时的参数,主要用来修改根布局背景颜色(一般白色)
     * @return 是否成功设置颜色
     */
    public static void setColor(Activity activity, @ColorInt int color, int fontDarkMode, @ColorInt int failedColor,@ColorInt int contentColorKITKAT){
        int visibility;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if(DARK_WHITE_AOTO == fontDarkMode){
                if(StatusBarUtil.isLightColor(color)){
                    activity.getWindow().setStatusBarColor(color);
                    visibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }else{
                    activity.getWindow().setStatusBarColor(failedColor);
                    visibility = View.SYSTEM_UI_FLAG_VISIBLE;
                }
            }else if(DARK_FONT_FORCE == fontDarkMode){
                activity.getWindow().setStatusBarColor(color);
                visibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }else{
                activity.getWindow().setStatusBarColor(color);
                visibility = View.SYSTEM_UI_FLAG_VISIBLE;
            }
            activity.getWindow().getDecorView().setSystemUiVisibility(visibility);
            setFitsSystemWindows(activity,true);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            visibility = View.SYSTEM_UI_FLAG_VISIBLE;
            if(DARK_FONT_FORCE == fontDarkMode){
                activity.getWindow().setStatusBarColor(color);
            } else {
                if(!StatusBarUtil.isLightColor(color)){
                    activity.getWindow().setStatusBarColor(color);
                }else{
                    activity.getWindow().setStatusBarColor(failedColor);
                }
            }
            activity.getWindow().getDecorView().setSystemUiVisibility(visibility);
            setFitsSystemWindows(activity,true);
        } else if(Build.VERSION.SDK_INT >= KITKAT){
            KITKAT_SetColor(activity,color,fontDarkMode,failedColor,contentColorKITKAT);
        }
    }

    /**
     * 主要用来修改根布局背景颜色(默认白色)，一般的需求用不到该方法
     * @param activity
     * @param statusColor
     * @param contentColor
     * @return
     */
    private static int KITKAT_SetColor(Activity activity, @ColorInt int statusColor,int fontDarkMode,int errorColor, @ColorInt int contentColor){
        if (Build.VERSION.SDK_INT >= KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View view0 = setFitsSystemWindows(activity,true);
            int height = StatusBarUtil.getStatusBarHeight(activity);
            if(null!=view0){
                if(DARK_FONT_FORCE != fontDarkMode && StatusBarUtil.isLightColor(statusColor)){
                    statusColor = errorColor;
                }
                ShapeDrawable statusDrawable = new ShapeDrawable();
                statusDrawable.getPaint().setColor(statusColor);
                statusDrawable.getPaint().setStyle(Paint.Style.FILL);
                ShapeDrawable contentDrawable = new ShapeDrawable();
                contentDrawable.getPaint().setColor(contentColor);
                contentDrawable.getPaint().setStyle(Paint.Style.FILL);
                Drawable[] layers = {statusDrawable,contentDrawable};
                LayerDrawable layerDrawable = new LayerDrawable(layers);
                layerDrawable.setLayerInset(1,0,height,0,0);
                view0.setBackground(layerDrawable);
            }
            return height;
        }
        return 0;
    }

}
