package com.littleyellow.utils.view;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorRes;
import android.support.annotation.FloatRange;
import android.support.v4.content.ContextCompat;
import android.view.View;

import static com.littleyellow.utils.view.Shape.getDrawableCompat;
import static com.littleyellow.utils.view.Shape.setBackgroundCompat;


/**
 * Created by 小黄 on 2020/5/27
 */
public class Selector {

    private final View view;

    private ShapeEditor normalShape;
    private ShapeEditor pressShape;
    private ShapeEditor disableShape;
    private ShapeEditor selectedShape;

    private Shape.Unit unit;

    private float defPressDark = 0.1f;

    private boolean pressRipple;

    public Selector(View view) {
        this.view = view;
        unit = new Shape.DpUnit(view.getContext());
    }

    public Selector setUnit(Shape.Unit unit) {
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
            GradientDrawable pressed = getDrawableCompat(disableShape.radius,colors,disableShape.orientation);
            setStroke(pressed,disableShape);
            sld.addState(new int[]{-android.R.attr.state_enabled}, pressed);
        }

        if(null != selectedShape){
            int[] colors = selectedShape.colors;
            GradientDrawable selected = getDrawableCompat(selectedShape.radius,colors,selectedShape.orientation);
            setStroke(selected,selectedShape);
            sld.addState(new int[]{android.R.attr.state_selected}, selected);
        }

        if(null != pressShape){
            int[] colors = pressShape.colors;

            if (pressRipple && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                rippleColor = colors[0];
            }else{
                GradientDrawable pressed = getDrawableCompat(pressShape.radius,colors,pressShape.orientation);
                setStroke(pressed,pressShape);
                sld.addState(new int[]{android.R.attr.state_pressed}, pressed);
            }
        }else if(null != normalShape){
           int length = normalShape.colors.length;
            int[] colors = new int[length];
            for(int i = 0;i<length;i++){
                colors[i] = translateDark(normalShape.colors[i], defPressDark);
            }
            rippleColor = colors[0];
            if (pressRipple && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                rippleColor = colors[0];
            }else{
                GradientDrawable pressed = getDrawableCompat(normalShape.radius,colors,normalShape.orientation);
                setStroke(pressed,normalShape);
                sld.addState(new int[]{android.R.attr.state_pressed}, pressed);
            }
        }

        if(null != normalShape){
            int[] colors = normalShape.colors;
            GradientDrawable normal = getDrawableCompat(normalShape.radius,colors,normalShape.orientation);
            setStroke(normal,normalShape);
            sld.addState(new int[]{}, normal);
        }

        if (pressRipple && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP&& -1 != rippleColor) {
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

    private void setStroke(GradientDrawable drawable,ShapeEditor shapeEditor) {
        if(0 == shapeEditor.dashWidth && 0 == shapeEditor.dashGap){
            drawable.setStroke(shapeEditor.strokeWidth,shapeEditor.strokeColor);
        }else{
            drawable.setStroke(shapeEditor.strokeWidth,shapeEditor.strokeColor,shapeEditor.dashWidth,shapeEditor.dashGap);
            view.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        }
    }


    public void setBackground(){
        setBackgroundCompat(getDrawable(),view);
    }

    public static class ShapeEditor {

        private float []radius = new float[]{0,0,0,0,0,0,0,0};
        private int []colors = new int[]{Color.TRANSPARENT,Color.TRANSPARENT};
        //    int bgColor;
        private int strokeWidth = 0;
        private int strokeColor = Color.TRANSPARENT;
        /**
         * 在android中设置虚线需要
         * 1. 用shape画虚线使用时，控件的layout_height一定要大于shape中stroke标签的width属性
         * 2. 将该控件设置关闭硬件加速（这里会处理）
         */
        private int dashWidth = 0,dashGap = 0;
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

        public ShapeEditor radius(float topLeft, float topRight, float bottomRight, float bottomLeft){
            float topLeft_ = selector.unit.convert(topLeft);
            float topRight_ = selector.unit.convert(topRight);
            float bottomRight_ = selector.unit.convert(bottomRight);
            float bottomLeft_ = selector.unit.convert(bottomLeft);
            radius = new float[]{topLeft_,topLeft_,topRight_,topRight_,bottomRight_,bottomRight_,bottomLeft_,bottomLeft_};
            return this;
        }

        public ShapeEditor radius(float topLeftX, float topLeftY, float topRightX, float topRightY, float bottomRightX, float bottomRightY, float bottomLeftX, float bottomLeftY){
            float topLeftX_ = selector.unit.convert(topLeftX);
            float topLeftY_ = selector.unit.convert(topLeftY);
            float topRightX_ = selector.unit.convert(topRightX);
            float topRightY_ = selector.unit.convert(topRightY);

            float bottomRightX_ = selector.unit.convert(bottomRightX);
            float bottomRightY_ = selector.unit.convert(bottomRightY);
            float bottomLeftX_ = selector.unit.convert(bottomLeftX);
            float bottomLeftY_ = selector.unit.convert(bottomLeftY);
            radius = new float[]{topLeftX_,topLeftY_,topRightX_,topRightY_,bottomRightX_,bottomRightY_,bottomLeftX_,bottomLeftY_};
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

        public ShapeEditor dash(int width, int gap) {
            this.dashWidth = (int) selector.unit.convert(width);
            this.dashGap = (int) selector.unit.convert(gap);
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





    public static int translateDark(int color,float ratio){
        ratio = 1f - ratio;
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * ratio);
        int g = (int) (((color >> 8) & 0xFF) * ratio);
        int b = (int) ((color & 0xFF) * ratio);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }


}

