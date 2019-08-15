package com.littleyellow.utils.statebar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import static com.littleyellow.utils.statebar.StatusBarUtil.DARK_FONT_DEFAULT;
import static com.littleyellow.utils.statebar.StatusBarUtil.DARK_FONT_FORCE;
import static com.littleyellow.utils.statebar.StatusBarUtil.DARK_WHITE_AOTO;
import static com.littleyellow.utils.statebar.StatusBarUtil.createStatusBarView;
import static com.littleyellow.utils.statebar.StatusBarUtil.getStatusBarHeight;
import static com.littleyellow.utils.statebar.StatusBarUtil.isLightColor;

/**
 * Created by 小黄 on 2018/9/26.
 * 自己创建假状态栏控件方式，
 * 会返回假状态栏控件，自己做UI操作(什么透明渐变啊，颜色渐变需求啊)
 * 在setContentView(R.layout.X)方法后调用
 */
public class StatusBarView {

    public static View setColor(Activity activity, @ColorInt int color){
        return setColor(activity,false,color,DARK_WHITE_AOTO);
    }

    /**
     *
     * @param activity
     * @param color 状态栏颜色
     * @param discretion 是否自行放置状态栏
     * @param fontColor 字体颜色,5.0系统下设置无效
     *             LIGHT_BAR_OFF:白色，
     *             LIGHT_BAR_ON:黑色(6.0系统下设置无效,"最初的想法是针对小米和魅族分别处理，其他系统在Android 6.0及以上才处理。经过查看用户分布(2018-02-23)Android 6.0之下占33.27%，小米用户6.69%，魅族用户1.08%。这样一看都没有必要对小米、魅族分别处理了，那就都统一到Android 6.0去设置吧。")，
     *             LIGHT_BAR_AOTO:根据颜色值自动选择
     * @return 是否成功设置颜色
     */
    public static View setColor(Activity activity, boolean discretion, @ColorInt int color, int fontColor){
        if(!updateStateBar(activity,color,fontColor)){
            return null;
        }
        // 生成一个状态栏大小的矩形
        View statusView = createStatusBarView(activity, color);
        if(!discretion) {
            ViewGroup decorView = (ViewGroup) activity.findViewById(android.R.id.content);
            decorView.addView(statusView, 0);
            if (!(decorView instanceof LinearLayout) && decorView.getChildAt(1) != null) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(0, getStatusBarHeight(activity), 0, 0);
                decorView.getChildAt(1).setLayoutParams(lp);
            }
        }
        return  statusView;
    }

    public static boolean updateStateBar(Activity activity, @ColorInt int color, int fontColor){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return false;
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            int visibility = View.SYSTEM_UI_FLAG_VISIBLE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M&&DARK_FONT_DEFAULT != fontColor) {
                if(DARK_FONT_FORCE == fontColor){
                    visibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }else if (isLightColor(color)){
                    visibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }else {
                    visibility = View.SYSTEM_UI_FLAG_VISIBLE;
                }
            }
            activity.getWindow().getDecorView().setSystemUiVisibility(visibility| View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        return true;
    }
}
