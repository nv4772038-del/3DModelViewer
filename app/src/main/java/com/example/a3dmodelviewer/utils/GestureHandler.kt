package com.example.a3dmodelviewer.utils


import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector

class GestureHandler(
    context: Context,
    private val callbacks: Callbacks
) {

    interface Callbacks {
        fun isInteractMode(): Boolean
        fun onContainerTranslate(dx: Float, dy: Float)
        fun onContainerScale(scaleFactor: Float)
        fun onModelRotate(dx: Float, dy: Float)
        fun onModelZoom(scaleFactor: Float)
    }

    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var isScaling = false

    private val scaleDetector = ScaleGestureDetector(
        context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                isScaling = true
                return true
            }

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (callbacks.isInteractMode()) {
                    callbacks.onModelZoom(detector.scaleFactor)
                } else {
                    callbacks.onContainerScale(detector.scaleFactor)
                }
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                isScaling = false
            }
        }
    )

    fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                activePointerId = event.getPointerId(0)
                lastTouchX = event.x
                lastTouchY = event.y
            }

            MotionEvent.ACTION_MOVE -> {
                if (isScaling) return true
                val pointerIndex = event.findPointerIndex(activePointerId)
                if (pointerIndex < 0) return true

                val dx = event.getX(pointerIndex) - lastTouchX
                val dy = event.getY(pointerIndex) - lastTouchY

                if (callbacks.isInteractMode()) {
                    callbacks.onModelRotate(dx, dy)
                } else {
                    callbacks.onContainerTranslate(dx, dy)
                }

                lastTouchX = event.getX(pointerIndex)
                lastTouchY = event.getY(pointerIndex)
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                activePointerId = MotionEvent.INVALID_POINTER_ID
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                if (pointerId == activePointerId) {
                    val newIndex = if (pointerIndex == 0) 1 else 0
                    lastTouchX = event.getX(newIndex)
                    lastTouchY = event.getY(newIndex)
                    activePointerId = event.getPointerId(newIndex)
                }
            }
        }
        return true
    }
}