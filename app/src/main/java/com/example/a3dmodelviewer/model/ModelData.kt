package com.example.a3dmodelviewer.model


import android.graphics.PointF

data class ModelData(
    val assetName: String,
    val displayName: String,
    val containerPosition: PointF = PointF(0f, 0f),
    val containerSize: Float = DEFAULT_SIZE,
    val isInteractMode: Boolean = false
) {
    companion object {
        const val DEFAULT_SIZE = 300f
        const val MIN_SIZE     = 150f
        const val MAX_SIZE     = 700f
    }
}