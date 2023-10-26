package com.littleyellow.utils.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;

import androidx.core.util.Pair;

public class WindowUtils {
    //WindowUtils通过代码添加蒙层(显示窗口前调用):
    private static void applyDim(Activity activity, float bgAlpha) {
        if (Build.VERSION.SDK_INT >= 18) {
            ViewGroup parent = (ViewGroup) activity.getWindow().getDecorView().getRootView();
            Drawable dim = new ColorDrawable(-16777216);
            dim.setBounds(0, 0, parent.getWidth(), parent.getHeight());
            dim.setAlpha((int) (255.0F * bgAlpha));
            ViewGroupOverlay overlay = parent.getOverlay();
            overlay.add(dim);
        }
    }
    //通过代码清除蒙层(销毁时调用):

    private static void clearDim(Activity activity) {
        if (Build.VERSION.SDK_INT >= 18) {
            ViewGroup parent = (ViewGroup) activity.getWindow().getDecorView().getRootView();
            ViewGroupOverlay overlay = parent.getOverlay();
            overlay.clear();
        }
    }

    /**
     * 设置背景阴影(蒙色)
     *
     * @param mContext context
     * @param bgAlpha  1.0F为清除灰蒙背景,0.5F为添加蒙色
     */
    public static void setBackgroundAlpha(Context mContext, float bgAlpha) {
        if (bgAlpha == 1.0F) {
            clearDim((Activity) mContext);
        } else {
            applyDim((Activity) mContext, bgAlpha);
        }
    }

    public static Pair<Integer, Integer> getContentViewSize(Activity activity){
        View contentView = activity.findViewById(android.R.id.content);
       return new Pair<Integer, Integer>(contentView.getMeasuredWidth(),contentView.getMeasuredHeight());
    }
}
