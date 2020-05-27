package com.littleyellow.utils.view;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.FloatRange;
import android.support.v4.content.ContextCompat;
import android.view.View;

/**
 * Created by 小黄
 */
public class Selector {

    private final View view;

    private ShapeEditor normalShape;
    private ShapeEditor pressShape;
    private ShapeEditor disableShape;
    private ShapeEditor selectedShape;

    private Unit unit;

    private float defPressDark = 0.1f;

    private boolean pressRipple;

    public Selector(View view) {
        this.view = view;
        unit = new DpUnit(view.getContext());
    }

    public Selector setUnit(Unit unit) {
        this.unit = unit;
        return this;
    }

    public void defPressDark(@FloatRange(from = 0,to = 1) float ratio) {
        this.defPressDark = ratio;
    }


    public ShapeEditor editNormal(){
        if(null==normalShape){
            normalShape = new ShapeEditor(this);
        }
        return normalShape;
    }

    public ShapeEditor editPress(){
        return editPress(false);
    }

    public ShapeEditor editPress(boolean isRipple){
        if(null==pressShape){
            pressShape = new ShapeEditor(this);
            //默认跟正常状态一样
            if(null!=normalShape){
                int length = normalShape.radius.length;
                for(int i=0;i<length;i++){
                    pressShape.radius[i] = normalShape.radius[i];
                }
            }
        }
        this.pressRipple = isRipple;
        return pressShape;
    }

    public ShapeEditor editDisable(){
        if(null==disableShape){
            disableShape = new ShapeEditor(this);
        }
        return disableShape;
    }
    public ShapeEditor editSelected(){
        if(null==selectedShape){
            selectedShape = new ShapeEditor(this);
        }
        return selectedShape;
    }

    public Drawable getDrawable(){

        int rippleColor = -1;

        //注意一点添加state时，是有顺序的，stateListDrawable会先执行最新添加的state，如果不是该state，在执行下面的state，如果把大范围的state放到前面添加，会导致直接执行大范围的state，而不执行后面的state。此外，在添加state中，在state前添加“-”号，表示此state为false（例如：-android.R.attr.state_selected），否则为true。
        StateListDrawable sld = new StateListDrawable();

        if(null != disableShape){
            int[] colors = disableShape.colors;
            GradientDrawable pressed = getDrawable(disableShape.radius,colors,disableShape.orientation);
            pressed.setStroke(disableShape.strokeWidth,disableShape.strokeColor);
            sld.addState(new int[]{-android.R.attr.state_enabled}, pressed);
        }

        if(null != selectedShape){
            int[] colors = selectedShape.colors;
            GradientDrawable selected = getDrawable(selectedShape.radius,colors,selectedShape.orientation);
            selected.setStroke(selectedShape.strokeWidth,selectedShape.strokeColor);
            sld.addState(new int[]{android.R.attr.state_selected}, selected);
        }

        if(null != pressShape){
            int[] colors = pressShape.colors;

            if (pressRipple && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rippleColor = colors[0];
            }else{
                GradientDrawable pressed = getDrawable(pressShape.radius,colors,pressShape.orientation);
                pressed.setStroke(pressShape.strokeWidth,pressShape.strokeColor);
                sld.addState(new int[]{android.R.attr.state_pressed}, pressed);
            }
        }else if(null != normalShape){
           int length = normalShape.colors.length;
            int[] colors = new int[length];
            for(int i = 0;i<length;i++){
                colors[i] = translateDark(normalShape.colors[i], defPressDark);
            }
            rippleColor = colors[0];
            if (pressRipple && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rippleColor = colors[0];
            }else{
                GradientDrawable pressed = getDrawable(normalShape.radius,colors,normalShape.orientation);
                pressed.setStroke(normalShape.strokeWidth,normalShape.strokeColor);
                sld.addState(new int[]{android.R.attr.state_pressed}, pressed);
            }
        }

        if(null != normalShape){
            int[] colors = normalShape.colors;
            GradientDrawable normal = getDrawable(normalShape.radius,colors,normalShape.orientation);
            normal.setStroke(normalShape.strokeWidth,normalShape.strokeColor);
            sld.addState(new int[]{}, normal);
        }

        if (pressRipple && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP&& -1 != rippleColor) {
            int[][] stateList = new int[][]{
                    new int[]{android.R.attr.state_pressed},
                    new int[]{android.R.attr.state_focused},
                    new int[]{android.R.attr.state_activated},
            };

            ShapeDrawable maskDrawable = null;
//            if(null!=pressShape){
//                RoundRectShape roundRectShape = new RoundRectShape(pressShape.radius, null, null);
//                maskDrawable = new ShapeDrawable();
//                maskDrawable.setShape(roundRectShape);
//                maskDrawable.getPaint().setColor(Color.parseColor("#000000"));
//                maskDrawable.getPaint().setStyle(Paint.Style.FILL);
//            }



            int[] stateColorList = new int[]{rippleColor,rippleColor,rippleColor};
            ColorStateList colorStateList = new ColorStateList(stateList, stateColorList);
            RippleDrawable rippleDrawable = new RippleDrawable(colorStateList, sld, null);
            return rippleDrawable;
        }

        return sld;
    }


    public void setBackground(){
        setBackground(getDrawable(),view);
    }

    public static class ShapeEditor {

        private float []radius = new float[]{0,0,0,0,0,0,0,0};
        private int []colors = new int[]{Color.TRANSPARENT,Color.TRANSPARENT};
        //    int bgColor;
        private int strokeWidth = 0;
        private int strokeColor = Color.TRANSPARENT;
        private GradientDrawable.Orientation orientation = GradientDrawable.Orientation.LEFT_RIGHT;

        private final Selector selector;

        public ShapeEditor(Selector selector) {
            this.selector = selector;
        }

        public ShapeEditor radius(float r){
            float r_ = selector.unit.convert(r);
            radius = new float[]{r_,r_,r_,r_,r_,r_,r_,r_};
            return this;
        }

        public ShapeEditor radius(float lr,float tr,float rr,float br){
            float lr_ = selector.unit.convert(lr);
            float tr_ = selector.unit.convert(tr);
            float rr_ = selector.unit.convert(rr);
            float br_ = selector.unit.convert(br);
            radius = new float[]{lr_,lr_,tr_,tr_,rr_,rr_,br_,br_};
            return this;
        }

        public ShapeEditor radius(float lx,float ly,float tx,float ty,float rx,float ry,float bx,float by){
            float lx_ = selector.unit.convert(lx);
            float ly_ = selector.unit.convert(ly);
            float tx_ = selector.unit.convert(tx);
            float ty_ = selector.unit.convert(ty);

            float rx_ = selector.unit.convert(rx);
            float ry_ = selector.unit.convert(ry);
            float bx_ = selector.unit.convert(bx);
            float by_ = selector.unit.convert(by);
            radius = new float[]{lx_,ly_,tx_,ty_,rx_,ry_,bx_,by_};
            return this;
        }

        public ShapeEditor colors(int... colors){
            if(colors.length<1){
                this.colors = new int[]{Color.TRANSPARENT,Color.TRANSPARENT};
            } else if(colors.length<2){
                this.colors = new int[]{colors[0],colors[0]};
            }else{
                this.colors = colors;
            }
            return this;
        }

        public ShapeEditor colorRes(@ColorRes int... res){
            if(res.length<1){
                this.colors = new int[]{Color.TRANSPARENT,Color.TRANSPARENT};
            } else if(res.length<2){
                int color = ContextCompat.getColor(selector.view.getContext(),res[0]);
                this.colors = new int[]{color,color};
            }else{
                this.colors = new int[res.length];
                Context context = selector.view.getContext();
                for(int i = 0;i<res.length;i++){
                    colors[i] = ContextCompat.getColor(context,res[i]);
                }
            }
            return this;
        }

        public ShapeEditor stroke(float width,int color) {
            this.strokeWidth = (int) selector.unit.convert(width);
            this.strokeColor = color;
            return this;
        }


        public ShapeEditor strokeRes(float width,@ColorRes int colorRes) {
            this.strokeWidth = (int) selector.unit.convert(width);
            this.strokeColor = ContextCompat.getColor(selector.view.getContext(),colorRes);
            return this;
        }

        public ShapeEditor orientation(GradientDrawable.Orientation orientation) {
            this.orientation = orientation;
            return this;
        }
        public ShapeEditor defPressDark(@FloatRange(from = 0,to = 1)float ratio) {
            this.selector.defPressDark(ratio);
            return this;
        }

        public ShapeEditor editNormal(){
            return selector.editNormal();
        }
        public ShapeEditor editDisable(){
            return selector.editDisable();
        }
        public ShapeEditor editSelected(){
            return selector.editSelected();
        }

        public ShapeEditor editPress(){
            return selector.editPress(false);
        }

        public ShapeEditor editPress(boolean isRipple){
            return selector.editPress(isRipple);
        }

        public Drawable drawable(){
            return selector.getDrawable();
        }

        public void setBackground(){
            selector.setBackground();
        }
    }

    /**
     *
     * @param radius 四个角的半径
     * @param colors 渐变的颜色
     * @return
     */
    public static GradientDrawable getDrawable(float []radius,int []colors,GradientDrawable.Orientation orientation) {
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
    public static void setBackground(Drawable drawable, View view){
        //判断当前版本号，版本号大于等于16，使用setBackground；版本号小于16，使用setBackgroundDrawable。
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            view.setBackground(drawable);
        }else{
            view.setBackgroundDrawable(drawable);
        }
    }

    public interface Unit{
        float convert(float dpValue);
    }

    public class DpUnit implements Unit{

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

    public static int translateDark(int color,float ratio){
        ratio = 1f - ratio;
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * ratio);
        int g = (int) (((color >> 8) & 0xFF) * ratio);
        int b = (int) ((color & 0xFF) * ratio);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }


}

