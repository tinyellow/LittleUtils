package com.littleyellow.utils.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import android.view.View;

/**
 * Created by 小黄 on 2020/5/28
 */
public class Shape {
    private final View view;

    private Unit unit;

    private int color;
    private float []radius = new float[]{0,0,0,0,0,0,0,0};
    private int strokeWidth = 0;
    private int strokeColor = Color.TRANSPARENT;
    /**
     * 在android中设置虚线需要
     * 1. 用shape画虚线使用时，控件的layout_height一定要大于shape中stroke标签的width属性
     * 2. 将该控件设置关闭硬件加速（这里会处理）
     */
    private int dashWidth = 0,dashGap = 0;

    public Shape(View view) {
        this.view = view;
        unit = new DpUnit(view.getContext());
    }

    public Shape setUnit(Unit unit) {
        this.unit = unit;
        return this;
    }

    public Shape radius(float r){
        float r_ = unit.convert(r);
        radius = new float[]{r_,r_,r_,r_,r_,r_,r_,r_};
        return this;
    }

    public Shape radius(float topLeft, float topRight, float bottomRight, float bottomLeft){
        float topLeft_ = unit.convert(topLeft);
        float topRight_ = unit.convert(topRight);
        float bottomRight_ = unit.convert(bottomRight);
        float bottomLeft_ = unit.convert(bottomLeft);
        radius = new float[]{topLeft_,topLeft_,topRight_,topRight_,bottomRight_,bottomRight_,bottomLeft_,bottomLeft_};
        return this;
    }

    public Shape radius(float topLeftX, float topLeftY, float topRightX, float topRightY, float bottomRightX, float bottomRightY, float bottomLeftX, float bottomLeftY){
        float topLeftX_ = unit.convert(topLeftX);
        float topLeftY_ = unit.convert(topLeftY);
        float topRightX_ = unit.convert(topRightX);
        float topRightY_ = unit.convert(topRightY);

        float bottomRightX_ = unit.convert(bottomRightX);
        float bottomRightY_ = unit.convert(bottomRightY);
        float bottomLeftX_ = unit.convert(bottomLeftX);
        float bottomLeftY_ = unit.convert(bottomLeftY);
        radius = new float[]{topLeftX_,topLeftY_,topRightX_,topRightY_,bottomRightX_,bottomRightY_,bottomLeftX_,bottomLeftY_};
        return this;
    }

    public Shape stroke(float width, int color) {
        this.strokeWidth = (int) unit.convert(width);
        this.strokeColor = color;
        return this;
    }


    public Shape strokeRes(float width, @ColorRes int colorRes) {
        this.strokeWidth = (int) unit.convert(width);
        this.strokeColor = ContextCompat.getColor(view.getContext(),colorRes);
        return this;
    }

    public Shape color(int color) {
        this.color = color;
        return this;
    }


    public Shape colorRes(@ColorRes int colorRes) {
        this.color = ContextCompat.getColor(view.getContext(),colorRes);
        return this;
    }

    public Shape dash(int width, int gap) {
        this.dashWidth = (int) unit.convert(width);
        this.dashGap = (int) unit.convert(gap);
        return this;
    }

    public void setShape(){
        setBackgroundCompat(getDrawable(),view);
    }

    public Drawable getDrawable(){

//        Drawable drawable = view.getBackground();
//        if(null != drawable && drawable instanceof ShapeDrawable){
//            shapeDrawable = (ShapeDrawable) drawable;
//        }
//        if(null == shapeDrawable){
//            shapeDrawable = new ShapeDrawable();
//        }
        GradientDrawable drawable = getDrawableCompat(radius,new int[]{color,color},GradientDrawable.Orientation.LEFT_RIGHT);
        if(0 == dashWidth && 0 == dashGap){
            drawable.setStroke(strokeWidth,strokeColor);
        }else{
            drawable.setStroke(strokeWidth,strokeColor,dashWidth,dashGap);
            view.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        }
        return drawable;
    }

    public interface Unit{
        float convert(float dpValue);
    }

    public static class DpUnit implements Unit{

        private Context context;

        public DpUnit(Context context) {
            this.context = context;
        }

        @Override
        public float convert(float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (dpValue * scale + 0.5f);
        }
    }

    public class PxUnit implements Unit{

        @Override
        public float convert(float dpValue) {
            return dpValue;
        }
    }

    /**
     *
     * @param radius 四个角的半径
     * @param colors 渐变的颜色
     * @return
     */
    public static GradientDrawable getDrawableCompat(float []radius,int []colors,GradientDrawable.Orientation orientation) {
        //TODO:判断版本是否大于16  项目中默认的都是Linear散射 都是从左到右 都是只有开始颜色和结束颜色
        GradientDrawable drawable;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            drawable=new GradientDrawable();
            drawable.setOrientation(orientation);
            drawable.setColors(colors);
        }else{
            drawable = new GradientDrawable(orientation, colors);
        }

        drawable.setCornerRadii(radius);
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        return drawable;
    }

    /**
     *
     * @param drawable 生成的背景
     * @param view 需要添加背景的View
     */
    public static void setBackgroundCompat(Drawable drawable, View view){
        //判断当前版本号，版本号大于等于16，使用setBackground；版本号小于16，使用setBackgroundDrawable。
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            view.setBackground(drawable);
        }else{
            view.setBackgroundDrawable(drawable);
        }
    }
}
