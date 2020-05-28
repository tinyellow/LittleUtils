package com.littleyellow.utils.view

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.*
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.FloatRange
import android.support.v4.content.ContextCompat
import android.view.View

fun View.selector(): Selector.ShapeEditor = Selector(this).editNormal()

fun View.shape(): Shape = Shape(this)

class Selector(private val view: View) {
    private var normalShape: ShapeEditor? = null
    private var pressShape: ShapeEditor? = null
    private var disableShape: ShapeEditor? = null
    private var selectedShape: ShapeEditor? = null
    private var unit: Shape.Unit
    private var defPressDark = 0.1f
    private var pressRipple = false
    fun setUnit(unit: Shape.Unit): Selector {
        this.unit = unit
        return this
    }

    fun defPressDark(@FloatRange(from = 0.0, to = 1.0) ratio: Float) {
        defPressDark = ratio
    }

    fun editNormal(): ShapeEditor {
        if (null == normalShape) {
            normalShape = ShapeEditor(this)
        }
        return normalShape!!
    }

    fun editPress(isRipple: Boolean = false): ShapeEditor {
        if (null == pressShape) {
            pressShape = ShapeEditor(this)
            //默认跟正常状态一样
            if (null != normalShape) {
                val length = normalShape!!.radius.size
                for (i in 0 until length) {
                    pressShape!!.radius[i] = normalShape!!.radius[i]
                }
            }
        }
        pressRipple = isRipple
        return pressShape!!
    }

    fun editDisable(): ShapeEditor {
        if (null == disableShape) {
            disableShape = ShapeEditor(this)
        }
        return disableShape!!
    }

    fun editSelected(): ShapeEditor {
        if (null == selectedShape) {
            selectedShape = ShapeEditor(this)
        }
        return selectedShape!!
    }//            if(null!=pressShape){
//                RoundRectShape roundRectShape = new RoundRectShape(pressShape.radius, null, null);
//                maskDrawable = new ShapeDrawable();
//                maskDrawable.setShape(roundRectShape);
//                maskDrawable.getPaint().setColor(Color.parseColor("#000000"));
//                maskDrawable.getPaint().setStyle(Paint.Style.FILL);
//            }

    //注意一点添加state时，是有顺序的，stateListDrawable会先执行最新添加的state，如果不是该state，在执行下面的state，如果把大范围的state放到前面添加，会导致直接执行大范围的state，而不执行后面的state。此外，在添加state中，在state前添加“-”号，表示此state为false（例如：-android.R.attr.state_selected），否则为true。
    val drawable: Drawable
        get() {
            var rippleColor = -1
            //注意一点添加state时，是有顺序的，stateListDrawable会先执行最新添加的state，如果不是该state，在执行下面的state，如果把大范围的state放到前面添加，会导致直接执行大范围的state，而不执行后面的state。此外，在添加state中，在state前添加“-”号，表示此state为false（例如：-android.R.attr.state_selected），否则为true。
            val sld = StateListDrawable()
            if (null != disableShape) {
                val colors = disableShape!!.colors
                val pressed = Shape.getDrawableCompat(disableShape!!.radius, colors, disableShape!!.orientation)
                setStroke(pressed, disableShape!!)
                sld.addState(intArrayOf(-R.attr.state_enabled), pressed)
            }
            if (null != selectedShape) {
                val colors = selectedShape!!.colors
                val selected = Shape.getDrawableCompat(selectedShape!!.radius, colors, selectedShape!!.orientation)
                setStroke(selected, selectedShape!!)
                sld.addState(intArrayOf(R.attr.state_selected), selected)
            }
            if (null != pressShape) {
                val colors = pressShape!!.colors
                if (pressRipple && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    rippleColor = colors[0]
                } else {
                    val pressed = Shape.getDrawableCompat(pressShape!!.radius, colors, pressShape!!.orientation)
                    setStroke(pressed, pressShape!!)
                    sld.addState(intArrayOf(R.attr.state_pressed), pressed)
                }
            } else if (null != normalShape) {
                val length = normalShape!!.colors.size
                val colors = IntArray(length)
                for (i in 0 until length) {
                    colors[i] = translateDark(normalShape!!.colors[i], defPressDark)
                }
                rippleColor = colors[0]
                if (pressRipple && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    rippleColor = colors[0]
                } else {
                    val pressed = Shape.getDrawableCompat(normalShape!!.radius, colors, normalShape!!.orientation)
                    setStroke(pressed, normalShape!!)
                    sld.addState(intArrayOf(R.attr.state_pressed), pressed)
                }
            }
            if (null != normalShape) {
                val colors = normalShape!!.colors
                val normal = Shape.getDrawableCompat(normalShape!!.radius, colors, normalShape!!.orientation)
                setStroke(normal, normalShape!!)
                sld.addState(intArrayOf(), normal)
            }
            if (pressRipple && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && -1 != rippleColor) {
                val stateList = arrayOf(intArrayOf(R.attr.state_pressed), intArrayOf(R.attr.state_focused), intArrayOf(R.attr.state_activated))
                val maskDrawable: ShapeDrawable? = null
                //            if(null!=pressShape){
//                RoundRectShape roundRectShape = new RoundRectShape(pressShape.radius, null, null);
//                maskDrawable = new ShapeDrawable();
//                maskDrawable.setShape(roundRectShape);
//                maskDrawable.getPaint().setColor(Color.parseColor("#000000"));
//                maskDrawable.getPaint().setStyle(Paint.Style.FILL);
//            }
                val stateColorList = intArrayOf(rippleColor, rippleColor, rippleColor)
                val colorStateList = ColorStateList(stateList, stateColorList)
                return RippleDrawable(colorStateList, sld, null)
            }
            return sld
        }

    private fun setStroke(drawable: GradientDrawable, shapeEditor: ShapeEditor) {
        if (0 == shapeEditor.dashWidth && 0 == shapeEditor.dashGap) {
            drawable.setStroke(shapeEditor.strokeWidth, shapeEditor.strokeColor)
        } else {
            drawable.setStroke(shapeEditor.strokeWidth, shapeEditor.strokeColor, shapeEditor.dashWidth.toFloat(), shapeEditor.dashGap.toFloat())
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
    }

    fun setBackground() {
        Shape.setBackgroundCompat(drawable, view)
    }

    class ShapeEditor(private val selector: Selector) {
        var radius = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        var colors = intArrayOf(Color.TRANSPARENT, Color.TRANSPARENT)
        //    int bgColor;
        var strokeWidth = 0
        var strokeColor = Color.TRANSPARENT
        /**
         * 在android中设置虚线需要
         * 1. 用shape画虚线使用时，控件的layout_height一定要大于shape中stroke标签的width属性
         * 2. 将该控件设置关闭硬件加速（这里会处理）
         */
        var dashWidth = 0
        var dashGap = 0
        var orientation = GradientDrawable.Orientation.LEFT_RIGHT
        fun radius(r: Float): ShapeEditor {
            val r_ = selector.unit.convert(r)
            radius = floatArrayOf(r_, r_, r_, r_, r_, r_, r_, r_)
            return this
        }

        fun radius(topLeft: Float, topRight: Float, bottomRight: Float, bottomLeft: Float): ShapeEditor {
            val topLeft_ = selector.unit.convert(topLeft)
            val topRight_ = selector.unit.convert(topRight)
            val bottomRight_ = selector.unit.convert(bottomRight)
            val bottomLeft_ = selector.unit.convert(bottomLeft)
            radius = floatArrayOf(topLeft_, topLeft_, topRight_, topRight_, bottomRight_, bottomRight_, bottomLeft_, bottomLeft_)
            return this
        }

        fun radius(topLeftX: Float, topLeftY: Float, topRightX: Float, topRightY: Float, bottomRightX: Float, bottomRightY: Float, bottomLeftX: Float, bottomLeftY: Float): ShapeEditor {
            val topLeftX_ = selector.unit.convert(topLeftX)
            val topLeftY_ = selector.unit.convert(topLeftY)
            val topRightX_ = selector.unit.convert(topRightX)
            val topRightY_ = selector.unit.convert(topRightY)
            val bottomRightX_ = selector.unit.convert(bottomRightX)
            val bottomRightY_ = selector.unit.convert(bottomRightY)
            val bottomLeftX_ = selector.unit.convert(bottomLeftX)
            val bottomLeftY_ = selector.unit.convert(bottomLeftY)
            radius = floatArrayOf(topLeftX_, topLeftY_, topRightX_, topRightY_, bottomRightX_, bottomRightY_, bottomLeftX_, bottomLeftY_)
            return this
        }

        fun colors(vararg colors: Int): ShapeEditor {
            if (colors.size < 1) {
                this.colors = intArrayOf(Color.TRANSPARENT, Color.TRANSPARENT)
            } else if (colors.size < 2) {
                this.colors = intArrayOf(colors[0], colors[0])
            } else {
                this.colors = colors
            }
            return this
        }

        fun colorRes(@ColorRes vararg res: Int): ShapeEditor {
            if (res.size < 1) {
                colors = intArrayOf(Color.TRANSPARENT, Color.TRANSPARENT)
            } else if (res.size < 2) {
                val color = ContextCompat.getColor(selector.view.context, res[0])
                colors = intArrayOf(color, color)
            } else {
                colors = IntArray(res.size)
                val context = selector.view.context
                for (i in 0 until res.size) {
                    colors[i] = ContextCompat.getColor(context, res[i])
                }
            }
            return this
        }

        fun stroke(width: Float, color: Int): ShapeEditor {
            strokeWidth = selector.unit.convert(width).toInt()
            strokeColor = color
            return this
        }

        fun strokeRes(width: Float, @ColorRes colorRes: Int): ShapeEditor {
            strokeWidth = selector.unit.convert(width).toInt()
            strokeColor = ContextCompat.getColor(selector.view.context, colorRes)
            return this
        }

        fun dash(width: Int, gap: Int): ShapeEditor {
            dashWidth = selector.unit.convert(width.toFloat()).toInt()
            dashGap = selector.unit.convert(gap.toFloat()).toInt()
            return this
        }

        fun orientation(orientation: GradientDrawable.Orientation): ShapeEditor {
            this.orientation = orientation
            return this
        }

        fun defPressDark(@FloatRange(from = 0.0, to = 1.0) ratio: Float): ShapeEditor {
            selector.defPressDark(ratio)
            return this
        }

        fun editNormal(): ShapeEditor {
            return selector.editNormal()
        }

        fun editDisable(): ShapeEditor {
            return selector.editDisable()
        }

        fun editSelected(): ShapeEditor {
            return selector.editSelected()
        }

        fun editPress(): ShapeEditor {
            return selector.editPress(false)
        }

        fun editPress(isRipple: Boolean): ShapeEditor {
            return selector.editPress(isRipple)
        }

        fun drawable(): Drawable {
            return selector.drawable
        }

        fun setBackground() {
            selector.setBackground()
        }

    }

    companion object {
        fun translateDark(color: Int, ratio: Float): Int {
            var ratio = ratio
            ratio = 1f - ratio
            val a = color shr 24 and 0xFF
            val r = ((color shr 16 and 0xFF) * ratio).toInt()
            val g = ((color shr 8 and 0xFF) * ratio).toInt()
            val b = ((color and 0xFF) * ratio).toInt()
            return a shl 24 or (r shl 16) or (g shl 8) or b
        }
    }

    init {
        unit = Shape.DpUnit(view.context)
    }
}

class Shape(private val view: View) {
    private var unit: Unit
    private var color = 0
    private var radius = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var strokeWidth = 0
    private var strokeColor = Color.TRANSPARENT
    /**
     * 在android中设置虚线需要
     * 1. 用shape画虚线使用时，控件的layout_height一定要大于shape中stroke标签的width属性
     * 2. 将该控件设置关闭硬件加速（这里会处理）
     */
    private var dashWidth = 0
    private var dashGap = 0
    fun setUnit(unit: Unit): Shape {
        this.unit = unit
        return this
    }

    fun radius(r: Float): Shape {
        val r_ = unit.convert(r)
        radius = floatArrayOf(r_, r_, r_, r_, r_, r_, r_, r_)
        return this
    }

    fun radius(topLeft: Float, topRight: Float, bottomRight: Float, bottomLeft: Float): Shape {
        val topLeft_ = unit.convert(topLeft)
        val topRight_ = unit.convert(topRight)
        val bottomRight_ = unit.convert(bottomRight)
        val bottomLeft_ = unit.convert(bottomLeft)
        radius = floatArrayOf(topLeft_, topLeft_, topRight_, topRight_, bottomRight_, bottomRight_, bottomLeft_, bottomLeft_)
        return this
    }

    fun radius(topLeftX: Float, topLeftY: Float, topRightX: Float, topRightY: Float, bottomRightX: Float, bottomRightY: Float, bottomLeftX: Float, bottomLeftY: Float): Shape {
        val topLeftX_ = unit.convert(topLeftX)
        val topLeftY_ = unit.convert(topLeftY)
        val topRightX_ = unit.convert(topRightX)
        val topRightY_ = unit.convert(topRightY)
        val bottomRightX_ = unit.convert(bottomRightX)
        val bottomRightY_ = unit.convert(bottomRightY)
        val bottomLeftX_ = unit.convert(bottomLeftX)
        val bottomLeftY_ = unit.convert(bottomLeftY)
        radius = floatArrayOf(topLeftX_, topLeftY_, topRightX_, topRightY_, bottomRightX_, bottomRightY_, bottomLeftX_, bottomLeftY_)
        return this
    }

    fun stroke(width: Float, color: Int): Shape {
        strokeWidth = unit.convert(width).toInt()
        strokeColor = color
        return this
    }

    fun strokeRes(width: Float, @ColorRes colorRes: Int): Shape {
        strokeWidth = unit.convert(width).toInt()
        strokeColor = ContextCompat.getColor(view.context, colorRes)
        return this
    }

    fun color(color: Int): Shape {
        this.color = color
        return this
    }

    fun colorRes(@ColorRes colorRes: Int): Shape {
        color = ContextCompat.getColor(view.context, colorRes)
        return this
    }

    fun dash(width: Int, gap: Int): Shape {
        dashWidth = unit.convert(width.toFloat()).toInt()
        dashGap = unit.convert(gap.toFloat()).toInt()
        return this
    }

    fun setShape(): kotlin.Unit {
        setBackgroundCompat(drawable, view)
    }

    //        Drawable drawable = view.getBackground();
//        if(null != drawable && drawable instanceof ShapeDrawable){
//            shapeDrawable = (ShapeDrawable) drawable;
//        }
//        if(null == shapeDrawable){
//            shapeDrawable = new ShapeDrawable();
//        }
    val drawable: Drawable
        get() { //        Drawable drawable = view.getBackground();
//        if(null != drawable && drawable instanceof ShapeDrawable){
//            shapeDrawable = (ShapeDrawable) drawable;
//        }
//        if(null == shapeDrawable){
//            shapeDrawable = new ShapeDrawable();
//        }
            val drawable = getDrawableCompat(radius, intArrayOf(color, color), GradientDrawable.Orientation.LEFT_RIGHT)
            if (0 == dashWidth && 0 == dashGap) {
                drawable.setStroke(strokeWidth, strokeColor)
            } else {
                drawable.setStroke(strokeWidth, strokeColor, dashWidth.toFloat(), dashGap.toFloat())
                view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            }
            return drawable
        }

    interface Unit {
        fun convert(dpValue: Float): Float
    }

    class DpUnit(private val context: Context) : Unit {
        override fun convert(dpValue: Float): Float {
            val scale = context.resources.displayMetrics.density
            return dpValue * scale + 0.5f
        }

    }

    inner class PxUnit : Unit {
        override fun convert(dpValue: Float): Float {
            return dpValue
        }
    }

    companion object {
        /**
         *
         * @param radius 四个角的半径
         * @param colors 渐变的颜色
         * @return
         */
        fun getDrawableCompat(radius: FloatArray?, colors: IntArray?, orientation: GradientDrawable.Orientation?): GradientDrawable { //TODO:判断版本是否大于16  项目中默认的都是Linear散射 都是从左到右 都是只有开始颜色和结束颜色
            val drawable: GradientDrawable
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                drawable = GradientDrawable()
                drawable.orientation = orientation
                drawable.colors = colors
            } else {
                drawable = GradientDrawable(orientation, colors)
            }
            drawable.cornerRadii = radius
            drawable.gradientType = GradientDrawable.LINEAR_GRADIENT
            return drawable
        }

        /**
         *
         * @param drawable 生成的背景
         * @param view 需要添加背景的View
         */
        fun setBackgroundCompat(drawable: Drawable?, view: View): kotlin.Unit { //判断当前版本号，版本号大于等于16，使用setBackground；版本号小于16，使用setBackgroundDrawable。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.background = drawable
            } else {
                view.setBackgroundDrawable(drawable)
            }
        }
    }

    init {
        unit = DpUnit(view.context)
    }
}



