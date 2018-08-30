package com.bitvale.lavafab.widget

import android.animation.FloatEvaluator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import androidx.annotation.IntDef
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by Alexander Kolpakov on 27.08.2018
 */
class Child(val parent: Parent, override val center: PointF, override val radius: Float) : LavaView {

    override var currentRadius: Float = 0f
    override val evaluator = FloatEvaluator()
    override var icon: Bitmap? = null
    override var listener: LavaView.LavaOnClickListener? = null

    // angles is used on step where's child is connected to parent
    // 1 & 2 - angles for parent circle arc (bezier start point)
    // 2 & 3 - angles for parent circle arc (bezier middle point)
    // 4 - end angle for evaluator (animate bezier middle point)
    private val angles = DoubleArray(5)

    private var initialRadius: Float = 0f
    var type = CHILD_NONE
        set(value) {
            field = value
            initProperties(field)
        }

    private var radiusOffset = 0f
    private var outParentCubicOffset = 0f
    private var outChildCubicOffset = 0f
    private var middlePointAngle = floatArrayOf(0f, 0f)

    private var animatedFraction = 0f

    init {
        initialRadius = radius - radius / 4f
    }

    private fun initProperties(type: Int) {
        when (type) {
            CHILD_LEFT -> {
                angles[0] = toRadians(45.0)
                angles[1] = toRadians(-45.0)
                angles[2] = toRadians(60.0)
                angles[3] = toRadians(-60.0)
                angles[4] = toRadians(0.0)
            }
            CHILD_LEFT_TOP -> {
                angles[0] = toRadians(0.0)
                angles[1] = toRadians(90.0)
                angles[2] = toRadians(-15.0)
                angles[3] = toRadians(105.0)
                angles[4] = toRadians(45.0)
            }
            CHILD_TOP -> {
                angles[0] = toRadians(45.0)
                angles[1] = toRadians(135.0)
                angles[2] = toRadians(30.0)
                angles[3] = toRadians(150.0)
                angles[4] = toRadians(90.0)
            }
            CHILD_RIGHT_TOP -> {
                angles[0] = toRadians(90.0)
                angles[1] = toRadians(180.0)
                angles[2] = toRadians(75.0)
                angles[3] = toRadians(195.0)
                angles[4] = toRadians(135.0)
            }
            CHILD_RIGHT -> {
                angles[0] = toRadians(135.0)
                angles[1] = toRadians(225.0)
                angles[2] = toRadians(120.0)
                angles[3] = toRadians(240.0)
                angles[4] = toRadians(180.0)
            }
            CHILD_RIGHT_BOTTOM -> {
                angles[0] = toRadians(180.0)
                angles[1] = toRadians(270.0)
                angles[2] = toRadians(165.0)
                angles[3] = toRadians(285.0)
                angles[4] = toRadians(225.0)
            }
            CHILD_BOTTOM -> {
                angles[0] = toRadians(315.0)
                angles[1] = toRadians(225.0)
                angles[2] = toRadians(330.0)
                angles[3] = toRadians(210.0)
                angles[4] = toRadians(270.0)
            }
            CHILD_LEFT_BOTTOM -> {
                angles[0] = toRadians(270.0)
                angles[1] = toRadians(360.0)
                angles[2] = toRadians(255.0)
                angles[3] = toRadians(375.0)
                angles[4] = toRadians(315.0)
            }
        }
    }

    override fun calculatePoints(animatedFraction: Float) {
        this.animatedFraction = animatedFraction

        radiusOffset = evaluator.evaluate(animatedFraction, 0f, radius / 4f)

        when (type) {
            CHILD_LEFT -> {
                center.x = evaluator.evaluate(animatedFraction, parent.center.x - initialRadius / 2,
                        parent.center.x - CHILD_DISTANCE_RATIO * parent.radius)
            }
            CHILD_LEFT_TOP -> {
                center.x = evaluator.evaluate(animatedFraction, parent.center.x - initialRadius / 2,
                        parent.center.x - CHILD_ANGLE_DISTANCE_RATIO * parent.radius)
                center.y = evaluator.evaluate(animatedFraction, parent.center.y - initialRadius / 2,
                        parent.center.y - CHILD_ANGLE_DISTANCE_RATIO * parent.radius)
            }
            CHILD_TOP -> {
                center.y = evaluator.evaluate(animatedFraction, parent.center.y - initialRadius / 2,
                        parent.center.y - CHILD_DISTANCE_RATIO * parent.radius)
            }
            CHILD_RIGHT_TOP -> {
                center.x = evaluator.evaluate(animatedFraction, parent.center.x + initialRadius / 2,
                        parent.center.x + CHILD_ANGLE_DISTANCE_RATIO * parent.radius)
                center.y = evaluator.evaluate(animatedFraction, parent.center.y - initialRadius / 2,
                        parent.center.y - CHILD_ANGLE_DISTANCE_RATIO * parent.radius)
            }
            CHILD_RIGHT -> {
                center.x = evaluator.evaluate(animatedFraction, parent.center.x + initialRadius / 2,
                        parent.center.x + CHILD_DISTANCE_RATIO * parent.radius)
            }
            CHILD_RIGHT_BOTTOM -> {
                center.x = evaluator.evaluate(animatedFraction, parent.center.x + initialRadius / 2,
                        parent.center.x + CHILD_ANGLE_DISTANCE_RATIO * parent.radius)
                center.y = evaluator.evaluate(animatedFraction, parent.center.y + initialRadius / 2,
                        parent.center.y + CHILD_ANGLE_DISTANCE_RATIO * parent.radius)
            }
            CHILD_BOTTOM -> {
                center.y = evaluator.evaluate(animatedFraction, parent.center.y + initialRadius / 2,
                        parent.center.y + CHILD_DISTANCE_RATIO * parent.radius)
            }
            CHILD_LEFT_BOTTOM -> {
                center.x = evaluator.evaluate(animatedFraction, parent.center.x - initialRadius / 2,
                        parent.center.x - CHILD_ANGLE_DISTANCE_RATIO * parent.radius)
                center.y = evaluator.evaluate(animatedFraction, parent.center.y + initialRadius / 2,
                        parent.center.y + CHILD_ANGLE_DISTANCE_RATIO * parent.radius)
            }
        }

        middlePointAngle[0] = evaluator.evaluate(animatedFraction, angles[2], angles[4])
        middlePointAngle[1] = evaluator.evaluate(animatedFraction, angles[3], angles[4])

        if (animatedFraction >= 0.8) {
            outParentCubicOffset = evaluator.evaluate(animatedFraction, radius * 4, 0f)
            outChildCubicOffset = evaluator.evaluate(animatedFraction, radius * 4, 0f)
        }
    }

    override fun draw(canvas: Canvas?) {
        parent.drawer.rewindChildCircle()
        parent.drawer.rewindHelpers()
        currentRadius = initialRadius + radiusOffset
        parent.drawer.addCircle(center.x, center.y, currentRadius, false)

        when (type) {
            CHILD_LEFT -> drawOnLeftOrRight(canvas, CHILD_LEFT)
            CHILD_LEFT_TOP -> drawLeftTopOrRightBottom(canvas, CHILD_LEFT_TOP)
            CHILD_TOP -> drawOnTopOrBottom(canvas, CHILD_TOP)
            CHILD_RIGHT_TOP -> drawLeftBottomOrRightTop(canvas, CHILD_RIGHT_TOP)
            CHILD_RIGHT -> drawOnLeftOrRight(canvas, CHILD_RIGHT)
            CHILD_RIGHT_BOTTOM -> drawLeftTopOrRightBottom(canvas, CHILD_RIGHT_BOTTOM)
            CHILD_BOTTOM -> drawOnTopOrBottom(canvas, CHILD_BOTTOM)
            CHILD_LEFT_BOTTOM -> drawLeftBottomOrRightTop(canvas, CHILD_LEFT_BOTTOM)
        }

        if (animatedFraction >= 1) {
            parent.drawer.rewindHelpers()
            parent.drawer.drawDisconnectedHelpers(canvas)
        }
        parent.drawer.drawChildCircle(canvas)
        drawIcon(canvas)
    }

    private fun drawIcon(canvas: Canvas?) {
        if (animatedFraction >= 0.5) parent.drawer.drawIcon(canvas, icon, center.x, center.y, currentRadius)
    }

    private fun drawOnLeftOrRight(canvas: Canvas?, direction: Int) {
        val parentDirector = if (direction == CHILD_LEFT) -1 else 1
        val childDirector = -parentDirector

        // draw two bezier between circles (circles are still connected)
        if (animatedFraction < 0.8) {
            var x1 = parent.center.x - parent.currentRadius * cos(angles[0]).toFloat()
            var y1 = parent.center.y - parent.currentRadius * sin(angles[0]).toFloat()

            var x2 = parent.center.x - parent.currentRadius * cos(middlePointAngle[0])
            var y2 = parent.center.y - parent.currentRadius * sin(middlePointAngle[0])

            var x4 = center.x
            var y4 = center.y - currentRadius

            parent.drawer.addConnectedBezier(x1, y1, x2, y2, x2, y2, x4, y4)

            x1 = parent.center.x - parent.currentRadius * cos(angles[1]).toFloat()
            y1 = parent.center.y - parent.currentRadius * sin(angles[1]).toFloat()

            x2 = parent.center.x - parent.currentRadius * cos(middlePointAngle[1])
            y2 = parent.center.y - parent.currentRadius * sin(middlePointAngle[1])

            x4 = center.x
            y4 = center.y + currentRadius

            parent.drawer.addConnectedBezier(x1, y1, x2, y2, x2, y2, x4, y4)

            parent.drawer.drawConnectedHelpers(canvas, center.x, center.y - currentRadius,
                    parent.center.x, center.y + currentRadius)
        }

        // draw two bezier between circles, on child end and parent start (circles are  disconnected)
        if (animatedFraction >= 0.8 && animatedFraction < 1) {

            var x1 = parent.center.x - parent.currentRadius * cos(angles[0]).toFloat()
            var y1 = parent.center.y - parent.currentRadius * sin(angles[0]).toFloat()

            var x2 = parent.center.x + parentDirector * (parent.currentRadius + outParentCubicOffset)
            var y2 = parent.center.y

            var x4 = parent.center.x - parent.currentRadius * cos(angles[1]).toFloat()
            var y4 = parent.center.y - parent.currentRadius * sin(angles[1]).toFloat()

            parent.drawer.addDisconnectedBezier(x1, y1, x2, y2, x2, y2, x4, y4)

            x1 = center.x + currentRadius * cos(angles[0]).toFloat()
            y1 = center.y + currentRadius * sin(angles[0]).toFloat()

            x2 = center.x + childDirector * (currentRadius + outChildCubicOffset)
            y2 = center.y

            x4 = center.x + currentRadius * cos(angles[1]).toFloat()
            y4 = center.y + currentRadius * sin(angles[1]).toFloat()

            parent.drawer.addDisconnectedBezier(x1, y1, x2, y2, x2, y2, x4, y4)

            parent.drawer.drawDisconnectedHelpers(canvas)
        }
    }

    private fun drawOnTopOrBottom(canvas: Canvas?, direction: Int) {
        val parentDirector = if (direction == CHILD_TOP) -1 else 1
        val childDirector = -parentDirector

        // draw two bezier between circles (circles are still connected)
        if (animatedFraction < 0.8) {
            var x1 = parent.center.x - parent.currentRadius * cos(angles[0]).toFloat()
            var y1 = parent.center.y - parent.currentRadius * sin(angles[0]).toFloat()

            var x2 = parent.center.x - parent.currentRadius * cos(middlePointAngle[0])
            var y2 = parent.center.y - parent.currentRadius * sin(middlePointAngle[0])

            var x4 = center.x - currentRadius
            var y4 = center.y

            parent.drawer.addConnectedBezier(x1, y1, x2, y2, x2, y2, x4, y4)

            x1 = parent.center.x - parent.currentRadius * cos(angles[1]).toFloat()
            y1 = parent.center.y - parent.currentRadius * sin(angles[1]).toFloat()

            x2 = parent.center.x - parent.currentRadius * cos(middlePointAngle[1])
            y2 = parent.center.y - parent.currentRadius * sin(middlePointAngle[1])

            x4 = center.x + currentRadius
            y4 = center.y

            parent.drawer.addConnectedBezier(x1, y1, x2, y2, x2, y2, x4, y4)

            parent.drawer.drawConnectedHelpers(canvas, center.x - currentRadius, center.y,
                    parent.center.x + currentRadius, parent.center.y)
        }

        // draw two bezier between circles, on child end and parent start (circles are  disconnected)
        if (animatedFraction >= 0.8 && animatedFraction < 1) {

            var x1 = parent.center.x - parent.currentRadius * cos(angles[0]).toFloat()
            var y1 = parent.center.y - parent.currentRadius * sin(angles[0]).toFloat()

            var x2 = parent.center.x
            var y2 = parent.center.y + parentDirector * (parent.currentRadius + outParentCubicOffset)

            var x4 = parent.center.x - parent.currentRadius * cos(angles[1]).toFloat()
            var y4 = parent.center.y - parent.currentRadius * sin(angles[1]).toFloat()

            parent.drawer.addDisconnectedBezier(x1, y1, x2, y2, x2, y2, x4, y4)

            x1 = center.x + currentRadius * cos(angles[0]).toFloat()
            y1 = center.y + currentRadius * sin(angles[0]).toFloat()

            x2 = center.x
            y2 = center.y + childDirector * (currentRadius + outChildCubicOffset)

            x4 = center.x + currentRadius * cos(angles[1]).toFloat()
            y4 = center.y + currentRadius * sin(angles[1]).toFloat()

            parent.drawer.addDisconnectedBezier(x1, y1, x2, y2, x2, y2, x4, y4)

            parent.drawer.drawDisconnectedHelpers(canvas)
        }
    }

    private fun drawLeftTopOrRightBottom(canvas: Canvas?, direction: Int) {
        val parentDirector = if (direction == CHILD_LEFT_TOP) -1 else 1
        val childDirector = -parentDirector

        if (animatedFraction >= 0.8 && animatedFraction < 1) {

            var x1 = parent.center.x - parent.currentRadius * cos(angles[0]).toFloat()
            var y1 = parent.center.y - parent.currentRadius * sin(angles[0]).toFloat()

            var x2 = parent.center.x + parentDirector * (parent.currentRadius + outParentCubicOffset)
            var y2 = parent.center.y + parentDirector * (parent.currentRadius + outParentCubicOffset)

            var x4 = parent.center.x - parent.currentRadius * cos(angles[1]).toFloat()
            var y4 = parent.center.y - parent.currentRadius * sin(angles[1]).toFloat()

            parent.drawer.addDisconnectedBezier(x1, y1, x2, y2, x2, y2, x4, y4)

            x1 = center.x + currentRadius * cos(angles[0]).toFloat()
            y1 = center.y + currentRadius * sin(angles[0]).toFloat()

            x2 = center.x + childDirector * (currentRadius + outChildCubicOffset)
            y2 = center.y + childDirector * (currentRadius + outChildCubicOffset)

            x4 = center.x + currentRadius * cos(angles[1]).toFloat()
            y4 = center.y + currentRadius * sin(angles[1]).toFloat()

            parent.drawer.addDisconnectedBezier(x1, y1, x2, y2, x2, y2, x4, y4)

            parent.drawer.drawDisconnectedHelpers(canvas)
        }
    }

    private fun drawLeftBottomOrRightTop(canvas: Canvas?, direction: Int) {
        val parentDirector = if (direction == CHILD_LEFT_BOTTOM) -1 else 1
        val childDirector = -parentDirector

        if (animatedFraction >= 0.8 && animatedFraction < 1) {

            var x1 = parent.center.x - parent.currentRadius * cos(angles[0]).toFloat()
            var y1 = parent.center.y - parent.currentRadius * sin(angles[0]).toFloat()

            var x2 = parent.center.x + parentDirector * (parent.currentRadius + outParentCubicOffset)
            var y2 = parent.center.y + childDirector * (parent.currentRadius + outParentCubicOffset)

            var x4 = parent.center.x - parent.currentRadius * cos(angles[1]).toFloat()
            var y4 = parent.center.y - parent.currentRadius * sin(angles[1]).toFloat()

            parent.drawer.addDisconnectedBezier(x1, y1, x2, y2, x2, y2, x4, y4)

            x1 = center.x + currentRadius * cos(angles[0]).toFloat()
            y1 = center.y + currentRadius * sin(angles[0]).toFloat()

            x2 = center.x + childDirector * (currentRadius + outChildCubicOffset)
            y2 = center.y + parentDirector * (currentRadius + outChildCubicOffset)

            x4 = center.x + currentRadius * cos(angles[1]).toFloat()
            y4 = center.y + currentRadius * sin(angles[1]).toFloat()

            parent.drawer.addDisconnectedBezier(x1, y1, x2, y2, x2, y2, x4, y4)

            parent.drawer.drawDisconnectedHelpers(canvas)
        }
    }

    override fun handleOnClick(x: Float, y: Float) {
        val clickRect = RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius)
        if (clickRect.contains(x, y)) {
            listener?.onClick()
        }
    }

    override fun setOnClickListener(listener: LavaView.LavaOnClickListener?) {
        this.listener = listener
    }

    override fun setOnClickListener(listener: () -> Unit) {
        this.listener = object : LavaView.LavaOnClickListener {
            override fun onClick() {
                listener()
            }
        }
    }

    @IntDef(CHILD_NONE,
            CHILD_TOP,
            CHILD_RIGHT,
            CHILD_BOTTOM,
            CHILD_LEFT,
            CHILD_LEFT_TOP,
            CHILD_RIGHT_TOP,
            CHILD_RIGHT_BOTTOM,
            CHILD_LEFT_BOTTOM,
            CHILD_ALL)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type

    companion object {
        const val CHILD_NONE = 0
        const val CHILD_TOP = 1
        const val CHILD_RIGHT = 2
        const val CHILD_BOTTOM = 4
        const val CHILD_LEFT = 8
        const val CHILD_LEFT_TOP = 16
        const val CHILD_RIGHT_TOP = 32
        const val CHILD_RIGHT_BOTTOM = 64
        const val CHILD_LEFT_BOTTOM = 128
        const val CHILD_ALL = 255

        const val CHILD_RADIUS_RATIO = 1.6f
        const val CHILD_DISTANCE_RATIO = 2.6f
        const val CHILD_ANGLE_DISTANCE_RATIO = 2.0f
    }
}